#include "report.h"

#include <assert.h>
#include <stdio.h>
#include <string.h>

static flight_plan_t make_flight(int flight_id, int index) {
    flight_plan_t flight;

    memset(&flight, 0, sizeof(flight));
    flight.flight_id = flight_id;
    flight.initial_latitude = 41.0 + index;
    flight.initial_longitude = -8.0 - index;
    flight.initial_altitude = 1000.0;
    flight.velocity_latitude = 0.01;
    flight.velocity_longitude = 0.01;
    flight.velocity_altitude = 10.0;

    return flight;
}

static position_update_t make_update(int flight_id, int step, int completed) {
    position_update_t update;

    memset(&update, 0, sizeof(update));
    update.flight_id = flight_id;
    update.step = step;
    update.latitude = 41.0 + step;
    update.longitude = -8.0 - step;
    update.altitude = 1000.0 + step;
    update.completed = completed;

    return update;
}

static safety_violation_t make_violation(int first_id, int second_id, int step, int predicted) {
    safety_violation_t violation;

    memset(&violation, 0, sizeof(violation));
    violation.first_flight_id = first_id;
    violation.second_flight_id = second_id;
    violation.step = step;
    violation.predicted = predicted;
    violation.horizontal_distance_meters = 5000.0;
    violation.vertical_distance_meters = 150.0;

    return violation;
}

static void test_init_clears_report(void) {
    simulation_report_t report;

    memset(&report, 0xFF, sizeof(report));
    init_simulation_report(&report);

    assert(report.flight_count == 0);
    assert(report.violation_count == 0);
    assert(report.simulation_interrupted == 0);
}

static void test_init_rejects_null(void) {
    init_simulation_report(NULL);
}

static void test_collect_marks_completed_flights(void) {
    simulation_report_t report;
    flight_plan_t flights[2];
    position_history_t history;
    position_update_t latest[MAX_FLIGHTS];

    memset(latest, 0, sizeof(latest));
    init_simulation_report(&report);
    init_position_history(&history);

    flights[0] = make_flight(101, 0);
    flights[1] = make_flight(202, 1);

    for (int step = 0; step < SIMULATION_STEPS; step++) {
        position_update_t u0 = make_update(101, step, step == SIMULATION_STEPS - 1);
        position_update_t u1 = make_update(202, step, step == SIMULATION_STEPS - 1);

        store_position_update(&history, latest, 2, 0, &u0);
        store_position_update(&history, latest, 2, 1, &u1);
    }

    report_collect_flight_results(&report, flights, 2, &history, 0);

    assert(report.flight_count == 2);
    assert(report.flight_results[0].flight_id == 101);
    assert(report.flight_results[0].status == FLIGHT_STATUS_COMPLETED);
    assert(report.flight_results[1].flight_id == 202);
    assert(report.flight_results[1].status == FLIGHT_STATUS_COMPLETED);
}

static void test_collect_marks_terminated_flights(void) {
    simulation_report_t report;
    flight_plan_t flights[1];
    position_history_t history;
    position_update_t latest[MAX_FLIGHTS];

    memset(latest, 0, sizeof(latest));
    init_simulation_report(&report);
    init_position_history(&history);

    flights[0] = make_flight(101, 0);

    position_update_t u = make_update(101, 0, 0);
    store_position_update(&history, latest, 1, 0, &u);

    report_collect_flight_results(&report, flights, 1, &history, 1);

    assert(report.flight_count == 1);
    assert(report.flight_results[0].status == FLIGHT_STATUS_TERMINATED);
    assert(report.simulation_interrupted == 1);
}

static void test_collect_rejects_null_arguments(void) {
    simulation_report_t report;
    flight_plan_t flights[1];
    position_history_t history;

    init_simulation_report(&report);
    init_position_history(&history);
    flights[0] = make_flight(101, 0);

    report_collect_flight_results(NULL, flights, 1, &history, 0);
    report_collect_flight_results(&report, NULL, 1, &history, 0);
    report_collect_flight_results(&report, flights, 1, NULL, 0);

    assert(report.flight_count == 0);
}

static void test_record_violation_stores_entry(void) {
    simulation_report_t report;
    safety_violation_t v = make_violation(101, 202, 2, 0);

    init_simulation_report(&report);
    report_record_violation(&report, &v);

    assert(report.violation_count == 1);
    assert(report.violations[0].first_flight_id == 101);
    assert(report.violations[0].second_flight_id == 202);
    assert(report.violations[0].step == 2);
    assert(report.violations[0].predicted == 0);
}

static void test_record_violation_rejects_null(void) {
    simulation_report_t report;
    safety_violation_t v = make_violation(101, 202, 0, 0);

    init_simulation_report(&report);
    report_record_violation(NULL, &v);
    report_record_violation(&report, NULL);

    assert(report.violation_count == 0);
}

static void test_save_to_file_creates_file(void) {
    simulation_report_t report;
    flight_plan_t flights[2];
    position_history_t history;
    position_update_t latest[MAX_FLIGHTS];
    const char *path = "/tmp/test_report_us109.txt";

    memset(latest, 0, sizeof(latest));
    init_simulation_report(&report);
    init_position_history(&history);

    flights[0] = make_flight(101, 0);
    flights[1] = make_flight(202, 1);

    for (int step = 0; step < SIMULATION_STEPS; step++) {
        position_update_t u0 = make_update(101, step, step == SIMULATION_STEPS - 1);
        position_update_t u1 = make_update(202, step, step == SIMULATION_STEPS - 1);

        store_position_update(&history, latest, 2, 0, &u0);
        store_position_update(&history, latest, 2, 1, &u1);
    }

    report_collect_flight_results(&report, flights, 2, &history, 0);

    safety_violation_t v = make_violation(101, 202, 1, 1);
    report_record_violation(&report, &v);

    assert(report_save_to_file(&report, path) == 0);

    FILE *file = fopen(path, "r");
    assert(file != NULL);

    char line[256];
    int found_summary = 0;
    int found_result = 0;
    int found_violation = 0;

    while (fgets(line, sizeof(line), file) != NULL) {
        if (strstr(line, "SUMMARY") != NULL) found_summary = 1;
        if (strstr(line, "FINAL RESULT") != NULL) found_result = 1;
        if (strstr(line, "VIOLATION") != NULL) found_violation = 1;
    }

    fclose(file);

    assert(found_summary == 1);
    assert(found_result == 1);
    assert(found_violation == 1);
}

static void test_save_to_file_rejects_null(void) {
    simulation_report_t report;

    init_simulation_report(&report);

    assert(report_save_to_file(NULL, "out.txt") == -1);
    assert(report_save_to_file(&report, NULL) == -1);
}

int main(void) {
    test_init_clears_report();
    test_init_rejects_null();
    test_collect_marks_completed_flights();
    test_collect_marks_terminated_flights();
    test_collect_rejects_null_arguments();
    test_record_violation_stores_entry();
    test_record_violation_rejects_null();
    test_save_to_file_creates_file();
    test_save_to_file_rejects_null();

    printf("PASS test_report\n");
    return 0;
}
