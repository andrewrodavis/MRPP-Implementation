import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class testNode {
    Graph g = new Graph("java/graph.csv");

    public testNode() throws IOException {
    }

    public void initNodeList() throws IOException {
        g.initGraph();
    }

    @Test
    public void testGetNeighborWeight() throws IOException {
        this.initNodeList();

        ArrayList<Integer> weights = new ArrayList<>();

        for(Node n : this.g.graph){
            for(int i = 0; i < n.numNeighbors; i++){
                weights.add(n.getNeighborWeight(n.neighborListNames.get(i)));
            }
            System.out.println("----Node: " + n.name + "----");
            for(int num : weights){
                System.out.println("Weight: " + num);
            }
            System.out.println();
        }
    }
}
