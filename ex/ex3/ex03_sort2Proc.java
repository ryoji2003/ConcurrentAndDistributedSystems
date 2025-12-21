import java.util.*;
import java.util.concurrent.CountDownLatch;


class ex03_sort2Proc{
    static final int N = 100000;
    static int A[] = new int[N];
    static CountDownLatch L = new CountDownLatch(2);
    
    static class SortThread implements Runnable
    {
        int Left, Right;
        
        public SortThread(int left, int right) 
        { 
            Left = left; Right = right;   
        }
                        
        public void run()
        {
            Arrays.sort(A, Left, Right);
            L.countDown();

        }
    }
                    
    static public void main(String args[]) 
    {
        Random rand = new Random();
        for(int i = 0; i < N; ++i)
            A[i] = rand.nextInt(1000);
        
        new Thread(new SortThread(0, N / 2)).start();
        new Thread(new SortThread(N / 2, N)).start();
        
        try
        {
            L.await();
        }
        catch(Exception e){}

        merge(0, N/2, N);
        
        System.out.println(Arrays.toString(Arrays.copyOfRange(A, 0, N)));
    }

    static void merge(int left, int mid, int right){
        int[] temp = new int[right - left];
        int i = left;
        int j = mid;
        int k = 0;

        while(i < mid && j < right){
            if(A[i] <= A[j]){
                temp[k++] = A[i++];
            } else {
                temp[k++] = A[j++];
            }
        }

        while (i < mid){
            temp[k++] = A[i++];
        }
        while (j < right){
            temp[k++] = A[j++];
        }
        System.arraycopy(temp, 0, A, left, temp.length);
    }
}