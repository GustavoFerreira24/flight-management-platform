#define _POSIX_C_SOURCE 200809L

#include "shared_simulation.h"

#include <fcntl.h>
#include <stdio.h>
#include <string.h>
#include <sys/mman.h>
#include <unistd.h>

int shared_simulation_create(shared_simulation_region_t *region,
                             const char *name,
                             const flight_plan_t flights[],
                             int flight_count) {
    if (region == NULL || name == NULL || flights == NULL ||
        flight_count <= 0 || flight_count > MAX_FLIGHTS) {
        return -1;
    }

    memset(region, 0, sizeof(*region));
    region->descriptor = -1;
    region->owner = 1;

    if (snprintf(region->name, sizeof(region->name), "%s", name) >=
        (int) sizeof(region->name)) {
        return -1;
    }

    shm_unlink(region->name);

    region->descriptor = shm_open(region->name, O_CREAT | O_EXCL | O_RDWR, 0600);
    if (region->descriptor == -1) {
        perror("shm_open");
        return -1;
    }

    if (ftruncate(region->descriptor, (off_t) sizeof(shared_simulation_t)) == -1) {
        perror("ftruncate");
        shared_simulation_close(region);
        return -1;
    }

    region->state = mmap(NULL,
                         sizeof(shared_simulation_t),
                         PROT_READ | PROT_WRITE,
                         MAP_SHARED,
                         region->descriptor,
                         0);
    if (region->state == MAP_FAILED) {
        perror("mmap");
        region->state = NULL;
        shared_simulation_close(region);
        return -1;
    }

    memset(region->state, 0, sizeof(*region->state));
    region->state->initialized = 1;
    region->state->flight_count = flight_count;
    region->state->current_step = -1;

    for (int i = 0; i < flight_count; i++) {
        region->state->flights[i] = flights[i];
    }

    return 0;
}

void shared_simulation_close(shared_simulation_region_t *region) {
    if (region == NULL) {
        return;
    }

    if (region->state != NULL) {
        munmap(region->state, sizeof(shared_simulation_t));
        region->state = NULL;
    }

    if (region->descriptor >= 0) {
        close(region->descriptor);
        region->descriptor = -1;
    }

    if (region->owner && region->name[0] != '\0') {
        shm_unlink(region->name);
    }
}
