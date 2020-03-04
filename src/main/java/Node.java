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

    // List of neighbors by name
    ArrayList<String> neighborListNames = new ArrayList<>();

    // Hash for easy access to weights of neighbors
    HashMap<Node, Integer> neighborList = new HashMap<Node, Integer>();

    public Node(String name){
        this.name = name;
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
}
