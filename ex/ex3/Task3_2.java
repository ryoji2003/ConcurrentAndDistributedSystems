import java.util.*;

class Task3_2 {
    static final int N = 100000;
    static int[] A = new int[N];
    static final int NUM_THREADS = 4;

    static class MaxThread implements Runnable {
        int left, right;
        int max = Integer.MIN_VALUE;

        public MaxThread(int left, int right) {
            this.left = left;
            this.right = right;
        }

        @Override
        public void run() {
            for (int i = left; i < right; i++) {
                if (A[i] > max) {
                    max = A[i];
                }
            }
        }

        public int getMax() {
            return max;
        }
    }

    public static void main(String[] args) {
        Random rand = new Random();
        for (int i = 0; i < N; ++i) {
            A[i] = rand.nextInt(10000);
        }

        Thread[] threads = new Thread[NUM_THREADS];
        MaxThread[] workers = new MaxThread[NUM_THREADS];

        int segmentSize = N / NUM_THREADS;

        for (int i = 0; i < NUM_THREADS; i++) {
            int left = i * segmentSize;
            int right = (i + 1) * segmentSize;
            
            if (i == NUM_THREADS - 1) {
                right = N;
            }

            workers[i] = new MaxThread(left, right);
            
            threads[i] = new Thread(workers[i]);
            threads[i].start();
        }

        try {
            for (int i = 0; i < NUM_THREADS; i++) {
                threads[i].join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int globalMax = Integer.MIN_VALUE;
        for (int i = 0; i < NUM_THREADS; i++) {
            int localMax = workers[i].getMax();
            System.out.println("Thread " + i + " max: " + localMax);
            
            if (localMax > globalMax) {
                globalMax = localMax;
            }
        }
        System.out.println("Global max: " + globalMax);
    }
}