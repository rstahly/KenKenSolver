import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Created by Rachel Feddersen on 3/2/2016.
 */
public class KenKenPuzzle {
    KenKenVariables variables;

    private File file;

    public KenKenPuzzle() {
        variables = new KenKenVariables();
    }

    public KenKenPuzzle(File f) {
        variables = new KenKenVariables();

        setFile(f);
    }

    /**
     * This method attempts to open the file
     * @param filename - The location of the file
     * @return - Return the new scanner with the opened file
     */
    private Scanner openFile(File filename) {
        // Open a scanner
        Scanner inputFile = null;

        // Try to create a new Scanner
        try {
            inputFile = new Scanner(filename);
            // If the file cannot be found
        } catch (FileNotFoundException e) {
            System.exit(0);
            e.printStackTrace();
        }

        return inputFile;
    }

    /**
     * This method attempts to read the file
     * @param filename - The location of the file
     * @return - Return the error if the file does not open
     */
    public boolean readFile(File filename) {
        // Open a scanner
        Scanner inputFile;
        // Create a new scanner
        inputFile = openFile(filename);
        boolean fileError = false;

        // Try to read the file
        try {
            // Read the board size and set the variable
            variables.setBoardSize(inputFile.nextInt());
            inputFile.nextLine();

            // Initialize the array for holding the values
            variables.setKenKenArray(new int[variables.getBoardSize()][variables.getBoardSize()]);

            List<String> domainList = new ArrayList<>();
            for (int i = 1; i <= variables.getBoardSize(); i++) {
                domainList.add("" + i);
            }

            // While there are more lines in the file,
            while(inputFile.hasNextLine()) {
                String[] values = inputFile.nextLine().split(" ", -1);
                // Call setInitialDomain method for setting domains for all the cells
                variables.domain.setInitialDomain(values, domainList);
                // Call createMathConst method for setting up the groups of cells based on their math equations
                variables.constraints.createMathConst(values);
            }

            // Close the file
            inputFile.close();

            // If the file does not match what the file normally would be like
        } catch (InputMismatchException e) {
            // Set the error to true
            fileError = true;
        }

        return fileError;
    }

    public void chooseValidDomain(String curCell) {
        for (String domainList: variables.domain.getKenKenDomain().get(curCell)) {
            if (variables.checkRowsCols(curCell.substring(0, 1), curCell.substring(1, 2), domainList)) {
                if (variables.mathCellValid(curCell, domainList)) {
                    variables.getKenKenArray()[Integer.parseInt(curCell.substring(0, 1))][Integer.parseInt(curCell.substring(1, 2))]
                            = Integer.parseInt(domainList);
                    break;
                }
            }
        }
    }

    public boolean doBackTracking(int currentVal, List<String> smallestDomains) {
        boolean backTrack = true;

        for (String domainList: variables.domain.getKenKenDomain().get(smallestDomains.get(0))) {
            if (Integer.parseInt(domainList) > currentVal) {
                if (variables.checkRowsCols(smallestDomains.get(0).substring(0, 1),
                        smallestDomains.get(0).substring(1, 2), domainList)) {
                    if (variables.mathCellValid(smallestDomains.get(0), domainList)) {
                        backTrack = false;

                        variables.getKenKenArray()[Integer.parseInt(smallestDomains.get(0).substring(0, 1))]
                                [Integer.parseInt(smallestDomains.get(0).substring(1, 2))] = Integer.parseInt(domainList);

                        break;
                    }
                }
            }
        }

        return backTrack;
    }

    public File getFile() {
        return file;
    }

    private void setFile(File file) {
        this.file = file;
    }
}