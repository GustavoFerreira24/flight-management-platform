#define _POSIX_C_SOURCE 200809L

#include "hybrid_sync.h"

#include <errno.h>
#include <fcntl.h>
#include <stdio.h>
#include <string.h>

static int wait_for_semaphore(sem_t *semaphore) {
    while (sem_wait(semaphore) == -1) {
        if (errno == EINTR) {
            continue;
        }

        perror("sem_wait");
        return -1;
    }

    return 0;
}

int hybrid_sync_create(hybrid_sync_t *sync, const char *prefix, int flight_count) {
    if (sync == NULL || prefix == NULL || flight_count <= 0 || flight_count > MAX_FLIGHTS) {
        return -1;
    }

    memset(sync, 0, sizeof(*sync));
    sync->flight_count = flight_count;
    sync->owner = 1;

    if (snprintf(sync->updates_ready_name,
                 sizeof(sync->updates_ready_name),
                 "%sr",
                 prefix) >= (int) sizeof(sync->updates_ready_name)) {
        return -1;
    }

    sem_unlink(sync->updates_ready_name);
    sync->updates_ready = sem_open(sync->updates_ready_name, O_CREAT | O_EXCL, 0600, 0);
    if (sync->updates_ready == SEM_FAILED) {
        perror("sem_open updates_ready");
        sync->updates_ready = NULL;
        return -1;
    }

    for (int i = 0; i < flight_count; i++) {
        if (snprintf(sync->start_names[i],
                     sizeof(sync->start_names[i]),
                     "%ss%02d",
                     prefix,
                     i) >= (int) sizeof(sync->start_names[i])) {
            hybrid_sync_close(sync);
            return -1;
        }

        sem_unlink(sync->start_names[i]);
        sync->start_step[i] = sem_open(sync->start_names[i], O_CREAT | O_EXCL, 0600, 0);
        if (sync->start_step[i] == SEM_FAILED) {
            perror("sem_open start_step");
            sync->start_step[i] = NULL;
            hybrid_sync_close(sync);
            return -1;
        }
    }

    return 0;
}

int hybrid_sync_release_flight(hybrid_sync_t *sync, int flight_index) {
    if (sync == NULL || flight_index < 0 || flight_index >= sync->flight_count ||
        sync->start_step[flight_index] == NULL) {
        return -1;
    }

    if (sem_post(sync->start_step[flight_index]) == -1) {
        perror("sem_post start_step");
        return -1;
    }

    return 0;
}

int hybrid_sync_release_all(hybrid_sync_t *sync) {
    if (sync == NULL) {
        return -1;
    }

    for (int i = 0; i < sync->flight_count; i++) {
        if (hybrid_sync_release_flight(sync, i) == -1) {
            return -1;
        }
    }

    return 0;
}

int hybrid_sync_wait_start(hybrid_sync_t *sync, int flight_index) {
    if (sync == NULL || flight_index < 0 || flight_index >= sync->flight_count ||
        sync->start_step[flight_index] == NULL) {
        return -1;
    }

    return wait_for_semaphore(sync->start_step[flight_index]);
}

int hybrid_sync_mark_update_ready(hybrid_sync_t *sync) {
    if (sync == NULL || sync->updates_ready == NULL) {
        return -1;
    }

    if (sem_post(sync->updates_ready) == -1) {
        perror("sem_post updates_ready");
        return -1;
    }

    return 0;
}

int hybrid_sync_wait_for_updates(hybrid_sync_t *sync, int update_count) {
    if (sync == NULL || sync->updates_ready == NULL || update_count < 0) {
        return -1;
    }

    for (int i = 0; i < update_count; i++) {
        if (wait_for_semaphore(sync->updates_ready) == -1) {
            return -1;
        }
    }

    return 0;
}

void hybrid_sync_close(hybrid_sync_t *sync) {
    if (sync == NULL) {
        return;
    }

    for (int i = 0; i < sync->flight_count; i++) {
        if (sync->start_step[i] != NULL) {
            sem_close(sync->start_step[i]);
            sync->start_step[i] = NULL;
        }

        if (sync->owner && sync->start_names[i][0] != '\0') {
            sem_unlink(sync->start_names[i]);
        }
    }

    if (sync->updates_ready != NULL) {
        sem_close(sync->updates_ready);
        sync->updates_ready = NULL;
    }

    if (sync->owner && sync->updates_ready_name[0] != '\0') {
        sem_unlink(sync->updates_ready_name);
    }
}
