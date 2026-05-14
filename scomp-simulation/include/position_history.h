#ifndef POSITION_HISTORY_H
#define POSITION_HISTORY_H

#include "simulation.h"

typedef struct {
    position_update_t updates[MAX_FLIGHTS][SIMULATION_STEPS];
    int received[MAX_FLIGHTS][SIMULATION_STEPS];
} position_history_t;

void init_position_history(position_history_t *history);

int store_position_update(position_history_t *history,
                          position_update_t latest_positions[MAX_FLIGHTS],
                          int flight_count,
                          int flight_index,
                          const position_update_t *update);

void print_position_history(const flight_plan_t flights[],
                            int flight_count,
                            const position_history_t *history);

#endif
