import java.io.IOException;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.*;

/**
 * Class: Graph
 *
 * Maintains a list of nodes that make up the entire graph
 */
public class Graph {
    // The graph to be maintained and used
    public List<Node> graph = new ArrayList<Node>();
    public int numNodes;
    public double instantIdleTime;
    public double avgIdleTime;

    // Hash to maintain vertex and index in graph for quick access
    public HashMap<String, Integer> nodeIdx = new HashMap<String, Integer>();

    FileHandler graphFile;

    /**
     * Constructor
     *
     *
     * @param graphFile text file to create the graph from as argument
     */
    public Graph(String graphFile) throws IOException {
        this.graphFile = new FileHandler(graphFile);
    }

    /**
     * Function: initGraph
     *
     * Initializes the graph by
     *      Filling this.graph with a node, each of which maintains its own neighbors list and weights
     *      (Maybe, depending on implementation) Initializes arc strength of each edge
     *
     * Format of input from file reading:
     *      [ NumberOfNodes-Node List-Number of neighbors for each node-Node 1: Node/weight Node/weight-Node2: Node/weight Node/weight... ]
     */
    public void initGraph() throws IOException {
        // Read the lines from the file
        Queue<String> linesFromFile = graphFile.readFromFile();

        // Get the number of nodes
        this.numNodes = Integer.parseInt(linesFromFile.remove());

        // Initialize the nodes and number of neighbors
        for(int i = 0; i < 2; i++){
            for(int j = 0; j < this.numNodes; j++){
                String currVal = linesFromFile.remove();

                // Skip delimiter
                if(currVal == "-"){
                    j--;
                    continue;
                }
                // Need to add all new nodes first
                if(i == 0){
                    // Add the new node
                    this.graph.add(new Node((currVal)));

                    // Map the index of the node
                    this.nodeIdx.put(currVal, j);
                }
                // Add num neighbors
                else{
                    this.graph.get(j).numNeighbors = Integer.parseInt(currVal);
                }
            }
        }

        // Add neighbor lists, and the average idle time at the last visit = 0
        for(int i = 0; i < this.numNodes; i++){
            if(linesFromFile.peek() == "-"){
                i--;
                linesFromFile.remove();
                continue;
            }

            //Iterate over the number of neighbors for the current node. Add the neighbor node object and weight to the hashmap of Node
            for(int j = 0; j < this.graph.get(i).numNeighbors; j++){
                // Get the name and weight from the file
                String name = linesFromFile.remove();
                double weight = Double.parseDouble(linesFromFile.remove());

                int adjustedWeight = (int)(weight * 10.0);

                // Find the node from the name
                int index = this.nodeIdx.get(name);

                // Add that node to the hash list used for fast weight getting, and name list
                this.graph.get(i).neighborList.put(this.graph.get(index), adjustedWeight);//, weight * 10.0);
                this.graph.get(i).neighborListNames.add(name);
                this.graph.get(i).neighborNodes.add(this.graph.get(index));

                // Add the initial arc length values
                this.graph.get(i).neighborArcStrengths.put(this.graph.get(index), 1.0);

//                this.graph.get(i).neighborList.put(name, weight);
            }
        }
    }

    /**
     * Function: getNodeIdx
     *
     * @param node The specified node to look for, named
     * @return Index of the desired node
     *      -1 on error
     */
    public int getNodeIdx(String node){
        return this.nodeIdx.getOrDefault(node, -1);
    }

    /**
     * Function: displayGraph
     *
     * When called, shows the graph in the following format:
     * Vertex  |  Neighbor (weight)
     *
     * @implNote Maybe put this in test
     *      Figure out spacing for pretty-printing
     * @sources: https://stackoverflow.com/questions/1066589/iterate-through-a-hashmap
     */
    public void displayGraph(){
        System.out.println("Generated Graph\n");
        for(int i = 0; i < this.numNodes; i++){
            System.out.println("------ Vertex: " + this.graph.get(i).name + " ------");
            System.out.println("Neighbor\t\t|Weight");

            Iterator it = this.graph.get(i).neighborList.entrySet().iterator();
            while(it.hasNext()){
                Map.Entry pair = (Map.Entry)it.next();
                Node node = (Node) pair.getKey();
                System.out.println(node.name + "\t\t\t\t|" + pair.getValue());
            }

            System.out.println("Neighbor\t\t|Arc Strength");
            it = this.graph.get(i).neighborArcStrengths.entrySet().iterator();
            while(it.hasNext()){
                Map.Entry pair = (Map.Entry)it.next();
                Node node = (Node) pair.getKey();
                System.out.println(node.name + "\t\t\t\t|" + pair.getValue());
            }
        }
    }

