import org.junit.Test;
import org.testng.annotations.AfterTest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class TestFileHandler {
    FileHandler f = new FileHandler("src/main/java/graph.csv");
    Queue<String> testInput = new LinkedList<>();

    /**
     * Function: testReadFromFile
     *
     *
     */
    @Test
    public void testReadFromFile() throws IOException {

        testInput = f.readFromFile();

        for(int i = 0; i < testInput.size(); i++){
            System.out.println(testInput.remove());
        }
    }
}
