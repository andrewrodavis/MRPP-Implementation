import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TestGraph {
    Graph g = new Graph("src/main/java/graph.csv");

    public TestGraph() throws IOException {
    }

    @Test
    public void testInitGraph() throws IOException {
        g.initGraph();
        g.displayGraph();
    }

    @Test
    public void testGetNodeIdx() throws IOException {
        g.initGraph();
        int diff = 0;       // Difference value
        String names[] = {"A","B","C","D","E","F","G"};

        // Expected Values
        List<Integer> expVals = new ArrayList<>();
        for(int i = 0; i < 6; i++){
            expVals.add(i);
        }
        expVals.add(-1);    // Error value for G

        // Test Values
        List<Integer> idxList = new ArrayList<>();
        for(int i = 0; i < names.length; i++){
            idxList.add(g.getNodeIdx(names[i]));
        }

        for(int i = 0; i < idxList.size(); i++){
            if(idxList.get(i) != expVals.get(i)){
                diff++;
            }
        }

        if(diff > 0){
            System.out.println("Incorrect Behavior");
            for(int i = 0; i < idxList.size(); i++){
                System.out.println("Pos: " + i + "\tIdx: " + idxList.get(i));
            }
        }
        else{
            System.out.println("Expected Behavior");
        }
    }
}
