import java.util.concurrent.CyclicBarrier;

class AccountType {
    private int amount = 0;

    public int GetValue() {
        return amount;
    }

    public synchronized void AddOneUnit() {
        amount++;
    }
}

class ex03_money {
    static AccountType account = new AccountType();
    static CyclicBarrier barrier = new CyclicBarrier(3);

    static class Spouse implements Runnable {
        private int Sum;
        public Spouse(int sum) { Sum = sum; }
                        
        public void run() {
            for (int i = 0; i < Sum; i++) {
                account.AddOneUnit();
            }
            
            try {
                barrier.await();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
                    
    static public void main(String args[]) {
        Spouse husband = new Spouse(500000);
        Spouse wife = new Spouse(500000);
        
        new Thread(husband).start();
        new Thread(wife).start();
        
        try {
            barrier.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
     
        System.out.println(account.GetValue());
    }
}