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
    public long timeOfLastVisit;
    public int numVisits;
    public boolean alreadyDeclared;

    // Move Calculation Variables: Kind of cheating
    public ArrayList<Double> degsOfBelief = new ArrayList<>();

    // **** Arc Strength Calculations
    public double normalizedVisit;

    // **** Probability of Move | Theta Calculations
    public double degOfBelief;
    public double instantIdleTime;
    public double avgIdleTimeNow;           // I_vi(t)
    public double avgIdleTimeLastVisit;     // I_vi(t_l)
    public double avgIdleTimeLastVisitPotential;

    // **** Entropy Node Calculations
    public double entropyValue;

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
        this.timeOfLastVisit = 0;
        this.instantIdleTime = 0.0;
        this.avgIdleTimeNow = 0.0;
        this.avgIdleTimeLastVisit = 0.0;
        this.avgIdleTimeLastVisitPotential = 0.0;
        this.alreadyDeclared = false;

        this.normalizedVisit = 0.0;

        this.degOfBelief = 0.0;
        this.entropyValue = 0.0;
    }

    /**
     * Function: reset
     *
     * Resets all pertinent vars for new simulation run
     *
     */
    public void reset(){
        this.numVisits = 0;
        this.numVisits = 0;
        this.timeOfLastVisit = 0;
        this.instantIdleTime = 0;
        this.avgIdleTimeNow = 0;
        this.avgIdleTimeLastVisit = 0;
        this.avgIdleTimeLastVisitPotential = 0;
        this.alreadyDeclared = false;

        this.normalizedVisit = 0.0;

        this.degOfBelief = 0.0;
        this.entropyValue = 0.0;

        // Reset all arc strengths to 1.0
        for(int i = 0; i < this.neighborArcStrengths.size(); i++){
            this.neighborArcStrengths.put(this.neighborNodes.get(i), 1.0);
        }
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
