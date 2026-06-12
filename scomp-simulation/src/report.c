#include "report.h"

#include <stdio.h>
#include <string.h>
#include <time.h>

void init_simulation_report(simulation_report_t *report) {
    if (report == NULL) {
        return;
    }

    memset(report, 0, sizeof(*report));
}

void report_collect_flight_results(simulation_report_t *report,
                                   const flight_plan_t flights[],
                                   int flight_count,
                                   const position_history_t *history,
                                   int simulation_interrupted) {
    if (report == NULL || flights == NULL || history == NULL) {
        return;
    }

    if (flight_count < 0 || flight_count > MAX_FLIGHTS) {
        return;
    }

    report->flight_count = flight_count;
    report->simulation_interrupted = simulation_interrupted;

    for (int i = 0; i < flight_count; i++) {
        report->flight_results[i].flight_id = flights[i].flight_id;

        const int last_step = SIMULATION_STEPS - 1;
        const int received_last = history->received[i][last_step];
        const int completed = received_last && history->updates[i][last_step].completed;

        report->flight_results[i].status = completed
                                           ? FLIGHT_STATUS_COMPLETED
                                           : FLIGHT_STATUS_TERMINATED;
    }
}

int report_build_violation_entry(report_violation_entry_t *entry,
                                 const safety_violation_t *violation,
                                 const position_history_t *history,
                                 int flight_count) {
    if (entry == NULL || violation == NULL || history == NULL) {
        return -1;
    }

    const int first = violation->first_flight_index;
    const int second = violation->second_flight_index;
    const int snap_step = (violation->step > 0 && violation->predicted)
                          ? violation->step - 1
                          : violation->step;
    const int step = snap_step < SIMULATION_STEPS ? snap_step : SIMULATION_STEPS - 1;

    entry->violation = *violation;

    if (first >= 0 && first < flight_count && history->received[first][step]) {
        entry->first_snapshot = history->updates[first][step];
    } else {
        memset(&entry->first_snapshot, 0, sizeof(entry->first_snapshot));
        entry->first_snapshot.flight_id = violation->first_flight_id;
    }

    if (second >= 0 && second < flight_count && history->received[second][step]) {
        entry->second_snapshot = history->updates[second][step];
    } else {
        memset(&entry->second_snapshot, 0, sizeof(entry->second_snapshot));
        entry->second_snapshot.flight_id = violation->second_flight_id;
    }

    return 0;
}

void report_append_violation_entry(simulation_report_t *report,
                                   const report_violation_entry_t *entry) {
    if (report == NULL || entry == NULL) {
        return;
    }

    if (report->violation_count >= REPORT_MAX_VIOLATIONS) {
        return;
    }

    report->violations[report->violation_count] = *entry;
    report->violation_count++;
}

void report_record_violation(simulation_report_t *report,
                             const safety_violation_t *violation,
                             const position_history_t *history,
                             int flight_count) {
    report_violation_entry_t entry;

    if (report == NULL) {
        return;
    }

    if (report_build_violation_entry(&entry, violation, history, flight_count) == -1) {
        return;
    }

    report_append_violation_entry(report, &entry);
}

static void write_separator(FILE *file) {
    fprintf(file, "========================================\n");
}

static void write_section_header(FILE *file, const char *title) {
    write_separator(file);
    fprintf(file, "%s\n", title);
    write_separator(file);
}

static void write_summary(FILE *file, const simulation_report_t *report) {
    int completed = 0;
    int terminated = 0;

    for (int i = 0; i < report->flight_count; i++) {
        if (report->flight_results[i].status == FLIGHT_STATUS_COMPLETED) {
            completed++;
        } else {
            terminated++;
        }
    }

    const int passed = report->violation_count == 0 && !report->simulation_interrupted;

    fprintf(file, "SUMMARY\n");
    fprintf(file, "-------\n");
    fprintf(file, "Total Flights   : %d\n", report->flight_count);
    fprintf(file, "Completed       : %d\n", completed);
    fprintf(file, "Terminated      : %d\n", terminated);
    fprintf(file, "Violations      : %d\n", report->violation_count);
    fprintf(file, "Validation      : %s\n\n", passed ? "PASSED" : "FAILED");
}

