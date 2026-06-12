#define _POSIX_C_SOURCE 200809L

#include "hybrid_simulation.h"

#include "hybrid_sync.h"
#include "position_history.h"
#include "report.h"
#include "safety_monitor.h"
#include "safety_report_notifier.h"
#include "shared_simulation.h"

#include <errno.h>
#include <pthread.h>
#include <signal.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/wait.h>
#include <time.h>
#include <unistd.h>

static volatile sig_atomic_t hybrid_stop_requested = 0;
static volatile sig_atomic_t hybrid_safety_signal_received = 0;

typedef struct {
    pthread_mutex_t mutex;
    pthread_cond_t step_completed_cond;
    pthread_cond_t safety_done_cond;
    pthread_cond_t report_cond;
    int last_completed_step;
    int last_safety_processed_step;
    int scheduler_finished;
    int safety_finished;
    int report_event_count;
} parent_thread_state_t;

typedef struct {
    shared_simulation_t *shared;
    hybrid_sync_t *sync;
    int flight_count;
    position_update_t latest_positions[MAX_FLIGHTS];
    position_history_t position_history;
    int reported_violations[MAX_FLIGHTS][MAX_FLIGHTS][SIMULATION_STEPS];
    int total_violations;
    simulation_report_t report;
    safety_report_notifier_t report_notifier;
    parent_thread_state_t threads;
} hybrid_context_t;

static void handle_hybrid_stop_signal(int signal_number) {
    (void) signal_number;
    hybrid_stop_requested = 1;
}

static void handle_hybrid_safety_signal(int signal_number) {
    (void) signal_number;
    hybrid_safety_signal_received = 1;
}

static int install_hybrid_stop_handlers(void) {
    struct sigaction action;
    struct sigaction ignore_action;

    memset(&action, 0, sizeof(action));
    action.sa_handler = handle_hybrid_stop_signal;
    action.sa_flags = SA_RESTART;
    sigemptyset(&action.sa_mask);

    if (sigaction(SIGINT, &action, NULL) == -1) {
        return -1;
    }

    if (sigaction(SIGTERM, &action, NULL) == -1) {
        return -1;
    }

    memset(&ignore_action, 0, sizeof(ignore_action));
    ignore_action.sa_handler = SIG_IGN;
    sigemptyset(&ignore_action.sa_mask);

    if (sigaction(SIGPIPE, &ignore_action, NULL) == -1) {
        return -1;
    }

    return 0;
}

static int install_hybrid_flight_handlers(void) {
    struct sigaction action;

    if (install_hybrid_stop_handlers() == -1) {
        return -1;
    }

    memset(&action, 0, sizeof(action));
    action.sa_handler = handle_hybrid_safety_signal;
    action.sa_flags = SA_RESTART;
    sigfillset(&action.sa_mask);

    if (sigaction(SIGUSR1, &action, NULL) == -1) {
        return -1;
    }

    return 0;
}

static void print_pending_hybrid_safety_notification(int flight_id) {
    if (hybrid_safety_signal_received) {
        printf("hybrid flight %d handled SIGUSR1: safety violation notification received\n",
               flight_id);
        fflush(stdout);
        hybrid_safety_signal_received = 0;
    }
}

static position_update_t build_position_update(const flight_plan_t *flight, int step) {
    position_update_t update;

    update.flight_id = flight->flight_id;
    update.step = step;
    update.process_id = getpid();
    update.latitude = flight->initial_latitude + flight->velocity_latitude * step;
    update.longitude = flight->initial_longitude + flight->velocity_longitude * step;
    update.altitude = flight->initial_altitude + flight->velocity_altitude * step;
    update.velocity_latitude = flight->velocity_latitude;
    update.velocity_longitude = flight->velocity_longitude;
    update.velocity_altitude = flight->velocity_altitude;
    update.completed = step == SIMULATION_STEPS - 1;

    return update;
}

static void print_update(const position_update_t *update) {
    printf("hybrid step=%d flight=%d pid=%d pos=(%.4f, %.4f, %.1f) "
           "vel=(%.4f, %.4f, %.1f)%s\n",
           update->step,
           update->flight_id,
           (int) update->process_id,
           update->latitude,
           update->longitude,
           update->altitude,
           update->velocity_latitude,
           update->velocity_longitude,
           update->velocity_altitude,
           update->completed ? " completed" : "");
    fflush(stdout);
}

