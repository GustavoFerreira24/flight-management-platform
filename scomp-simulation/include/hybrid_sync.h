#ifndef HYBRID_SYNC_H
#define HYBRID_SYNC_H

#include "simulation.h"

#include <semaphore.h>

#define HYBRID_SYNC_NAME_LENGTH 32

typedef struct {
    int flight_count;
    int owner;
    char start_names[MAX_FLIGHTS][HYBRID_SYNC_NAME_LENGTH];
    char updates_ready_name[HYBRID_SYNC_NAME_LENGTH];
    sem_t *start_step[MAX_FLIGHTS];
    sem_t *updates_ready;
} hybrid_sync_t;

int hybrid_sync_create(hybrid_sync_t *sync, const char *prefix, int flight_count);

int hybrid_sync_release_flight(hybrid_sync_t *sync, int flight_index);

int hybrid_sync_release_all(hybrid_sync_t *sync);

int hybrid_sync_wait_start(hybrid_sync_t *sync, int flight_index);

int hybrid_sync_mark_update_ready(hybrid_sync_t *sync);

int hybrid_sync_wait_for_updates(hybrid_sync_t *sync, int update_count);

void hybrid_sync_close(hybrid_sync_t *sync);

#endif
