#ifndef SIMULATION_H
#define SIMULATION_H

#include <sys/types.h>

#define MAX_FLIGHTS 8
#define SIMULATION_STEPS 5

typedef struct {
    int flight_id;
    double initial_latitude;
    double initial_longitude;
    double initial_altitude;
    double velocity_latitude;
    double velocity_longitude;
    double velocity_altitude;
} flight_plan_t;

typedef struct {
    int flight_id;
    int step;
    pid_t process_id;
    double latitude;
    double longitude;
    double altitude;
    double velocity_latitude;
    double velocity_longitude;
    double velocity_altitude;
    int completed;
} position_update_t;

#endif
