import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class testStuff {


    @Test
    public void testList(){
        ArrayList<Integer> list = new ArrayList<>();

        list.add(4);
        list.add(5);
        list.add(6);
        list.add(7);
        list.add(8);

        System.out.println("Original List");
        for(int i = 0; i < list.size(); i++){
            System.out.println("Index: " + i + " -> " + list.get(i));
        }

        System.out.println("Popping...");
        for(int i = 0; i < list.size(); i++){
            int number = list.get(0);
            list.remove(0);
            System.out.println("Popped Number: " + number);
            list.add(number);
        }

        System.out.println("Post-Popped List");
        for(int i = 0; i < list.size(); i++){
            System.out.println("Index: " + i + " -> " + list.get(i));
        }

        Collections.shuffle(list);
        System.out.println("Shuffling...");
        for(int i = 0; i < list.size(); i++){
            System.out.println("Index: " + i + " -> " + list.get(i));
        }

    }
}
