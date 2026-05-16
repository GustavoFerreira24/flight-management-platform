#include "safety_monitor.h"

#include <assert.h>
#include <stdio.h>
#include <string.h>

static position_update_t make_update(int flight_id, int step, double latitude,
                                     double longitude, double altitude) {
    position_update_t update;

    memset(&update, 0, sizeof(update));
    update.flight_id = flight_id;
    update.step = step;
    update.latitude = latitude;
    update.longitude = longitude;
    update.altitude = altitude;

    return update;
}

static void test_detects_current_violation(void) {
    const position_update_t first = make_update(101, 0, 41.0000, -8.0000, 1000.0);
    const position_update_t second = make_update(202, 0, 41.0100, -8.0100, 1100.0);
    safety_violation_t violation;

    assert(detect_pair_safety_violation(&first, &second, 0, &violation) == 1);
    assert(violation.predicted == 0);
    assert(violation.step == 0);
    assert(violation.first_flight_id == 101);
    assert(violation.second_flight_id == 202);
}

static void test_rejects_safe_pair(void) {
    const position_update_t first = make_update(101, 0, 41.0000, -8.0000, 1000.0);
    const position_update_t second = make_update(202, 0, 42.0000, -9.0000, 5000.0);
    safety_violation_t violation;

    assert(detect_pair_safety_violation(&first, &second, 0, &violation) == 0);
}

static void test_detects_predicted_violation(void) {
    position_update_t first = make_update(101, 1, 41.4129, -8.5552, 669.0);
    position_update_t second = make_update(202, 1, 41.7129, -8.2952, 1769.0);
    safety_violation_t violation;

    first.velocity_latitude = 0.1500;
    first.velocity_longitude = 0.1300;
    first.velocity_altitude = 600.0;
    second.velocity_latitude = -0.1500;
    second.velocity_longitude = -0.1300;
    second.velocity_altitude = -500.0;

    assert(detect_pair_safety_violation(&first, &second, 1, &violation) == 1);
    assert(violation.predicted == 1);
    assert(violation.step == 2);
}

static void test_scan_uses_received_positions_only(void) {
    position_history_t history;
    safety_scan_result_t result;
    position_update_t latest_positions[MAX_FLIGHTS];
    const position_update_t first = make_update(101, 0, 41.0000, -8.0000, 1000.0);
    const position_update_t second = make_update(202, 0, 41.0100, -8.0100, 1100.0);

    memset(latest_positions, 0, sizeof(latest_positions));
    init_position_history(&history);
    assert(store_position_update(&history, latest_positions, 2, 0, &first) == 0);
    assert(scan_safety_violations(&history, 2, 0, &result) == 0);
    assert(result.count == 0);

    assert(store_position_update(&history, latest_positions, 2, 1, &second) == 0);
    assert(scan_safety_violations(&history, 2, 0, &result) == 0);
    assert(result.count == 1);
}

static void test_scan_reports_flight_indexes_and_ids(void) {
    position_history_t history;
    safety_scan_result_t result;
    position_update_t latest_positions[MAX_FLIGHTS];
    const position_update_t first = make_update(101, 0, 41.0000, -8.0000, 1000.0);
    const position_update_t second = make_update(202, 0, 41.0100, -8.0100, 1100.0);

    memset(latest_positions, 0, sizeof(latest_positions));
    init_position_history(&history);

    assert(store_position_update(&history, latest_positions, 2, 0, &first) == 0);
    assert(store_position_update(&history, latest_positions, 2, 1, &second) == 0);
    assert(scan_safety_violations(&history, 2, 0, &result) == 0);

    assert(result.count == 1);
    assert(result.violations[0].first_flight_index == 0);
    assert(result.violations[0].second_flight_index == 1);
    assert(result.violations[0].first_flight_id == 101);
    assert(result.violations[0].second_flight_id == 202);
}

static void test_scan_detects_predicted_violation_from_history(void) {
    position_history_t history;
    safety_scan_result_t result;
    position_update_t latest_positions[MAX_FLIGHTS];
    position_update_t first = make_update(101, 1, 41.4129, -8.5552, 669.0);
    position_update_t second = make_update(202, 1, 41.7129, -8.2952, 1769.0);

    first.velocity_latitude = 0.1500;
    first.velocity_longitude = 0.1300;
    first.velocity_altitude = 600.0;
    second.velocity_latitude = -0.1500;
    second.velocity_longitude = -0.1300;
    second.velocity_altitude = -500.0;

    memset(latest_positions, 0, sizeof(latest_positions));
    init_position_history(&history);

    assert(store_position_update(&history, latest_positions, 2, 0, &first) == 0);
    assert(store_position_update(&history, latest_positions, 2, 1, &second) == 0);
    assert(scan_safety_violations(&history, 2, 1, &result) == 0);

    assert(result.count == 1);
    assert(result.violations[0].predicted == 1);
    assert(result.violations[0].step == 2);
    assert(result.violations[0].first_flight_id == 101);
    assert(result.violations[0].second_flight_id == 202);
}

static void test_scan_rejects_invalid_arguments(void) {
    position_history_t history;
    safety_scan_result_t result;

    init_position_history(&history);

    assert(scan_safety_violations(NULL, 2, 0, &result) == -1);
    assert(scan_safety_violations(&history, 2, 0, NULL) == -1);
    assert(scan_safety_violations(&history, -1, 0, &result) == -1);
    assert(scan_safety_violations(&history, MAX_FLIGHTS + 1, 0, &result) == -1);
    assert(scan_safety_violations(&history, 2, -1, &result) == -1);
    assert(scan_safety_violations(&history, 2, SIMULATION_STEPS, &result) == -1);
}

int main(void) {
    test_detects_current_violation();
    test_rejects_safe_pair();
    test_detects_predicted_violation();
    test_scan_uses_received_positions_only();
    test_scan_reports_flight_indexes_and_ids();
    test_scan_detects_predicted_violation_from_history();
    test_scan_rejects_invalid_arguments();

    printf("PASS test_safety_monitor\n");

    return 0;
}
