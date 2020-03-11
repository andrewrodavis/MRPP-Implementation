import org.junit.Test;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;

public class TestCalculations {
    @Test
    public void testRandFuncs(){
        ArrayList<Integer> rand = new ArrayList<>();

        rand.add(1);
        rand.add(5);
        rand.add(10);
        rand.add(10);
        rand.add(8);
        rand.add(9);
        rand.add(10);
        rand.add(10);
        rand.add(10);
        rand.add(10);
        rand.add(10);
        rand.add(10);

        for(int i = 0; i < 200; i++){
            int max = rand.indexOf(Collections.max(rand));
            System.out.println("Max idx: " + max);
        }
    }
}
