#include "hybrid_simulation.h"

#include <assert.h>
#include <stdio.h>

static void test_hybrid_threads_complete_safe_simulation(void) {
    const flight_plan_t flights[] = {
            {101, 35.0000, -8.0000, 1000.0, 0.0100, 0.0000, 10.0},
            {202, 45.0000, -2.0000, 5000.0, 0.0000, 0.0100, 10.0}
    };

    assert(run_hybrid_simulation(flights, 2) == 0);
}

static void test_hybrid_rejects_invalid_arguments(void) {
    const flight_plan_t flights[] = {
            {101, 35.0000, -8.0000, 1000.0, 0.0100, 0.0000, 10.0}
    };

    assert(run_hybrid_simulation(NULL, 1) == -1);
    assert(run_hybrid_simulation(flights, 0) == -1);
    assert(run_hybrid_simulation(flights, MAX_FLIGHTS + 1) == -1);
}

int main(void) {
    test_hybrid_rejects_invalid_arguments();
    test_hybrid_threads_complete_safe_simulation();

    printf("PASS test_hybrid_simulation\n");
    return 0;
}