static void write_flight_statuses(FILE *file, const simulation_report_t *report) {
    write_section_header(file, "FLIGHT EXECUTION STATUS");

    for (int i = 0; i < report->flight_count; i++) {
        const flight_result_t *result = &report->flight_results[i];
        const char *status_label = result->status == FLIGHT_STATUS_COMPLETED
                                   ? "COMPLETED"
                                   : "TERMINATED";

        fprintf(file, "[FLIGHT %03d]  Status: %s\n", result->flight_id, status_label);
    }

    fprintf(file, "\n");
}

static void write_violations(FILE *file, const simulation_report_t *report) {
    write_section_header(file, "SAFETY VIOLATION EVENTS");

    if (report->violation_count == 0) {
        fprintf(file, "No safety violations detected.\n\n");
        return;
    }

    for (int i = 0; i < report->violation_count; i++) {
        const report_violation_entry_t *entry = &report->violations[i];
        const safety_violation_t *v = &entry->violation;
        const position_update_t *f = &entry->first_snapshot;
        const position_update_t *s = &entry->second_snapshot;

        fprintf(file, "[VIOLATION #%d]\n", i + 1);
        fprintf(file, "  Type              : %s\n", v->predicted ? "PREDICTED" : "DETECTED");
        fprintf(file, "  Step              : %d (t=%ds)\n", v->step, v->step * TIME_STEP_SECONDS);
        if (v->predicted) {
            const int detect_step = v->step > 0 ? v->step - 1 : 0;
            fprintf(file, "  Detected at step  : %d (t=%ds)\n", detect_step, detect_step * TIME_STEP_SECONDS);
        }
        fprintf(file, "  Flights           : %d <-> %d\n", v->first_flight_id, v->second_flight_id);
        fprintf(file, "  Horizontal Dist.  : %.1f m\n", v->horizontal_distance_meters);
        fprintf(file, "  Vertical Dist.    : %.1f m\n", v->vertical_distance_meters);
        fprintf(file, "  Flight %d position (at detection) : lat=%.4f lon=%.4f alt=%.1f m\n",
                f->flight_id, f->latitude, f->longitude, f->altitude);
        fprintf(file, "  Flight %d velocity (at detection) : vlat=%.4f vlon=%.4f valt=%.1f m/s\n",
                f->flight_id, f->velocity_latitude, f->velocity_longitude, f->velocity_altitude);
        fprintf(file, "  Flight %d position (at detection) : lat=%.4f lon=%.4f alt=%.1f m\n",
                s->flight_id, s->latitude, s->longitude, s->altitude);
        fprintf(file, "  Flight %d velocity (at detection) : vlat=%.4f vlon=%.4f valt=%.1f m/s\n",
                s->flight_id, s->velocity_latitude, s->velocity_longitude, s->velocity_altitude);
        fprintf(file, "\n");
    }
}

static void write_final_result(FILE *file, const simulation_report_t *report) {
    const int passed = report->violation_count == 0 && !report->simulation_interrupted;

    write_separator(file);
    fprintf(file, "FINAL RESULT: *** %s ***\n", passed ? "PASSED" : "FAILED");
    write_separator(file);
    fprintf(file, "\n");
}

int report_save_to_file(const simulation_report_t *report, const char *file_path) {
    if (report == NULL || file_path == NULL) {
        return -1;
    }

    FILE *file = fopen(file_path, "w");
    if (file == NULL) {
        return -1;
    }

    time_t now = time(NULL);
    char timestamp[64];
    strftime(timestamp, sizeof(timestamp), "%Y-%m-%d %H:%M:%S", localtime(&now));

    write_separator(file);
    fprintf(file, "      SIMULATION REPORT\n");
    fprintf(file, "      Generated: %s\n", timestamp);
    write_separator(file);
    fprintf(file, "\n");

    write_summary(file, report);
    write_flight_statuses(file, report);
    write_violations(file, report);
    write_final_result(file, report);

    if (fclose(file) != 0) {
        return -1;
    }

    return 0;
}
