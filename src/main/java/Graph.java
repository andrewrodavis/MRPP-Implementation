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
     */
    public void initGraph() throws IOException {
        // Read the lines from the file
        Queue<String> linesFromFile = graphFile.readFromFile();

        // Get the number of nodes
        this.numNodes = Integer.parseInt(linesFromFile.remove());

        // Initialize the nodes, num neighbors
        for(int i = 0; i < 2; i++){
            for(int j = 0; j < this.numNodes; j++){
                String currVal = linesFromFile.remove();

                // Skip delimiter
                if(currVal == "-"){
                    j--;
                    continue;
                }
                // Add new Node()
                if(i == 0){
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

            // Add the average idle time of last visit, of now, time of last visit, and instant idle time
            this.graph.get(i).avgIdleTimeLastVisit = this.graph.get(i).avgIdleTimeNow = this.graph.get(i).timeOfLastVisit = this.graph.get(i).instantIdleTime = 0;

            //Iterate over the number of neighbors for the current node. Add the neighbor node object and weight to the hashmap of Node
            for(int j = 0; j < this.graph.get(i).numNeighbors; j++){
                // Get the name and weight from the file
                String name = linesFromFile.remove();
                int weight = Integer.parseInt(linesFromFile.remove());

                // Find the node from the name
                int index = this.nodeIdx.get(name);

                // Add that node to the hash list used for fast weight getting, and name list
                this.graph.get(i).neighborList.put(this.graph.get(index), weight * 10);
                this.graph.get(i).neighborListNames.add(name);
                this.graph.get(i).neighborNodeObjs.add(this.graph.get(index));

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

}
