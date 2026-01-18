import mpi.*;
import java.util.Random;

public class ex06_skunkWithBugs {
	static final int ROUNDS = 5;
	static final int NEW_ROUND = 0, DECIDE = 1, REC_DICE = 2, SHUTDOWN = 3;
	static final int SITTING = 0, STANDING = 1;

	static int standingPlayers()
	{
		// ask each player's decision and return the number of players still standing
		int recv[] = new int[1];
		MPI.COMM_WORLD.Bcast(new int[]{ DECIDE }, 0, 1, MPI.INT, 0);
		MPI.COMM_WORLD.Reduce(new int[1], 0, recv, 0, 1, MPI.INT, MPI.SUM, 0);
		return recv[0];
	}

	static void host()
	{
		java.util.Random r = new java.util.Random();

		for (int i = 1; i <= ROUNDS; ++i)
		{
			// Announce new round to all players
			System.out.println(String.format("Round %d start.", i));
			MPI.COMM_WORLD.Bcast(new int[]{ NEW_ROUND }, 0, 1, MPI.INT, 0);

			int sp;
			while ((sp = standingPlayers()) > 0)
			{
				int d1 = r.nextInt(6) + 1;
				int d2 = r.nextInt(6) + 1;
				System.out.println(String.format("Active players: %d. Rolling dice: %d %d.", sp, d1, d2));

				// send dice to everyone
				MPI.COMM_WORLD.Bcast(new int[]{ REC_DICE, d1, d2 }, 0, 3, MPI.INT, 0);
			}
		}

		// finalize total sums and request process shutdown
		MPI.COMM_WORLD.Bcast(new int[]{ NEW_ROUND }, 0, 1, MPI.INT, 0);
		MPI.COMM_WORLD.Bcast(new int[]{ SHUTDOWN }, 0, 1, MPI.INT, 0);
		
		// collect final sums and determine the winner 
		int recv[] = new int[MPI.COMM_WORLD.Size()];
		MPI.COMM_WORLD.Gather(new int[1], 0, 1, MPI.INT, recv, 0, 1, MPI.INT, 0);
				
		int max = recv[0], idx = 0;
		for (int i = 0; i < recv.length; ++i)
			if (recv[i] > max)
			{
				max = recv[i];
				idx = i;
			}

		System.out.println(String.format("Winner: %d, Score: %d", idx, max));
	}

	static void player()
	{
		java.util.Random r = new java.util.Random();
		int roundSum = 0, totalSum = 0;
		int lastDecision = STANDING;
		boolean gameOver = false;

		while (!gameOver)
		{
			int recv[] = new int[3];
			MPI.COMM_WORLD.Bcast(recv, 0, 3, MPI.INT, 0);
			
			switch (recv[0])
			{
				case SHUTDOWN: 	gameOver = true;
								break;

				case DECIDE:	// only standing players may decide; 1 out of 5 decisions is "sitting"
								if (lastDecision == STANDING)
									lastDecision = r.nextInt(5) == 0 ? SITTING : STANDING;
								MPI.COMM_WORLD.Reduce(new int[]{ lastDecision }, 0, new int[1], 0, 1, MPI.INT, MPI.SUM, 0);
								System.out.println(String.format("ID: %d, Decision: %d", MPI.COMM_WORLD.Rank(), lastDecision));
								break;

				case NEW_ROUND: totalSum += roundSum;
								roundSum = 0;
								lastDecision = STANDING;
								break;

				case REC_DICE:	// dice are relevant for standing players only
								if (lastDecision == STANDING)
								{
									roundSum += recv[1] + recv[2];

									if (recv[1] == 1 && recv[2] == 1)
										totalSum = 0;
									if (recv[1] == 1 || recv[2] == 1)
									{
										roundSum = 0;
										lastDecision = SITTING;
									}
								}
								System.out.println(String.format("ID: %d, roundSum: %d, totalSum: %d", 
												   MPI.COMM_WORLD.Rank(), roundSum, totalSum));
								break;
			}
		}

		MPI.COMM_WORLD.Gather(new int[]{totalSum}, 0, 1, MPI.INT, new int[MPI.COMM_WORLD.Size()], 0, 1, MPI.INT, 0);
	}

	public static void main(String args[]) throws Exception 
	{
		MPI.Init(args);
		int ID = MPI.COMM_WORLD.Rank(); // proc ID
	
		if(ID == 0)
			host();
		else
			player();

		MPI.Finalize();
	}
}
