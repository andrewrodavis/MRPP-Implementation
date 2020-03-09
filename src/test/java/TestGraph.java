import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestGraph {
    Graph g = new Graph("src/test/java/graph.csv");

    // Test Parameters
    int numNodes = 6;
    String nodeList[] = {"A", "B", "C", "D", "E", "F"};
    String neighbors[] = {"B", "D", "A", "C", "F", "B", "E", "F", "A", "F", "C", "F", "B", "C", "D", "E"};
    int weights[] = {3, 5, 3, 600, 3, 600, 3, 5, 5, 9, 3, 10, 3, 5, 9, 10};
    double arcs[] = new double[neighbors.length];

    public TestGraph() throws IOException {
    }

    @Test
    public void testInitGraph() throws IOException {
        Arrays.fill(this.arcs, 1.0);
        int numFailures = 0;

        System.out.println("dir: " + System.getProperty("user.dir"));

        System.out.println("===== Testing Init Graph =====");
        g.initGraph();

        // Check size and node name
        System.out.println("--Checking Num Nodes--");
        if(g.numNodes != this.numNodes){
            System.out.println("\t!! Number of nodes incorrect");
            System.out.println("\tInit Graph: " + g.numNodes);
            System.out.println("\tExpected Value: " + this.numNodes + "\n");
            numFailures++;
        }
        System.out.println("--Checking Node List--");
        for(int i = 0; i < this.nodeList.length; i++){
            if(!(this.nodeList[i].equals(g.graph.get(i).name))){
                System.out.println("\t!! Name of node incorrect");
                System.out.println("\tInit Graph: " + g.graph.get(i).name);
                System.out.println("\tExpected Value: " + this.nodeList[i] + "\n");
                numFailures++;
            }
        }
        System.out.println("--Checking Neighbor List--");
        int k = 0;
        for(int i = 0; i < g.graph.size(); i++){
            for(int j = 0; j < g.graph.get(i).numNeighbors; j++){
                if(!(g.graph.get(i).neighborListNames.get(j)).equals(this.neighbors[k])){
                    System.out.println("\t!! A neighbor was incorrect");
                    System.out.println("\tInit Graph: " + g.graph.get(i).neighborListNames.get(j));
                    System.out.println("\tExpected Value: " + this.neighbors[k] + "\n");
                    numFailures++;
                }
                k++;
            }
        }
        System.out.println("--Checking Neighbor Weights; getNeighborWeight()--");
        k = 0;
        for(int i = 0; i < g.graph.size(); i++){
            for(int j = 0; j < g.graph.get(i).numNeighbors; j++){
                String n = g.graph.get(i).neighborNodes.get(j).name;
                if(!(g.graph.get(i).getNeighborWeight(n) == this.weights[k] * 10)){
                    System.out.println("\t!! A weight was incorrect");
                    System.out.println("\tInit Graph: " + g.graph.get(i).getNeighborWeight(n));
                    System.out.println("\tExpected Value: " + this.weights[k] + "\n");
                    numFailures++;
                }
                k++;
            }
        }
        System.out.println("--Checking Neighbor Arc Strengths; getNeighborArc()--");
        k = 0;
        for(int i = 0; i < g.graph.size(); i++){
            for(int j = 0; j < g.graph.get(i).numNeighbors; j++){
                String n = g.graph.get(i).neighborNodes.get(j).name;
                if(!(g.graph.get(i).getNeighborArc(n) == this.arcs[k])){
                    System.out.println("\t!! An Arc was incorrect");
                    System.out.println("\tInit Graph: " + g.graph.get(i).getNeighborArc(n));
                    System.out.println("\tExpected Value: " + this.arcs[k] + "\n");
                    numFailures++;
                }
                k++;
            }
        }
        System.out.println("--Checking initial settings correct");
        for(Node n : g.graph){
            if(n.timeOfLastVisit != 0){
                System.out.println("Time of Last Visit Incorrect: " + n.timeOfLastVisit + "\n");
                numFailures++;
            }
            if(n.instantIdleTime != 0){
                System.out.println("Instant Idle Time Incorrect: " + n.instantIdleTime + "\n");
                numFailures++;
            }
            if(n.avgIdleTimeNow != 0){
                System.out.println("Average Idle Time Now Incorrect: " + n.avgIdleTimeNow + "\n");
                numFailures++;
            }
            if(n.avgIdleTimeLastVisit != 0){
                System.out.println("Average Idle Time of Last Visit: " + n.avgIdleTimeLastVisit + "\n");
                numFailures++;
            }
            if(n.numVisits != 0){
                System.out.println("Number of Visits Incorrect: " + n.numVisits + "\n");
                numFailures++;
            }
            if(n.alreadyDeclared){
                System.out.println("Already Declared Incorrect");
                numFailures++;
            }
        }

        System.out.println("Failures: " + numFailures);
        System.out.println("===== Testing Init Graph Complete =====");
//        g.displayGraph();
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
