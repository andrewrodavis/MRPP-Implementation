import java.util.ArrayList;

public class Agent {
    // List of all the nodes that the agent visits over the course of the simulation
    public ArrayList<Node> visitedList = new ArrayList<>();

    Graph graph;

    /**
     * Constructor
     *
     * Stores a copy/reference? to the graph
     *
     * @param g The constructed graph
     */
    public Agent(Graph g){
        this.graph = g;
    }

    /**
     * Function: patrol()
     *
     * The algorithm ran for a robot to patrol the nodes.
     * Updates the universal table
     */
    public void patrol(){

    }
}
