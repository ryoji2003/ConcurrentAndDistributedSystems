/*
 * Task 8.4: OpenMP Game of Life
 *
 * Conway's Game of Life simulation using OpenMP parallelization.
 * Uses a 6x6 toroidal (wrap-around) board.
 *
 * Rules:
 * 1. A cell with 2 or 3 neighbors survives
 * 2. A dead cell with exactly 3 neighbors becomes alive
 * 3. All other cells die or stay dead
 *
 * Compile: gcc-15 -fopenmp -O2 -o game_of_life_omp game_of_life_omp.c
 * Run: ./game_of_life_omp
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <omp.h>

#define ROWS 6
#define COLS 6

// Print the game board
void print_board(int board[ROWS][COLS], int step) {
    printf("Step %d:\n", step);
    for (int i = 0; i < ROWS; i++) {
        for (int j = 0; j < COLS; j++) {
            printf("%c ", board[i][j] ? '#' : '.');
        }
        printf("\n");
    }
    printf("\n");
}

// Count live neighbors with toroidal wrapping
int count_neighbors(int board[ROWS][COLS], int row, int col) {
    int count = 0;

    for (int di = -1; di <= 1; di++) {
        for (int dj = -1; dj <= 1; dj++) {
            if (di == 0 && dj == 0) continue;  // Skip self

            // Toroidal wrapping
            int ni = (row + di + ROWS) % ROWS;
            int nj = (col + dj + COLS) % COLS;

            count += board[ni][nj];
        }
    }

    return count;
}

// Compute the next generation using OpenMP
void next_generation(int current[ROWS][COLS], int next[ROWS][COLS]) {
    // Parallelize the outer loop
    #pragma omp parallel for collapse(2)
    for (int i = 0; i < ROWS; i++) {
        for (int j = 0; j < COLS; j++) {
            int neighbors = count_neighbors(current, i, j);

            if (current[i][j]) {
                // Live cell: survives with 2 or 3 neighbors
                next[i][j] = (neighbors == 2 || neighbors == 3) ? 1 : 0;
            } else {
                // Dead cell: becomes alive with exactly 3 neighbors
                next[i][j] = (neighbors == 3) ? 1 : 0;
            }
        }
    }
}

// Copy board contents
void copy_board(int dest[ROWS][COLS], int src[ROWS][COLS]) {
    #pragma omp parallel for collapse(2)
    for (int i = 0; i < ROWS; i++) {
        for (int j = 0; j < COLS; j++) {
            dest[i][j] = src[i][j];
        }
    }
}

// Clear the board
void clear_board(int board[ROWS][COLS]) {
    memset(board, 0, sizeof(int) * ROWS * COLS);
}

// Run simulation for N steps
void run_simulation(int board[ROWS][COLS], int steps, const char *name) {
    int next[ROWS][COLS];

    printf("=== %s ===\n\n", name);
    print_board(board, 0);

    for (int step = 1; step <= steps; step++) {
        next_generation(board, next);
        copy_board(board, next);
        print_board(board, step);
    }
}

// Test: Blinker (oscillates between horizontal and vertical)
void test_blinker() {
    int board[ROWS][COLS];
    clear_board(board);

    // Place blinker at center (horizontal)
    // . . . . . .
    // . . . . . .
    // . # # # . .
    // . . . . . .
    // . . . . . .
    // . . . . . .
    board[2][1] = 1;
    board[2][2] = 1;
    board[2][3] = 1;

    run_simulation(board, 4, "BLINKER (should oscillate)");
}

// Test: Block (should stay intact - still life)
void test_block() {
    int board[ROWS][COLS];
    clear_board(board);

    // Place block at top-left area
    // . . . . . .
    // . # # . . .
    // . # # . . .
    // . . . . . .
    // . . . . . .
    // . . . . . .
    board[1][1] = 1;
    board[1][2] = 1;
    board[2][1] = 1;
    board[2][2] = 1;

    run_simulation(board, 3, "BLOCK (should stay intact)");
}

// Test: Glider (should move diagonally across the board)
void test_glider() {
    int board[ROWS][COLS];
    clear_board(board);

    // Place glider
    // . # . . . .
    // . . # . . .
    // # # # . . .
    // . . . . . .
    // . . . . . .
    // . . . . . .
    board[0][1] = 1;
    board[1][2] = 1;
    board[2][0] = 1;
    board[2][1] = 1;
    board[2][2] = 1;

    run_simulation(board, 20, "GLIDER (should glide across the board)");
}

int main() {
    printf("Conway's Game of Life with OpenMP\n");
    printf("==================================\n");
    printf("Board size: %dx%d (toroidal)\n", ROWS, COLS);
    printf("Number of threads: %d\n\n", omp_get_max_threads());

    test_blinker();
    test_block();
    test_glider();

    return 0;
}