static void request_hybrid_stop(hybrid_context_t *context) {
    hybrid_stop_requested = 1;
    if (context != NULL && context->shared != NULL) {
        context->shared->stop_requested = 1;
    }
}

static int init_parent_thread_state(parent_thread_state_t *state) {
    memset(state, 0, sizeof(*state));
    state->last_completed_step = -1;
    state->last_safety_processed_step = -1;

    if (pthread_mutex_init(&state->mutex, NULL) != 0) {
        return -1;
    }

    if (pthread_cond_init(&state->step_completed_cond, NULL) != 0) {
        pthread_mutex_destroy(&state->mutex);
        return -1;
    }

    if (pthread_cond_init(&state->safety_done_cond, NULL) != 0) {
        pthread_cond_destroy(&state->step_completed_cond);
        pthread_mutex_destroy(&state->mutex);
        return -1;
    }

    if (pthread_cond_init(&state->report_cond, NULL) != 0) {
        pthread_cond_destroy(&state->safety_done_cond);
        pthread_cond_destroy(&state->step_completed_cond);
        pthread_mutex_destroy(&state->mutex);
        return -1;
    }

    return 0;
}

static void destroy_parent_thread_state(parent_thread_state_t *state) {
    pthread_cond_destroy(&state->report_cond);
    pthread_cond_destroy(&state->safety_done_cond);
    pthread_cond_destroy(&state->step_completed_cond);
    pthread_mutex_destroy(&state->mutex);
}

static void init_hybrid_context(hybrid_context_t *context,
                                shared_simulation_t *shared,
                                hybrid_sync_t *sync,
                                int flight_count) {
    memset(context, 0, sizeof(*context));
    context->shared = shared;
    context->sync = sync;
    context->flight_count = flight_count;
    init_position_history(&context->position_history);
    init_simulation_report(&context->report);
}

static void wait_for_next_timestep(void) {
    struct timespec delay;
    struct timespec remaining;

    delay.tv_sec = TIME_STEP_SECONDS;
    delay.tv_nsec = 0;

    while (!hybrid_stop_requested && nanosleep(&delay, &remaining) == -1) {
        if (errno != EINTR) {
            return;
        }

        delay = remaining;
    }
}

static void run_hybrid_flight_process(shared_simulation_t *shared,
                                      hybrid_sync_t *sync,
                                      int flight_index) {
    if (install_hybrid_flight_handlers() == -1) {
        perror("sigaction");
        _exit(EXIT_FAILURE);
    }

    while (!hybrid_stop_requested) {
        if (hybrid_sync_wait_start(sync, flight_index) == -1) {
            if (hybrid_stop_requested) {
                break;
            }

            _exit(EXIT_FAILURE);
        }

        if (shared->stop_requested) {
            break;
        }

        const int step = shared->current_step;
        const flight_plan_t *flight = &shared->flights[flight_index];

        if (step < 0 || step >= SIMULATION_STEPS) {
            fprintf(stderr, "invalid hybrid timestep command %d for flight %d\n",
                    step, flight->flight_id);
            _exit(EXIT_FAILURE);
        }

        print_pending_hybrid_safety_notification(flight->flight_id);

        const position_update_t update = build_position_update(flight, step);
        shared->updates[flight_index][step] = update;
        shared->received[flight_index][step] = 1;

        if (hybrid_sync_mark_update_ready(sync) == -1) {
            _exit(EXIT_FAILURE);
        }

        print_pending_hybrid_safety_notification(flight->flight_id);

        if (update.completed) {
            break;
        }
    }

    _exit(EXIT_SUCCESS);
}

static int start_hybrid_flight_processes(shared_simulation_t *shared,
                                         hybrid_sync_t *sync,
                                         int flight_count) {
    for (int i = 0; i < flight_count; i++) {
        const pid_t pid = fork();

        if (pid == -1) {
            perror("fork");
            return -1;
        }

        if (pid == 0) {
            run_hybrid_flight_process(shared, sync, i);
        }

        shared->flight_pids[i] = pid;
    }

    return 0;
}

