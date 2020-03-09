import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

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
        Graph graph = new Graph("src/main/java/graph2.csv");
        int numAgents = 3;
        double agentSpeed = 0.2;
        ArrayList<String> agentNames = new ArrayList<>();

        // Setup Graph
        graph.initGraph();

        // Agent name list
        agentNames.add("1");
        agentNames.add("2");
        agentNames.add("3");
//        agentNames.add("4");
//        agentNames.add("5");

        // Check that you don't go out of range
        if(agentNames.size() != numAgents){
            throw new ArrayIndexOutOfBoundsException("Name list not == number of agents");
        }

        // Agent list
        ArrayList<Agent> agentList = new ArrayList<>();
//        Queue<Agent> agentList = new LinkedList<>();

        // Initialize agents
        for(int i = 0; i < numAgents; i++){
            // Get start node: start off all at same node
            Node startNode = graph.graph.get(0);
            agentList.add(new Agent(agentNames.get(i), graph, startNode, agentSpeed));
        }

        // Simulator object
        Simulator sim = new Simulator(graph, agentList, agentNames, "bayes", 3);

        // Run Simulation
        long runTime = sim.runSimulation();

        // Print all information
        System.out.println("=============Simulation Completed Successfully=============");
        System.out.println("Number of visits to each node");
        for(Node node : graph.graph){
            System.out.println("\tNode " + node.name + ": " + node.numVisits);
        }
    }
}
