#include "safety_report_notifier.h"

#include <string.h>

int safety_report_notifier_init(safety_report_notifier_t *notifier) {
    if (notifier == NULL) {
        return -1;
    }

    memset(notifier, 0, sizeof(*notifier));

    if (pthread_mutex_init(&notifier->mutex, NULL) != 0) {
        return -1;
    }

    if (pthread_cond_init(&notifier->condition, NULL) != 0) {
        pthread_mutex_destroy(&notifier->mutex);
        return -1;
    }

    return 0;
}

void safety_report_notifier_destroy(safety_report_notifier_t *notifier) {
    if (notifier == NULL) {
        return;
    }

    pthread_cond_destroy(&notifier->condition);
    pthread_mutex_destroy(&notifier->mutex);
}

int safety_report_notifier_notify(safety_report_notifier_t *notifier,
                                  const safety_violation_t *violation,
                                  const position_history_t *history,
                                  int flight_count) {
    report_violation_entry_t entry;

    if (notifier == NULL) {
        return -1;
    }

    if (report_build_violation_entry(&entry, violation, history, flight_count) == -1) {
        return -1;
    }

    if (pthread_mutex_lock(&notifier->mutex) != 0) {
        return -1;
    }

    if (notifier->closed || notifier->event_count >= REPORT_MAX_VIOLATIONS) {
        pthread_mutex_unlock(&notifier->mutex);
        return -1;
    }

    notifier->events[notifier->event_count] = entry;
    notifier->event_count++;
    pthread_cond_signal(&notifier->condition);
    pthread_mutex_unlock(&notifier->mutex);

    return 0;
}

int safety_report_notifier_wait(safety_report_notifier_t *notifier,
                                report_violation_entry_t *entry) {
    if (notifier == NULL || entry == NULL) {
        return -1;
    }

    if (pthread_mutex_lock(&notifier->mutex) != 0) {
        return -1;
    }

    while (notifier->next_event >= notifier->event_count && !notifier->closed) {
        if (pthread_cond_wait(&notifier->condition, &notifier->mutex) != 0) {
            pthread_mutex_unlock(&notifier->mutex);
            return -1;
        }
    }

    if (notifier->next_event >= notifier->event_count && notifier->closed) {
        pthread_mutex_unlock(&notifier->mutex);
        return 0;
    }

    *entry = notifier->events[notifier->next_event];
    notifier->next_event++;
    pthread_mutex_unlock(&notifier->mutex);

    return 1;
}

void safety_report_notifier_close(safety_report_notifier_t *notifier) {
    if (notifier == NULL) {
        return;
    }

    if (pthread_mutex_lock(&notifier->mutex) != 0) {
        return;
    }

    notifier->closed = 1;
    pthread_cond_broadcast(&notifier->condition);
    pthread_mutex_unlock(&notifier->mutex);
}