static void terminate_hybrid_children(const shared_simulation_t *shared, int flight_count) {
    for (int i = 0; i < flight_count; i++) {
        if (shared->flight_pids[i] > 0) {
            kill(shared->flight_pids[i], SIGTERM);
        }
    }
}

static void wait_for_hybrid_children(const shared_simulation_t *shared, int flight_count) {
    for (int i = 0; i < flight_count; i++) {
        if (shared->flight_pids[i] > 0) {
            int status;
            if (waitpid(shared->flight_pids[i], &status, 0) == -1) {
                perror("waitpid");
            }
        }
    }
}

static int collect_shared_updates_for_step(hybrid_context_t *context, int step) {
    for (int flight_index = 0; flight_index < context->flight_count; flight_index++) {
        if (!context->shared->received[flight_index][step]) {
            fprintf(stderr, "missing shared update from flight index %d at step %d\n",
                    flight_index, step);
            return -1;
        }

        const position_update_t *update = &context->shared->updates[flight_index][step];
        if (update->step != step) {
            fprintf(stderr,
                    "out-of-order shared update from flight index %d: expected step %d, got %d\n",
                    flight_index,
                    step,
                    update->step);
            return -1;
        }

        if (store_position_update(&context->position_history,
                                  context->latest_positions,
                                  context->flight_count,
                                  flight_index,
                                  update) == -1) {
            fprintf(stderr, "invalid shared update from flight index %d at step %d\n",
                    flight_index, step);
            return -1;
        }

        print_update(update);
    }

    return 0;
}

static void finish_scheduler(hybrid_context_t *context) {
    context->shared->stop_requested = 1;
    hybrid_sync_release_all(context->sync);

    pthread_mutex_lock(&context->threads.mutex);
    context->threads.scheduler_finished = 1;
    pthread_cond_broadcast(&context->threads.step_completed_cond);
    pthread_cond_broadcast(&context->threads.safety_done_cond);
    pthread_cond_broadcast(&context->threads.report_cond);
    pthread_mutex_unlock(&context->threads.mutex);

    hybrid_sync_mark_parent_step_ready(context->sync);
}

static void *scheduler_thread_main(void *argument) {
    hybrid_context_t *context = argument;

    for (int step = 0; step < SIMULATION_STEPS && !hybrid_stop_requested; step++) {
        const int simulation_time = step * TIME_STEP_SECONDS;

        printf("\n--- hybrid timestep %d (t=%ds) ---\n", step, simulation_time);
        fflush(stdout);

        context->shared->current_step = step;

        if (hybrid_sync_release_all(context->sync) == -1) {
            request_hybrid_stop(context);
            break;
        }

        if (hybrid_sync_wait_for_updates(context->sync, context->flight_count) == -1) {
            request_hybrid_stop(context);
            break;
        }

        if (collect_shared_updates_for_step(context, step) == -1) {
            request_hybrid_stop(context);
            break;
        }

        printf("hybrid timestep %d semaphore barrier complete: %d/%d updates processed\n",
               step, context->flight_count, context->flight_count);
        fflush(stdout);

        pthread_mutex_lock(&context->threads.mutex);
        context->threads.last_completed_step = step;
        pthread_cond_broadcast(&context->threads.step_completed_cond);
        pthread_mutex_unlock(&context->threads.mutex);

        if (hybrid_sync_mark_parent_step_ready(context->sync) == -1) {
            request_hybrid_stop(context);
            break;
        }

        if (hybrid_sync_wait_parent_step_done(context->sync) == -1) {
            request_hybrid_stop(context);
            break;
        }

        printf("hybrid timestep %d parent-thread semaphore lockstep complete\n", step);
        fflush(stdout);

        if (!hybrid_stop_requested && step < SIMULATION_STEPS - 1) {
            wait_for_next_timestep();
        }
    }

    finish_scheduler(context);
    return NULL;
}

