CC = gcc
CFLAGS = -std=c11 -Wall -Wextra -Werror -pedantic -Iinclude
BUILD_DIR = build
TARGET = $(BUILD_DIR)/simulator
SOURCES = src/main.c

.PHONY: all clean run

all: $(TARGET)

$(TARGET): $(SOURCES) include/simulation.h
	mkdir -p $(BUILD_DIR)
	$(CC) $(CFLAGS) $(SOURCES) -o $(TARGET)

run: $(TARGET)
	./$(TARGET)

clean:
	rm -rf $(BUILD_DIR)