    /**
     * getMaxArcStrengthNode/getMinArcStrengthNode
     *
     * Variation on something similar to Collections.max()
     * Finds the node with the largest neighbor arc strength for the current node's neighbor list
     *
     * @param sourceNode The node to start searching from
     * @return
     */
    public Node getMaxArcStrengthNode(Node sourceNode){
        double maxValue = Double.MIN_VALUE;
        Node highestNode = null;

        for(Node node : sourceNode.neighborNodes){
            if(node.normalizedVisit > maxValue){
                highestNode = node;
            }
        }
        return highestNode;
    }
    /**********************************************************/
    public Node getMinArcStrengthNode(Node sourceNode){
        double minValue = Double.MAX_VALUE;
        Node lowestNode = null;

        for(Node node : sourceNode.neighborNodes){
            if(node.normalizedVisit < minValue){
                lowestNode = node;
            }
        }
        return lowestNode;
    }

    /**
     * function: getMaxDegreeOfBelief
     *
     * Variation on something similar to Collections.max()
     * Finds the node that is maximal in the given list based on the degrees of belief
     * Different from above two due to the list passed in here being smaller on subsequent calls
     *
     * @param potentialList
     * @return
     */
    public Node getMaxDegreeOfBelief(ArrayList<Node> potentialList){
        if(potentialList == null){
            return null;
        }
        double maxValue = Double.MIN_VALUE;
        Node highestNode = null;

        for(Node node : potentialList){
            if(node.degOfBelief > maxValue){
                highestNode = node;
                maxValue = node.degOfBelief;
            }
        }
        return highestNode;
    }

    /**
     * Function: getMaxEntropy/getMinEntropy
     *
     * Same-ish as above but for entropy
     * This adds a tiebreaker
     *
     * @param potentialList
     * @return
     */
    public Node getMaxEntropy(ArrayList<Node> potentialList) {
        // If empty, no more available moves
        if (potentialList.size() == 0) {
            return null;
        }
        // If 1, just return it already
        if(potentialList.size() == 1){
            return potentialList.get(0);
        }

        Node moveNode = potentialList.get(0);
        boolean ties = false;
        ArrayList<Node> tiedNodes = new ArrayList<>();

        // Get the highest
        for (Node node : potentialList) {
            if (moveNode.entropyValue < node.entropyValue) {
                moveNode = node;
            }
        }

        // Then check for ties
        for (Node node : potentialList) {
            if (moveNode.entropyValue == node.entropyValue && !(moveNode.name.equals(node.name))) {
                tiedNodes.add(node);
                ties = true;
            }
        }

        // If a tie, get the one with the smaller last visit time, and therefore the one that has been visited the furthest in the past
        if (ties) {
            for (Node node : tiedNodes) {
                if (moveNode.timeOfLastVisit > node.timeOfLastVisit) {
                    moveNode = node;
                }
            }
        }

        return moveNode;
    }
    /**********************************************************/
    public Node getMinEntropy(ArrayList<Node> potentialList) {
        // If empty, no more available moves
        if (potentialList.size() == 0) {
            return null;
        }
        // If 1, just return it already
        if(potentialList.size() == 1){
            return potentialList.get(0);
        }

        Node moveNode = potentialList.get(0);
        boolean ties = false;
        ArrayList<Node> tiedNodes = new ArrayList<>();

        // Get the highest
        for (Node node : potentialList) {
            if (moveNode.entropyValue > node.entropyValue) {
                moveNode = node;
            }
        }

        // Then check for ties
        for (Node node : potentialList) {
            if (moveNode.entropyValue == node.entropyValue && !(moveNode.name.equals(node.name))) {
                tiedNodes.add(node);
                ties = true;
            }
        }

        // If a tie, get the one with the smaller last visit time, and therefore the one that has been visited the furthest in the past
        if (ties) {
            for (Node node : tiedNodes) {
                if (moveNode.timeOfLastVisit < node.timeOfLastVisit) {
                    moveNode = node;
                }
            }
        }

        return moveNode;
    }
}
