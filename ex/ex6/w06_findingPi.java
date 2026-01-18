import mpi.*;
import java.util.Random;

public class w06_findingPi {
    public static void main(String args[]) throws Exception {
        MPI.Init(args);

        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();

        // Parse command line arguments: N (total throws) and r (radius)
        // args[0], args[1], args[2] are MPI arguments, user args start at args[3]
        long N = 10000000; // default total throws
        double r = 10000;  // default radius

        if (args.length > 3) {
            N = Long.parseLong(args[3]);
        }
        if (args.length > 4) {
            r = Double.parseDouble(args[4]);
        }

        // Each process makes N/P throws
        long throwsPerProcess = N / size;
        // Handle remainder: first processes get one extra throw
        if (rank < N % size) {
            throwsPerProcess++;
        }

        Random random = new Random(rank + System.nanoTime());
        long hitsInsideCircle = 0;

        // Generate random points in square [-r, r] x [-r, r]
        // Count how many fall inside circle of radius r
        for (long i = 0; i < throwsPerProcess; i++) {
            // Random point in square: x in [-r, r], y in [-r, r]
            double x = (random.nextDouble() * 2 - 1) * r;
            double y = (random.nextDouble() * 2 - 1) * r;

            // Check if inside circle: x^2 + y^2 <= r^2
            if (x * x + y * y <= r * r) {
                hitsInsideCircle++;
            }
        }

        // Reduce to sum all hits
        long[] localHits = new long[]{hitsInsideCircle};
        long[] totalHits = new long[1];
        MPI.COMM_WORLD.Reduce(localHits, 0, totalHits, 0, 1, MPI.LONG, MPI.SUM, 0);

        // Root process calculates and prints Pi
        if (rank == 0) {
            // Pi = 4 * N0 / N
            // Area of circle = pi * r^2
            // Area of square = (2r)^2 = 4r^2
            // Ratio = pi * r^2 / 4r^2 = pi / 4
            // So N0 / N ≈ pi / 4, therefore pi ≈ 4 * N0 / N
            double pi = 4.0 * totalHits[0] / N;
            System.out.println("Pi = " + pi);
        }

        MPI.Finalize();
    }
}
