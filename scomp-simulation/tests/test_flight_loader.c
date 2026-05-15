#include "flight_loader.h"

#include <assert.h>
#include <stdio.h>

static void test_loads_csv_flight_plans(void) {
    flight_plan_t flights[MAX_FLIGHTS];
    const int count = load_flight_plans("data/flights_from_simulador_v1.csv", flights, MAX_FLIGHTS);

    assert(count == 3);
    assert(flights[0].flight_id == 101);
    assert(flights[0].initial_latitude > 41.0);
    assert(flights[0].initial_longitude < -8.0);
}

static void test_rejects_missing_file(void) {
    flight_plan_t flights[MAX_FLIGHTS];

    assert(load_flight_plans("data/does_not_exist.csv", flights, MAX_FLIGHTS) == -1);
}

static void test_loads_large_dataset(void) {
    flight_plan_t flights[MAX_FLIGHTS];
    const int count = load_flight_plans("data/large_normal_flights.csv", flights, MAX_FLIGHTS);

    assert(count == 40);
    assert(flights[0].flight_id == 1000);
    assert(flights[39].flight_id == 1039);
}

int main(void) {
    test_loads_csv_flight_plans();
    test_rejects_missing_file();
    test_loads_large_dataset();

    printf("PASS test_flight_loader\n");
    return 0;
}
