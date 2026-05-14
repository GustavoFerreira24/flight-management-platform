#ifndef SAFETY_MONITOR_H
#define SAFETY_MONITOR_H

#include "position_history.h"

#include <stdio.h>

#define SAFETY_MIN_HORIZONTAL_DISTANCE_METERS 10000.0
#define SAFETY_MIN_VERTICAL_DISTANCE_METERS 300.0
#define SAFETY_MAX_STEP_VIOLATIONS 32
#define SAFETY_MAX_TOTAL_VIOLATIONS 3

typedef struct {
    int step;
    int first_flight_index;
    int second_flight_index;
    int first_flight_id;
    int second_flight_id;
    double horizontal_distance_meters;
    double vertical_distance_meters;
    int predicted;
} safety_violation_t;

typedef struct {
    safety_violation_t violations[SAFETY_MAX_STEP_VIOLATIONS];
    int count;
} safety_scan_result_t;

void init_safety_scan_result(safety_scan_result_t *result);

int detect_pair_safety_violation(const position_update_t *first,
                                 const position_update_t *second,
                                 int step,
                                 safety_violation_t *violation);

int scan_safety_violations(const position_history_t *history,
                           int flight_count,
                           int step,
                           safety_scan_result_t *result);

void log_safety_violation(FILE *output,
                          const safety_violation_t *violation);

#endif
