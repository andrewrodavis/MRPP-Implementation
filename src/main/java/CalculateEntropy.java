public class CalculateEntropy {
    public static void calculateEntropyValue(int numAgents, int graphsize, Node sourceNode, Node potentialDestNode){
        // Calculate traveling to the destination node, adding the potential visit
        double probMoveToDest = (potentialDestNode.numVisits + 1.0) / (double)numAgents;

        // Calculate the entropy for all nodes - the node in question
        double summation = 0.0;
        for(Node node : sourceNode.neighborNodes){
            double numVisitsVal = ((double)node.numVisits / (double)numAgents);
            double fullVal = probMoveToDest + numVisitsVal;
            // Check if log_2(0) occurs
            if(fullVal == 0.0){
                continue;
            }
            summation += (fullVal * (Math.log(fullVal) / Math.log(2)));
//            summation += (numVisitsVal * (Math.log(numVisitsVal) / Math.log(2)));
        }
        summation = summation * -1.0;
//        summation = summation * (1.0 / graphsize);

        potentialDestNode.entropyValue = (summation + probMoveToDest) / (Math.log(sourceNode.numNeighbors) / Math.log(2));
    }
}
