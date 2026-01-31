import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

class Ex11_1
{
    static AtomicInteger Account = new AtomicInteger(0);
    static Semaphore S = new Semaphore(0);

    static class Spouse implements Runnable
    {
        private int Sum;
        public Spouse(int sum) { Sum = sum; }
                        
        public void run()
        {
            for (int i = 0; i < Sum; i++)
            {
                Account.incrementAndGet();
            }
            
            S.release();
        }
    }
                    
    static public void main(String args[]) 
    {
        Spouse husband = new Spouse(500000);
        Spouse wife = new Spouse(500000);
        
        new Thread(husband).start();
        new Thread(wife).start();
        
        try
        {
            S.acquire();
            S.acquire();
        }
        catch(Exception e){}
     
        System.out.println(Account.get());
    }
}
