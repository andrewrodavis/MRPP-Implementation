import java.lang.reflect.Array;
import java.util.*;

public class Simulator {
    public Graph g;
    public ArrayList<Agent> agentList;
    public ArrayList<String> agentNames;
    public String algorithm;
    public int numVisitsToStop;

    // Analysis Variables
    long totalRunTime;
    int runs;

    boolean debugFlag = false;

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
        // Need to reset all pertinent variables
        this.clearForNewRun();

        // Set the number of of visits for each node an agent is starting at
        for(Agent a : this.agentList){
            a.currentNode.numVisits++;
        }

        System.out.println("=====================Starting Simulation=====================");
        long startTime = System.nanoTime();

        // Begin the main loop
        while(true){
            this.runs++;

            // Iterate over all agents, each who makes their move decisions
            for(int i = 0; i < this.agentList.size(); i++) {
                // Get the next agent from the queue and remove from the list
                Agent a = this.agentList.get(0);
                this.agentList.remove(0);

                // If the agent has 1) reached their destination or 2) does not have a destination
                if(a.distanceTraveled >= a.distanceGoal || a.destNode == null){
                    // Set the agents current node to their just reached one and increment number of visits for that node
//                    a.currentNode = a.destNode;
//                    a.destNode = null;

                    if(this.algorithm.equals("Bayes")) {
                        this.Bayes(a);
                    }
                    else{
                        this.EntropyLocal(a);
                    }
                    // Add back to the list
                    this.agentList.add(a);
                }
                else{
                    a.distanceTraveled += a.speed;
                    this.agentList.add(a);
                }
            }

            // Check the end condition. If true, get the final time and break. False, continue
            if(this.checkEndCondition(this.g)){
                this.totalRunTime = (System.nanoTime() - startTime);
                break;
            }
            // Otherwise, shuffle the list and continue
            Collections.shuffle(this.agentList);

            // ********** Debugging
            if(debugFlag) {
                if (runs % 1 == 0) {
                    // Print agent current paths taken
                    System.out.println("Number of Visits");
                    for (Node node : this.g.graph) {
                        System.out.println("Node " + node.name + ": " + node.numVisits);
                    }
                    this.printCurrentPaths(agentList);
                    Scanner obj = new Scanner(System.in);
                    String cont = obj.nextLine();
                }
            }
            // **********
        }
        return this.totalRunTime;
    }
    /**
     * Function: clearForNewRun
     */
    public void clearForNewRun(){
        this.runs = 0;
        this.g.instantIdleTime = 0.0;
        this.g.avgIdleTime = 0.0;
        for(Node node : this.g.graph){
            node.reset();
        }
        for(Agent a : this.agentList){
            a.reset();
        }
    }

    /**
     * Function: Entropy
     *
     * @param a The current agent
     */
    public void EntropyLocal(Agent a){
        // Get the current time
        long currentTime = System.nanoTime();

        // Handle all variables when an agent reaches their destination node
        if(a.destNode != null){
            this.handleDestNodeBeginning(a, currentTime);
        }

        // Calculate the entropy values of all neighboring nodes
        ArrayList<Node> potentialDestinationNode = new ArrayList<>();
        for(Node node : a.currentNode.neighborNodes){
            CalculateEntropy.calculateEntropyValue(this.agentList.size() + 1, a.graph.numNodes, a.currentNode, node);
            potentialDestinationNode.add(node);
        }

        // Find the node with the highest degree of belief THAT can also be visited
        Node currentPotential = null;
        while(true){
            currentPotential = a.graph.getMaxEntropy(potentialDestinationNode);

            // In this case, all possible nodes have been found untraveled-able
            if(currentPotential == null){
                break;
            }
            // Check if the current node is visitable, this is the destination
            if (!currentPotential.alreadyDeclared) {
                break;
            } else {
                potentialDestinationNode.remove(currentPotential);
                currentPotential = null;
            }
        }

        // If no nodes can be reached, return no move
        if(currentPotential == null){
            return;
        }

        this.handleDestNodeEnd(a, currentPotential, currentTime);
    }

    /**
     * Function: Bayes
     *
     * Called when the agent reaches a new node and calculates appropriate variables
     *
     * @param a The current agent
     */
    public void Bayes(Agent a) {
        // Get the current time
        long currentTime = System.nanoTime();

        // Handle all variables when an agent reaches their destination node
        if (a.destNode != null) {
            this.handleDestNodeBeginning(a, currentTime);
            CalculateBayes.updateArcStrength(a.graph, a.sourceNode, currentTime);    // Update the arc strengths appropriately
        }

        // Calculate the degrees of belief for all neighbor nodes
        ArrayList<Node> potentialDestinationNode = new ArrayList<>();
        for (Node node : a.currentNode.neighborNodes) {
            // Degrees of Belief are stored in the node, no need to maintain a list here
            CalculateBayes.calculateDegreeOfBelief(a.graph, a.currentNode, node, currentTime);

            potentialDestinationNode.add(node);
        }

        // Find the node with the highest degree of belief THAT can also be visited
        Node currentPotential = null;
        while (true) {
            currentPotential = a.graph.getMaxDegreeOfBelief(potentialDestinationNode);

            // In this case, all possible nodes have been found untraveled-able
            if(currentPotential == null){
                break;
            }
            // Check if the current node is visitable, this is the destination
            if (!currentPotential.alreadyDeclared) {
                break;
            } else {
                potentialDestinationNode.remove(currentPotential);
                currentPotential = null;
            }
        }

        // If no nodes can be reached
        if(currentPotential == null){
            return;
        }

        this.handleDestNodeEnd(a, currentPotential, currentTime);
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

    /**
     * Function: handleDestNode
     *
     * Resets/sets all pertinent variables when the destination node has been reached by the agent
     *
     * @param a
     * @param currentTime
     */
    public void handleDestNodeBeginning(Agent a, long currentTime){
        a.destNode.numVisits++;     // Increment number of visits to the node
        a.currentNode = a.destNode; // Reassign the current node
        a.destNode.timeOfLastVisit = currentTime;   // Update the last visit time of the node
        a.visitedList.add(a.destNode.name);  // Add the just reached node to the visited list
        a.destNode = null;          // Erase the destination node, it no longer exists
    }

    /**
     * Function: handleDestNodeEnd
     *
     * Similar to above, but after the calculations have been made
     *
     * @param a
     * @param currentPotential
     * @param currentTime
     */
    public void handleDestNodeEnd(Agent a, Node currentPotential, long currentTime){
        // Set the destination node variables
        a.destNode = currentPotential;
        a.destNode.alreadyDeclared = true;

        // Set the source node
        a.sourceNode = a.currentNode;

        // Hacky solution to maintaining the average idle time of the visit at the last visit> ** Don't know if needed still
        a.destNode.avgIdleTimeLastVisit = a.destNode.avgIdleTimeLastVisitPotential;

        // Set movement variables
        a.distanceGoal = a.currentNode.getNeighborWeight(a.destNode.name);
        a.distanceTraveled = 0;

        // Set the current node variables
        a.currentNode.alreadyDeclared = false;
        a.currentNode = null;

        // Once decided, the agent leaves immediately
        a.distanceTraveled += a.speed;
    }
    /**
     * Function: printAgentDebugging
     *
     * @param a The current agent
     * @param flag The data to print
     * @param degrees The degrees of belief
     * @param degreesNodes The degrees nodes
     */
    public void printAgentDebugging(Agent a, int flag, ArrayList<Double> degrees, ArrayList<Node> degreesNodes){
        if(flag == 1) {

            System.out.println("\n===== Agent: " + a.name + " =====");
            if (a.currentNode != null) {
                System.out.println("-- Current Node: " + a.currentNode.name);
                this.printNodeDebugging(a.currentNode);
            }
            if(a.destNode != null) {
                System.out.println("-- Destination Node: " + a.destNode.name);
                System.out.println("-- Distance to " + a.destNode.name + ": " + a.distanceGoal);
                System.out.println("-- Distance Traveled: " + a.distanceTraveled);
                this.printNodeDebugging(a.destNode);
            }
        }
    }

    /**
     * Function: printNodeDebugging
     *
     * @param node The Node Obj
     */
    public void printNodeDebugging(Node node){
        System.out.println("Node " + node.name);
        System.out.println("-- Number of Visits: " + node.numVisits);
        System.out.println("-- Time of Last Visit: " + node.timeOfLastVisit);
    }

    /**
     * Function: printMathDebuggging
     *
     * @param aName Agent name
     * @param degreesOfBelief
     * @param degreeNodes
     * @param currentTime
     */
    public void printMathDebugging(String aName, ArrayList<Double> degreesOfBelief, ArrayList<Node> degreeNodes, long currentTime, double entropy){
        System.out.println("\n===== Math for Agent: " + aName + " =====");
        for(int i = 0; i < degreesOfBelief.size(); i++){
            System.out.println("Node " + degreeNodes.get(i).name + " Degree of Belief: " + degreesOfBelief.get(i) * 10000);
        }
        System.out.println("Current Time: " + currentTime);
        System.out.println("Entropy: " + entropy);
    }

    /**
     * Function: printCurrentPaths
     *
     * Does just that. All paths of agents thus far
     *
     * @param agents
     */
    public void printCurrentPaths(ArrayList<Agent> agents){
        for(Agent a : agents){
            System.out.println("Agent: " + a.name);
            System.out.print(a.initialNode.name);
            for(String name : a.visitedList){
                System.out.print(" -> " + name);
            }
            System.out.println();
        }
    }
}