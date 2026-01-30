import java.util.concurrent.*;
import java.util.*;

// class PrimeExec
// convenience function: return true if n is prime

class ex11_executors 
{
	static private boolean isPrime(long n)  
	{	
		for(long i = 2; i <= Math.sqrt(n); ++i)
			if(n % i == 0)
				return false;
			return true;
	}

	static class FindPrimes implements Callable<Vector> 
	{
		private final long from, to;
		
		public FindPrimes(long from_, long to_) { from = from_; to = to_; }

		public Vector call()  
		{
			Vector v = new Vector();
			for(long i = from; i <= to; ++i)
				if(isPrime(i))
					v.add(i);
			return v;        // return found primes
		}
	}

	public static void main(String args[]) throws Exception 
	{
		// list all the tasks to be performed
		FindPrimes[] tasks = new FindPrimes[]{ 	new FindPrimes(3, 100),
												new FindPrimes(500, 1000), 				
												new FindPrimes(5000, 9000)};

		ExecutorService executor = Executors.newFixedThreadPool(4);

		// run all tasks and collect results
		List results = executor.invokeAll(Arrays.asList(tasks));

		// wait until all the tasks are finished
		executor.shutdown();

		for(Future r : (List<Future>)results) 
		{
			Vector v = (Vector)r.get();
			for(Object o : v)
				System.out.print(o + " ");
		}
	}
}