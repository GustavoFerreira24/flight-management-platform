#include "safety_monitor.h"

#include <math.h>
#include <stdio.h>
#include <string.h>

static double absolute_double(double value) {
    return value < 0.0 ? -value : value;
}

static double squared_horizontal_distance_meters(const position_update_t *first,
                                                 const position_update_t *second) {
    const double meters_per_degree = 111320.0;
    const double latitude_distance = (first->latitude - second->latitude) * meters_per_degree;
    const double longitude_distance = (first->longitude - second->longitude) * meters_per_degree;

    return latitude_distance * latitude_distance + longitude_distance * longitude_distance;
}

static double vertical_distance_meters(const position_update_t *first,
                                       const position_update_t *second) {
    return absolute_double(first->altitude - second->altitude);
}

static int positions_violate_safety_limits(const position_update_t *first,
                                           const position_update_t *second) {
    const double horizontal_limit = SAFETY_MIN_HORIZONTAL_DISTANCE_METERS;
    const double horizontal_distance_squared = squared_horizontal_distance_meters(first, second);
    const double horizontal_limit_squared = horizontal_limit * horizontal_limit;
    const double vertical_distance = vertical_distance_meters(first, second);

    return horizontal_distance_squared < horizontal_limit_squared
           && vertical_distance < SAFETY_MIN_VERTICAL_DISTANCE_METERS;
}

static position_update_t predict_next_position(const position_update_t *update) {
    position_update_t predicted = *update;

    predicted.step = update->step + 1;
    predicted.latitude += update->velocity_latitude;
    predicted.longitude += update->velocity_longitude;
    predicted.altitude += update->velocity_altitude;

    return predicted;
}

static void fill_violation(safety_violation_t *violation,
                           const position_update_t *first,
                           const position_update_t *second,
                           int step,
                           int predicted) {
    violation->step = step;
    violation->first_flight_index = -1;
    violation->second_flight_index = -1;
    violation->first_flight_id = first->flight_id;
    violation->second_flight_id = second->flight_id;
    violation->horizontal_distance_meters = sqrt(squared_horizontal_distance_meters(first, second));
    violation->vertical_distance_meters = vertical_distance_meters(first, second);
    violation->predicted = predicted;
}

void init_safety_scan_result(safety_scan_result_t *result) {
    if (result != NULL) {
        memset(result, 0, sizeof(*result));
    }
}

int detect_pair_safety_violation(const position_update_t *first,
                                 const position_update_t *second,
                                 int step,
                                 safety_violation_t *violation) {
    if (first == NULL || second == NULL || violation == NULL) {
        return 0;
    }

    if (positions_violate_safety_limits(first, second)) {
        fill_violation(violation, first, second, step, 0);
        return 1;
    }

    const position_update_t predicted_first = predict_next_position(first);
    const position_update_t predicted_second = predict_next_position(second);

    if (positions_violate_safety_limits(&predicted_first, &predicted_second)) {
        fill_violation(violation, &predicted_first, &predicted_second, step + 1, 1);
        return 1;
    }

    return 0;
}

int scan_safety_violations(const position_history_t *history,
                           int flight_count,
                           int step,
                           safety_scan_result_t *result) {
    if (history == NULL || result == NULL) {
        return -1;
    }

    if (flight_count < 0 || flight_count > MAX_FLIGHTS || step < 0 || step >= SIMULATION_STEPS) {
        return -1;
    }

    init_safety_scan_result(result);

    for (int first = 0; first < flight_count; first++) {
        if (!history->received[first][step]) {
            continue;
        }

        for (int second = first + 1; second < flight_count; second++) {
            if (!history->received[second][step]) {
                continue;
            }

            safety_violation_t violation;

            if (!detect_pair_safety_violation(&history->updates[first][step],
                                              &history->updates[second][step],
                                              step,
                                              &violation)) {
                continue;
            }

            if (violation.step < 0 || violation.step >= SIMULATION_STEPS) {
                continue;
            }

            if (result->count >= SAFETY_MAX_STEP_VIOLATIONS) {
                return -1;
            }

            violation.first_flight_index = first;
            violation.second_flight_index = second;
            result->violations[result->count] = violation;
            result->count++;
        }
    }

    return 0;
}

void log_safety_violation(FILE *output,
                          const safety_violation_t *violation) {
    if (output == NULL || violation == NULL) {
        return;
    }

    if (violation->predicted) {
        fprintf(output,
                "SAFETY WARNING predicted for step=%d flights=%d/%d horizontal=%.1fm vertical=%.1fm\n",
                violation->step,
                violation->first_flight_id,
                violation->second_flight_id,
                violation->horizontal_distance_meters,
                violation->vertical_distance_meters);
    } else {
        fprintf(output,
                "SAFETY VIOLATION detected at step=%d flights=%d/%d horizontal=%.1fm vertical=%.1fm\n",
                violation->step,
                violation->first_flight_id,
                violation->second_flight_id,
                violation->horizontal_distance_meters,
                violation->vertical_distance_meters);
    }
}
