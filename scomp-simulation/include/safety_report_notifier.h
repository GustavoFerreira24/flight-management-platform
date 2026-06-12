#ifndef SAFETY_REPORT_NOTIFIER_H
#define SAFETY_REPORT_NOTIFIER_H

#include "report.h"

#include <pthread.h>

typedef struct {
    pthread_mutex_t mutex;
    pthread_cond_t condition;
    report_violation_entry_t events[REPORT_MAX_VIOLATIONS];
    int event_count;
    int next_event;
    int closed;
} safety_report_notifier_t;

int safety_report_notifier_init(safety_report_notifier_t *notifier);

void safety_report_notifier_destroy(safety_report_notifier_t *notifier);

int safety_report_notifier_notify(safety_report_notifier_t *notifier,
                                  const safety_violation_t *violation,
                                  const position_history_t *history,
                                  int flight_count);

int safety_report_notifier_wait(safety_report_notifier_t *notifier,
                                report_violation_entry_t *entry);

void safety_report_notifier_close(safety_report_notifier_t *notifier);

#endif
