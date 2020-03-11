import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class Calculations {
    /**
     * Function: calcDegOfBelief
     *
     * Find the degree of belief to move to node i using:
     *      P(move_i | Theta_0,i)
     *
     * Requires ____ equations
     *
     * @param sourceNode The node that the agent is currently sitting at
     * @param destNode the node that should be calculated -- node in question to travel to. NOT source node
     * @param graph The graph of the entire system
     * @param timeNow The time the agent begins calculations
     */
    public static double calcDegOfBelief(Node sourceNode, Node destNode, Graph graph, double timeNow){
        // Get the probability of the move from node 0 -> i
        // P(move_i | theta_0,i)
        double probMove = calcProbMove(graph, destNode, timeNow);

        // Need to get arc strength from source node to node i
        // theta_0,i
        double theta = sourceNode.getNeighborArc(destNode.name);

        // Get probability of theta | pMove
        // P(theta_0,i | move_i)
        double thetaGivenMove = calcThetaGivenMove(theta, graph);

        // Return this
        double degOfBelief = (probMove * thetaGivenMove) / (theta);

        return degOfBelief;
    }

    /**
     * Function: calcProbMove
     *
     * Find P(move_i) = [ Avg Idle Time @ T ] / [ Sum of(Avg Idle Time of all Nodes) ]
     *
     * @param graph The current graph information
     * @param nodeI The node that should be calculated
     * @param timeNow The time to do calculations with
     */
    public static double calcProbMove(Graph graph, Node nodeI, double timeNow){
        double probMove = calcAvgIdleTimeNow(nodeI, timeNow) / calcSumAvgIdleTime(graph, timeNow);

        return probMove;
    }

    /**
     * Function: calcAvgIdleTimeNow
     *
     * Calculates the average idle time of the node at the current time, as passed in
     *
     * @param nodeI Current node to do calculations with
     * @param timeNow The time to do calculations with
     */
    public static double calcAvgIdleTimeNow(Node nodeI, double timeNow){
        double numerator = (nodeI.avgIdleTimeLastVisit * nodeI.numVisits) + calcInstantIdleTime(nodeI, timeNow);
        double denom = nodeI.numVisits + 1;
        double avgIdleNow = numerator / denom;
        nodeI.avgIdleTimeLastVisitPotential = avgIdleNow;

        return avgIdleNow;
    }

    /**
     * Function: calcSumAvgIdleTime
     *
     * Calculates the sum of the average idle times of all nodes in the system
     *
     * @param graph The graph of the entire system
     * @param timeNow The time to do calculations with
     */
    public static double calcSumAvgIdleTime(Graph graph, double timeNow){
        double allAvgIdleTimes = 0.0;
        for(Node n : graph.graph){
            allAvgIdleTimes += calcAvgIdleTimeNow(n, timeNow);
        }
        return allAvgIdleTimes;
    }

    /**
     * Function: calcThetaGivenMove
     *
     * Calculates P(theta_0i | move_i) using
     *      (theta_0i) / ( Sum of every arc strength from every node to every node )
     *
     * @param theta The arc strength of the edge in question
     * @param graph The entire graph
     */
    public static double calcThetaGivenMove(double theta, Graph graph){
        double thetaGivenMove = 0.0;
        double sumOfArcStrength = 0.0;

        // Iterate over every node, then their respective neighbors. Sum the arc strengths of the current nodes in question
        for(Node source : graph.graph){
            for(Node neighbor : source.neighborNodes){
                sumOfArcStrength += source.getNeighborArc(neighbor.name);
            }
        }

        thetaGivenMove = theta / sumOfArcStrength;

        return thetaGivenMove;
    }

    /**
     * Function: calcInstantIdleTime
     *
     * Calculates the instant idle time of current node
     *
     * Equation: I_vi(t) = t - t_l
     *
     * @param nodeI Current node to do calculations with
     * @param timeNow The time to do the calculations with, same for every node.
     *                Get the time as soon as an agent gets to the node
     *
     * @return The instantaneous idle time
     */
    public static double calcInstantIdleTime(Node nodeI, double timeNow){
        return timeNow - nodeI.timeOfLastVisit;
    }

    /******************************************************************************************
     * Function: arcStrength
     *
     * Updates the arc strengths using:
     *      theta_0,i(Now) = theta_0,i(Last Time) + arc_0,i(Now)
     *      arc_0,i = S_0,i( VisitsOf_i, InstantVelocityOf_i ) * (1 - H(move | theta) )
     *
     * Check every neighbor of the source node
     *
     * @param sourceNode The node the agent is leaving from
     * @param timeNow The time the agent started calculations
     * @param entropy The previously calculated entropy value
     */
    public static void arcStrength(Node sourceNode, long timeNow, double entropy){
        // If sourceNode has 1 neighbor, no need to do anything
        if(sourceNode.numNeighbors <= 1){
            return;
        }

        // Otherwise, go through finding the node to punish and the node to reward
        // First Calculate the normalizedVisit number for every neighbor
        for(Node node : sourceNode.neighborNodes){
            node.normalizedVisit = ((double)node.numVisits / (double)node.numNeighbors);
        }

        // Next, find which nodes have the highest normalized visit value, and the lowest. Store in a list in case there are multiple
        ArrayList<Node> highestNormal = new ArrayList<>();
        ArrayList<Node> lowestNormal = new ArrayList<>();

        Node highest = sourceNode.neighborNodes.get(0);
        Node lowest = sourceNode.neighborNodes.get(0);
        for(int i = 1; i < sourceNode.numNeighbors; i++){
            if(highest.normalizedVisit < sourceNode.neighborNodes.get(i).normalizedVisit){
                highest = sourceNode.neighborNodes.get(i);
            }
            if(lowest.normalizedVisit > sourceNode.neighborNodes.get(i).normalizedVisit){
                lowest = sourceNode.neighborNodes.get(i);
            }
        }
        // calculate the instantaneous idle time
        highest.instantIdleTime = calcInstantIdleTime(highest, timeNow);
        lowest.instantIdleTime = calcInstantIdleTime(lowest, timeNow);

        // Add to the list
        highestNormal.add(highest);
        lowestNormal.add(lowest);

        // Find duplicates. Iterate over the list again, looking for equal values. Add all equal values to their respective list
        int highDups = 0;
        int lowDups = 0;
        for(Node node : sourceNode.neighborNodes){
            // If a node is equal to what already exists in the list, it is a tie
            if(node.normalizedVisit == highestNormal.get(0).normalizedVisit){
                highDups++;
            }
            if(node.normalizedVisit == lowestNormal.get(0).normalizedVisit){
                lowDups++;
            }
            // If the counters are > 1, than all subsequent equals are the duplicates. Add to the list
            if(highDups > 1){
                // calculate the instantaneous idle time of the newly found duplicate
                node.instantIdleTime = calcInstantIdleTime(node, timeNow);
                highestNormal.add(node);
            }
            if(lowDups > 1){
                node.instantIdleTime = calcInstantIdleTime(node, timeNow);
                lowestNormal.add(node);
            }
        }

        // Next, if there are duplicates, determine which has the winning instantaneous idle time
        if(highDups > 1){
            for(Node node : highestNormal){
                if(highest.instantIdleTime > node.instantIdleTime){
                    highest = node;
                }
            }
        }
        if(lowDups > 1){
            for(Node node : lowestNormal){
                if(lowest.instantIdleTime < node.instantIdleTime){
                    lowest = node;
                }
            }
        }

        // Here, we should have the highest valued node (to punish) and the lowest (to reward)
        // Next, we need to calculate entropy costs --> Given as function arg
        entropy = 1 - entropy;

        double gammaPunish = entropy * -1;
        double gammaReward = entropy * 1;
        double finalHigh = gammaPunish + sourceNode.getNeighborArc(highest.name);
        double finalLow = gammaReward + sourceNode.getNeighborArc(lowest.name);

        // Finally, set to the right values
        sourceNode.neighborArcStrengths.put(highest, finalHigh);
        sourceNode.neighborArcStrengths.put(lowest, finalLow);
    }

    public static double calcEntropy(double posterierProb){
        double logOf = Math.log(posterierProb) / Math.log(2);
        double retVal = posterierProb * logOf;

        return retVal;
    }
}
