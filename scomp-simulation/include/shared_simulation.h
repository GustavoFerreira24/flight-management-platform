#ifndef SHARED_SIMULATION_H
#define SHARED_SIMULATION_H

#include "simulation.h"

#define SHARED_SIMULATION_NAME_LENGTH 32

typedef struct {
    int initialized;
    int flight_count;
    int current_step;
    int stop_requested;
    flight_plan_t flights[MAX_FLIGHTS];
    pid_t flight_pids[MAX_FLIGHTS];
    position_update_t updates[MAX_FLIGHTS][SIMULATION_STEPS];
    int received[MAX_FLIGHTS][SIMULATION_STEPS];
} shared_simulation_t;

typedef struct {
    char name[SHARED_SIMULATION_NAME_LENGTH];
    int descriptor;
    int owner;
    shared_simulation_t *state;
} shared_simulation_region_t;

int shared_simulation_create(shared_simulation_region_t *region,
                             const char *name,
                             const flight_plan_t flights[],
                             int flight_count);

void shared_simulation_close(shared_simulation_region_t *region);

#endif
