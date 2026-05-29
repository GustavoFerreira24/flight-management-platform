#include "hybrid_sync.h"

#include <assert.h>
#include <stdio.h>
#include <unistd.h>

static void build_sync_prefix(char prefix[HYBRID_SYNC_NAME_LENGTH]) {
    const int result = snprintf(prefix, HYBRID_SYNC_NAME_LENGTH, "/tsync%d", (int) getpid());

    assert(result > 0);
    assert(result < HYBRID_SYNC_NAME_LENGTH - 3);
}

static void test_creates_named_semaphores(void) {
    hybrid_sync_t sync;
    char prefix[HYBRID_SYNC_NAME_LENGTH];

    build_sync_prefix(prefix);

    assert(hybrid_sync_create(&sync, prefix, 2) == 0);
    assert(sync.flight_count == 2);
    assert(sync.owner == 1);
    assert(sync.start_step[0] != NULL);
    assert(sync.start_step[1] != NULL);
    assert(sync.updates_ready != NULL);

    hybrid_sync_close(&sync);
}

static void test_release_and_wait_cycle(void) {
    hybrid_sync_t sync;
    char prefix[HYBRID_SYNC_NAME_LENGTH];

    build_sync_prefix(prefix);

    assert(hybrid_sync_create(&sync, prefix, 2) == 0);

    assert(hybrid_sync_release_flight(&sync, 0) == 0);
    assert(hybrid_sync_wait_start(&sync, 0) == 0);

    assert(hybrid_sync_release_all(&sync) == 0);
    assert(hybrid_sync_wait_start(&sync, 0) == 0);
    assert(hybrid_sync_wait_start(&sync, 1) == 0);

    assert(hybrid_sync_mark_update_ready(&sync) == 0);
    assert(hybrid_sync_mark_update_ready(&sync) == 0);
    assert(hybrid_sync_wait_for_updates(&sync, 2) == 0);

    hybrid_sync_close(&sync);
}

static void test_rejects_invalid_arguments(void) {
    hybrid_sync_t sync;
    char prefix[HYBRID_SYNC_NAME_LENGTH];

    build_sync_prefix(prefix);

    assert(hybrid_sync_create(NULL, prefix, 1) == -1);
    assert(hybrid_sync_create(&sync, NULL, 1) == -1);
    assert(hybrid_sync_create(&sync, prefix, 0) == -1);
    assert(hybrid_sync_create(&sync, prefix, MAX_FLIGHTS + 1) == -1);
}

int main(void) {
    test_creates_named_semaphores();
    test_release_and_wait_cycle();
    test_rejects_invalid_arguments();

    printf("PASS test_hybrid_sync\n");
    return 0;
}
