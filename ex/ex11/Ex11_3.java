import java.util.concurrent.*;
import java.util.*;

class Ex11_3 {
    static int N = 20; // Board size
    static int STEPS = 10;
    static int[][] board = new int[N][N];
    static int[][] nextBoard = new int[N][N];
    static int THREADS = Runtime.getRuntime().availableProcessors(); // [cite: 76]

    // Initialize with a glider for testing [cite: 58]
    static void initBoard() {
        // Glider pattern
        board[1][2] = 1;
        board[2][3] = 1;
        board[3][1] = 1;
        board[3][2] = 1;
        board[3][3] = 1;
    }

    static void printBoard(int step) {
        System.out.println("Step: " + step);
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                System.out.print(board[i][j] == 1 ? "■ " : "□ ");
            }
            System.out.println();
        }
        System.out.println();
    }

    static int countNeighbors(int r, int c) {
        int count = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue;
                // Toroidal math (donut shape) [cite: 48]
                int nr = (r + i + N) % N;
                int nc = (c + j + N) % N;
                if (board[nr][nc] == 1) count++;
            }
        }
        return count;
    }

    static class LifeTask implements Callable<Void> {
        int startRow, endRow;

        public LifeTask(int startRow, int endRow) {
            this.startRow = startRow;
            this.endRow = endRow;
        }

        public Void call() {
            for (int i = startRow; i < endRow; i++) {
                for (int j = 0; j < N; j++) {
                    int neighbors = countNeighbors(i, j);
                    // Apply rules [cite: 39, 40]
                    if (board[i][j] == 1) {
                        if (neighbors == 2 || neighbors == 3) nextBoard[i][j] = 1;
                        else nextBoard[i][j] = 0;
                    } else {
                        if (neighbors == 3) nextBoard[i][j] = 1;
                        else nextBoard[i][j] = 0;
                    }
                }
            }
            return null;
        }
    }

    public static void main(String[] args) throws Exception {
        initBoard();
        printBoard(0);

        ExecutorService executor = Executors.newFixedThreadPool(THREADS);

        for (int s = 1; s <= STEPS; s++) {
            List<LifeTask> tasks = new ArrayList<>();
            // Divide board into horizontal strips for threads
            int chunkSize = N / THREADS;
            if (chunkSize == 0) chunkSize = 1; 
            
            for (int i = 0; i < N; i += chunkSize) {
                int end = Math.min(i + chunkSize, N);
                tasks.add(new LifeTask(i, end));
            }

            // Execute all tasks for this generation
            executor.invokeAll(tasks);

            // Swap boards
            for (int i = 0; i < N; i++) {
                System.arraycopy(nextBoard[i], 0, board[i], 0, N);
            }
            printBoard(s);
        }
        executor.shutdown();
    }
}