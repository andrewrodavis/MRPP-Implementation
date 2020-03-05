import java.util.*;

public class Simulator {
    public Graph g;
//    public Queue<Agent> agentList;
    public ArrayList<Agent> agentList;
    public ArrayList<String> agentNames;
    public String algorithm;
    public int numVisitsToStop;

    // Analysis Variables
    long totalRunTime;

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
            // Iterate over all agents for 1 time step
            for(int i = 0; i < this.agentList.size(); i++) {
                // Get the next agent from the queue and remove from the list
                Agent a = this.agentList.get(0);
                this.agentList.remove(0);

                // Increment an agents walk, then check if it has reached a node. If it has, calculate new node. If not, re-add it to the list
                a.distanceTraveled += a.speed;
                if(a.distanceTraveled >= a.distanceGoal){
                    this.atNode(a);
                }
                else{
                    this.agentList.add(a);
                }
            }
            // Check the end condition. If true, get the final time and break. False, continue
            if(this.checkEndCondition()){
                this.totalRunTime = (System.nanoTime() - startTime) / 1000;
                break;
            }
            // Otherwise, shuffle the list and continue
            Collections.shuffle(this.agentList);
        }

        return this.totalRunTime;
    }

    /**
     * Function: atNode
     *
     * Called when the agent reaches a new node and calculates appropriate variables
     *
     * @param a The current agent
     */
    public void atNode(Agent a){
        // Get the current time
        long currentTime = System.nanoTime();

        // The list to hold all calculated degrees of belief
        ArrayList<Double> degsOfBelief = new ArrayList<Double>();

        // Iterate over all neighbors of the current node the agent is at
        for(int i = 0; i < a.currentNode.numNeighbors; i++){
            degsOfBelief.add(Calculations.calcDegOfBelief(a.currentNode, a.currentNode.neighborNodes.get(i), a.graph, currentTime));
        }

        // Get the node with the highest degree of belief. Need to account for node being unvisitable
        int highestIdx = 0;
        while(true){
            // get the index
            highestIdx = degsOfBelief.indexOf(Collections.max(degsOfBelief));

            // check to see if the node is visitable. If yes, break the loop
            if(a.currentNode.neighborNodes.get(highestIdx).alreadyDeclared == true){
                // If not able to visit, remove that index from the list and start over.
                degsOfBelief.remove(highestIdx);

                // Maybe need to check for empty set after removal
            }
            else{
                break;
            }
        }

        // Set all appropriate variables
        a.destNode = a.currentNode.neighborNodes.get(highestIdx);
        a.distanceGoal = a.currentNode.getNeighborWeight(a.destNode.name);    // Check for error, if == -1
        a.distanceTraveled = 0;
        a.destNode.alreadyDeclared = true;
        a.currentNode = null;
    }

    /**
     * Function: checkEndCondition
     *
     */
    public boolean checkEndCondition(){
        return true;
    }
}
