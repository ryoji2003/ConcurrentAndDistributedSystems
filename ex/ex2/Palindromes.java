import java.util.*;
import java.io.*;

// 全スレッドで共有するデータを保持するクラス
class Global {
    // Vectorはスレッドセーフ（同期化されている）ですが、
    // check-then-act（確認してから操作）の複合操作までは守られません。
    public static Vector<String> palindromes = new Vector<String>();
}

// ファイルを読み込んで回文を探すスレッド
class MyThread implements Runnable {
    private String filename;

    public MyThread(String filename) {
        this.filename = filename;
    }

    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
            
            // ファイルを1行ずつ読み込む
            while ((line = reader.readLine()) != null) {
                // 行を反転させる
                String revline = (new StringBuilder(line)).reverse().toString();

                // 回文であり、かつリストにまだ含まれていない場合
                // ★ここに競合状態(Race Condition)の問題があります★
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

// メインクラス
public class Palindromes {
    static public void main(final String args[]) {
        // 2つのスレッドを作成して開始
        new Thread(new MyThread("words1.txt")).start();
        new Thread(new MyThread("words2.txt")).start();
    }
}
