import java.util.*;

public class Ex11_2 {
        static final int SIZE = 5000000; // リストのサイズ
    static final int ITERATIONS = 10; // ループ回数

    public static void main(String[] args) {
        // ArrayList
        ArrayList<Integer> arrayList = new ArrayList<>(Collections.nCopies(SIZE, 0));
        long start = System.nanoTime();
        for (int i = 0; i < SIZE; i++) {
            arrayList.set(i, i);
        }
        long end = System.nanoTime();
        double arrayListTime = (end - start) / 1e6;
        System.out.println("ArrayList Time: " + arrayListTime + " ms");

        // Vector
        Vector<Integer> vector = new Vector<>(Collections.nCopies(SIZE, 0));
        start = System.nanoTime();
        for (int i = 0; i < SIZE; i++) {
            vector.set(i, i);
        }
        end = System.nanoTime();
        double vectorTime = (end - start) / 1e6;
        System.out.println("Vector Time:    " + vectorTime + " ms");

        System.out.printf("ArrayList is %.2f times faster than Vector%n", vectorTime / arrayListTime);
    }
}
