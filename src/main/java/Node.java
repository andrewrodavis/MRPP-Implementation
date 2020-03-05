/**
 *
 * Sources:
 *      https://www.tutorialspoint.com/java/java_hashmap_class.htm
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Node {
    public String name;
    public int numNeighbors;

    // Updated whenever an agent reaches the node
    public double timeOfLastVisit;
    public double instantIdleTime;
    public double avgIdleTimeNow;           // I_vi(t)
    public double avgIdleTimeLastVisit;     // I_vi(t_l)
    public int numVisits;
    public boolean alreadyDeclared;

    // List of neighbors by name -- Delete Eventually
    ArrayList<String> neighborListNames = new ArrayList<>();

    // Replacement for ^^
    ArrayList<Node> neighborNodes = new ArrayList<>();

    // Hash for easy access to weights of neighbors
    HashMap<Node, Integer> neighborList = new HashMap<Node, Integer>();
    // Hash for quick access to arc strengths of neighbors
    HashMap<Node, Double> neighborArcStrengths = new HashMap<Node, Double>();

    public Node(String name){
        this.name = name;
        this.numVisits = 0;
        this.timeOfLastVisit = 0.0;
        this.instantIdleTime = 0.0;
        this.avgIdleTimeNow = 0.0;
        this.avgIdleTimeLastVisit = 0.0;
        this.alreadyDeclared = false;
    }

    /**
     * Function: getNeighborWeight
     *
     * @param name The name of the node whose weight must be returned
     * @return weight The weight corresponding to the desired node name
     *
     */
    public int getNeighborWeight(String name){
        Iterator it = this.neighborList.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry pair = (Map.Entry)it.next();
            Node node = (Node) pair.getKey();
            if(node.name.equals(name)){
                return (int)pair.getValue();
            }
        }
        return -1;
    }

    /**
     * Function: getNeighborArc
     *
     * @param name The name of the neighbor whose arc strength is to be returned
     */
    public double getNeighborArc(String name){
        Iterator it = this.neighborArcStrengths.entrySet().iterator();

        while(it.hasNext()){
            Map.Entry pair = (Map.Entry)it.next();
            Node node = (Node) pair.getKey();
            if(node.name.equals(name)){
                return (double)pair.getValue();
            }
        }
        return -1; // If DNE
    }
}
