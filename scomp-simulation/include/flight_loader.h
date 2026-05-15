#ifndef FLIGHT_LOADER_H
#define FLIGHT_LOADER_H

#include "simulation.h"

int load_flight_plans(const char *file_path,
                      flight_plan_t flights[MAX_FLIGHTS],
                      int max_flights);

#endif
