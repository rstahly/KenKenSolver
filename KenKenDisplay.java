import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.TreeSet;

/**
 * Created by Rachel Feddersen on 3/2/2016.
 */
public class KenKenDisplay extends JPanel {
    KenKenPuzzle puzzle;
    int cellSize = 50;
    int divWidX = 2;
    int divWidY = 2;
    int start_X = 8;
    int start_Y = 11;
    int letterOffSet_Y = 43;
    int letterOffSet_X = 20;
    int numberOffSet_Y = 20;
    int numberOffSet_X = 10;

    Color[] colors = {Color.white};

    Font bigFont = new Font("Arial", Font.PLAIN, 30);
    Font smallFont = new Font("Arial", Font.BOLD, 15);

    public KenKenDisplay(KenKenPuzzle p) {
        puzzle = p;
    }

    public void setPuzzle(KenKenPuzzle p) {
        puzzle = p;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLACK);
        g.fillRect(start_X,
                start_Y,
                (cellSize + divWidX) * puzzle.variables.getBoardSize() + divWidX + 8,
                (cellSize + divWidY) * puzzle.variables.getBoardSize() + divWidY + 8);

        for (int row = 0; row < puzzle.variables.getBoardSize(); row++) {
            for (int col = 0; col < puzzle.variables.getBoardSize(); col++) {
                g.setColor(colors[0]);
                int fromStartX = start_X;
                int fromStartY = start_Y;
                if (row == 0) {
                    divWidY = 6;
                    fromStartY += divWidY;
                } else {
                    divWidY = 2;
                    fromStartY += divWidY + 4;
                }
                if (col == 0) {
                    divWidX = 6;
                    fromStartX += divWidX;
                } else {
                    divWidX = 2;
                    fromStartX += divWidX + 4;
                }
                g.fillRect(fromStartX + (cellSize+divWidX) * col,
                        fromStartY + (cellSize+divWidY) * row,
                        cellSize,
                        cellSize);

                if (puzzle.variables.getKenKenArray()[row][col] != 0) {
                    g.setColor(Color.BLACK);
                    g.setFont(bigFont);
                    g.drawString("" + puzzle.variables.getKenKenArray()[row][col],
                            start_X + divWidX + (cellSize + divWidX) * col + letterOffSet_X,
                            start_Y + divWidY + (cellSize + divWidY) * row + letterOffSet_Y);
                }
            }
        }

        if (puzzle.getFile() != null) {
            createInitialBoardDisplay(g);
        }
    }

    public void createInitialBoardDisplay(Graphics g) {
        TreeSet<String> cellGroup;
        TreeSet<String> farthestRow = new TreeSet<>();
        TreeSet<String> farthestCol = new TreeSet<>();
        TreeSet<String> mostNorthCell = new TreeSet<>();

        for(Map.Entry<String,String[]> entry : puzzle.variables.constraints.getMathConstraints().entrySet()) {
            cellGroup = new TreeSet<>();
            String[] value = entry.getValue();

            cellGroup.add(entry.getKey());
            for (int i = 0; i < value.length-1; i++) {
                cellGroup.add(value[i]);
            }

            farthestCol.addAll(setMathBorders(cellGroup, farthestCol, 0, 1, 1, 2));

            farthestRow.addAll(setMathBorders(cellGroup, farthestRow, 1, 2, 0, 1));

            mostNorthCell.add(cellGroup.first());
        }

        g.setColor(Color.BLACK);

        for (String colDivides: farthestCol) {
            g.fillRect(start_X + 2 + (cellSize+divWidX) + (cellSize+divWidX) * Integer.parseInt(colDivides.substring(1,2)),
                    start_Y + 2 + (cellSize+divWidY) * Integer.parseInt(colDivides.substring(0,1)),
                    6,
                    56);
        }

        for (String rowDivides: farthestRow) {
            g.fillRect(start_X + 2 + (cellSize+divWidX) * Integer.parseInt(rowDivides.substring(1,2)),
                    start_Y + 2 + (cellSize+divWidY) + (cellSize+divWidY) * Integer.parseInt(rowDivides.substring(0,1)),
                    56,
                    6);
        }

        for (String numberCells: mostNorthCell) {
            g.setFont(smallFont);
            String mathEquation = "";

            for(Map.Entry<String,String[]> entry : puzzle.variables.constraints.getMathConstraints().entrySet()) {
                if (entry.getKey().equals(numberCells)) {
                    for (String cells: entry.getValue()) {
                        String endChar = cells.substring(cells.length()-1, cells.length());
                        if (endChar.equals("+") || endChar.equals("-")) {
                            mathEquation = cells;
                        } else if (endChar.equalsIgnoreCase("x")) {
                            mathEquation = cells.substring(0, cells.length()-1) + "x";
                        } else if (endChar.equals("/")) {
                            mathEquation = cells.substring(0, cells.length()-1) + "\u00F7";
                        } else if (endChar.equals("=")) {
                            mathEquation = cells.substring(0, 1);
                        }
                    }
                } else {
                    for (String testCells: entry.getValue()) {
                        if (testCells.equals(numberCells)) {
                            for (String cells: entry.getValue()) {
                                String endChar = cells.substring(cells.length()-1, cells.length());
                                if (endChar.equals("+") || endChar.equals("-")) {
                                    mathEquation = cells;
                                } else if (endChar.equalsIgnoreCase("x")) {
                                    mathEquation = cells.substring(0, cells.length()-1) + "x";
                                } else if (endChar.equals("/")) {
                                    mathEquation = cells.substring(0, cells.length()-1) + "\u00F7";
                                } else if (endChar.equals("=")) {
                                    mathEquation = cells.substring(0, 1);
                                }
                            }
                        }
                    }
                }
            }

            g.drawString(mathEquation,
                    (start_X + (cellSize + divWidX) * Integer.parseInt(numberCells.substring(1,2)) + numberOffSet_X),
                    (start_Y + (cellSize + divWidY) * Integer.parseInt(numberCells.substring(0,1)) + numberOffSet_Y));
        }
    }

    public TreeSet<String> setMathBorders(TreeSet<String> cellGroup, TreeSet<String> farthestCell, int mb, int me, int b, int e) {
        String tempFarthest;

        for (String initialCell: cellGroup) {
            tempFarthest = "00";
            for (String otherCell: cellGroup) {
                if (initialCell.substring(mb, me).equals(otherCell.substring(mb, me))) {
                    if (initialCell.substring(b,e).compareTo(otherCell.substring(b,e)) > 0
                    && initialCell.substring(b,e).compareTo(tempFarthest.substring(b,e)) > 0) {
                        tempFarthest = initialCell;
                    } else if (otherCell.substring(b,e).compareTo(initialCell.substring(b,e)) > 0
                    && otherCell.substring(b,e).compareTo(tempFarthest.substring(b,e)) > 0) {
                        tempFarthest = otherCell;
                    }
                }
            }

            if (tempFarthest.equals("00")) {
                tempFarthest = initialCell;
            }

            if (!farthestCell.contains(tempFarthest)) {
                farthestCell.add(tempFarthest);
            }
        }
        return farthestCell;
    }
}
