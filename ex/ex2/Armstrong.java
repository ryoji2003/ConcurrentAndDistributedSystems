import java.util.Scanner;

public class Armstrong {

    public static void main(String[] args) throws InterruptedException {
        int N;
        int P;

        if (args.length >= 2) {
            N = Integer.parseInt(args[0]);
            P = Integer.parseInt(args[1]);
        } else {
            Scanner scanner = new Scanner(System.in);
            System.out.print("input N : ");
            N = scanner.nextInt();
            System.out.print("input P : ");
            P = scanner.nextInt();
        }

        int startNum = 10;
        int totalNumbers = N - startNum + 1;

        int step = totalNumbers / P;
        int remainder = totalNumbers % P;

        Thread[] threads = new Thread[P];
        int currentStart = startNum;

        for (int i = 0; i < P; i++) {
            int rangeSize = step + (i < remainder ? 1 : 0);
            int currentEnd = currentStart + rangeSize - 1;

            if (rangeSize > 0) {
                threads[i] = new Thread(new ArmstrongWorker(currentStart, currentEnd));
                threads[i].start();
            }

            currentStart += rangeSize;
        }

        for (int i = 0; i < P; i++) {
            if (threads[i] != null) {
                threads[i].join();
            }
        }
        
        System.out.println();
    }

    public static synchronized void printArmstrong(int number) {
        System.out.print(number + " ");
    }

    static class ArmstrongWorker implements Runnable {
        private int start;
        private int end;

        public ArmstrongWorker(int start, int end) {
            this.start = start;
            this.end = end;
        }

        @Override
        public void run() {
            for (int n = start; n <= end; n++) {
                if (isArmstrong(n)) {
                    Armstrong.printArmstrong(n);
                }
            }
        }

        private boolean isArmstrong(int num) {
            String s = Integer.toString(num);
            int m = s.length();
            int sum = 0;
            int temp = num;

            while (temp > 0) {
                int digit = temp % 10;
                sum += Math.pow(digit, m);
                temp /= 10;
            }

            return sum == num;
        }
    }
}