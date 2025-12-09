import java.util.*;

public class TopologyExperiment {
    static final int NUM_NODE = 17;
    static final int NUM_EXPERIMENTS = 5000;
    static final int DELAY_PER_HOP = 5;
    
    public static void main(String[] args){
        List<List<Integer>> BasicNetwork = buildBasicNetwork();
        List<List<Integer>> AdvancedNetwork = buildAdvancedNetwork();

        double avgBasic = runSimulation(BasicNetwork);
        double avgAdvanced = runSimulation(AdvancedNetwork);

        System.out.printf("basic network: %.0f milliseconds, advanced network: %.0f milliseconds.%n", avgBasic, avgAdvanced);
    }

    private static List<List<Integer>> buildBasicNetwork(){
        List<List<Integer>> graph = new ArrayList<>();
        for (int i = 0; i < NUM_NODE; i++){
            graph.add(new ArrayList<>());
        }

        connect(graph, 0, 1);
        connect(graph, 0, 5);
        connect(graph, 0, 9);
        connect(graph, 0, 13);
        
        int[] armStarts = {1, 5, 9, 13};
        for (int start : armStarts){
            for(int i = 0; i < 3; i++){
                connect(graph, start + i, start + i + 1);
            }
        }
        return graph;
    }

    private static List<List<Integer>> buildAdvancedNetwork(){
        List<List<Integer>> graph = buildBasicNetwork();

        connect(graph, 4, 8);
        connect(graph, 8, 12);
        connect(graph, 12, 16);
        connect(graph, 16, 4);

        return graph;
    }

    private static void connect(List<List<Integer>> graph, int u, int v){
        graph.get(u).add(v);
        graph.get(v).add(u);
    }

    private static double runSimulation(List<List<Integer>> graph){
        long totalTime = 0;
        Random rand = new Random();

        for (int i = 0; i < NUM_EXPERIMENTS; i++){
            int startNode = rand.nextInt(NUM_NODE);
            int endNode = rand.nextInt(NUM_NODE);

            while(startNode == endNode){
                endNode = rand.nextInt(NUM_NODE);
            }

            int hops = getShortestPathBFS(graph, startNode, endNode);

            totalTime += hops * DELAY_PER_HOP;
        }

        return (double) totalTime / NUM_EXPERIMENTS;
    }

    private static int getShortestPathBFS(List<List<Integer>> graph, int start, int target){
        if (start == target) {
            return 0;
        }

        boolean[] visited = new boolean[NUM_NODE];
        int[] distance = new int[NUM_NODE];
        Queue<Integer> queue = new LinkedList<>();

        visited[start] = true;
        queue.add(start);

        while (!queue.isEmpty()) {
            int current = queue.poll();

            if (current == target) {
                return distance[current];
            }

            for (int neighbor : graph.get(current)) {
                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    distance[neighbor] = distance[current] + 1;
                    queue.add(neighbor);
                }
            }
        }
        return -1; 
    }
}