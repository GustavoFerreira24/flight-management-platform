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

void report_record_violation(simulation_report_t *report,
                             const safety_violation_t *violation) {
    if (report == NULL || violation == NULL) {
        return;
    }

    if (report->violation_count >= REPORT_MAX_VIOLATIONS) {
        return;
    }

    report->violations[report->violation_count] = *violation;
    report->violation_count++;
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
        const safety_violation_t *v = &report->violations[i];

        fprintf(file, "[VIOLATION #%d]\n", i + 1);
        fprintf(file, "  Type              : %s\n", v->predicted ? "PREDICTED" : "DETECTED");
        fprintf(file, "  Step              : %d\n", v->step);
        fprintf(file, "  Flights           : %d <-> %d\n", v->first_flight_id, v->second_flight_id);
        fprintf(file, "  Horizontal Dist.  : %.1f m\n", v->horizontal_distance_meters);
        fprintf(file, "  Vertical Dist.    : %.1f m\n", v->vertical_distance_meters);
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
