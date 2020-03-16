import java.util.ArrayList;
import java.util.Collections;

public class CalculateBayes {

    /*********************************************************************
     *                      CALCULATE P(MOVE_I | THETA_0,I)
     *********************************************************************/

    /**
     * Function: calculateDegreeOfBelief
     *
     * Returns the probability of moving to the node presented given the arc strength of the origin node to the node presented
     *
     * P(move_i | theta_0,i) = [ P(move_i)P(theta_0,i | move_i) ] / [ P(theta_0,i) ]
     *
     * @param graph The entire graph
     * @param currentNode The node the agent is sitting at
     * @param destNode The destination node
     * @param currentTime The time to use for calculations
     * @return
     */
    public static double calculateDegreeOfBelief(Graph graph, Node currentNode, Node destNode, long currentTime){
        // Calculate the numerator
        double probabilityOfMoveI = CalculateBayes.calcProbMoveI(graph, destNode, currentTime);
        double probabilityOfThetaGivenMove = CalculateBayes.calcThetaGivenMove(graph, currentNode.getNeighborArc(destNode.name));
        double numerator = probabilityOfMoveI * probabilityOfThetaGivenMove;

        // Calculate the denominator, just get theta
        double theta = currentNode.getNeighborArc(destNode.name);

        // Get the full degree of belief for the single destination node and return
        destNode.degOfBelief = numerator / theta;
        return numerator / theta;
    }

    /**
     * Function: calcProbMoveI
     *
     * Calculate part of the degree of belief
     *
     * P(move_i) = Average Idle Time of destination node / Sum of all Average Idle Times in the graph
     *
     * @param graph
     * @param destNode
     * @param currentTime The time to use for calculations
     * @return
     */
    public static double calcProbMoveI(Graph graph, Node destNode, long currentTime){
        // Get the numerator
        double avgIdleTimeOfDestNode = CalculateBayes.calcAvgIdleTimeNow(destNode, currentTime);

        // Get the denominator
        double denominator = CalculateBayes.sumGraphAvgIdleTime(false, graph, currentTime);

        // Get the full probability of move to i/destNode
        double probMove = avgIdleTimeOfDestNode / denominator;

        return probMove;
    }

    /**
     * Function: calcAvgIdleTimeNow
     *
     * I_v_i(t) = (last avgerage idle time * node visits + instant idle time) / (node visits + 1)
     *
     * @param node The node to carry out calculations from
     * @param currentTime The current time to calculate with
     * @return
     */
    public static double calcAvgIdleTimeNow(Node node, long currentTime){
        // Get the numerator
        double lastAvgIdleTime = node.avgIdleTimeLastVisit;
        node.instantIdleTime = CalculateBayes.calcInstantIdleTime((double)node.timeOfLastVisit, (double)currentTime);

        double numerator = ( lastAvgIdleTime * (double)node.numVisits ) + node.instantIdleTime;

        // Get final average idle time and store appropriately
        double avgIdleNow = numerator / (node.numVisits + 1.0);
        node.avgIdleTimeLastVisitPotential = avgIdleNow;

        return avgIdleNow;
    }

    /**
     * Function: calcInstantIdleTime
     *
     * last visit - current time
     *
     * @param lastVisit Recorded in node, sent as parameter rather than whole node object
     * @param currentTime The current time to calculate with
     * @return
     */
    public static double calcInstantIdleTime(double lastVisit, double currentTime){
        return currentTime - lastVisit;
    }

    /**
     * Function: sumGraphAvgIdleTime
     *
     * Calculates the average idle time of the graph
     *
     * @param normalizeFlag If true, then divide by the normalization. This is done at the end. Otherwise use to calculate P(move_i)
     * @param graph The entire Graph
     * @param currentTime The current time to calculate with
     * @return
     */
    public static double sumGraphAvgIdleTime(boolean normalizeFlag, Graph graph, long currentTime){
        // The final sum
        double summation = 0.0;

        // Calculate the average idle time of the graph including the normalization
        if(normalizeFlag){
            for(Node node : graph.graph){
                summation += node.avgIdleTimeLastVisit;
            }
            summation = summation / (1.0 / (double)graph.numNodes);
            return summation;
        }
        else {
            // Calculate for P(move_i)
            for (Node node : graph.graph) {
                // Calculate the average idle time
                double avgIdleTime = CalculateBayes.calcAvgIdleTimeNow(node, currentTime);

                // Add it to the final count
                summation += avgIdleTime;
            }
            return summation;
        }
    }

    /**
     * Function: calcThetaGivenMove
     *
     * Used in calculateDegreeOfBelief
     *
     * P(theta_0,i | move_i) = theta_0,i / Summation of all arc strengths in the graph
     *
     * @param graph
     * @param arcStrengthSourceToDest
     * @return
     */
    public static double calcThetaGivenMove(Graph graph, double arcStrengthSourceToDest){
        // numerator = arcStrengthSourceToDest

        // Calculate the denominator
        double summation = 0.0;

        for(Node j : graph.graph){
            for(Node k : j.neighborNodes){
                 // Sum the arc strength of all arc strengths in the graph
                summation += j.getNeighborArc(k.name);
            }
        }

        return arcStrengthSourceToDest / summation;
    }

