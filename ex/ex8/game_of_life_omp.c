#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <omp.h>

#define ROWS 6
#define COLS 6

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

int count_neighbors(int board[ROWS][COLS], int row, int col) {
    int count = 0;

    for (int di = -1; di <= 1; di++) {
        for (int dj = -1; dj <= 1; dj++) {
            if (di == 0 && dj == 0) continue;

            int ni = (row + di + ROWS) % ROWS;
            int nj = (col + dj + COLS) % COLS;

            count += board[ni][nj];
        }
    }

    return count;
}

void next_generation(int current[ROWS][COLS], int next[ROWS][COLS]) {
    #pragma omp parallel for collapse(2)
    for (int i = 0; i < ROWS; i++) {
        for (int j = 0; j < COLS; j++) {
            int neighbors = count_neighbors(current, i, j);

            if (current[i][j]) {
                next[i][j] = (neighbors == 2 || neighbors == 3) ? 1 : 0;
            } else {
                next[i][j] = (neighbors == 3) ? 1 : 0;
            }
        }
    }
}

void copy_board(int dest[ROWS][COLS], int src[ROWS][COLS]) {
    #pragma omp parallel for collapse(2)
    for (int i = 0; i < ROWS; i++) {
        for (int j = 0; j < COLS; j++) {
            dest[i][j] = src[i][j];
        }
    }
}

void clear_board(int board[ROWS][COLS]) {
    memset(board, 0, sizeof(int) * ROWS * COLS);
}

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

void test_blinker() {
    int board[ROWS][COLS];
    clear_board(board);

    board[2][1] = 1;
    board[2][2] = 1;
    board[2][3] = 1;

    run_simulation(board, 4, "BLINKER");
}

void test_block() {
    int board[ROWS][COLS];
    clear_board(board);

    board[1][1] = 1;
    board[1][2] = 1;
    board[2][1] = 1;
    board[2][2] = 1;

    run_simulation(board, 3, "BLOCK");
}

void test_glider() {
    int board[ROWS][COLS];
    clear_board(board);

    board[0][1] = 1;
    board[1][2] = 1;
    board[2][0] = 1;
    board[2][1] = 1;
    board[2][2] = 1;

    run_simulation(board, 20, "GLIDER");
}

int main() {
    printf("Conway's Game of Life with OpenMP\n");
    printf("Board size: %dx%d (toroidal)\n", ROWS, COLS);
    printf("Number of threads: %d\n\n", omp_get_max_threads());

    test_blinker();
    test_block();
    test_glider();

    return 0;
}
