CC=gcc
CFLAGS=-std=c11 -Wall -Wextra -Werror -pedantic -Iinclude
LDFLAGS=-lm
BUILD_DIR=build
TARGET=build/simulator
TEST_TARGET=build/test_position_history
TEST_SAFETY_TARGET=build/test_safety_monitor
TEST_LOADER_TARGET=build/test_flight_loader
SOURCES=src/main.c src/position_history.c src/safety_monitor.c src/flight_loader.c
TEST_SOURCES=tests/test_position_history.c src/position_history.c
TEST_SAFETY_SOURCES=tests/test_safety_monitor.c src/position_history.c src/safety_monitor.c
TEST_LOADER_SOURCES=tests/test_flight_loader.c src/flight_loader.c

.PHONY: all clean run test

all: $(TARGET)

$(TARGET): $(SOURCES) include/simulation.h include/position_history.h include/safety_monitor.h include/flight_loader.h
	mkdir -p build
	$(CC) $(CFLAGS) $(SOURCES) -o $(TARGET) $(LDFLAGS)

run: $(TARGET)
	./$(TARGET)

run-large-normal: $(TARGET)
	./$(TARGET) data/large_normal_flights.csv

run-large-violations: $(TARGET)
	./$(TARGET) data/large_violation_flights.csv

test: $(TEST_TARGET) $(TEST_SAFETY_TARGET) $(TEST_LOADER_TARGET)
	./$(TEST_TARGET)
	./$(TEST_SAFETY_TARGET)
	./$(TEST_LOADER_TARGET)

$(TEST_TARGET): $(TEST_SOURCES) include/simulation.h include/position_history.h
	mkdir -p build
	$(CC) $(CFLAGS) $(TEST_SOURCES) -o $(TEST_TARGET) $(LDFLAGS)

$(TEST_SAFETY_TARGET): $(TEST_SAFETY_SOURCES) include/simulation.h include/position_history.h include/safety_monitor.h
	mkdir -p build
	$(CC) $(CFLAGS) $(TEST_SAFETY_SOURCES) -o $(TEST_SAFETY_TARGET) $(LDFLAGS)

$(TEST_LOADER_TARGET): $(TEST_LOADER_SOURCES) include/simulation.h include/flight_loader.h
	mkdir -p build
	$(CC) $(CFLAGS) $(TEST_LOADER_SOURCES) -o $(TEST_LOADER_TARGET) $(LDFLAGS)

clean:
	rm -rf build
