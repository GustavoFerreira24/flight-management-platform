#include "flight_loader.h"
#include "hybrid_simulation.h"
#include "simulation.h"

#include <stdio.h>
#include <stdlib.h>

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

int main(int argc, char *argv[]) {
    flight_plan_t flights[MAX_FLIGHTS];
    int flight_count;

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

    return run_hybrid_simulation(flights, flight_count) == 0 ? EXIT_SUCCESS : EXIT_FAILURE;
}
