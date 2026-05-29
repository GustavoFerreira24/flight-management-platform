CC=gcc
CFLAGS=-std=c11 -Wall -Wextra -Werror -pedantic -Iinclude
LDFLAGS=-lm
THREAD_FLAGS=-pthread
BUILD_DIR=build
TARGET=build/simulator
HYBRID_TARGET=build/hybrid_simulator
TEST_TARGET=build/test_position_history
TEST_SAFETY_TARGET=build/test_safety_monitor
TEST_LOADER_TARGET=build/test_flight_loader
TEST_REPORT_TARGET=build/test_report
TEST_SHARED_TARGET=build/test_shared_simulation
TEST_SYNC_TARGET=build/test_hybrid_sync
SOURCES=src/main.c src/position_history.c src/safety_monitor.c src/flight_loader.c src/report.c
HYBRID_SOURCES=src/hybrid_main.c src/hybrid_simulation.c src/shared_simulation.c src/hybrid_sync.c src/position_history.c src/safety_monitor.c src/flight_loader.c src/report.c
TEST_SOURCES=tests/test_position_history.c src/position_history.c
TEST_SAFETY_SOURCES=tests/test_safety_monitor.c src/position_history.c src/safety_monitor.c
TEST_LOADER_SOURCES=tests/test_flight_loader.c src/flight_loader.c
TEST_REPORT_SOURCES=tests/test_report.c src/report.c src/position_history.c src/safety_monitor.c
TEST_SHARED_SOURCES=tests/test_shared_simulation.c src/shared_simulation.c
TEST_SYNC_SOURCES=tests/test_hybrid_sync.c src/hybrid_sync.c

.PHONY: all clean run run-hybrid run-hybrid-large-normal run-hybrid-large-violations test

all: $(TARGET)

$(TARGET): $(SOURCES) include/simulation.h include/position_history.h include/safety_monitor.h include/flight_loader.h include/report.h
	mkdir -p build
	$(CC) $(CFLAGS) $(SOURCES) -o $(TARGET) $(LDFLAGS)

$(HYBRID_TARGET): $(HYBRID_SOURCES) include/simulation.h include/position_history.h include/safety_monitor.h include/flight_loader.h include/report.h include/shared_simulation.h include/hybrid_sync.h include/hybrid_simulation.h
	mkdir -p build
	$(CC) $(CFLAGS) $(THREAD_FLAGS) $(HYBRID_SOURCES) -o $(HYBRID_TARGET) $(LDFLAGS) $(THREAD_FLAGS)

run: $(TARGET)
	./$(TARGET)

run-hybrid: $(HYBRID_TARGET)
	./$(HYBRID_TARGET)

run-hybrid-large-normal: $(HYBRID_TARGET)
	./$(HYBRID_TARGET) data/large_normal_flights.csv

run-hybrid-large-violations: $(HYBRID_TARGET)
	./$(HYBRID_TARGET) data/large_violation_flights.csv

run-large-normal: $(TARGET)
	./$(TARGET) data/large_normal_flights.csv

run-large-violations: $(TARGET)
	./$(TARGET) data/large_violation_flights.csv

test: $(TEST_TARGET) $(TEST_SAFETY_TARGET) $(TEST_LOADER_TARGET) $(TEST_REPORT_TARGET) $(TEST_SHARED_TARGET) $(TEST_SYNC_TARGET)
	./$(TEST_TARGET)
	./$(TEST_SAFETY_TARGET)
	./$(TEST_LOADER_TARGET)
	./$(TEST_REPORT_TARGET)
	./$(TEST_SHARED_TARGET)
	./$(TEST_SYNC_TARGET)

$(TEST_TARGET): $(TEST_SOURCES) include/simulation.h include/position_history.h
	mkdir -p build
	$(CC) $(CFLAGS) $(TEST_SOURCES) -o $(TEST_TARGET) $(LDFLAGS)

$(TEST_SAFETY_TARGET): $(TEST_SAFETY_SOURCES) include/simulation.h include/position_history.h include/safety_monitor.h
	mkdir -p build
	$(CC) $(CFLAGS) $(TEST_SAFETY_SOURCES) -o $(TEST_SAFETY_TARGET) $(LDFLAGS)

$(TEST_LOADER_TARGET): $(TEST_LOADER_SOURCES) include/simulation.h include/flight_loader.h
	mkdir -p build
	$(CC) $(CFLAGS) $(TEST_LOADER_SOURCES) -o $(TEST_LOADER_TARGET) $(LDFLAGS)

$(TEST_REPORT_TARGET): $(TEST_REPORT_SOURCES) include/simulation.h include/position_history.h include/safety_monitor.h include/report.h
	mkdir -p build
	$(CC) $(CFLAGS) $(TEST_REPORT_SOURCES) -o $(TEST_REPORT_TARGET) $(LDFLAGS)

$(TEST_SHARED_TARGET): $(TEST_SHARED_SOURCES) include/simulation.h include/shared_simulation.h
	mkdir -p build
	$(CC) $(CFLAGS) $(TEST_SHARED_SOURCES) -o $(TEST_SHARED_TARGET) $(LDFLAGS)

$(TEST_SYNC_TARGET): $(TEST_SYNC_SOURCES) include/simulation.h include/hybrid_sync.h
	mkdir -p build
	$(CC) $(CFLAGS) $(THREAD_FLAGS) $(TEST_SYNC_SOURCES) -o $(TEST_SYNC_TARGET) $(LDFLAGS) $(THREAD_FLAGS)

clean:
	rm -rf build
