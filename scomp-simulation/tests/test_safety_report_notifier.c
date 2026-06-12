#include "safety_report_notifier.h"

#include <assert.h>
#include <pthread.h>
#include <stdio.h>
#include <string.h>

typedef struct {
    safety_report_notifier_t *notifier;
    simulation_report_t *report;
    int wait_result;
} report_thread_args_t;

static position_update_t make_update(int flight_id, int step) {
    position_update_t update;

    memset(&update, 0, sizeof(update));
    update.flight_id = flight_id;
    update.step = step;
    update.latitude = 41.0 + step;
    update.longitude = -8.0 - step;
    update.altitude = 1000.0 + step;
    update.velocity_latitude = 0.1;
    update.velocity_longitude = 0.1;
    update.velocity_altitude = 10.0;

    return update;
}

static safety_violation_t make_violation(void) {
    safety_violation_t violation;

    memset(&violation, 0, sizeof(violation));
    violation.step = 0;
    violation.first_flight_index = 0;
    violation.second_flight_index = 1;
    violation.first_flight_id = 101;
    violation.second_flight_id = 202;
    violation.horizontal_distance_meters = 5000.0;
    violation.vertical_distance_meters = 100.0;

    return violation;
}

static void build_history(position_history_t *history) {
    position_update_t latest[MAX_FLIGHTS];
    const position_update_t first = make_update(101, 0);
    const position_update_t second = make_update(202, 0);

    memset(latest, 0, sizeof(latest));
    init_position_history(history);

    assert(store_position_update(history, latest, 2, 0, &first) == 0);
    assert(store_position_update(history, latest, 2, 1, &second) == 0);
}

static void *single_event_report_thread(void *argument) {
    report_thread_args_t *args = argument;
    report_violation_entry_t entry;

    args->wait_result = safety_report_notifier_wait(args->notifier, &entry);

    if (args->wait_result == 1) {
        report_append_violation_entry(args->report, &entry);
    }

    return NULL;
}

static void test_condition_variable_notifies_report_thread(void) {
    safety_report_notifier_t notifier;
    simulation_report_t report;
    position_history_t history;
    pthread_t report_thread;
    report_thread_args_t args;
    const safety_violation_t violation = make_violation();

    build_history(&history);
    init_simulation_report(&report);
    assert(safety_report_notifier_init(&notifier) == 0);

    args.notifier = &notifier;
    args.report = &report;
    args.wait_result = -1;

    assert(pthread_create(&report_thread, NULL, single_event_report_thread, &args) == 0);
    assert(safety_report_notifier_notify(&notifier, &violation, &history, 2) == 0);
    assert(pthread_join(report_thread, NULL) == 0);

    assert(args.wait_result == 1);
    assert(report.violation_count == 1);
    assert(report.violations[0].violation.first_flight_id == 101);
    assert(report.violations[0].violation.second_flight_id == 202);
    assert(report.violations[0].first_snapshot.flight_id == 101);
    assert(report.violations[0].second_snapshot.flight_id == 202);

    safety_report_notifier_destroy(&notifier);
}

static void test_close_unblocks_waiting_report_thread(void) {
    safety_report_notifier_t notifier;
    simulation_report_t report;
    pthread_t report_thread;
    report_thread_args_t args;

    init_simulation_report(&report);
    assert(safety_report_notifier_init(&notifier) == 0);

    args.notifier = &notifier;
    args.report = &report;
    args.wait_result = -1;

    assert(pthread_create(&report_thread, NULL, single_event_report_thread, &args) == 0);
    safety_report_notifier_close(&notifier);
    assert(pthread_join(report_thread, NULL) == 0);

    assert(args.wait_result == 0);
    assert(report.violation_count == 0);

    safety_report_notifier_destroy(&notifier);
}

static void test_rejects_invalid_arguments(void) {
    safety_report_notifier_t notifier;
    position_history_t history;
    report_violation_entry_t entry;
    const safety_violation_t violation = make_violation();

    build_history(&history);
    assert(safety_report_notifier_init(&notifier) == 0);

    assert(safety_report_notifier_init(NULL) == -1);
    assert(safety_report_notifier_notify(NULL, &violation, &history, 2) == -1);
    assert(safety_report_notifier_notify(&notifier, NULL, &history, 2) == -1);
    assert(safety_report_notifier_notify(&notifier, &violation, NULL, 2) == -1);
    assert(safety_report_notifier_wait(NULL, &entry) == -1);
    assert(safety_report_notifier_wait(&notifier, NULL) == -1);

    safety_report_notifier_destroy(&notifier);
}

int main(void) {
    test_condition_variable_notifies_report_thread();
    test_close_unblocks_waiting_report_thread();
    test_rejects_invalid_arguments();

    printf("PASS test_safety_report_notifier\n");
    return 0;
}
