import java.util.concurrent.*;

class Ex11_4 {
    static int N = 20;
    static int STEPS = 10;
    static int[][] board = new int[N][N];
    static int[][] nextBoard = new int[N][N];
    static int THRESHOLD = 5; // K value [cite: 75]

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
                int nr = (r + i + N) % N;
                int nc = (c + j + N) % N;
                if (board[nr][nc] == 1) count++;
            }
        }
        return count;
    }

    static class LifeAction extends RecursiveAction {
        int r1, c1, r2, c2; // Rectangle coordinates

        LifeAction(int r1, int c1, int r2, int c2) {
            this.r1 = r1; this.c1 = c1;
            this.r2 = r2; this.c2 = c2;
        }

        protected void compute() {
            int rows = r2 - r1;
            int cols = c2 - c1;

            if (rows <= THRESHOLD && cols <= THRESHOLD) {
                // Compute directly [cite: 81]
                for (int i = r1; i < r2; i++) {
                    for (int j = c1; j < c2; j++) {
                        int neighbors = countNeighbors(i, j);
                        if (board[i][j] == 1) {
                            if (neighbors == 2 || neighbors == 3) nextBoard[i][j] = 1;
                            else nextBoard[i][j] = 0;
                        } else {
                            if (neighbors == 3) nextBoard[i][j] = 1;
                            else nextBoard[i][j] = 0;
                        }
                    }
                }
            } else {
                // Split logic [cite: 82, 87]
                if (rows > cols) {
                    // Split horizontally (top/bottom)
                    int mid = r1 + (rows / 2);
                    invokeAll(new LifeAction(r1, c1, mid, c2), 
                              new LifeAction(mid, c1, r2, c2));
                } else {
                    // Split vertically (left/right)
                    int mid = c1 + (cols / 2);
                    invokeAll(new LifeAction(r1, c1, r2, mid), 
                              new LifeAction(r1, mid, r2, c2));
                }
            }
        }
    }

    public static void main(String[] args) {
        initBoard();
        printBoard(0);
        ForkJoinPool pool = new ForkJoinPool();

        for (int s = 1; s <= STEPS; s++) {
            // Create a task covering the whole board
            LifeAction task = new LifeAction(0, 0, N, N);
            pool.invoke(task);

            // Swap boards
            for (int i = 0; i < N; i++) {
                System.arraycopy(nextBoard[i], 0, board[i], 0, N);
            }
            printBoard(s);
        }
    }
}