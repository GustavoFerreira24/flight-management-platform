#include "position_history.h"

#include <assert.h>
#include <stdio.h>
#include <string.h>

static position_update_t make_update(int flight_id, int step) {
    position_update_t update;

    memset(&update, 0, sizeof(update));
    update.flight_id = flight_id;
    update.step = step;
    update.latitude = 41.0 + step;
    update.longitude = -8.0 - step;
    update.altitude = 1000.0 + step;

    return update;
}

static void test_init_position_history_clears_received_updates(void) {
    position_history_t history;

    init_position_history(&history);

    for (int flight_index = 0; flight_index < MAX_FLIGHTS; flight_index++) {
        for (int step = 0; step < SIMULATION_STEPS; step++) {
            assert(history.received[flight_index][step] == 0);
        }
    }
}

static void test_store_position_update_accepts_valid_update(void) {
    position_history_t history;
    position_update_t latest_positions[MAX_FLIGHTS];
    const position_update_t update = make_update(101, 2);

    memset(latest_positions, 0, sizeof(latest_positions));
    init_position_history(&history);

    assert(store_position_update(&history, latest_positions, 3, 0, &update) == 0);
    assert(history.received[0][2] == 1);
    assert(history.updates[0][2].flight_id == 101);
    assert(history.updates[0][2].step == 2);
    assert(history.updates[0][2].latitude == update.latitude);
    assert(history.updates[0][2].longitude == update.longitude);
    assert(history.updates[0][2].altitude == update.altitude);
    assert(latest_positions[0].flight_id == 101);
    assert(latest_positions[0].step == 2);
}

static void test_store_position_update_rejects_invalid_step(void) {
    position_history_t history;
    position_update_t latest_positions[MAX_FLIGHTS];
    const position_update_t update = make_update(101, SIMULATION_STEPS);

    memset(latest_positions, 0, sizeof(latest_positions));
    init_position_history(&history);

    assert(store_position_update(&history, latest_positions, 3, 0, &update) == -1);
    assert(latest_positions[0].flight_id == 0);
}

static void test_store_position_update_rejects_invalid_flight_index(void) {
    position_history_t history;
    position_update_t latest_positions[MAX_FLIGHTS];
    const position_update_t update = make_update(101, 0);

    memset(latest_positions, 0, sizeof(latest_positions));
    init_position_history(&history);

    assert(store_position_update(&history, latest_positions, 3, 3, &update) == -1);
    assert(latest_positions[0].flight_id == 0);
}

static void test_store_position_update_rejects_null_arguments(void) {
    position_history_t history;
    position_update_t latest_positions[MAX_FLIGHTS];
    const position_update_t update = make_update(101, 0);

    memset(latest_positions, 0, sizeof(latest_positions));
    init_position_history(&history);

    assert(store_position_update(NULL, latest_positions, 3, 0, &update) == -1);
    assert(store_position_update(&history, NULL, 3, 0, &update) == -1);
    assert(store_position_update(&history, latest_positions, 3, 0, NULL) == -1);
}

int main(void) {
    test_init_position_history_clears_received_updates();
    test_store_position_update_accepts_valid_update();
    test_store_position_update_rejects_invalid_step();
    test_store_position_update_rejects_invalid_flight_index();
    test_store_position_update_rejects_null_arguments();

    printf("PASS test_position_history\n");

    return 0;
}
