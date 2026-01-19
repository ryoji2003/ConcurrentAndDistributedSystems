import mpi.*;
import java.util.Random;
import java.util.Arrays;

public class Task6_4 {
    static final int BOARD_SIZE = 4;
    static final char EMPTY = '.';
    static final char[] SYMBOLS = {' ', 'X', 'O', '#'};
    static final int HOST = 0;
    static final int MSG_BOARD = 0;
    static final int MSG_SHUTDOWN = 1;

    public static void main(String args[]) throws Exception {
        MPI.Init(args);
        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();

        if (size < 4) {
            if (rank == 0) System.out.println("Need 4 processes (1 host + 3 players).");
            MPI.Finalize();
            return;
        }

        if (rank == HOST) host();
        else player(rank);

        MPI.Finalize();
    }

    static void host() throws MPIException {
        char[][] board = new char[BOARD_SIZE][BOARD_SIZE];
        for (char[] row : board) Arrays.fill(row, EMPTY);

        int currentPlayer = 1;
        boolean gameOver = false;
        int winner = 0;

        while (!gameOver) {
            System.out.println(SYMBOLS[currentPlayer] + " turn:");
            
            int[] header = {MSG_BOARD, currentPlayer};
            char[] flatBoard = flatten(board);
            
            MPI.COMM_WORLD.Bcast(header, 0, 2, MPI.INT, HOST);
            MPI.COMM_WORLD.Bcast(flatBoard, 0, flatBoard.length, MPI.CHAR, HOST);

            int[] move = new int[2];
            MPI.COMM_WORLD.Recv(move, 0, 2, MPI.INT, currentPlayer, 0);

            int r = move[0];
            int c = move[1];
            if (board[r][c] == EMPTY) {
                board[r][c] = SYMBOLS[currentPlayer];
            } else {
                System.out.println("Invalid move received.");
            }

            printBoard(board);

            if (checkWin(board, SYMBOLS[currentPlayer])) {
                gameOver = true;
                winner = currentPlayer;
            } else if (isFull(board)) {
                gameOver = true;
                winner = 0;
            } else {
                currentPlayer = (currentPlayer % 3) + 1;
            }
        }

        if (winner != 0) System.out.println("The winner is: " + SYMBOLS[winner]);
        else System.out.println("Draw.");

        int[] header = {MSG_SHUTDOWN, 0};
        MPI.COMM_WORLD.Bcast(header, 0, 2, MPI.INT, HOST);
        MPI.COMM_WORLD.Bcast(new char[BOARD_SIZE*BOARD_SIZE], 0, BOARD_SIZE*BOARD_SIZE, MPI.CHAR, HOST);
    }

    static void player(int myRank) throws MPIException {
        Random rand = new Random();
        boolean running = true;
        char[] flatBoard = new char[BOARD_SIZE * BOARD_SIZE];
        int[] header = new int[2];

        while (running) {
            MPI.COMM_WORLD.Bcast(header, 0, 2, MPI.INT, HOST);
            MPI.COMM_WORLD.Bcast(flatBoard, 0, flatBoard.length, MPI.CHAR, HOST);

            int command = header[0];
            int turn = header[1];

            if (command == MSG_SHUTDOWN) {
                running = false;
                break;
            }

            if (turn == myRank) {
                int r, c;
                do {
                    r = rand.nextInt(BOARD_SIZE);
                    c = rand.nextInt(BOARD_SIZE);
                } while (flatBoard[r * BOARD_SIZE + c] != EMPTY);

                MPI.COMM_WORLD.Send(new int[]{r, c}, 0, 2, MPI.INT, HOST, 0);
            }
        }
    }

    static boolean checkWin(char[][] b, char sym) {
        for (int i = 0; i < 4; i++) {
            if (checkLine(b[i][0], b[i][1], b[i][2], b[i][3], sym)) return true;
        }
        
        for (int j = 0; j < 4; j++) {
            if (checkLine(b[0][j], b[1][j], b[2][j], b[3][j], sym)) return true;
        }
        
        for(int r=0; r<4; r++) {
            for(int c=0; c<=1; c++) { 
                if(b[r][c]==sym && b[r][c+1]==sym && b[r][c+2]==sym) return true;
            }
        }
        
        for(int c=0; c<4; c++) {
            for(int r=0; r<=1; r++) {
                if(b[r][c]==sym && b[r+1][c]==sym && b[r+2][c]==sym) return true;
            }
        }
        
        for(int r=0; r<=1; r++) {
            for(int c=0; c<=1; c++) {
                if(b[r][c]==sym && b[r+1][c+1]==sym && b[r+2][c+2]==sym) return true;
                if(b[r][c+2]==sym && b[r+1][c+1]==sym && b[r+2][c]==sym) return true;
            }
             for(int c=2; c<=3; c++) {
                 if(b[r][c]==sym && b[r+1][c-1]==sym && b[r+2][c-2]==sym) return true;
             }
        }
        
        return false;
    }

    static boolean checkLine(char c1, char c2, char c3, char c4, char sym) {
        return (c1 == sym && c2 == sym && c3 == sym) || (c2 == sym && c3 == sym && c4 == sym);
    }

    static boolean isFull(char[][] b) {
        for (int i = 0; i < BOARD_SIZE; i++)
            for (int j = 0; j < BOARD_SIZE; j++)
                if (b[i][j] == EMPTY) return false;
        return true;
    }

    static char[] flatten(char[][] b) {
        char[] flat = new char[BOARD_SIZE * BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i++)
            System.arraycopy(b[i], 0, flat, i * BOARD_SIZE, BOARD_SIZE);
        return flat;
    }

    static void printBoard(char[][] b) {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                System.out.print(b[i][j] + " ");
            }
            System.out.println();
        }
    }
}