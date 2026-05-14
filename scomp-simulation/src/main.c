#define _POSIX_C_SOURCE 200809L

#include "simulation.h"
#include "position_history.h"

#include <errno.h>
#include <signal.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/wait.h>
#include <unistd.h>

static volatile sig_atomic_t stop_requested = 0;

static void handle_stop_signal(int signal_number) {
    (void) signal_number;
    stop_requested = 1;
}

static int install_stop_handlers(void) {
    struct sigaction action;

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

    return 0;
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

static void run_flight_process(const flight_plan_t *flight, int write_descriptor) {
    if (install_stop_handlers() == -1) {
        perror("sigaction");
        close(write_descriptor);
        _exit(EXIT_FAILURE);
    }

    for (int step = 0; step < SIMULATION_STEPS && !stop_requested; step++) {
        const position_update_t update = build_position_update(flight, step);

        if (write_exact(write_descriptor, &update, sizeof(update)) == -1) {
            perror("write");
            close(write_descriptor);
            _exit(EXIT_FAILURE);
        }
    }

    close(write_descriptor);
    _exit(EXIT_SUCCESS);
}

static int start_flight_processes(const flight_plan_t flights[], int flight_count,
                                  int pipes[][2], pid_t child_pids[]) {
    for (int i = 0; i < flight_count; i++) {
        if (pipe(pipes[i]) == -1) {
            perror("pipe");
            return -1;
        }

        const pid_t pid = fork();

        if (pid == -1) {
            perror("fork");
            close(pipes[i][0]);
            close(pipes[i][1]);
            return -1;
        }

        if (pid == 0) {
            for (int previous = 0; previous < i; previous++) {
                close(pipes[previous][0]);
                close(pipes[previous][1]);
            }

            close(pipes[i][0]);
            run_flight_process(&flights[i], pipes[i][1]);
        }

        child_pids[i] = pid;
        close(pipes[i][1]);
    }

    return 0;
}

static void terminate_children(const pid_t child_pids[], int flight_count) {
    for (int i = 0; i < flight_count; i++) {
        if (child_pids[i] > 0) {
            kill(child_pids[i], SIGTERM);
        }
    }
}

static void close_parent_pipes(int pipes[][2], int flight_count) {
    for (int i = 0; i < flight_count; i++) {
        close(pipes[i][0]);
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
}

static void process_simulation_updates(int pipes[][2], int flight_count,
                                       position_update_t latest_positions[],
                                       position_history_t *position_history) {
    for (int step = 0; step < SIMULATION_STEPS && !stop_requested; step++) {
        for (int flight_index = 0; flight_index < flight_count && !stop_requested; flight_index++) {
            position_update_t update;
            const int result = read_exact(pipes[flight_index][0], &update, sizeof(update));

            if (result == 0) {
                fprintf(stderr, "flight pipe closed before step %d\n", step);
                continue;
            }

            if (result == -1) {
                perror("read");
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
        }
    }
}

int main(void) {
    const flight_plan_t flights[] = {
            {101, 41.2629, -8.6852, 69.0, 0.1500, 0.1300, 600.0},
            {202, 40.4895, -3.5643, 610.0, 0.0800, -0.0900, 450.0},
            {303, 38.7742, -9.1342, 114.0, -0.0600, 0.1200, 520.0}
    };
    const int flight_count = (int) (sizeof(flights) / sizeof(flights[0]));
    int pipes[MAX_FLIGHTS][2];
    pid_t child_pids[MAX_FLIGHTS] = {0};
    position_update_t latest_positions[MAX_FLIGHTS];
    position_history_t position_history;

    if (flight_count > MAX_FLIGHTS) {
        fprintf(stderr, "too many flights configured\n");
        return EXIT_FAILURE;
    }

    memset(pipes, -1, sizeof(pipes));
    memset(latest_positions, 0, sizeof(latest_positions));
    init_position_history(&position_history);

    if (install_stop_handlers() == -1) {
        perror("sigaction");
        return EXIT_FAILURE;
    }

    printf("starting simulation with %d flights and %d steps\n", flight_count, SIMULATION_STEPS);

    if (start_flight_processes(flights, flight_count, pipes, child_pids) == -1) {
        terminate_children(child_pids, flight_count);
        close_parent_pipes(pipes, flight_count);
        wait_for_children(child_pids, flight_count);
        return EXIT_FAILURE;
    }

    process_simulation_updates(pipes, flight_count, latest_positions, &position_history);

    if (stop_requested) {
        fprintf(stderr, "simulation interrupted, terminating child processes\n");
        terminate_children(child_pids, flight_count);
    }

    close_parent_pipes(pipes, flight_count);
    wait_for_children(child_pids, flight_count);

    print_position_history(flights, flight_count, &position_history);

    printf("simulation finished\n");
    return stop_requested ? EXIT_FAILURE : EXIT_SUCCESS;
}
