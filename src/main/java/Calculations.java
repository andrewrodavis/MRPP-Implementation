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
        double probMove = calcProbMove(graph, destNode, timeNow);

        // Need to get arc strength from source node to node i
        getArcStrength(sourceNode, destNode);

        return 0.0;
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
        double numerator = (calcAvgIdleTimeAtLastVisit(nodeI) * nodeI.numVisits) + calcInstantIdleTime(nodeI, timeNow);
        double denom = nodeI.numVisits + 1;

        return numerator / denom;
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
     * Function: calcAvgIdleTimeAtLastVisit
     *
     * Calculates the average idle time at the last visit time of the node
     *
     * @param nodeI The current node
     */
    public static double calcAvgIdleTimeAtLastVisit(Node nodeI){

        return 0.0;
    }

    /**
     * Function: getcArcStrength
     *
     * Gets the arc strength from node A to B
     *
     * @param sourceNode
     * @param destNode
     */
    public static double getArcStrength(Node sourceNode, Node destNode){

        return 0.0;
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

        return 0.0;
    }


}
