import mpi.*;
import java.util.Random;

public class w06_ticTacToe {
    static final int BOARD_SIZE = 4;
    static final int NUM_PLAYERS = 3;
    static final char[] SYMBOLS = {'.', 'X', 'O', '#'}; // 0=empty, 1=X, 2=O, 3=#

    // Message tags
    static final int TAG_BOARD = 0;
    static final int TAG_MOVE = 1;
    static final int TAG_SHUTDOWN = 2;

    public static void main(String args[]) throws Exception {
        MPI.Init(args);

        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();

        if (size != 4) {
            if (rank == 0) {
                System.out.println("This game requires exactly 4 processes (1 host + 3 players)");
            }
            MPI.Finalize();
            return;
        }

        if (rank == 0) {
            host();
        } else {
            player(rank);
        }

        MPI.Finalize();
    }

    static void host() {
        int[] board = new int[BOARD_SIZE * BOARD_SIZE]; // 0=empty, 1=X, 2=O, 3=#
        int currentPlayer = 1; // Start with player 1 (X)
        int winner = 0;
        int moveCount = 0;

        while (winner == 0 && moveCount < BOARD_SIZE * BOARD_SIZE) {
            // Send board state to current player
            // First element: -1 for shutdown, otherwise player ID
            int[] message = new int[BOARD_SIZE * BOARD_SIZE + 1];
            message[0] = currentPlayer; // Indicator for play request
            System.arraycopy(board, 0, message, 1, BOARD_SIZE * BOARD_SIZE);
            MPI.COMM_WORLD.Send(message, 0, message.length, MPI.INT, currentPlayer, TAG_BOARD);

            // Receive move from player
            int[] move = new int[1]; // Position (0-15)
            MPI.COMM_WORLD.Recv(move, 0, 1, MPI.INT, currentPlayer, TAG_MOVE);

            // Apply move
            board[move[0]] = currentPlayer;
            moveCount++;

            // Print board
            System.out.println(SYMBOLS[currentPlayer] + " turn:");
            printBoard(board);
            System.out.println();

            // Check for winner
            winner = checkWinner(board);

            // Next player
            currentPlayer = (currentPlayer % NUM_PLAYERS) + 1;
        }

        // Send shutdown to all players
        int[] shutdownMsg = new int[BOARD_SIZE * BOARD_SIZE + 1];
        shutdownMsg[0] = -1; // Shutdown signal
        for (int p = 1; p <= NUM_PLAYERS; p++) {
            MPI.COMM_WORLD.Send(shutdownMsg, 0, shutdownMsg.length, MPI.INT, p, TAG_BOARD);
        }

        // Announce result
        if (winner > 0) {
            System.out.println("The winner is: " + SYMBOLS[winner]);
        } else {
            System.out.println("It's a draw!");
        }
    }

    static void player(int rank) {
        Random random = new Random(rank + System.nanoTime());

        while (true) {
            // Receive board state from host
            int[] message = new int[BOARD_SIZE * BOARD_SIZE + 1];
            MPI.COMM_WORLD.Recv(message, 0, message.length, MPI.INT, 0, TAG_BOARD);

            // Check for shutdown
            if (message[0] == -1) {
                break;
            }

            // Extract board
            int[] board = new int[BOARD_SIZE * BOARD_SIZE];
            System.arraycopy(message, 1, board, 0, BOARD_SIZE * BOARD_SIZE);

            // Find an empty cell (random strategy)
            int move = -1;

            // First, try to find a winning move or block opponent
            move = findStrategicMove(board, rank);

            // If no strategic move, pick random empty cell
            if (move == -1) {
                int[] emptyCells = new int[BOARD_SIZE * BOARD_SIZE];
                int emptyCount = 0;
                for (int i = 0; i < BOARD_SIZE * BOARD_SIZE; i++) {
                    if (board[i] == 0) {
                        emptyCells[emptyCount++] = i;
                    }
                }
                if (emptyCount > 0) {
                    move = emptyCells[random.nextInt(emptyCount)];
                }
            }

            // Send move to host
            MPI.COMM_WORLD.Send(new int[]{move}, 0, 1, MPI.INT, 0, TAG_MOVE);
        }
    }

    static int findStrategicMove(int[] board, int mySymbol) {
        // Try to find a winning move
        for (int i = 0; i < BOARD_SIZE * BOARD_SIZE; i++) {
            if (board[i] == 0) {
                board[i] = mySymbol;
                if (checkWinner(board) == mySymbol) {
                    board[i] = 0;
                    return i;
                }
                board[i] = 0;
            }
        }
        return -1;
    }

    static int checkWinner(int[] board) {
        // Check rows
        for (int row = 0; row < BOARD_SIZE; row++) {
            int winner = checkLine(board, row * BOARD_SIZE, 1);
            if (winner > 0) return winner;
        }

        // Check columns
        for (int col = 0; col < BOARD_SIZE; col++) {
            int winner = checkLine(board, col, BOARD_SIZE);
            if (winner > 0) return winner;
        }

        // Check diagonals (main diagonal and anti-diagonal)
        // Main diagonal starting from (0,0)
        int winner = checkLine(board, 0, BOARD_SIZE + 1);
        if (winner > 0) return winner;

        // Anti-diagonal starting from (0,3)
        winner = checkLine(board, BOARD_SIZE - 1, BOARD_SIZE - 1);
        if (winner > 0) return winner;

        // Check shifted diagonals (for 3-in-a-row in 4x4 board)
        // Shifted main diagonals
        winner = checkThreeInLine(board, 1, BOARD_SIZE + 1, 3); // Start at (0,1)
        if (winner > 0) return winner;
        winner = checkThreeInLine(board, BOARD_SIZE, BOARD_SIZE + 1, 3); // Start at (1,0)
        if (winner > 0) return winner;

        // Shifted anti-diagonals
        winner = checkThreeInLine(board, BOARD_SIZE - 2, BOARD_SIZE - 1, 3); // Start at (0,2)
        if (winner > 0) return winner;
        winner = checkThreeInLine(board, 2 * BOARD_SIZE - 1, BOARD_SIZE - 1, 3); // Start at (1,3)
        if (winner > 0) return winner;

        return 0;
    }

    static int checkLine(int[] board, int start, int step) {
        // Check for 3 or 4 in a row
        for (int len = 3; len <= BOARD_SIZE; len++) {
            for (int offset = 0; offset <= BOARD_SIZE - len; offset++) {
                int first = board[start + offset * step];
                if (first == 0) continue;
                boolean win = true;
                for (int i = 1; i < len; i++) {
                    if (board[start + (offset + i) * step] != first) {
                        win = false;
                        break;
                    }
                }
                if (win) return first;
            }
        }
        return 0;
    }

    static int checkThreeInLine(int[] board, int start, int step, int len) {
        int first = board[start];
        if (first == 0) return 0;
        for (int i = 1; i < len; i++) {
            if (board[start + i * step] != first) {
                return 0;
            }
        }
        return first;
    }

    static void printBoard(int[] board) {
        for (int row = 0; row < BOARD_SIZE; row++) {
            StringBuilder sb = new StringBuilder(" ");
            for (int col = 0; col < BOARD_SIZE; col++) {
                sb.append(SYMBOLS[board[row * BOARD_SIZE + col]]);
                if (col < BOARD_SIZE - 1) sb.append(" ");
            }
            System.out.println(sb.toString());
        }
    }
}
