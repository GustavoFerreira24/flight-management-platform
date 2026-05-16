#ifndef REPORT_H
#define REPORT_H

#include "simulation.h"
#include "safety_monitor.h"
#include "position_history.h"

#define REPORT_MAX_VIOLATIONS 256
#define REPORT_FILE_PATH "simulation_report.txt"

typedef enum {
    FLIGHT_STATUS_COMPLETED,
    FLIGHT_STATUS_TERMINATED
} flight_status_t;

typedef struct {
    int flight_id;
    flight_status_t status;
} flight_result_t;

typedef struct {
    int flight_count;
    flight_result_t flight_results[MAX_FLIGHTS];
    safety_violation_t violations[REPORT_MAX_VIOLATIONS];
    int violation_count;
    int simulation_interrupted;
} simulation_report_t;

void init_simulation_report(simulation_report_t *report);

void report_collect_flight_results(simulation_report_t *report,
                                   const flight_plan_t flights[],
                                   int flight_count,
                                   const position_history_t *history,
                                   int simulation_interrupted);

void report_record_violation(simulation_report_t *report,
                             const safety_violation_t *violation);

int report_save_to_file(const simulation_report_t *report, const char *file_path);

#endif
