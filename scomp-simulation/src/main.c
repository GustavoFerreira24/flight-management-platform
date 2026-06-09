#define _POSIX_C_SOURCE 200809L

#include "simulation.h"
#include "position_history.h"
#include "safety_monitor.h"
#include "flight_loader.h"
#include "report.h"

#include <errno.h>
#include <signal.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/wait.h>
#include <time.h>
#include <unistd.h>

static volatile sig_atomic_t stop_requested = 0;
static volatile sig_atomic_t safety_violation_received = 0;

static void handle_stop_signal(int signal_number) {
    (void) signal_number;
    stop_requested = 1;
}

static void handle_safety_violation_signal(int signal_number) {
    (void) signal_number;
    safety_violation_received = 1;
}

static int install_stop_handlers(void) {
    struct sigaction action;
    struct sigaction ignore_action;

    memset(&action, 0, sizeof(action));
    action.sa_handler = handle_stop_signal;
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

static int install_flight_signal_handlers(void) {
    struct sigaction action;

    if (install_stop_handlers() == -1) {
        return -1;
    }

    memset(&action, 0, sizeof(action));
    action.sa_handler = handle_safety_violation_signal;
    action.sa_flags = SA_RESTART;
    sigfillset(&action.sa_mask);

    if (sigaction(SIGUSR1, &action, NULL) == -1) {
        return -1;
    }

    return 0;
}

static void print_pending_safety_notification(int flight_id) {
    if (safety_violation_received) {
        printf("flight %d handled SIGUSR1: safety violation notification received\n", flight_id);
        fflush(stdout);
        safety_violation_received = 0;
    }
}

static int write_exact(int file_descriptor, const void *buffer, size_t size) {
    const char *cursor = buffer;
    size_t written = 0;

    while (written < size) {
        const ssize_t result = write(file_descriptor, cursor + written, size - written);

        if (result == -1) {
            if (errno == EINTR) {
                continue;
            }
            return -1;
        }

        written += (size_t) result;
    }

    return 0;
}

static int read_exact(int file_descriptor, void *buffer, size_t size) {
    char *cursor = buffer;
    size_t received = 0;

    while (received < size) {
        const ssize_t result = read(file_descriptor, cursor + received, size - received);

        if (result == 0) {
            return received == 0 ? 0 : -1;
        }

        if (result == -1) {
            if (errno == EINTR) {
                continue;
            }
            return -1;
        }

        received += (size_t) result;
    }

    return 1;
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

static void close_if_open(int file_descriptor) {
    if (file_descriptor >= 0) {
        close(file_descriptor);
    }
}

static void run_flight_process(const flight_plan_t *flight, int update_write_descriptor,
                               int control_read_descriptor) {
    if (install_flight_signal_handlers() == -1) {
        perror("sigaction");
        close_if_open(update_write_descriptor);
        close_if_open(control_read_descriptor);
        _exit(EXIT_FAILURE);
    }

    while (!stop_requested) {
        int step;
        const int control_result = read_exact(control_read_descriptor, &step, sizeof(step));

        if (control_result == 0) {
            break;
        }

        if (control_result == -1) {
            perror("read control");
            close_if_open(update_write_descriptor);
            close_if_open(control_read_descriptor);
            _exit(EXIT_FAILURE);
        }

        if (step < 0 || step >= SIMULATION_STEPS) {
            fprintf(stderr, "invalid timestep command %d for flight %d\n", step, flight->flight_id);
            close_if_open(update_write_descriptor);
            close_if_open(control_read_descriptor);
            _exit(EXIT_FAILURE);
        }

        print_pending_safety_notification(flight->flight_id);

        const position_update_t update = build_position_update(flight, step);

        if (write_exact(update_write_descriptor, &update, sizeof(update)) == -1) {
            perror("write update");
            close_if_open(update_write_descriptor);
            close_if_open(control_read_descriptor);
            _exit(EXIT_FAILURE);
        }

        print_pending_safety_notification(flight->flight_id);

        if (update.completed) {
            break;
        }
    }

    print_pending_safety_notification(flight->flight_id);

    close_if_open(update_write_descriptor);
    close_if_open(control_read_descriptor);
    _exit(EXIT_SUCCESS);
}

static int start_flight_processes(const flight_plan_t flights[], int flight_count,
                                  int update_pipes[][2], int control_pipes[][2],
                                  pid_t child_pids[]) {
    for (int i = 0; i < flight_count; i++) {
        if (pipe(update_pipes[i]) == -1) {
            perror("update pipe");
            return -1;
        }

        if (pipe(control_pipes[i]) == -1) {
            perror("control pipe");
            close_if_open(update_pipes[i][0]);
            close_if_open(update_pipes[i][1]);
            update_pipes[i][0] = -1;
            update_pipes[i][1] = -1;
            return -1;
        }

        const pid_t pid = fork();

        if (pid == -1) {
            perror("fork");
            close_if_open(update_pipes[i][0]);
            close_if_open(update_pipes[i][1]);
            close_if_open(control_pipes[i][0]);
            close_if_open(control_pipes[i][1]);
            update_pipes[i][0] = -1;
            update_pipes[i][1] = -1;
            control_pipes[i][0] = -1;
            control_pipes[i][1] = -1;
            return -1;
        }

        if (pid == 0) {
            for (int previous = 0; previous < i; previous++) {
                close_if_open(update_pipes[previous][0]);
                close_if_open(update_pipes[previous][1]);
                close_if_open(control_pipes[previous][0]);
                close_if_open(control_pipes[previous][1]);
            }

            close_if_open(update_pipes[i][0]);
            close_if_open(control_pipes[i][1]);
            run_flight_process(&flights[i], update_pipes[i][1], control_pipes[i][0]);
        }

        child_pids[i] = pid;
        close_if_open(update_pipes[i][1]);
        update_pipes[i][1] = -1;
        close_if_open(control_pipes[i][0]);
        control_pipes[i][0] = -1;
    }

    return 0;
}

static int load_default_flights(flight_plan_t flights[MAX_FLIGHTS]) {
    const flight_plan_t default_flights[] = {
            {101, 41.2629, -8.6852, 69.0, 0.1500, 0.1300, 600.0},
            {202, 41.8629, -8.1652, 2269.0, -0.1500, -0.1300, -500.0},
            {303, 38.7742, -9.1342, 114.0, -0.0600, 0.1200, 520.0}
    };
    const int flight_count = (int) (sizeof(default_flights) / sizeof(default_flights[0]));

    for (int i = 0; i < flight_count; i++) {
        flights[i] = default_flights[i];
    }

    return flight_count;
}

static void terminate_children(const pid_t child_pids[], int flight_count) {
    for (int i = 0; i < flight_count; i++) {
        if (child_pids[i] > 0) {
            kill(child_pids[i], SIGTERM);
        }
    }
}

static void close_parent_pipes(int update_pipes[][2], int control_pipes[][2], int flight_count) {
    for (int i = 0; i < flight_count; i++) {
        close_if_open(update_pipes[i][0]);
        update_pipes[i][0] = -1;
        close_if_open(control_pipes[i][1]);
        control_pipes[i][1] = -1;
    }
}

static void wait_for_children(const pid_t child_pids[], int flight_count) {
    for (int i = 0; i < flight_count; i++) {
        if (child_pids[i] > 0) {
            int status;
            if (waitpid(child_pids[i], &status, 0) == -1) {
                perror("waitpid");
            }
        }
    }
}

static void print_update(const position_update_t *update) {
    printf("step=%d flight=%d pid=%d pos=(%.4f, %.4f, %.1f) vel=(%.4f, %.4f, %.1f)%s\n",
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

static void notify_safety_violations(const safety_scan_result_t *scan_result,
                                     const pid_t child_pids[],
                                     int flight_count,
                                     int reported_violations[MAX_FLIGHTS][MAX_FLIGHTS][SIMULATION_STEPS],
                                     int *total_violations,
                                     simulation_report_t *report,
                                     const position_history_t *position_history) {
    for (int i = 0; i < scan_result->count && !stop_requested; i++) {
        const safety_violation_t *violation = &scan_result->violations[i];
        const int first = violation->first_flight_index;
        const int second = violation->second_flight_index;

        if (first < 0 || first >= flight_count || second < 0 || second >= flight_count) {
            continue;
        }

        if (violation->step < 0 || violation->step >= SIMULATION_STEPS) {
            continue;
        }

        if (reported_violations[first][second][violation->step]) {
            continue;
        }

        reported_violations[first][second][violation->step] = 1;

        log_safety_violation(stdout, violation);
        fflush(stdout);

        report_record_violation(report, violation, position_history, flight_count);

        kill(child_pids[first], SIGUSR1);
        kill(child_pids[second], SIGUSR1);

        (*total_violations)++;

        if (*total_violations >= SAFETY_MAX_TOTAL_VIOLATIONS) {
            fprintf(stderr,
                    "safety violation threshold reached (%d), requesting early termination\n",
                    *total_violations);
            stop_requested = 1;
        }
    }
}

static void detect_and_notify_step_violations(const position_history_t *position_history,
                                              int step,
                                              int flight_count,
                                              const pid_t child_pids[],
                                              int reported_violations[MAX_FLIGHTS][MAX_FLIGHTS][SIMULATION_STEPS],
                                              int *total_violations,
                                              simulation_report_t *report) {
    safety_scan_result_t scan_result;

    if (scan_safety_violations(position_history, flight_count, step, &scan_result) == -1) {
        fprintf(stderr, "failed to scan safety violations at step %d\n", step);
        stop_requested = 1;
        return;
    }

    notify_safety_violations(&scan_result,
                             child_pids,
                             flight_count,
                             reported_violations,
                             total_violations,
                             report,
                             position_history);
}

static int release_flights_for_step(int control_pipes[][2], int flight_count, int step) {
    for (int flight_index = 0; flight_index < flight_count && !stop_requested; flight_index++) {
        if (write_exact(control_pipes[flight_index][1], &step, sizeof(step)) == -1) {
            perror("write control");
            return -1;
        }
    }

    return stop_requested ? -1 : 0;
}

static void wait_for_next_timestep(void) {
    struct timespec delay;
    struct timespec remaining;

    delay.tv_sec = TIME_STEP_SECONDS;
    delay.tv_nsec = 0;

    while (!stop_requested && nanosleep(&delay, &remaining) == -1) {
        if (errno != EINTR) {
            return;
        }

        delay = remaining;
    }
}

static void process_simulation_updates(int update_pipes[][2], int control_pipes[][2],
                                        int flight_count,
                                        position_update_t latest_positions[],
                                        position_history_t *position_history,
                                        const pid_t child_pids[],
                                        int reported_violations[MAX_FLIGHTS][MAX_FLIGHTS][SIMULATION_STEPS],
                                        int *total_violations,
                                        simulation_report_t *report) {
    for (int step = 0; step < SIMULATION_STEPS && !stop_requested; step++) {
        const int simulation_time = step * TIME_STEP_SECONDS;

        printf("\n--- timestep %d (t=%ds) ---\n", step, simulation_time);
        fflush(stdout);

        if (release_flights_for_step(control_pipes, flight_count, step) == -1) {
            stop_requested = 1;
            break;
        }

        int received_updates = 0;

        for (int flight_index = 0; flight_index < flight_count && !stop_requested; flight_index++) {
            position_update_t update;
            const int result = read_exact(update_pipes[flight_index][0], &update, sizeof(update));

            if (result == 0) {
                fprintf(stderr, "flight pipe closed before step %d\n", step);
                stop_requested = 1;
                break;
            }

            if (result == -1) {
                perror("read");
                stop_requested = 1;
                break;
            }

            if (update.step != step) {
                fprintf(stderr,
                        "out-of-order update received from flight index %d: expected step %d, got %d\n",
                        flight_index,
                        step,
                        update.step);
                stop_requested = 1;
                break;
            }

            if (store_position_update(position_history, latest_positions, flight_count,
                                      flight_index, &update) == -1) {
                fprintf(stderr, "invalid update received from flight index %d at step %d\n",
                        flight_index, update.step);
                stop_requested = 1;
                break;
            }

            print_update(&update);
            received_updates++;
        }

        if (!stop_requested && received_updates == flight_count) {
            printf("timestep %d barrier complete: %d/%d updates processed\n",
                   step, received_updates, flight_count);
            fflush(stdout);

            detect_and_notify_step_violations(position_history,
                                               step,
                                               flight_count,
                                               child_pids,
                                               reported_violations,
                                               total_violations,
                                               report);
        }

        if (!stop_requested && step < SIMULATION_STEPS - 1) {
            wait_for_next_timestep();
        }
    }
}

int main(int argc, char *argv[]) {
    flight_plan_t flights[MAX_FLIGHTS];
    int flight_count;
    int update_pipes[MAX_FLIGHTS][2];
    int control_pipes[MAX_FLIGHTS][2];
    pid_t child_pids[MAX_FLIGHTS] = {0};
    position_update_t latest_positions[MAX_FLIGHTS];
    position_history_t position_history;
    int reported_violations[MAX_FLIGHTS][MAX_FLIGHTS][SIMULATION_STEPS];
    int total_violations = 0;
    simulation_report_t report;

    if (argc > 2) {
        fprintf(stderr, "usage: %s [flight_csv]\n", argv[0]);
        return EXIT_FAILURE;
    }

    if (argc == 2) {
        flight_count = load_flight_plans(argv[1], flights, MAX_FLIGHTS);
        if (flight_count <= 0) {
            fprintf(stderr, "failed to load flight data from %s\n", argv[1]);
            return EXIT_FAILURE;
        }
    } else {
        flight_count = load_default_flights(flights);
    }

    memset(update_pipes, -1, sizeof(update_pipes));
    memset(control_pipes, -1, sizeof(control_pipes));
    memset(latest_positions, 0, sizeof(latest_positions));
    memset(reported_violations, 0, sizeof(reported_violations));
    init_position_history(&position_history);
    init_simulation_report(&report);

    if (install_stop_handlers() == -1) {
        perror("sigaction");
        return EXIT_FAILURE;
    }

    printf("starting simulation with %d flights, %d steps and %ds timesteps\n",
           flight_count, SIMULATION_STEPS, TIME_STEP_SECONDS);
    fflush(stdout);

    if (start_flight_processes(flights, flight_count, update_pipes, control_pipes, child_pids) == -1) {
        terminate_children(child_pids, flight_count);
        close_parent_pipes(update_pipes, control_pipes, flight_count);
        wait_for_children(child_pids, flight_count);
        return EXIT_FAILURE;
    }

    process_simulation_updates(update_pipes,
                               control_pipes,
                               flight_count,
                               latest_positions,
                               &position_history,
                               child_pids,
                               reported_violations,
                               &total_violations,
                               &report);

    if (stop_requested) {
        fprintf(stderr, "simulation interrupted, terminating child processes\n");
        terminate_children(child_pids, flight_count);
    }

    close_parent_pipes(update_pipes, control_pipes, flight_count);
    wait_for_children(child_pids, flight_count);

    print_position_history(flights, flight_count, &position_history);

    report_collect_flight_results(&report, flights, flight_count, &position_history, stop_requested);

    if (report_save_to_file(&report, REPORT_FILE_PATH) == 0) {
        printf("simulation report saved to %s\n", REPORT_FILE_PATH);
    } else {
        fprintf(stderr, "failed to save simulation report to %s\n", REPORT_FILE_PATH);
    }

    printf("simulation finished\n");
    return stop_requested ? EXIT_FAILURE : EXIT_SUCCESS;
}
