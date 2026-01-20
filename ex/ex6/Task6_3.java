import mpi.*;
import java.util.Random;

public class Task6_3 {
    public static void main(String args[]) throws Exception {
        MPI.Init(args);
        
        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();
        
        long N = 0;
        int r = 0;

        if (args.length >= 5) {
            N = Long.parseLong(args[3]); 
            r = Integer.parseInt(args[4]); 
        } else {
            if (rank == 0) {
                 System.out.println("Usage: mpjrun ... Task6_3 <N> <r>");
                 System.out.println("Received args length: " + args.length);
            }
            MPI.Finalize();
            return;
        }

        // 各プロセスの担当回数
        long myN = N / size;
        long hits = 0;
        
        Random rand = new Random();
        
        for (long i = 0; i < myN; i++) {
            double x = (rand.nextDouble() * 2 * r) - r; // -r to r
            double y = (rand.nextDouble() * 2 * r) - r; // -r to r
            
            if (x * x + y * y <= (double)r * r) {
                hits++;
            }
        }

        long[] totalHits = new long[1];
        MPI.COMM_WORLD.Reduce(new long[]{hits}, 0, totalHits, 0, 1, MPI.LONG, MPI.SUM, 0);

        if (rank == 0) {
            double pi = 4.0 * ((double)totalHits[0] / N);
            System.out.println("Pi=" + pi);
        }

        MPI.Finalize();
    }
}