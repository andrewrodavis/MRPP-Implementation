/**
 *
 * Sources:
 *      https://www.tutorialspoint.com/java/java_hashmap_class.htm
 */

import java.util.HashMap;

public class Node {
    public String name;
    public int numNeighbors;

    public double timeOfLastVisit;
    public double instantIdleTime;
    public double avgIdleTime;

    // Hash for easy access to weights of neighbors
    HashMap<Node, Integer> neighborList = new HashMap<Node, Integer>();

    public Node(String name){
        this.name = name;
    }
}
