import mpi.*;
import java.util.Random;

public class  ex06_skunkWithBugs{ 
    static final int ROUNDS = 5;
    static final int NEW_ROUND = 0, DECIDE = 1, REC_DICE = 2, SHUTDOWN = 3;
    static final int SITTING = 0, STANDING = 1;

    static int standingPlayers() throws MPIException {
        int recv[] = new int[1];
        MPI.COMM_WORLD.Bcast(new int[]{ DECIDE }, 0, 1, MPI.INT, 0);
        MPI.COMM_WORLD.Reduce(new int[1], 0, recv, 0, 1, MPI.INT, MPI.SUM, 0);
        return recv[0];
    }

    static void host() throws MPIException {
        java.util.Random r = new java.util.Random();

        for (int i = 1; i <= ROUNDS; ++i) {
            System.out.println(String.format("Round %d start.", i));
            MPI.COMM_WORLD.Bcast(new int[]{ NEW_ROUND }, 0, 1, MPI.INT, 0);

            int sp;
            while ((sp = standingPlayers()) > 0) {
                int d1 = r.nextInt(6) + 1;
                int d2 = r.nextInt(6) + 1;
                System.out.println(String.format("Active players: %d. Rolling dice: %d %d.", sp, d1, d2));

                MPI.COMM_WORLD.Bcast(new int[]{ REC_DICE, d1, d2 }, 0, 3, MPI.INT, 0);
            }
        }

        MPI.COMM_WORLD.Bcast(new int[]{ NEW_ROUND }, 0, 1, MPI.INT, 0);
        MPI.COMM_WORLD.Bcast(new int[]{ SHUTDOWN }, 0, 1, MPI.INT, 0);
        
        // collect final sums
        int recv[] = new int[MPI.COMM_WORLD.Size()];
        MPI.COMM_WORLD.Gather(new int[1], 0, 1, MPI.INT, recv, 0, 1, MPI.INT, 0);
                
        int max = recv[0], idx = 0;

        for (int i = 1; i < recv.length; ++i) 
            if (recv[i] > max) {
                max = recv[i];
                idx = i;
            }

        System.out.println(String.format("Winner: %d, Score: %d", idx, max));
    }

    static void player() throws MPIException {
        java.util.Random r = new java.util.Random();
        int roundSum = 0, totalSum = 0;
        int lastDecision = STANDING;
        boolean gameOver = false;

        while (!gameOver) {
            int recv[] = new int[3];
            MPI.COMM_WORLD.Bcast(recv, 0, 3, MPI.INT, 0);
            
            switch (recv[0]) {
                case SHUTDOWN: 
                    gameOver = true;
                    break;

                case DECIDE:    
                    if (lastDecision == STANDING)
                        lastDecision = r.nextInt(5) == 0 ? SITTING : STANDING;
                    // 立っているなら1、座っているなら0を送る
                    MPI.COMM_WORLD.Reduce(new int[]{ lastDecision }, 0, new int[1], 0, 1, MPI.INT, MPI.SUM, 0);
                    break;

                case NEW_ROUND: 
                    totalSum += roundSum;
                    roundSum = 0;
                    lastDecision = STANDING;
                    break;

                case REC_DICE:  
                    if (lastDecision == STANDING) {
                        int val = recv[1] + recv[2];
                        if (recv[1] == 1 && recv[2] == 1) {
                            totalSum = 0;
                            roundSum = 0;
                            lastDecision = SITTING;
                        } else if (recv[1] == 1 || recv[2] == 1) {
                            roundSum = 0;
                            lastDecision = SITTING;
                        } else {
                            roundSum += val;
                        }
                    }
                    break;
            }
        }

        MPI.COMM_WORLD.Gather(new int[]{totalSum}, 0, 1, MPI.INT, new int[MPI.COMM_WORLD.Size()], 0, 1, MPI.INT, 0);
    }

    public static void main(String args[]) throws Exception {
        MPI.Init(args);
        int ID = MPI.COMM_WORLD.Rank();
        if(ID == 0) host();
        else player();
        MPI.Finalize();
    }
}