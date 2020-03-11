import java.util.*;

public class Simulator {
    public Graph g;
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
        System.out.println("=====================Simulation Starting=====================");
        int runs = 0;
        while(true){
            System.out.println("~~~~~ Run: " + runs + "~~~~~");
            runs++;
            System.out.println("Number of Visits");
            for(Node node : this.g.graph){
                System.out.println("Node " + node.name + ": " + node.numVisits);
            }
            // Iterate over all agents for 1 time step
            for(int i = 0; i < this.agentList.size(); i++) {
                // Get the next agent from the queue and remove from the list
                Agent a = this.agentList.get(0);
                this.agentList.remove(0);

                System.out.println("\n---------------Agent: " + a.name + "---------------");
                System.out.println("\nPre Movement---------------");
                System.out.println("Agent Distance Traveled: " + a.distanceTraveled);
                System.out.println("Agent Distance to Goal: " + a.distanceGoal);
                System.out.println("Agent Current Goal: " + a.destNode.name);
                if(a.currentNode == null){
                    System.out.println("Agent is on the move");
                }
                else{
                    System.out.println("Agent Currently at: " + a.currentNode.name);
                }
                System.out.println("\nPost Movement---------------");

                // Increment an agents walk, then check if it has reached a node. If it has, calculate new node. If not, re-add it to the list
                a.distanceTraveled += a.speed;
                if(a.distanceTraveled >= a.distanceGoal){
                    System.out.println("Calculating Next Move---------------");
                    // Set the agents current node to their just reached one and increment number of visits for that node
                    a.currentNode = a.destNode;

                    this.Bayes(a);
                    this.agentList.add(a);
                    System.out.println("Agent Distance Traveled: " + a.distanceTraveled);
                    System.out.println("Agent Distance to Goal: " + a.distanceGoal);
                    System.out.println("Agent Current Goal: " + a.destNode.name);
                    if(a.currentNode == null){
                        System.out.println("Agent is on the move");
                    }
                    else{
                        System.out.println("Agent Currently at: " + a.currentNode.name);
                    }
                }
                else{
                    this.agentList.add(a);
                }
            }
            Scanner obj = new Scanner(System.in);
            String cont = obj.nextLine();
            // Check the end condition. If true, get the final time and break. False, continue
            if(this.checkEndCondition(this.g)){
                this.totalRunTime = (System.nanoTime() - startTime) / 1000;
                break;
            }
            // Otherwise, shuffle the list and continue
            Collections.shuffle(this.agentList);
        }

        return this.totalRunTime;
    }

    /**
     * Function: Bayes
     *
     * Called when the agent reaches a new node and calculates appropriate variables
     *
     * @param a The current agent
     */
    public void Bayes(Agent a){
        // Get the current time
        long currentTime = System.nanoTime();

        // Increment number of visit
        a.destNode.numVisits++;

        // The list to hold all calculated degrees of belief
        ArrayList<Node> degsOfBeliefNodes = new ArrayList<>();
        ArrayList<Double> degsOfBelief = new ArrayList<Double>();

        // The value of entropy for updating arc strength later
        double entropy = 0.0;

        // Iterate over all neighbors of the current node the agent is at
        for(Node node : a.currentNode.neighborNodes){
            degsOfBelief.add(Calculations.calcDegOfBelief(a.currentNode, node, a.graph, currentTime));
            degsOfBeliefNodes.add(node);
        }

        // Calculate the entropy and get node with highest degree of belief
        for(int i = 0; i < degsOfBelief.size(); i++){
            entropy += Calculations.calcEntropy(degsOfBelief.get(i));
        }
        // Normalize
        entropy = entropy / (Math.log(a.currentNode.numNeighbors) / Math.log(2));
        entropy = entropy * -1;

        Node goalNode;
        int highestIdx;
        while(true){
            // get the index
            highestIdx = degsOfBelief.indexOf(Collections.max(degsOfBelief));

            // check to see if the node is visitable. If yes, break the loop
            if(degsOfBeliefNodes.get(highestIdx).alreadyDeclared == true){
                // If not able to visit, remove that index from the list and start over.
                degsOfBelief.remove(highestIdx);
                degsOfBeliefNodes.remove(highestIdx);

                // Maybe need to check for empty set after removal -- YES
                // Empty means no nodes can be traveled to, agent does nothing for now
                if(degsOfBelief.isEmpty() || degsOfBeliefNodes.isEmpty()){
                    return;
                }
            }
            else{
                break;
            }
        }

        // Set the destination node
        a.destNode = degsOfBeliefNodes.get(highestIdx);

        // Reset current node vars
        a.currentNode.alreadyDeclared = false;

        // --UPDATE arc strengths
        Calculations.arcStrength(a.currentNode, currentTime, entropy);

        // Set all appropriate variables
        a.distanceGoal = a.currentNode.getNeighborWeight(a.destNode.name);    // Check for error, if == -1
        a.distanceTraveled = 0;
        a.destNode.alreadyDeclared = true;
        a.currentNode = null;

        // Hacky solution to maintaining the average idle time of the visit at the last visit
        a.destNode.avgIdleTimeLastVisit = a.destNode.avgIdleTimeLastVisitPotential;

    }
    /**
     * Function: checkEndCondition
     *
     * @param graph The entire graph to check all visit counts
     */
    public boolean checkEndCondition(Graph graph){
        for(Node node : graph.graph){
            if(node.numVisits < 3){
                return false;
            }
        }
        return true;
    }
}