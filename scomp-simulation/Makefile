CC=gcc
CFLAGS=-std=c11 -Wall -Wextra -Werror -pedantic -Iinclude
BUILD_DIR=build
TARGET=build/simulator
TEST_TARGET=build/test_position_history
SOURCES=src/main.c src/position_history.c
TEST_SOURCES=tests/test_position_history.c src/position_history.c

.PHONY: all clean run test

all: $(TARGET)

$(TARGET): $(SOURCES) include/simulation.h include/position_history.h
	mkdir -p build
	$(CC) $(CFLAGS) $(SOURCES) -o $(TARGET)

run: $(TARGET)
	./$(TARGET)

test: $(TEST_TARGET)
	./$(TEST_TARGET)

$(TEST_TARGET): $(TEST_SOURCES) include/simulation.h include/position_history.h
	mkdir -p build
	$(CC) $(CFLAGS) $(TEST_SOURCES) -o $(TEST_TARGET)

clean:
	rm -rf build
