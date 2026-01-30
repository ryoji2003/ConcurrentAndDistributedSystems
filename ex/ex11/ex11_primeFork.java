import java.util.concurrent.*;
import java.util.*;

class ex11_primeFork 
{
	static private boolean isPrime(long n)  
	{
		for(long i = 2; i <= Math.sqrt(n); ++i)
			if(n % i == 0)
				return false;
		return true;
   }
   
	static class FindPrimes extends RecursiveAction 
	{
		private final long from, to;
		private Vector results;
		
		public FindPrimes(long from_, long to_) 
		{ 
			from = from_; 
			to = to_; 
			results = new Vector(); 
		}
		
		public Vector Results() { return results; }
  
		// the main method should be named compute()
		protected void compute() 
		{ 
			if(to - from + 1 < 10) 
				solveSmall();
			else
				solveBig();
		}

		private void solveSmall() 
		{
			for(long i = from; i <= to; ++i)		
				if(isPrime(i))    
					results.add(i);
		}

		private void solveBig() 
		{
			long midpoint = from + (to - from) / 2;  
			FindPrimes fp1 = new FindPrimes(from, midpoint); 
			FindPrimes fp2 = new FindPrimes(midpoint + 1, to);  

			invokeAll(fp1, fp2); // solve both problems
			results.addAll(fp1.Results()); // combine the
			results.addAll(fp2.Results()); // results
		}
	} 

	public static void main(String args[])
	{
		FindPrimes p = new FindPrimes(1, 1000);
		ForkJoinPool pool = new ForkJoinPool();
		pool.invoke(p);
	
		for(Object o : p.Results())
		System.out.print(o + " ");
	}  
}