static void notify_hybrid_safety_violations(hybrid_context_t *context,
                                            const safety_scan_result_t *scan_result) {
    for (int i = 0; i < scan_result->count && !hybrid_stop_requested; i++) {
        const safety_violation_t *violation = &scan_result->violations[i];
        const int first = violation->first_flight_index;
        const int second = violation->second_flight_index;

        if (first < 0 || first >= context->flight_count ||
            second < 0 || second >= context->flight_count) {
            continue;
        }

        if (violation->step < 0 || violation->step >= SIMULATION_STEPS) {
            continue;
        }

        if (context->reported_violations[first][second][violation->step]) {
            continue;
        }

        context->reported_violations[first][second][violation->step] = 1;

        log_safety_violation(stdout, violation);
        fflush(stdout);

        if (safety_report_notifier_notify(&context->report_notifier,
                                          violation,
                                          &context->position_history,
                                          context->flight_count) == -1) {
            fprintf(stderr, "failed to notify report thread about safety violation\n");
            request_hybrid_stop(context);
            return;
        }

        kill(context->shared->flight_pids[first], SIGUSR1);
        kill(context->shared->flight_pids[second], SIGUSR1);

        context->total_violations++;

        if (context->total_violations >= SAFETY_MAX_TOTAL_VIOLATIONS) {
            fprintf(stderr,
                    "hybrid safety violation threshold reached (%d), requesting early termination\n",
                    context->total_violations);
            request_hybrid_stop(context);
        }
    }
}

static void process_hybrid_safety_step(hybrid_context_t *context, int step) {
    safety_scan_result_t scan_result;

    if (scan_safety_violations(&context->position_history,
                               context->flight_count,
                               step,
                               &scan_result) == -1) {
        fprintf(stderr, "failed to scan hybrid safety violations at step %d\n", step);
        request_hybrid_stop(context);
        return;
    }

    notify_hybrid_safety_violations(context, &scan_result);
}

static void *safety_thread_main(void *argument) {
    hybrid_context_t *context = argument;

    while (1) {
        if (hybrid_sync_wait_parent_step_ready(context->sync) == -1) {
            request_hybrid_stop(context);
            break;
        }

        pthread_mutex_lock(&context->threads.mutex);

        if (context->threads.scheduler_finished &&
            context->threads.last_completed_step <= context->threads.last_safety_processed_step) {
            context->threads.safety_finished = 1;
            pthread_mutex_unlock(&context->threads.mutex);
            break;
        }

        if (context->threads.last_completed_step <= context->threads.last_safety_processed_step) {
            pthread_mutex_unlock(&context->threads.mutex);
            continue;
        }

        const int step = context->threads.last_safety_processed_step + 1;
        pthread_mutex_unlock(&context->threads.mutex);

        process_hybrid_safety_step(context, step);

        pthread_mutex_lock(&context->threads.mutex);
        context->threads.last_safety_processed_step = step;
        pthread_cond_signal(&context->threads.safety_done_cond);
        pthread_mutex_unlock(&context->threads.mutex);

        if (hybrid_sync_mark_parent_step_done(context->sync) == -1) {
            request_hybrid_stop(context);
            break;
        }
    }

    pthread_mutex_lock(&context->threads.mutex);
    context->threads.safety_finished = 1;
    pthread_mutex_unlock(&context->threads.mutex);
    safety_report_notifier_close(&context->report_notifier);

    return NULL;
}

static void *report_thread_main(void *argument) {
    hybrid_context_t *context = argument;
    report_violation_entry_t entry;

    while (1) {
        const int wait_result = safety_report_notifier_wait(&context->report_notifier, &entry);

        if (wait_result == 0) {
            break;
        }

        if (wait_result == -1) {
            request_hybrid_stop(context);
            break;
        }

        report_append_violation_entry(&context->report, &entry);

        printf("hybrid report thread processed safety event #%d for flights %d/%d at step %d\n",
               context->report.violation_count,
               entry.violation.first_flight_id,
               entry.violation.second_flight_id,
               entry.violation.step);
        fflush(stdout);
    }

    report_collect_flight_results(&context->report,
                                  context->shared->flights,
                                  context->flight_count,
                                  &context->position_history,
                                  hybrid_stop_requested);

    if (report_save_to_file(&context->report, REPORT_FILE_PATH) == 0) {
        printf("hybrid simulation report saved to %s\n", REPORT_FILE_PATH);
    } else {
        fprintf(stderr, "failed to save hybrid simulation report to %s\n", REPORT_FILE_PATH);
    }

    return NULL;
}

