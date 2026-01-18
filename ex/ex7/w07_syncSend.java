import mpi.*;

public class w07_syncSend {
	public static void main(String args[]) throws Exception{
		MPI.Init(args);
		
		java.util.Random r = new java.util.Random();
		int N = 1000000;
		int IT = 10;
		
		if(MPI.COMM_WORLD.Rank() == 0)
		{
			long startTime = System.currentTimeMillis();
			for(int i = 0; i < IT; ++i)
			{
				int arr[] = new int[N];
				for(int k = 0; k < N; ++k)
					arr[k] = r.nextInt(500);
				
				MPI.COMM_WORLD.Send(arr, 0, N, MPI.INT, 1, 0);
				MPI.COMM_WORLD.Recv(arr, 0, N, MPI.INT, MPI.ANY_SOURCE, MPI.ANY_TAG);
			}
			
			System.out.println("msec per array: " + (System.currentTimeMillis() - startTime) / IT);
				
		}
		else
		{	
			
			//Thread.sleep(MPI.COMM_WORLD.Rank() * 500);
			for(int i = 0; i < IT; ++i)
			{
				int arr[] = new int[N];
				MPI.COMM_WORLD.Recv(arr, 0, N, MPI.INT, MPI.ANY_SOURCE, MPI.ANY_TAG);
				java.util.Arrays.sort(arr);
				MPI.COMM_WORLD.Send(arr, 0, N, MPI.INT, 0, 0);
			}
		}
		
			
		MPI.Finalize();
	}
}
