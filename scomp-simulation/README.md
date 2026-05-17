# SCOMP Flight Simulation System

This module implements the SCOMP flight simulation component for Sprint 2.

## Component Diagram

```text
                               +-------------------------+
                               |      Main Process       |
                               |      simulator          |
                               +-----------+-------------+
                                           |
                 +-------------------------+-------------------------+
                 |                         |                         |
        control pipe 0            control pipe 1            control pipe N
        parent -> child           parent -> child           parent -> child
                 |                         |                         |
        +--------v--------+       +--------v--------+       +--------v--------+
        | Flight Process  |       | Flight Process  |       | Flight Process  |
        | Flight 0        |       | Flight 1        |       | Flight N        |
        +--------+--------+       +--------+--------+       +--------+--------+
                 |                         |                         |
        update pipe 0             update pipe 1             update pipe N
        child -> parent           child -> parent           child -> parent
                 |                         |                         |
                 +-------------------------+-------------------------+
                                           |
                               +-----------v-------------+
                               |      Main Process       |
                               | receives position_update|
                               +-----------+-------------+
                                           |
             +-----------------------------+-----------------------------+
             |                             |                             |
 +-----------v-----------+     +-----------v-----------+     +-----------v-----------+
 | Position History      |     | Safety Monitor        |     | Report Generator      |
 | position_history.c    |     | safety_monitor.c      |     | report.c              |
 | stores past positions |     | detects violations    |     | writes final report   |
 +-----------------------+     +-----------+-----------+     +-----------+-----------+
                                           |                             |
                                           | SIGUSR1                     |
                                           v                             v
                                  +----------------+          +----------------------+
                                  | Flight Process |          | simulation_report.txt|
                                  | notified       |          +----------------------+
                                  +----------------+

Input files:
  data/flights_from_simulador_v1.csv
  data/large_normal_flights.csv
  data/large_violation_flights.csv

Build/test files:
  Makefile
  tests/test_position_history.c
  tests/test_safety_monitor.c
  tests/test_flight_loader.c
  tests/test_report.c
```

## Implemented User Stories

### US100 - Simulate Flights In A Given Area

The simulator creates one child process for each flight using `fork()`.

For each flight, the parent process creates two pipes:

- a control pipe, used by the parent to send the current timestep to the flight process;
- an update pipe, used by the flight process to send its `position_update_t` to the parent.

Each child process executes its assigned flight plan step by step. The parent process controls the simulation, receives updates, handles termination and waits for all child processes with `waitpid()`.

Signals are used for interruption and notification:

- `SIGINT` and `SIGTERM` request simulation stop;
- `SIGUSR1` notifies a flight process about a safety violation;
- `SIGPIPE` is ignored to avoid unexpected termination when writing to closed pipes.

Status: implemented successfully.

### US101 - Capture And Process Flight Movements

Each flight process sends a `position_update_t` structure to the parent process through its update pipe.

The parent validates and stores each received update in:

```c
position_history_t
```

The position history stores positions by flight and timestep:

```c
updates[flight_index][step]
```

The module also keeps a `received` matrix to know which positions were actually received. This avoids treating zero-initialized data as valid flight data.

Status: implemented successfully.

### US102 - Detect Aircraft Safety Violations In Real Time

After all updates for a timestep are received, the parent process scans the stored positions using `safety_monitor.c`.

The monitor compares aircraft pairs and checks:

- horizontal distance;
- vertical distance;
- predicted next-step position.

When a violation or predicted violation is detected:

- the event is logged to the terminal;
- the report module records the event;
- the involved flight processes are notified with `SIGUSR1`;
- the flight processes handle the signal and print a user-visible message.

If the number of violations reaches the configured threshold, the simulation requests early termination.

Status: implemented successfully.

### US103 - Synchronize Flight Execution With A Time Step

The simulation progresses timestep by timestep.

For each timestep:

1. the parent sends the timestep number to every child through the control pipes;
2. each child calculates and sends its position update;
3. the parent waits until it receives all updates for that timestep;
4. only then the parent scans for violations and advances to the next timestep.

This creates a barrier implemented with pipes. The parent only advances when:

```text
received_updates == flight_count
```

Example output:

```text
timestep 0 barrier complete: 40/40 updates processed
```

Status: implemented successfully.

### US109 - Generate And Store Final Simulation Report

At the end of the simulation, the parent process generates `simulation_report.txt`.

The report includes:

- total number of flights;
- individual flight execution status;
- safety violation events;
- simulation timestep and timestamp in seconds;
- aircraft positions at detection time;
- aircraft velocity vectors at detection time;
- final validation result, `PASSED` or `FAILED`.

The report is generated by the parent process after the simulation concludes. Threads were not used because the Sprint 2 SCOMP evaluation focused on processes, pipes, signals, timestep synchronization and final report generation.

Status: implemented successfully.

## What Was Not Implemented

The following mechanisms were not implemented because they are associated with later or non-required user stories for this Sprint 2 SCOMP delivery:

- threads with `pthread_create`;
- mutexes;
- condition variables;
- semaphores;
- shared memory;
- UDP remote logging;
- HTTP/AJAX visualization;
- environmental/weather thread.

The timestep synchronization was implemented with pipes, not semaphores.

## Build And Run

Build the simulator:

```sh
make clean
make
```

Run the default simulation:

```sh
make run
```

Run a large normal scenario:

```sh
make run-large-normal
```

Run a large scenario with violations:

```sh
make run-large-violations
```

Run all tests:

```sh
make test
```

Expected test result:

```text
PASS test_position_history
PASS test_safety_monitor
PASS test_flight_loader
PASS test_report
```

## Validation Evidence

The solution was validated with:

- default simulation with 3 flights;
- large normal simulation with 40 flights;
- large violation simulation with 40 flights;
- unit tests for position history;
- unit tests for safety monitoring;
- unit tests for CSV flight loading;
- unit tests for final report generation.

The large simulations demonstrate that the parent process receives all updates before advancing:

```text
timestep 0 barrier complete: 40/40 updates processed
timestep 1 barrier complete: 40/40 updates processed
timestep 2 barrier complete: 40/40 updates processed
timestep 3 barrier complete: 40/40 updates processed
timestep 4 barrier complete: 40/40 updates processed
```

## Group Self-Assessment

Replace the placeholder names and percentages with the real group members before delivery.

| Student | Contribution (%) | Main Contribution |
| --- | ---: | --- |
| Student 1 | 25 | US100/US103 process and pipe coordination |
| Student 2 | 25 | US101 position history and tests |
| Student 3 | 25 | US102 safety monitor and violation signalling |
| Student 4 | 25 | US109 report generation, loader and validation |

Total: 100%.
