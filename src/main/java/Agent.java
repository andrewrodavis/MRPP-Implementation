import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class Agent {
    // List of all the nodes that the agent visits over the course of the simulation
    public ArrayList<Node> visitedList = new ArrayList<>();
    public ArrayList<Node> currentNeighbors = new ArrayList<>();

    // Name of agent
    String name;

    Node currentNode;   // The node the agent is currently at
    Node goalNode;      // The node that the agent is moving towards

    // Local copy of graph needed?
    Graph graph;

    // Movement Variables
    int speed;
    int distanceTraveled;
    int distanceGoal;

    /**
     * Constructor
     *
     * Stores a copy/reference? to the graph
     *
     * @param g The constructed graph
     * @param name The name of the agent, no necessary
     */
    public Agent(String name, Graph g, Node currentNode, double inSpeed){
        this.graph = g;
        this.name = name;

        // Empty the visitedList just in case
        this.visitedList.clear();


        // Set the current node and all its neighbors
        this.currentNode = currentNode;


        // Iterate over the currentNode's neighbor list hashmap
        Iterator it = this.currentNode.neighborList.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry pair = (Map.Entry)it.next();
            this.currentNeighbors.add((Node) pair.getKey());
        }

        // Set Traveling variabes. Speed needs to be made whole number
        inSpeed = inSpeed * 10;
        this.speed = (int) inSpeed;
        this.distanceGoal = 0;
        this.distanceTraveled = 0;
        this.goalNode = null;
    }

    /**
     * Function: bayesPatrol()
     *
     * The algorithm ran for a robot to patrol the nodes from the paper
     * Updates the universal table
     */
    public void bayesPatrol(){

    }
}
