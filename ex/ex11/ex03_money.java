import java.util.concurrent.Semaphore;

class ex03_money
{
    static int Account = 0;
    static Semaphore S = new Semaphore(0);
    static Semaphore M = new Semaphore(1);

    static class Spouse implements Runnable
    {
        private int Sum;
        public Spouse(int sum) { Sum = sum; }
                        
        public void run()
        {
            for (int i = 0; i < Sum; i++)
            {
                try { M.acquire(); } catch(Exception e){}
                    
                Account++;
                
                M.release();
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
     
        System.out.println(Account);
    }
}
