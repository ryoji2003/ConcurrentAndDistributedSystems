import mpi.*;

public class Task7_2 {
    public static void main(String args[]) throws Exception {
        MPI.Init(args);
        int rank = MPI.COMM_WORLD.Rank();

        int[] index = {1, 4, 6, 8, 11, 12}; 
        int[] edges = {
            1, 
            0, 2, 4,
            1, 3,
            2, 4,
            1, 3, 5,
            4
        };
        Graphcomm graphComm = MPI.COMM_WORLD.Create_graph(index, edges, true);

        if (rank == 0) {
            System.out.println("Topology defined successfully.");
        }

        MPI.Finalize();
    }
}