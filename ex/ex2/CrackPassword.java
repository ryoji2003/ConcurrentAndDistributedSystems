import java.io.*;

public class CrackPassword {
    public static volatile boolean found = false;

    public static void main(String[] args) {
        String[] targetFiles = { "ex02_longtail.enc", "ex02_pride.enc", "ex02_scarlet.enc" };
        
        int maxPassword = 99999;
        int threadCount = 4;

        for (String fileName : targetFiles) {
            System.out.println("Target File: " + fileName);
            
            found = false;
            
            Thread[] threads = new Thread[threadCount];
            int rangeSize = (maxPassword + 1) / threadCount;

            for (int i = 0; i < threadCount; i++) {
                int start = i * rangeSize;
                int end = (i == threadCount - 1) ? maxPassword : (start + rangeSize - 1);
                
                threads[i] = new Thread(new CrackerRunnable(start, end, fileName));
                threads[i].start();
            }

            for (int i = 0; i < threadCount; i++) {
                try {
                    threads[i].join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            
            if (!found) {
                System.out.println("Result: Password NOT found.");
            }
        }
    }
}

class CrackerRunnable implements Runnable {
    private int startRange;
    private int endRange;
    private String targetFile;

    public CrackerRunnable(int start, int end, String targetFile) {
        this.startRange = start;
        this.endRange = end;
        this.targetFile = targetFile;
    }

    @Override
    public void run() {
        for (int i = startRange; i <= endRange; i++) {
            if (CrackPassword.found) return;

            String passwordAttempt = String.valueOf(i);
            String outFileName = "decrypted_" + targetFile.replace(".enc", ".txt");

            // 修正箇所: try-catch で囲みました
            try {
                boolean result = ex02_fileCrypto.Decrypt(
                    targetFile, 
                    outFileName, 
                    passwordAttempt
                );

                if (result) {
                    CrackPassword.found = true;
                    System.out.println(">>> SUCCESS! Password for [" + targetFile + "] is: " + passwordAttempt);
                    return;
                }
            } catch (Exception e) {
                // パスワード違いなどで例外が出た場合は無視して次へ進む
            }
        }
    }
}