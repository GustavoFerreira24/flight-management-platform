#include "shared_simulation.h"

#include <assert.h>
#include <stdio.h>
#include <string.h>
#include <unistd.h>

static void build_shared_memory_name(char name[SHARED_SIMULATION_NAME_LENGTH]) {
    const int result = snprintf(name, SHARED_SIMULATION_NAME_LENGTH, "/tshm%d", (int) getpid());

    assert(result > 0);
    assert(result < SHARED_SIMULATION_NAME_LENGTH);
}

static void test_create_initializes_shared_state(void) {
    shared_simulation_region_t region;
    char name[SHARED_SIMULATION_NAME_LENGTH];
    const flight_plan_t flights[] = {
            {101, 41.0, -8.0, 1000.0, 0.1, 0.2, 10.0},
            {202, 42.0, -9.0, 2000.0, -0.1, -0.2, -10.0}
    };

    build_shared_memory_name(name);

    assert(shared_simulation_create(&region, name, flights, 2) == 0);
    assert(region.state != NULL);
    assert(region.descriptor >= 0);
    assert(region.owner == 1);
    assert(strcmp(region.name, name) == 0);

    assert(region.state->initialized == 1);
    assert(region.state->flight_count == 2);
    assert(region.state->current_step == -1);
    assert(region.state->stop_requested == 0);
    assert(region.state->flights[0].flight_id == 101);
    assert(region.state->flights[1].flight_id == 202);
    assert(region.state->received[0][0] == 0);

    shared_simulation_close(&region);
    assert(region.state == NULL);
    assert(region.descriptor == -1);
}

static void test_rejects_invalid_arguments(void) {
    shared_simulation_region_t region;
    char name[SHARED_SIMULATION_NAME_LENGTH];
    const flight_plan_t flights[] = {
            {101, 41.0, -8.0, 1000.0, 0.1, 0.2, 10.0}
    };

    build_shared_memory_name(name);

    assert(shared_simulation_create(NULL, name, flights, 1) == -1);
    assert(shared_simulation_create(&region, NULL, flights, 1) == -1);
    assert(shared_simulation_create(&region, name, NULL, 1) == -1);
    assert(shared_simulation_create(&region, name, flights, 0) == -1);
    assert(shared_simulation_create(&region, name, flights, MAX_FLIGHTS + 1) == -1);
}

int main(void) {
    test_create_initializes_shared_state();
    test_rejects_invalid_arguments();

    printf("PASS test_shared_simulation\n");
    return 0;
}
