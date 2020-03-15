import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class Agent {
    // List of all the nodes that the agent visits over the course of the simulation
    public ArrayList<String> visitedList = new ArrayList<>();
    public ArrayList<Node> currentNeighbors = new ArrayList<>();

    // Name of agent
    String name;

    // Node Position Tracking
    Node currentNode;   // The node the agent is currently at
    Node destNode;      // The node selected to travel to
    Node sourceNode;    // The node traveled from. i.e. Path = sourceNode -> destNode
    Node initialNode;

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
        this.sourceNode = currentNode;
        this.destNode = null;
        this.initialNode = currentNode;

        this.currentNode.alreadyDeclared = true;


        // Iterate over the currentNode's neighbor list hashmap
        Iterator it = this.currentNode.neighborList.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry pair = (Map.Entry)it.next();
            this.currentNeighbors.add((Node) pair.getKey());
        }

        // Set Traveling variables. Speed needs to be made whole number
        inSpeed = inSpeed * 10;
        this.speed = (int) inSpeed;
        this.distanceGoal = 0;
        this.distanceTraveled = 0;
    }

    public void reset(){
        this.visitedList.clear();
        this.currentNode = this.initialNode;
        this.sourceNode = this.initialNode;
        this.destNode = null;
        this.currentNode.alreadyDeclared = true;
        this.distanceTraveled = 0;
        this.distanceGoal = 0;
    }
}
