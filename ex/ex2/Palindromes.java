import java.util.*;
import java.io.*;

class Global {
    public static Vector<String> palindromes = new Vector<String>();
}

class MyThread implements Runnable {
    private String filename;

    public MyThread(String filename) {
        this.filename = filename;
    }

    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
            
            while ((line = reader.readLine()) != null) {
                String revline = (new StringBuilder(line)).reverse().toString();
                //
                if (line.length() > 0 && line.equals(revline) && !Global.palindromes.contains(line)) {
                    System.out.println(line);
                    Global.palindromes.add(line);
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

public class Palindromes {
    static public void main(final String args[]) {
        new Thread(new MyThread("words1.txt")).start();
        new Thread(new MyThread("words2.txt")).start();
    }
}
