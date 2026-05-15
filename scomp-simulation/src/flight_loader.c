#include "flight_loader.h"

#include <ctype.h>
#include <stdio.h>
#include <string.h>

static char *skip_leading_spaces(char *line) {
    while (*line != '\0' && isspace((unsigned char) *line)) {
        line++;
    }

    return line;
}

static int is_ignored_line(const char *line) {
    return line[0] == '\0' || line[0] == '\n' || line[0] == '#';
}

static int is_header_line(const char *line) {
    return strncmp(line, "flight_id", 9) == 0;
}

int load_flight_plans(const char *file_path,
                      flight_plan_t flights[MAX_FLIGHTS],
                      int max_flights) {
    FILE *file;
    char line[256];
    int count = 0;

    if (file_path == NULL || flights == NULL || max_flights <= 0) {
        return -1;
    }

    file = fopen(file_path, "r");
    if (file == NULL) {
        return -1;
    }

    while (fgets(line, sizeof(line), file) != NULL) {
        flight_plan_t flight;
        char extra;
        char *content = skip_leading_spaces(line);
        const int parsed = sscanf(content,
                                  " %d , %lf , %lf , %lf , %lf , %lf , %lf %c",
                                  &flight.flight_id,
                                  &flight.initial_latitude,
                                  &flight.initial_longitude,
                                  &flight.initial_altitude,
                                  &flight.velocity_latitude,
                                  &flight.velocity_longitude,
                                  &flight.velocity_altitude,
                                  &extra);

        if (is_ignored_line(content) || is_header_line(content)) {
            continue;
        }

        if (parsed != 7) {
            fclose(file);
            return -1;
        }

        if (count >= max_flights) {
            fclose(file);
            return -1;
        }

        flights[count] = flight;
        count++;
    }

    fclose(file);
    return count;
}
