import java.util.*;

public class Simulator {
    public Graph g;
//    public Queue<Agent> agentList;
    public ArrayList<Agent> agentList;
    public ArrayList<String> agentNames;
    public String algorithm;
    public int numVisitsToStop;

    /**
     * Constructor
     *
     */
    public Simulator(Graph inGraph, ArrayList<Agent> inList, ArrayList<String> inNames, String inAlg, int inVisits){
        this.g = inGraph;
        this.agentList = inList;
        this.agentNames = inNames;
        this.algorithm = inAlg;
        this.numVisitsToStop = inVisits;
    }

    /**
     * Function: runSimulation
     *
     */
    public long runSimulation(){
        long startTime;
        long totalTime;

        startTime = System.nanoTime();
        // Main Loop
        while(true){
            for(int i = 0; i < this.agentList.size(); i++) {
                // Get the next agent from the queue and remove from the list
                Agent a = this.agentList.get(0);
                this.agentList.remove(0);

                // If agent is either at a node, or their next move puts them at a node, do stuff to figure out next destination
                if(a.currentNode != null || (a.distanceTraveled += a.speed) == a.distanceGoal){
                    this.atNode(a);
                }
                // Otherwise, they are still traveling
                else{
                    a.distanceTraveled += a.speed;
                }

                // Put the agent back at the end of the queue
                this.agentList.add(a);
            }
            // Shuffle the list
            Collections.shuffle(this.agentList);
            break;
        }
        totalTime = (System.nanoTime() - startTime) / 1000;

        return totalTime;
    }

    /**
     * Function: atNode
     *
     * Called when the agent reaches a new node and calculates appropriate variables
     *
     * @param a The current agent
     */
    public void atNode(Agent a){
        // Get the time now
        double timeNow = System.nanoTime();

        // Calculate probability list of all nodes potential travelled to
        ArrayList<Double> probList = new ArrayList<>();
        for(int i = 0; i < a.currentNode.numNeighbors; i++){
            // Get the next neighbor to check
            Node destNode = a.currentNode.neighborNodeObjs.get(i);
            probList.add(Calculations::calcDegOfBelief(a.currentNode, destNode, this.g, timeNow));
        }

        // Determine which node has the highest probability
        int highestProbIdx = Integer.MIN_VALUE;
        for(int i = 0; i < probList.size(); i++){
            if(probList.get(i) > highestProbIdx){
                highestProbIdx = i;
            }
        }

        // highestProbIdx is the index, from neighbor list of a.currentNode, to travel to. Set all variables for travel
        Node destNode = a.currentNode.neighborNodeObjs.get(highestProbIdx);

        // Set the travel distance
        a.distanceGoal = a.currentNode.getNeighborWeight(destNode.name);        // Set the travel distance

        // Reset distance Traveled
        a.distanceTraveled = 0;

        // Update node traveling to
        // distance to travel
        a.distanceGoal = 0; // ******Change to the distance of the neighbor chosen

        // currentNode = null
        a.currentNode = null;

        // goalNode = desired node

        // Reset travelling variables
        a.distanceTraveled = 0;

        // Increment walk
        a.distanceTraveled += a.speed;
    }

    /**
     * Function: checkEndCondition
     *
     */
}
