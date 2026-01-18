import mpi.*;

public class w07_asyncSend {
	public static void main(String args[]) throws Exception{
		MPI.Init(args);
		
		java.util.Random r = new java.util.Random();
		int N = 1000000;
		int IT = 10;
		
		if(MPI.COMM_WORLD.Rank() == 0)
		{
			long startTime = System.currentTimeMillis();
			
			int arr[] = new int[N];
			
			for(int k = 0; k < N; ++k)
				arr[k] = r.nextInt(500);
			
			MPI.COMM_WORLD.Send(arr, 0, N, MPI.INT, 1, 0);

			for(int i = 0; i < IT - 1; ++i)
			{
				for(int k = 0; k < N; ++k)
					arr[k] = r.nextInt(500);
				
				MPI.COMM_WORLD.Send(arr, 0, N, MPI.INT, 1, 0);
				MPI.COMM_WORLD.Recv(arr, 0, N, MPI.INT, MPI.ANY_SOURCE, MPI.ANY_TAG);
			}
			
			MPI.COMM_WORLD.Recv(arr, 0, N, MPI.INT, MPI.ANY_SOURCE, MPI.ANY_TAG);
			
			System.out.println("msec per array: " + (System.currentTimeMillis() - startTime) / IT);
				
		}
		else
		{	
			Request curRq, nextRq;
			int curArr[] = new int[N], nextArr[] = new int[N];
			curRq = MPI.COMM_WORLD.Irecv(curArr, 0, N, MPI.INT, MPI.ANY_SOURCE, MPI.ANY_TAG);
			
			for(int i = 0; i < IT - 1; ++i)
			{
				curRq.Wait();
				nextRq =  MPI.COMM_WORLD.Irecv(nextArr, 0, N, MPI.INT, MPI.ANY_SOURCE, MPI.ANY_TAG);
				
				java.util.Arrays.sort(curArr);
				
				MPI.COMM_WORLD.Send(curArr, 0, N, MPI.INT, 0, 0);
	
				curArr = nextArr;
				curRq = nextRq;
			}
			
			MPI.COMM_WORLD.Send(curArr, 0, N, MPI.INT, 0, 0);
		}
			
		MPI.Finalize();
	}
}
