import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.*;

public class main {

    /**
     * User updated variables -- Eventually put these in all file for configuration from there
     *      Graph g: Update file path for .csv file
     *      numAgents: Update for number of agents
     *      agentSpeed: Update for speed of agent. Must be no more than 1 decimal place for now
     *      agentNames:
     *
     * @param args d
     * @throws IOException d
     */
    public static void main(String[] args) throws IOException {
        // Changeable variables
        String graphFile = "src/main/java/graphs/simGraph.csv";
        String dataFile = "src/main/java/dataV2.csv";
        int numAgents = 8;
        int numRuns = 10_000;
        double agentSpeed = 0.2;
        boolean randStart = true;

        Graph graph = new Graph(graphFile);
        ArrayList<String> algorithms = new ArrayList<>();
        ArrayList<String> agentNames = new ArrayList<>();
        ArrayList<Integer> startingNodes = new ArrayList<>();

        algorithms.add("Bayes");
        algorithms.add("Entropy");

        // Setup Graph
        graph.initGraph();


        if(randStart) {
            // Generate random start nodes
            Random rand = new Random();
            // Between 0 - numAgents
            for (int i = 0; i < numAgents; i++) {
                startingNodes.add(rand.nextInt(graph.numNodes));
                agentNames.add(String.valueOf(i));
            }
        }
        else{
            for (int i = 0; i < numAgents; i++) {
                startingNodes.add(0);
                agentNames.add(String.valueOf(i));
            }
        }

        // Agent list
        ArrayList<Agent> agentList = new ArrayList<>();
//        Queue<Agent> agentList = new LinkedList<>();

        // Initialize agents
        for(int i = 0; i < numAgents; i++){
            // Get start node: start off all at same node
            Node startNode = graph.graph.get(startingNodes.get(0));
            agentList.add(new Agent(agentNames.get(i), graph, startNode, agentSpeed));
        }

        StringBuilder sb = new StringBuilder();

        // Add Headers for Performance
        sb.append("\n----- NEW DATA SET -----\n");
        sb.append("Algorithm,");
        sb.append("Random Initial Node,");
        sb.append("Run Number,");
        sb.append("Number of Agents,");
        sb.append("Agent Speed,");
        sb.append("Simulation Iterations,");
        sb.append("Run Time ns,");
        sb.append("Run Time ms,");
        sb.append("Run Time seconds,");
        sb.append("Average Idle Graph Time ns,");
        sb.append("Average Idle Graph Time ms,");
        sb.append("Average Idle Graph Time sec,");
        FileHandler.writeToFile(dataFile, sb);
        sb.setLength(0);

        main.runSim(algorithms, graph, agentList, agentNames, numRuns, numAgents, randStart, dataFile, agentSpeed);
    }

    /**
     *
     * @param algorithms
     * @param graph
     * @param agentList
     * @param agentNames
     * @param numRuns
     * @param numAgents
     * @param randStart
     * @param dataFile
     * @param agentSpeed
     * @throws IOException
     */
    public static void runSim(ArrayList<String> algorithms, Graph graph, ArrayList<Agent> agentList, ArrayList<String> agentNames,
                              int numRuns, int numAgents, boolean randStart, String dataFile, double agentSpeed) throws IOException {
        StringBuilder sb = new StringBuilder();

        // Run Simulation n times
        for(int j = 0; j < 2; j++) {
            String algorithm = algorithms.get(j);

            // Simulator object
            Simulator sim = new Simulator(graph, agentList, agentNames, algorithm, 3);

            for (int i = 0; i < numRuns; i++) {
                // Clear write buffer
//                writeFile.clear();

                // Get timings of run
                long runTimeNano = sim.runSimulation();
                double runTimeMilli = (double) runTimeNano / 1000000.0;
                double runTimeSeconds = (double) runTimeNano / 1_000_000_000.0;
                // Get timings of average idleness
                long avgGraphIdleTimeNano = (long)CalculateBayes.calculateAvgIdleTimeOfGraph(graph);
                double graphTimeMilli = (double) avgGraphIdleTimeNano / 1000000.0;
                double graphTimeSeconds = (double) avgGraphIdleTimeNano / 1_000_000_000.0;

                // Add info to write buffer
                sb.append(algorithm + ",");
                if(randStart){
                    sb.append("yes,");
                }
                else{
                    sb.append("no,");
                }
                sb.append(String.valueOf(i) + ",");
                sb.append(numAgents + ",");
                sb.append(agentSpeed + ",");
                sb.append(String.valueOf(sim.runs) + ",");
                sb.append(String.valueOf(runTimeNano) + ",");
                sb.append(String.valueOf(runTimeMilli) + ",");
                sb.append(String.valueOf(runTimeSeconds) + ",");
                sb.append(String.valueOf(avgGraphIdleTimeNano) + ",");
                sb.append(String.valueOf(graphTimeMilli) + ",");
                sb.append(String.valueOf(graphTimeSeconds) + ",");

                FileHandler.writeToFile(dataFile, sb);
                sb.setLength(0);
            }
        }
    }
}