int run_hybrid_simulation(const flight_plan_t flights[], int flight_count) {
    shared_simulation_region_t shared_region;
    hybrid_sync_t sync;
    hybrid_context_t context;
    pthread_t scheduler_thread;
    pthread_t safety_thread;
    pthread_t report_thread;
    char shared_name[SHARED_SIMULATION_NAME_LENGTH];
    char sync_prefix[HYBRID_SYNC_NAME_LENGTH];
    int scheduler_started = 0;
    int safety_started = 0;
    int report_started = 0;

    if (flights == NULL || flight_count <= 0 || flight_count > MAX_FLIGHTS) {
        return -1;
    }

    hybrid_stop_requested = 0;
    hybrid_safety_signal_received = 0;

    if (install_hybrid_stop_handlers() == -1) {
        perror("sigaction");
        return -1;
    }

    if (snprintf(shared_name, sizeof(shared_name), "/scshm%d", (int) getpid()) >=
        (int) sizeof(shared_name)) {
        return -1;
    }

    if (snprintf(sync_prefix, sizeof(sync_prefix), "/scs%d", (int) getpid()) >=
        (int) sizeof(sync_prefix)) {
        return -1;
    }

    if (shared_simulation_create(&shared_region, shared_name, flights, flight_count) == -1) {
        return -1;
    }

    if (hybrid_sync_create(&sync, sync_prefix, flight_count) == -1) {
        shared_simulation_close(&shared_region);
        return -1;
    }

    init_hybrid_context(&context, shared_region.state, &sync, flight_count);

    if (init_parent_thread_state(&context.threads) == -1) {
        hybrid_sync_close(&sync);
        shared_simulation_close(&shared_region);
        return -1;
    }

    if (safety_report_notifier_init(&context.report_notifier) == -1) {
        destroy_parent_thread_state(&context.threads);
        hybrid_sync_close(&sync);
        shared_simulation_close(&shared_region);
        return -1;
    }

    printf("starting hybrid simulation with %d flights, %d steps and %ds timesteps\n",
           flight_count, SIMULATION_STEPS, TIME_STEP_SECONDS);
    fflush(stdout);

    if (start_hybrid_flight_processes(shared_region.state, &sync, flight_count) == -1) {
        request_hybrid_stop(&context);
        terminate_hybrid_children(shared_region.state, flight_count);
        wait_for_hybrid_children(shared_region.state, flight_count);
        safety_report_notifier_destroy(&context.report_notifier);
        destroy_parent_thread_state(&context.threads);
        hybrid_sync_close(&sync);
        shared_simulation_close(&shared_region);
        return -1;
    }

    if (pthread_create(&safety_thread, NULL, safety_thread_main, &context) != 0) {
        request_hybrid_stop(&context);
    } else {
        safety_started = 1;
    }

    if (!hybrid_stop_requested &&
        pthread_create(&report_thread, NULL, report_thread_main, &context) != 0) {
        request_hybrid_stop(&context);
    } else if (!hybrid_stop_requested) {
        report_started = 1;
    }

    if (!hybrid_stop_requested &&
        pthread_create(&scheduler_thread, NULL, scheduler_thread_main, &context) != 0) {
        request_hybrid_stop(&context);
    } else if (!hybrid_stop_requested) {
        scheduler_started = 1;
    }

    if (scheduler_started) {
        pthread_join(scheduler_thread, NULL);
    } else {
        finish_scheduler(&context);
    }

    if (hybrid_stop_requested) {
        fprintf(stderr, "hybrid simulation interrupted, terminating child processes\n");
        terminate_hybrid_children(shared_region.state, flight_count);
        hybrid_sync_release_all(&sync);
    }

    if (safety_started) {
        pthread_join(safety_thread, NULL);
    }

    if (!safety_started) {
        pthread_mutex_lock(&context.threads.mutex);
        context.threads.safety_finished = 1;
        pthread_mutex_unlock(&context.threads.mutex);
        safety_report_notifier_close(&context.report_notifier);
    }

    if (report_started) {
        pthread_join(report_thread, NULL);
    }

    wait_for_hybrid_children(shared_region.state, flight_count);
    print_position_history(shared_region.state->flights, flight_count, &context.position_history);

    safety_report_notifier_destroy(&context.report_notifier);
    destroy_parent_thread_state(&context.threads);
    hybrid_sync_close(&sync);
    shared_simulation_close(&shared_region);

    printf("hybrid simulation finished\n");
    return hybrid_stop_requested ? -1 : 0;
}
