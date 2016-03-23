import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import java.util.List;

/**
 * Created by Rachel Feddersen on 3/2/2016.
 */
public class KenKenDriver extends JFrame {
    int win_wid = 250;
    int win_hei = 300;
    boolean solvingWithConst = false;
    boolean solvingWithOutConst = false;
    private List<String> smallestDomains = new LinkedList<>();
    private List<String> assignedOrder = new LinkedList<>();
    private int domainSize = 1;

    KenKenPuzzle puzzle;
    KenKenDisplay display;

    private File file;
    private JMenuItem loadFileItem;
    private JMenuItem exitMenuItem;
    private JMenuItem solveWithConst;
    private JMenuItem solveWithOutConst;

    public KenKenDriver() {
        this.setTitle("KenKen Solver");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(win_wid, win_hei);

        puzzle = new KenKenPuzzle();
        display = new KenKenDisplay(puzzle);

        // Create the menu bar and put it in the window
        JMenuBar bar = buildPuzzleBar();
        setJMenuBar(bar);

        this.add(display);
        this.setVisible(true);// Set the title and close operation

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (solvingWithConst) {
                    boolean valuesRemoved = doWithConstraint();

                    if (!valuesRemoved && !puzzle.variables.solved()) {
                        doWithOutConstraint();
                    } else if (puzzle.variables.solved()) {
                        solvingWithConst = false;
                    }

                    repaint();
                } else if (solvingWithOutConst) {
                    if (!puzzle.variables.solved()) {
                        doWithOutConstraint();

                        repaint();
                    } else if (puzzle.variables.solved()) {
                        solvingWithOutConst = false;
                    }
                }
            }
        });
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode()==KeyEvent.VK_ENTER && solvingWithConst) {
                    boolean valuesRemoved = doWithConstraint();

                    if (!valuesRemoved && !puzzle.variables.solved()) {
                        doWithOutConstraint();
                    } else if (puzzle.variables.solved()) {
                        solvingWithConst = false;
                    }

                    repaint();
                } else if (e.getKeyCode()==KeyEvent.VK_ENTER && solvingWithOutConst) {
                    if (!puzzle.variables.solved()) {
                        doWithOutConstraint();
                    } else if (puzzle.variables.solved()) {
                        solvingWithOutConst = false;
                    }

                    repaint();
                }
            }
        });

        // Set the menu options to be disabled
        solveWithConst.setEnabled(false);
        solveWithOutConst.setEnabled(false);
    }

    /**
     * The method sets up the menu bar that has the different menu options like file, map, search
     * types, and village distances
     * @return - Return the assembled menu bar
     */
    private JMenuBar buildPuzzleBar(){
        // Create a menu bar
        JMenuBar menuBar = new JMenuBar();

        // Create and add the menu options for the menu bar
        JMenu fileMenu = buildFileMenu();
        JMenu routeMenu = buildPuzzleMenu();
        menuBar.add(fileMenu);
        menuBar.add(routeMenu);

        return menuBar;
    }

    /**
     * The method for creating the options for the file menu item
     * @return - The completed fileMenu
     */
    private JMenu buildFileMenu(){
        // Create
        JMenu fileMenu = new JMenu("File");

        // Create the menu items
        solveWithConst = new JMenuItem("Solve Puzzle w/ Constraint");
        solveWithOutConst = new JMenuItem("Solve Puzzle w/out Constraint");
        exitMenuItem = new JMenuItem("Exit");

        // Add these menu items into fileMenu
        fileMenu.add(solveWithConst);
        fileMenu.add(solveWithOutConst);
        fileMenu.addSeparator();
        fileMenu.add(exitMenuItem);

        // Hook up the menu items with the listener
        MyListener listener = new MyListener();
        solveWithConst.addActionListener(listener);
        solveWithOutConst.addActionListener(listener);
        exitMenuItem.addActionListener(listener);

        return fileMenu;
    }

    /**
     * The method for creating the options for the map menu item
     * @return - The completed mapMenu
     */
    private JMenu buildPuzzleMenu(){
        // Create
        JMenu routeMenu = new JMenu("KenKen Puzzle");

        // Create the menu items
        loadFileItem = new JMenuItem("Load File");

        // Add these menu items into routeMenu
        routeMenu.add(loadFileItem);

        // Hook up the menu items with the listener
        MyListener listener = new MyListener();
        loadFileItem.addActionListener(listener);

        return routeMenu;
    }

    /**
     * This is the private class for the action listener
     * @author Rachel Feddersen
     * It has the actionPerformed method inside of it so it can listen to what the user is doing
     * and respond accordingly
     */
    private class MyListener implements ActionListener {
        /**
         * The actionPerformed method that gets the action that was performed and does the
         * corresponding action
         */
        public void actionPerformed(ActionEvent e) {
            boolean fileError = false;

            // If the user clicks on the exitMenuItem
            if (e.getSource() == exitMenuItem) {
                // End the program
                System.exit(0);
                // If the user clicks on the loadFileItem
            } else if (e.getSource() == loadFileItem) {
                fileError = getFileInformation();
                display.setPuzzle(puzzle);
                display.repaint();

                // Reset the window size based on the board size
                win_wid = (puzzle.variables.getBoardSize() + 1) * 50 + 5;
                win_hei = (puzzle.variables.getBoardSize() + 2) * 50 + 5;
                setSize(win_wid, win_hei);
                // If the user clicks on the solveWithConst option
            } else if (e.getSource() == solveWithConst) {
                solvingWithConst = true;
                domainSize = 1;
                // If the user clicks on the solveWithOutConst option
            } else if (e.getSource() == solveWithOutConst) {
                solvingWithOutConst = true;
                domainSize = 1;
            }

            // Call method to enable options
            setEnabledOptions(e, fileError);
        }
    }

    /**
     * The method for getting the file that the user choose and calling methods to retrieve
     * the files information
     * @return - Return the file error
     */
    public boolean getFileInformation() {
        // Show a dialog to allow the user to choose files
        JFileChooser fc = new JFileChooser("./");  //set starting point
        int status = fc.showOpenDialog(null);
        boolean fileError = false;
        // If the user actually chose a file
        if (status == JFileChooser.APPROVE_OPTION){
            // Get the selected file
            file = fc.getSelectedFile();

            // Create a new routeFinder class and get the TreeMap
            puzzle = new KenKenPuzzle(file);
            fileError = puzzle.readFile(file);

            // If there was not an error with the file
            if (fileError) {
                //searchResults.setText("The file chosen does not contain the correct information. Please choose a new one.");
                // Disable the options that might have been enabled
                solveWithConst.setEnabled(false);
                solveWithOutConst.setEnabled(false);
            }
        }

        return fileError;
    }

    /**
     * The method that re-enables the options the user has to pick from on the menu bar
     * @param e - The ActionEvent variable that can be used to determine what action just occured
     */
    public void setEnabledOptions(ActionEvent e, boolean fileError) {
        // If a file has been chosen and a search of distance option has not been chosen
        if (file != null && e.getSource() != solveWithConst && e.getSource() != solveWithOutConst
                && !fileError) {
            // Enable all of the options
            solveWithConst.setEnabled(true);
            solveWithOutConst.setEnabled(true);
        } else if (file != null && (e.getSource() == solveWithConst || e.getSource() == solveWithOutConst)) {
            // Disable the options not related to getting the file
            solveWithConst.setEnabled(false);
            solveWithOutConst.setEnabled(false);
        }
    }

    public boolean doWithConstraint() {
        puzzle.variables.removeMathValues();

        boolean valuesRemoved = constraintSearch();

        return valuesRemoved;
    }

    public void doWithOutConstraint() {
        if (smallestDomains.size() == 0) {
            do {
                domainSize += 1;
                for(Map.Entry<String, java.util.List<String>> entry : puzzle.variables.domain.getKenKenDomain().entrySet()) {
                    if (entry.getValue().size() == domainSize) {
                        smallestDomains.add(entry.getKey());
                    }
                }
            } while (smallestDomains.size() == 0);
        } else {
            performSearch();
        }
    }

    public boolean constraintSearch() {
        boolean valuesRemoved = false;

        for(Map.Entry<String, java.util.List<String>> entry : puzzle.variables.domain.getKenKenDomain().entrySet()) {
            if (entry.getValue().size() == 1) {
                String row = entry.getKey().substring(0, 1),
                       col = entry.getKey().substring(1, 2);
                if (puzzle.variables.getKenKenArray()[Integer.parseInt(row)][Integer.parseInt(col)] == 0) {
                    puzzle.variables.performNodeConsistency(row, col, entry.getValue().get(0));
                    puzzle.variables.getKenKenArray()[Integer.parseInt(row)][Integer.parseInt(col)]
                           = Integer.parseInt(entry.getValue().get(0).substring(0, 1));
                    valuesRemoved = true;
                }
            }
        }

        return valuesRemoved;
    }

    public void performSearch() {
        boolean backTrack;

        assignedOrder.add(smallestDomains.get(0));
        smallestDomains.remove(0);
        String curCell = assignedOrder.get(assignedOrder.size()-1);
        puzzle.chooseValidDomain(curCell);

        if (puzzle.variables.getKenKenArray()[Integer.parseInt(curCell.substring(0, 1))][Integer.parseInt(curCell.substring(1, 2))] == 0) {
            backTrack = true;
            int currentVal;

            smallestDomains.add(0, assignedOrder.get(assignedOrder.size()-1));
            assignedOrder.remove(assignedOrder.size()-1);

            while(backTrack) {
                smallestDomains.add(0, assignedOrder.get(assignedOrder.size()-1));
                assignedOrder.remove(assignedOrder.size()-1);

                currentVal = puzzle.variables.getKenKenArray()[Integer.parseInt(smallestDomains.get(0).substring(0, 1))]
                        [Integer.parseInt(smallestDomains.get(0).substring(1, 2))];

                // Reset this cell assignment
                puzzle.variables.getKenKenArray()[Integer.parseInt(smallestDomains.get(0).substring(0, 1))]
                        [Integer.parseInt(smallestDomains.get(0).substring(1, 2))] = 0;

                backTrack = puzzle.doBackTracking(currentVal, smallestDomains);

                if (!backTrack) {
                    assignedOrder.add(smallestDomains.get(0));
                    smallestDomains.remove(0);
                }
            }
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new KenKenDriver();
    }
}