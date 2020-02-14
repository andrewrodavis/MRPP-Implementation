import java.util.HashMap;

public class Node {
    public String name;
    public int numNeighbors;

    public double timeOfLastVisit;
    public double instantIdleTime;
    public double avgIdleTime;

    // Hash for easy access to weights of neighbors
    HashMap<String, Integer> neighborConns = new HashMap<String, Integer>();

    public Node(String name){
        this.name = name;
    }
}
