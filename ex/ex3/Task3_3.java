import java.util.*;

class Task3_3 {
    static int P = 4;
    static int[] scores;
    static int[] roundResults;
    
    static class PlayerThread implements Runnable {
        int id;
        
        public PlayerThread(int id) {
            this.id = id;
        }

        public void run() {
            Random rand = new Random();
            roundResults[id] = rand.nextInt(10); 
        }
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            P = Integer.parseInt(args[0]);

        }

        scores = new int[P];
        roundResults = new int[P];
        int roundCount = 1;

        while (true) {
            Thread[] threads = new Thread[P];

            for (int i = 0; i < P; i++) {
                threads[i] = new Thread(new PlayerThread(i));
                threads[i].start();
            }

            try {
                for (int i = 0; i < P; i++) {
                    threads[i].join();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            int maxVal = -1;
            for (int val : roundResults) {
                if (val > maxVal) {
                    maxVal = val;
                }
            }

            System.out.println("round " + roundCount + " winners");
            StringBuilder winnersStr = new StringBuilder();
            
            for (int i = 0; i < P; i++) {
                if (roundResults[i] == maxVal) {
                    scores[i]++; 
                    winnersStr.append((i + 1)).append(" "); 
                }
            }
            System.out.println(winnersStr.toString().trim());
            System.out.println(); 

            boolean gameOver = false;
            for (int s : scores) {
                if (s >= 3) {
                    gameOver = true;
                    break;
                }
            }

            if (gameOver) {
                break;
            }
            
            roundCount++;
        }

        System.out.print("winner(s): ");
        StringBuilder finalWinners = new StringBuilder();
        for (int i = 0; i < P; i++) {
            if (scores[i] >= 3) {
                finalWinners.append((i + 1)).append(" ");
            }
        }
        System.out.println(finalWinners.toString().trim());
    }
}