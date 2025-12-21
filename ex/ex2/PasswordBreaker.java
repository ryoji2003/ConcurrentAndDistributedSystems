import java.io.File;

public class PasswordBreaker {

    public static volatile boolean found = false;

    public static void main(String[] args) {
        String[] targetFiles = {
            "ex02_longtail.enc",
            "ex02_pride.enc",
            "ex02_scarlet.enc"
        };

        for (String filename : targetFiles) {
            crackFile(filename);
        }
    }

    public static void crackFile(String filename) {
        found = false;
        int numThreads = 4;
        Thread[] threads = new Thread[numThreads];

        for (int i = 0; i < numThreads; i++) {
            threads[i] = new Thread(new BreakerThread(i, numThreads, filename));
            threads[i].start();
        }

        for (int i = 0; i < numThreads; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    static class BreakerThread implements Runnable {
        private int threadId;
        private int totalThreads;
        private String fileName;

        public BreakerThread(int id, int total, String fileName) {
            this.threadId = id;
            this.totalThreads = total;
            this.fileName = fileName;
        }

        @Override
        public void run() {
            for (int length = 1; length <= 5; length++) {
                int maxVal = (int) Math.pow(10, length);

                for (int i = 0; i < maxVal; i++) {
                    if (PasswordBreaker.found) {
                        return;
                    }

                    if (i % totalThreads == threadId) {
                        String passwordAttempt = String.format("%0" + length + "d", i);
                        String outFile = "recovered_" + fileName.replace(".enc", ".txt");
                        
                        boolean success = ex02_fileCrypto.Decrypt(fileName, outFile, passwordAttempt);

                        if (success) {
                            System.out.println(fileName + ": " + passwordAttempt);
                            PasswordBreaker.found = true;
                            return;
                        }
                    }
                }
            }
        }
    }
}