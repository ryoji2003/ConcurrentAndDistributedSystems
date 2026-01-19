import mpi.*;

public class Task7_3 {
    static boolean isGaussianPrime(int n) {
        if (n <= 2) return false;
        if (n % 4 != 3) return false;
        
        for (int i = 2; i * i <= n; i++) {
            if (n % i == 0) return false;
        }
        return true;
    }

    public static void main(String args[]) throws Exception {
        MPI.Init(args);
        
        int M = (args.length > 3) ? Integer.parseInt(args[3]) : 1000;
        
        int[] index = {3, 5, 7, 9, 10, 11, 12};
        int[] edges = {1, 2, 3, 0, 5, 0, 4, 0, 6, 2, 1, 3};
        Graphcomm comm = MPI.COMM_WORLD.Create_graph(index, edges, true);
        
        int rank = comm.Rank();
        int neighborsCount = comm.Graph_neighbors_count(rank);
        int[] neighbors = comm.Graph_neighbors(rank);

        int TAG_JOB = 1;
        int TAG_RESULT = 2;

        int[] range = new int[2];
        int totalPrimes = 0;

        if (neighborsCount > 2) {
            int start = 2;
            int end = M;
            int len = end - start;
            int part = len / neighborsCount;

            for (int i = 0; i < neighborsCount; i++) {
                int[] subRange = new int[2];
                subRange[0] = start + i * part;
                subRange[1] = (i == neighborsCount - 1) ? end : (start + (i + 1) * part);
                comm.Send(subRange, 0, 2, MPI.INT, neighbors[i], TAG_JOB);
            }

            for (int i = 0; i < neighborsCount; i++) {
                int[] result = new int[1];
                comm.Recv(result, 0, 1, MPI.INT, MPI.ANY_SOURCE, TAG_RESULT);
                totalPrimes += result[0];
            }
            System.out.println("Total Gaussian Primes found: " + totalPrimes);
        }
        else if (neighborsCount == 2) {
            Status status = comm.Recv(range, 0, 2, MPI.INT, MPI.ANY_SOURCE, TAG_JOB);
            int sender = status.source;
            int otherNeighbor = (neighbors[0] == sender) ? neighbors[1] : neighbors[0];

            int mid = range[0] + (range[1] - range[0]) / 2;
            int[] subRange = {range[0], mid};
            int[] myRange = {mid, range[1]};

            comm.Send(subRange, 0, 2, MPI.INT, otherNeighbor, TAG_JOB);

            int myCount = 0;
            for (int k = myRange[0]; k < myRange[1]; k++) {
                if (isGaussianPrime(k)) myCount++;
            }

            int[] childResult = new int[1];
            comm.Recv(childResult, 0, 1, MPI.INT, otherNeighbor, TAG_RESULT);

            int[] total = {myCount + childResult[0]};
            comm.Send(total, 0, 1, MPI.INT, sender, TAG_RESULT);
        }
        else if (neighborsCount == 1) {
            Status status = comm.Recv(range, 0, 2, MPI.INT, MPI.ANY_SOURCE, TAG_JOB);
            int sender = status.source;

            int count = 0;
            for (int k = range[0]; k < range[1]; k++) {
                if (isGaussianPrime(k)) count++;
            }

            int[] res = {count};
            comm.Send(res, 0, 1, MPI.INT, sender, TAG_RESULT);
        }

        MPI.Finalize();
    }
}