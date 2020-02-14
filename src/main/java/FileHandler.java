import java.io.*;
import java.util.*;

public class FileHandler {

    // File object
    public File f;

    public FileHandler(String graphFile) {
        f = new File(graphFile);
    }

    /**
     * Function: readFromFile
     *
     * Reads data, line-by-line from the file object
     *
     */
    public Queue<String> readFromFile() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(this.f));
        String line;

        // Return Array
        Queue<String> lines = new LinkedList<>();

        // Utility for add delimiter for neighbor list
        List<Integer> numNeighbors = new ArrayList<>();

        Scanner scanner = new Scanner(new File(String.valueOf(this.f)));
        scanner.useDelimiter(",");

        // Get the first line == number of vertices and add delimiter
        lines.add(scanner.next());
        lines.add("-");

        // Get vertex list
        for(int i = 0; i < Integer.parseInt(lines.peek()); i++){
            String current = scanner.next().replaceAll("\r","").replace("\n","");
            lines.add(current);
        }
        // Add delimiter and get number of neighbors list
        lines.add("-");
        for(int i = 0; i < Integer.parseInt(lines.peek()); i++){
            String current = scanner.next().replaceAll("\r","").replace("\n","");

            // Add the number of neighbors
            lines.add(current);

            // Store 2*number of neighbors for utility later
            numNeighbors.add(Integer.parseInt(current) * 2);
        }
        // Add connections and delimiter
        lines.add("-");
        for(int i = 0; i < Integer.parseInt(lines.peek()); i++){
            // Add exception catch in case scanner out of range. If this is the case, the .csv file is formatted incorrectly
            for(int j = 0; j < numNeighbors.get(i); j++){
                String current = scanner.next().replaceAll("\r","").replace("\n","");
                lines.add(current);
            }
            lines.add("-");
        }

        scanner.close();

        return lines;
    }

    /**
     * Function: writeToFile
     *
     * Writes the input data to the specified file
     *
     * @param
     */
    public void writeToFile(){

    }
}
