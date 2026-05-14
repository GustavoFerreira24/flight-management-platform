#include "position_history.h"

#include <stdio.h>
#include <string.h>

void init_position_history(position_history_t *history) {
    memset(history, 0, sizeof(*history));
}

int store_position_update(position_history_t *history,
                          position_update_t latest_positions[MAX_FLIGHTS],
                          int flight_count,
                          int flight_index,
                          const position_update_t *update) {
    if (history == NULL || latest_positions == NULL || update == NULL) {
        return -1;
    }

    if (flight_index < 0 || flight_index >= flight_count || flight_index >= MAX_FLIGHTS) {
        return -1;
    }

    if (update->step < 0 || update->step >= SIMULATION_STEPS) {
        return -1;
    }

    history->updates[flight_index][update->step] = *update;
    history->received[flight_index][update->step] = 1;
    latest_positions[flight_index] = *update;

    return 0;
}

void print_position_history(const flight_plan_t flights[],
                            int flight_count,
                            const position_history_t *history) {
    if (flights == NULL || history == NULL) {
        return;
    }

    printf("\nposition history summary\n");

    for (int flight_index = 0; flight_index < flight_count && flight_index < MAX_FLIGHTS; flight_index++) {
        printf("Flight %d history:\n", flights[flight_index].flight_id);

        for (int step = 0; step < SIMULATION_STEPS; step++) {
            if (!history->received[flight_index][step]) {
                printf("  step %d -> no update received\n", step);
                continue;
            }

            const position_update_t *update = &history->updates[flight_index][step];

            printf("  step %d -> lat=%.4f, lon=%.4f, alt=%.1f\n",
                   step,
                   update->latitude,
                   update->longitude,
                   update->altitude);
        }
    }
}
