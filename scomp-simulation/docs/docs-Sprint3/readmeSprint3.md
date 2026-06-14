# SCOMP Flight Simulation - Sprint 3

This module implements the SCOMP Sprint 3 flight simulation requirements. The main Sprint 3 executable is the hybrid simulator, available with:

```sh
make run-hybrid
```

The project also keeps the previous pipe-based simulator (`make run`) as a legacy/base version, but the Sprint 3 implementation is focused on shared memory, semaphores, parent threads, condition variables and final report generation.

## Main Code Structure

| Path                           | Purpose                                                                                          |
| ------------------------------ | ------------------------------------------------------------------------------------------------ |
| `src/hybrid_main.c`            | Entry point of the Sprint 3 hybrid simulator.                                                    |
| `src/hybrid_simulation.c`      | Main orchestration of the hybrid simulation: processes, threads, synchronization and shutdown.   |
| `src/shared_simulation.c`      | Creates, maps and removes the POSIX shared memory area.                                          |
| `src/hybrid_sync.c`            | Creates and manages the named POSIX semaphores used for timestep synchronization.                |
| `src/safety_report_notifier.c` | Thread-safe notification queue between the safety thread and the report thread.                  |
| `src/safety_monitor.c`         | Detects current and predicted aircraft safety violations.                                        |
| `src/position_history.c`       | Stores position updates by flight and timestep.                                                  |
| `src/report.c`                 | Builds and saves the final `simulation_report.txt` file.                                         |
| `src/flight_loader.c`          | Loads flight data from CSV files.                                                                |
| `tests/`                       | Tests for the main modules, including shared memory, semaphores, notifier and report generation. |

## Sprint 3 Implementation

### US105 - Hybrid Simulation Environment With Shared Memory

The hybrid simulator creates a POSIX shared memory area using `shm_open`, `ftruncate` and `mmap`. This shared region stores the simulation state used by the parent process and the flight child processes.

Each child process writes its position update directly into shared memory instead of sending it through pipes. The parent reads those updates from the same shared region.

### US106 - Function-Specific Parent Threads

The parent process creates three main threads:

* scheduler thread;
* safety detection thread;
* report generation thread.

The scheduler controls the timesteps and releases the flight processes. The safety thread checks completed timesteps for violations. The report thread stores safety events and later writes the final report.

### US107 - Report Thread Notification With Condition Variables

Safety violations are passed from the safety thread to the report thread through `safety_report_notifier.c`.

This module uses a mutex and a condition variable. The report thread waits while there are no events. When the safety thread detects a violation, it stores the event and signals the condition variable, waking the report thread.

### US108 - Step-By-Step Synchronization

The hybrid simulator uses semaphores to synchronize each timestep.

For each step, the scheduler releases the child processes. Each child computes one update, writes it to shared memory and signals that the update is ready. The parent only advances after receiving all expected updates for that timestep.

This guarantees that safety detection and reporting always work with complete timestep data.

### US109 - Final Simulation Report

At the end of the simulation, the system generates `simulation_report.txt`.

The report includes:

* number of simulated flights;
* execution status of each flight;
* detected safety violations;
* timestep and timestamp of each violation;
* aircraft positions and velocity vectors;
* final result: `PASSED` or `FAILED`.

## Not Implemented / Not Applicable

US110 was not implemented because it only applies to groups with five or more students in SCOMP.

The current simulator also does not implement:

* real weather/environmental influence;
* UDP remote logging;
* HTTP/AJAX visualization;
* integration with the Java application or database;
* real aircraft physics or fuel consumption.

## Build, Run And Test

Build the project:

```sh
make clean
make
```

Run the Sprint 3 hybrid simulator:

```sh
make run-hybrid
```

Run larger hybrid scenarios:

```sh
make run-hybrid-large-normal
make run-hybrid-large-violations
```

Run all tests:

```sh
make test
```

The test suite validates the main modules used in Sprint 3: shared memory setup, semaphore synchronization, safety detection, report notification, report generation and hybrid simulation execution.


## Group Contribution Self-Assessment

| Student          | Contribution |
| ---------------- | -----------: |
| Bruno Monteiro   |          25% |
| Francisco Vieira |          25% |
| Gustavo Ferreira |          25% |
| Pedro Rodrigues  |          25% |