    /*********************************************************************
     *                      CALCULATE ARC STRENGTHS
     *********************************************************************/

    /**
     * Function: updateArcStrength
     *
     * Special Consideration: This is calculated after an agent reaches a new node, so we must keep track of the source node traveled from
     *
     * @param graph
     * @param sourceNode
     * @param currenttime
     */
    public static void updateArcStrength(Graph graph, Node sourceNode, long currenttime){
        // First, if the current node has <= 1 neighbors, there is no need to update the arc strengths, per (14)
        if(sourceNode.numNeighbors <= 1){
            return;
        }

        // Calculate gamma_0,i, where 0 = current node and i = every neighbor node

        // First, calculate the normalized visit value for each node
        for(Node node : sourceNode.neighborNodes){
            // Calculate the number of normalized visits for each node, store within themselves
            double visits = Double.valueOf(node.numVisits);
            double neighbors = Double.valueOf(node.numNeighbors);
            node.normalizedVisit = visits / neighbors;
        }

        // Second, find the nodes with the highest and lowest normalizedVisit value
        Node highest = CalculateBayes.getHighestNode(graph, sourceNode);
        Node lowest = CalculateBayes.getLowestNode(graph, sourceNode);

        // Third, calculate the entropy of the system
        double entropy = CalculateBayes.calcEntropy(graph, sourceNode);

        // Fourth, calculate the reward/punish values
        double rewardVal = entropy;
        double punishVal = (-1 * entropy);

        // Assign to the appropriate arcs
        sourceNode.neighborArcStrengths.put(highest, punishVal);
        sourceNode.neighborArcStrengths.put(lowest, rewardVal);
    }

    /**
     * Function: getHighestNode/getLowestNode
     *
     * Finds and returns the node with the highest normalized visit count, and therefore the node to punish
     *
     * @param graph The entire graph
     * @param sourceNode The node the agent moved from
     * @return
     */
    public static Node getHighestNode(Graph graph, Node sourceNode){
        ArrayList<Node> highestList = new ArrayList<>();    // The highest container, stores in case of ties

        // Get the highest value and add to the list
        highestList.add(graph.getMaxArcStrengthNode(sourceNode));
        Node returnHigh = highestList.get(0); // The node to return

        boolean highTie = false;    // True if there is an equal value

        // Look for ties
        for(Node node : sourceNode.neighborNodes){
            // If node values are equal AND they are not the same node, add to the tie list
             if(node.normalizedVisit == highestList.get(0).normalizedVisit && !(node.name.equals(highestList.get(0).name))){
                 highestList.add(node);
                 highTie = true;
             }
        }

        // Handle ties
        if(highTie){
            for(Node node : highestList){
                if(node.instantIdleTime < returnHigh.instantIdleTime){
                    returnHigh = node;
                }
            }
        }

        // Return the highest node/node to punish
        return returnHigh;
    }
    public static Node getLowestNode(Graph graph, Node sourceNode){
        ArrayList<Node> lowestList = new ArrayList<>();    // The highest container, stores in case of ties

        // Get the highest value and add to the list
        lowestList.add(graph.getMinArcStrengthNode(sourceNode));
        Node returnLow = lowestList.get(0); // The node to return

        boolean lowTie = false;    // True if there is an equal value

        // Look for ties
        for(Node node : sourceNode.neighborNodes){
            // If node values are equal AND they are not the same node, add to the tie list
            if(node.normalizedVisit == lowestList.get(0).normalizedVisit && !(node.name.equals(lowestList.get(0).name))){
                lowestList.add(node);
                lowTie = true;
            }
        }

        // Handle ties
        if(lowTie){
            for(Node node : lowestList){
                if(node.instantIdleTime > returnLow.instantIdleTime){
                    returnLow = node;
                }
            }
        }

        // Return the highest node/node to punish
        return returnLow;
    }

    /**
     * Function: calcEntropy
     *
     * Calculates the entropy of a move to a neighbor of the sourcenode given theta
     * H(move_i | THETA) where THETA is the arc strength for each arc between the source node and its neighbors
     *
     * @param graph
     * @param sourceNode
     * @return
     */
    public static double calcEntropy(Graph graph, Node sourceNode){
        // Calculate numerator -- H(move_i | THETA)
        double entropySummation = 0.0;
        for(Node node : sourceNode.neighborNodes){
            entropySummation += (node.degOfBelief * (Math.log(node.degOfBelief) / Math.log(2)));
        }

        entropySummation = entropySummation * -1;

        // Calculate log_2 of Beta
        double logB = Math.log(sourceNode.numNeighbors) / Math.log(2);

        return (1 - (entropySummation / logB));
    }

    /**
     * Function: calculateAvgIdletimeOfGraph
     *
     * The final data collection step
     *
     * @param graph
     * @return
     */
    public static double calculateAvgIdleTimeOfGraph(Graph graph){
        double avgIdleTime = 0.0;

        for(Node node : graph.graph){
            avgIdleTime += node.avgIdleTimeLastVisit;
        }
        return (avgIdleTime) / (1.0 / (double)graph.numNodes);
    }
}
