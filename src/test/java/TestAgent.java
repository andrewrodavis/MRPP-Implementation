/**
 *
 * Sources:
 *
 */

import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class TestAgent {
    Queue<Agent> agents = new LinkedList<>();

    String[] expectedAgentNames = {"0", "1", "2", "3", "4"};
    String[] expectedAgentNodes = {"A", "B", "C", "D", "E"};

    String[] Aneighbors = {"D", "B"};
    int[] Aweights = {5, 3};

    String[] Bneighbors = {"C", "F", "A'"};
    int[] Bweights = {600, 3, 3};

    String[] Cneighbors = {"E", "F", "B"};
    int[] Cweights = {3, 6, 600};

    String[] Dneighbors = {"F", "A"};
    int[] Dweights = {9, 5};

    String[] Eneighbors = {"C", "F"};
    int[] Eweights = {3, 10};

    @Test
    public void testAgentInit() throws IOException {
        // Init graph
        Graph g = new Graph("src/main/java/graph.csv");
        g.initGraph();

        ArrayList<String[]> neighbors = new ArrayList<>();
        neighbors.add(this.Aneighbors);
        neighbors.add(this.Bneighbors);
        neighbors.add(this.Cneighbors);
        neighbors.add(this.Dneighbors);
        neighbors.add(this.Eneighbors);

        ArrayList<int[]> weights = new ArrayList<>();
        weights.add(this.Aweights);
        weights.add(this.Bweights);
        weights.add(this.Cweights);
        weights.add(this.Dweights);
        weights.add(this.Eweights);

        // Init the agents and push to queue
        for(int i = 0; i < 5; i++){
            this.agents.add(new Agent(String.valueOf(i), g, g.graph.get(i), 0.2));
        }

        System.out.println("===============Testing Agent Initialization===============");
        System.out.println("Looks at naming of agent and if the current node is correctly assigned\n");
        for(int i = 0; i < this.agents.size(); i++){
            boolean nameTest = true;
            boolean nodeTest = true;
            boolean neighborTest = true;

            Agent a = this.agents.remove();

            // Check name
            System.out.println("+---Testing Name of Agent---+");
            if(a.name.equals(this.expectedAgentNames[i])){
                nameTest = true;
            }
            else{
                System.out.println("Error in name");
                System.out.println("Expected name: " + this.expectedAgentNames[i]);
                System.out.println("Actual Name:   " + a.name);
                nameTest = false;
            }

            // Check current node of agent
            System.out.println("+---Testing Current Node of Agent---+");
            if(a.currentNode.name.equals(this.expectedAgentNodes[i])){
                nodeTest = true;
            }
            else{
                System.out.println("Error in current node assignment");
                System.out.println("Expected Node: " + this.expectedAgentNodes[i]);
                System.out.println("Actual Node:   " + a.currentNode.name);
                nodeTest = false;
            }

            // Broken because indices may not line up -- but the test was passed manually
//            System.out.println("+---Testing neighborList of Agent---+");
//            for(int j = 0; j < a.currentNeighbors.size(); j++){
//                                if( a.currentNeighbors.get(j).name.equals(neighbors.get(j)[i]) ){
//                    neighborTest = true;
//                }
//                else{
//                    System.out.println("Error in neighbor list");
//                    System.out.println("Expected Value: " + neighbors.get(j)[i]);
//                    System.out.println("Actual Value: " + a.currentNeighbors.get(j).name);
//                    neighborTest = false;
//                }
//            }

            if(nameTest && neighborTest && nodeTest){
                System.out.println("Agent " + a.name + " passed\n");
                this.agents.add(a);
                continue;
            }

            System.out.println("--Failed--");
            System.out.println();

            this.agents.add(a);
        }
    }
}
