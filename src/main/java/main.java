import java.io.IOException;
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
        Graph graph = new Graph("src/main/java/graphs/simGraph.csv");
        int numAgents = 1;
        int numRuns = 3;
        double agentSpeed = 5.0;
        boolean randStart = false;
        String algorithm = "d";

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

        ArrayList<String> writeFile = new ArrayList<>();
        FileHandler handler = new FileHandler("data.csv");

        //
        writeFile.add("Run Number");
        writeFile.add("Simulation Iterations");
        writeFile.add("Run Time ns");
        writeFile.add("Run Time ms");
        writeFile.add("Run Time seconds");
        writeFile.add("Average Idle Graph Time");
        writeFile.add("Agent");
        writeFile.add("Path");
        handler.writeToFile(writeFile);

        // Run Simulation n times
        for(int j = 0; j < 2; j++) {
            algorithm = algorithms.get(j);

            // Simulator object
            Simulator sim = new Simulator(graph, agentList, agentNames, algorithm, 3);

            for (int i = 0; i < numRuns; i++) {
                // Clear write buffer
                writeFile.clear();

                // Get timings of run
                long runTimeNano = sim.runSimulation();
                double runTimeMilli = (double) runTimeNano / 1000000.0;
                double runTimeSeconds = (double) runTimeNano / 1_000_000_000.0;
                // Get timings of average idleness
                long avgGraphIdleTimeNano = (long) CalculateBayes.calculateAvgIdleTimeOfGraph(graph);
                double graphTimeMilli = (double) avgGraphIdleTimeNano / 1000000.0;
                double graphTimeSeconds = (double) avgGraphIdleTimeNano / 1_000_000_000.0;

                // Add info to write buffer
                writeFile.add(algorithm);
                writeFile.add(String.valueOf(i));
                writeFile.add(String.valueOf(sim.runs));
                writeFile.add(String.valueOf(runTimeNano));
                writeFile.add(String.valueOf(runTimeMilli));
                writeFile.add(String.valueOf(runTimeSeconds));
                writeFile.add(String.valueOf(avgGraphIdleTimeNano));
                writeFile.add(String.valueOf(graphTimeMilli));
                writeFile.add(String.valueOf(graphTimeSeconds));

                for(Agent a : sim.agentList){
                    writeFile.add(a.name);
                    writeFile.add(a.initialNode.name);
                    for(String node : a.visitedList){
                        writeFile.add(" -> ");
                        writeFile.add(node);
                    }
                }

                handler.writeToFile(writeFile);
            }
        }





        // Calculate graph average idle time
//        long runTimeNano = sim.runSimulation();
//        double runTimeMilli = (double) runTimeNano / 1000000.0;
//        double runTimeSeconds = (double) runTimeNano / 1_000_000_000.0;
//        // Get timings of average idleness
//        long avgGraphIdleTimeNano = (long) CalculateBayes.calculateAvgIdleTimeOfGraph(graph);
//        double graphTimeMilli = (double) avgGraphIdleTimeNano / 1000000.0;
//        double graphTimeSeconds = (double) avgGraphIdleTimeNano / 1_000_000_000.0;
//
//        // Print all information
//        System.out.println("=============Simulation Completed Successfully=============");
//        System.out.println("Total Runtime: " + runTimeNano + " ns");
//        System.out.println("Total Runtime: " + runTimeMilli + " ms");
//        System.out.println("Total Runtime: " + runTimeSeconds + " seconds");
//        System.out.println("Average Graph Idle Time: " + avgGraphIdleTimeNano + "ns");
//        System.out.println("Average Graph Idle Time: " + graphTimeMilli + " ms");
//        System.out.println("Average Graph Idle Time: " + graphTimeSeconds + " seconds");
//        System.out.println("Node Average Idle Times");
//        System.out.println("Total Iterations: " + sim.runs);
//        System.out.println("Number of visits to each node");
//        for(Node node : graph.graph){
//            System.out.println("\tNode " + node.name + ": " + node.numVisits);
//        }
//        System.out.println("Paths of each agent");
//        sim.printCurrentPaths(sim.agentList);
    }
}
