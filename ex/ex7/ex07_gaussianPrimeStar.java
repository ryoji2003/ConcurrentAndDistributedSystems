import mpi.*;

public class ex07_gaussianPrimeStar {

    // Check if a number is a Gaussian prime (prime of form 4k + 3)
    static boolean isGaussianPrime(int n) {
        if (n < 2) return false;
        // Check if n is prime
        for (int i = 2; i * i <= n; i++) {
            if (n % i == 0) return false;
        }
        // Check if n is of form 4k + 3
        return (n % 4 == 3);
    }

    // Count Gaussian primes in interval [left, right)
    static int countGaussianPrimes(int left, int right) {
        int count = 0;
        for (int i = left; i < right; i++) {
            if (isGaussianPrime(i)) {
                count++;
            }
        }
        return count;
    }

    public static void main(String args[]) throws Exception {
        MPI.Init(args);

        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();

        // Define the star topology
        // Node 0: neighbors 1, 2, 3, 4 (center)
        // Node 1: neighbors 0, 5
        // Node 2: neighbors 0, 4
        // Node 3: neighbors 0, 6
        // Node 4: neighbors 0, 2
        // Node 5: neighbor 1 (leaf)
        // Node 6: neighbor 3 (leaf)
        int[] index = {4, 6, 8, 10, 12, 13, 14};
        int[] edges = {1, 2, 3, 4, 0, 5, 0, 4, 0, 6, 0, 2, 1, 3};

        // Create graph topology
        GraphComm graphComm = MPI.COMM_WORLD.Create_graph(index, edges, false);

        int numNeighbors = graphComm.Neighbours_count(rank);
        int[] neighbors = new int[numNeighbors];
        graphComm.Neighbours(rank, neighbors);

        int[] sendBuf = new int[3];  // [left, right, senderId]
        int[] recvBuf = new int[3];
        int[] resultBuf = new int[1];

        if (numNeighbors > 2) {
            // Coordinator node (Node 0): divides [2, M) and sends to all neighbors
            int M = 1000;  // Default value
            if (args.length > 0) {
                M = Integer.parseInt(args[0]);
            }

            int intervalSize = (M - 2) / numNeighbors;
            int totalCount = 0;

            // Send intervals to all neighbors
            for (int i = 0; i < numNeighbors; i++) {
                int left = 2 + i * intervalSize;
                int right = (i == numNeighbors - 1) ? M : (2 + (i + 1) * intervalSize);
                sendBuf[0] = left;
                sendBuf[1] = right;
                sendBuf[2] = rank;
                graphComm.Send(sendBuf, 0, 3, MPI.INT, neighbors[i], 0);
            }

            // Collect results from all neighbors
            for (int i = 0; i < numNeighbors; i++) {
                graphComm.Recv(resultBuf, 0, 1, MPI.INT, MPI.ANY_SOURCE, MPI.ANY_TAG);
                totalCount += resultBuf[0];
            }

            System.out.println("Total Gaussian primes in [2, " + M + "): " + totalCount);

        } else if (numNeighbors == 2) {
            // Node with 2 neighbors: split interval and delegate half
            graphComm.Recv(recvBuf, 0, 3, MPI.INT, MPI.ANY_SOURCE, MPI.ANY_TAG);
            int left = recvBuf[0];
            int right = recvBuf[1];
            int sender = recvBuf[2];

            // Find the other neighbor (not the sender)
            int otherNeighbor = -1;
            for (int n : neighbors) {
                if (n != sender) {
                    otherNeighbor = n;
                    break;
                }
            }

            // Split interval into two halves
            int mid = (left + right) / 2;
            int h1Left = left;
            int h1Right = mid;
            int h2Left = mid;
            int h2Right = right;

            // Send h1 to the other neighbor
            sendBuf[0] = h1Left;
            sendBuf[1] = h1Right;
            sendBuf[2] = rank;
            graphComm.Send(sendBuf, 0, 3, MPI.INT, otherNeighbor, 0);

            // Process h2 locally
            int localCount = countGaussianPrimes(h2Left, h2Right);

            // Receive result from other neighbor
            graphComm.Recv(resultBuf, 0, 1, MPI.INT, otherNeighbor, MPI.ANY_TAG);
            int combinedCount = localCount + resultBuf[0];

            // Send combined result back to sender
            resultBuf[0] = combinedCount;
            graphComm.Send(resultBuf, 0, 1, MPI.INT, sender, 0);

        } else if (numNeighbors == 1) {
            // Leaf node: receive interval, count primes, send result back
            graphComm.Recv(recvBuf, 0, 3, MPI.INT, MPI.ANY_SOURCE, MPI.ANY_TAG);
            int left = recvBuf[0];
            int right = recvBuf[1];
            int sender = recvBuf[2];

            int count = countGaussianPrimes(left, right);

            resultBuf[0] = count;
            graphComm.Send(resultBuf, 0, 1, MPI.INT, sender, 0);
        }

        MPI.Finalize();
    }
}
