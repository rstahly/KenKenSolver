import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * Created by Rachel Feddersen on 3/8/2016.
 */
public class KenKenVariables {
    KenKenConstraints constraints;
    KenKenDomain domain;

    private int[][] kenKenArray = new int[4][4];

    private int boardSize = 4;

    public KenKenVariables () {
    }

    public int[][] getKenKenArray() {
        return kenKenArray;
    }

    public void setKenKenArray(int[][] kenKenArray) {
        this.kenKenArray = kenKenArray;

        domain = new KenKenDomain();
        constraints = new KenKenConstraints(domain, getBoardSize());
    }

    public int getBoardSize() {
        return boardSize;
    }

    public void setBoardSize(int boardSize) {
        this.boardSize = boardSize;
    }

    public void removeMathValues() {
        constraints.removeEqualsDomain();

        for(Map.Entry<String,String[]> entry : constraints.getMathConstraints().entrySet()) {
            TreeSet<String> cellGroup = new TreeSet<>();
            String[] value = entry.getValue();

            cellGroup.add(entry.getKey());
            for (int i = 0; i < value.length-1; i++) {
                cellGroup.add(value[i]);
            }

            for (String cells: entry.getValue()) {
                if (cells.substring(cells.length()-1, cells.length()).equals("+")) {
                    constraints.removeAddValues(cellGroup, getKenKenArray());
                } else if (cells.substring(cells.length()-1, cells.length()).equals("-")) {
                    constraints.removeSubValues(cellGroup, cells.substring(0, cells.length()-1), getKenKenArray());
                } else if (cells.substring(cells.length()-1, cells.length()).equalsIgnoreCase("x")) {
                    constraints.removeMultValues(cellGroup, cells.substring(0, cells.length()-1), getKenKenArray());
                } else if (cells.substring(cells.length()-1, cells.length()).equals("/")) {
                    constraints.removeDivValues(cellGroup, cells.substring(0, cells.length()-1), getKenKenArray());
                }
            }
        }
    }

    public void performNodeConsistency(String row, String col, String value) {
        List<String> newDomain;

        for(Map.Entry<String,List<String>> entry : domain.getKenKenDomain().entrySet()) {
            newDomain = new LinkedList<>();
            if (entry.getKey().substring(0, 1).equals(row) && !entry.getKey().substring(1, 2).equals(col)) {
                for (int i = 0; i < entry.getValue().size(); i++) {
                    if (!entry.getValue().get(i).equals(value)) {
                        newDomain.add("" + entry.getValue().get(i));
                    }
                }
                domain.getKenKenDomain().replace(entry.getKey(), newDomain);
            }

            if (entry.getKey().substring(1, 2).equals(col) && !entry.getKey().substring(0, 1).equals(row)) {
                for (int i = 0; i < entry.getValue().size(); i++) {
                    if (!entry.getValue().get(i).equals(value)) {
                        newDomain.add("" + entry.getValue().get(i));
                    }
                }
                domain.getKenKenDomain().replace(entry.getKey(), newDomain);
            }
        }
    }

    public boolean checkRowsCols(String row, String col, String value) {
        boolean validValue = true;

        for (int i = 0; i < getKenKenArray().length; i++) {
            if (i == Integer.parseInt(row)) {
                for (int j = 0; j < getKenKenArray().length; j++) {
                    if (j != Integer.parseInt(col)) {
                        if (getKenKenArray()[i][j] == Integer.parseInt(value)) {
                            validValue = false;
                        } else if (getKenKenArray()[i][j] == 0) {
                            if (domain.getKenKenDomain().get(""+i+j).contains(value)) {
                                if (domain.getKenKenDomain().get(""+i+j).size()-1 < 1) {
                                    validValue = false;
                                }
                            }
                        }
                    }
                }
            }

            if (i == Integer.parseInt(col)) {
                for (int j = 0; j < getKenKenArray().length; j++) {
                    if (j != Integer.parseInt(row)) {
                        if (getKenKenArray()[j][i] == Integer.parseInt(value)) {
                            validValue = false;
                        } else if (getKenKenArray()[j][i] == 0) {
                            if (domain.getKenKenDomain().get(""+j+i).contains(value)) {
                                if (domain.getKenKenDomain().get(""+j+i).size()-1 < 1) {
                                    validValue = false;
                                }
                            }
                        }
                    }
                }
            }
        }

        return validValue;
    }

    public boolean mathCellValid(String cell, String value) {
        boolean validValue = false;

        for(Map.Entry<String,String[]> entry : constraints.getMathConstraints().entrySet()) {
            TreeSet<String> cellGroup = new TreeSet<>();
            String[] valueArray = entry.getValue();

            cellGroup.add(entry.getKey());
            for (int i = 0; i < valueArray.length-1; i++) {
                cellGroup.add(valueArray[i]);
            }

            String eqTotal = valueArray[valueArray.length-1].substring(0, valueArray[valueArray.length-1].length()-1);

            String sign = valueArray[valueArray.length-1].substring(valueArray[valueArray.length-1].length()-1,
                    valueArray[valueArray.length-1].length());

            for (String cells: cellGroup) {
                if (cells.equals(cell)) {
                    validValue = doMathValidation(cellGroup, cell, sign, eqTotal, value);
                }
            }
        }

        return validValue;
    }

    private boolean doMathValidation(TreeSet<String> cellGroup, String cell, String sign, String eqTotal, String value) {
        boolean validValue = false;
        boolean allCellsFilled = true;
        int cellsTotal = 0;

        for (String cells: cellGroup) {
            if (!cells.equals(cell)) {
                if (sign.equals("+") || sign.equals("-")) {
                    if (getKenKenArray()[Integer.parseInt(cells.substring(0, 1))][Integer.parseInt(cells.substring(1, 2))] != 0) {
                        cellsTotal += getKenKenArray()[Integer.parseInt(cells.substring(0, 1))][Integer.parseInt(cells.substring(1, 2))];
                    } else {
                        allCellsFilled = false;
                    }
                } else if (sign.equalsIgnoreCase("x") || sign.equals("/")) {
                    if (cellsTotal == 0) {
                        cellsTotal += 1;
                    }
                    if (getKenKenArray()[Integer.parseInt(cells.substring(0, 1))][Integer.parseInt(cells.substring(1, 2))] != 0) {
                        cellsTotal *= getKenKenArray()[Integer.parseInt(cells.substring(0, 1))][Integer.parseInt(cells.substring(1, 2))];
                    } else {
                        allCellsFilled = false;
                    }
                }
            }
        }

        if (sign.equals("=")) {
            if (Integer.parseInt(value) == Integer.parseInt(eqTotal)) {
                validValue = true;
            }
        }else if (sign.equals("+")) {
            validValue = checkAddition(value, eqTotal, cellsTotal, allCellsFilled);
        } else if (sign.equals("-")) {
            validValue = checkSubtraction(value, eqTotal, cellsTotal, allCellsFilled);
        } else if (sign.equalsIgnoreCase("x")) {
            validValue = checkMultiplication(value, eqTotal, cellsTotal, allCellsFilled);
        } else if (sign.equals("/")) {
            validValue = checkDivision(value, eqTotal, cellsTotal, allCellsFilled);
        }

        return validValue;
    }

    public boolean checkAddition(String value, String eqTotal, int cellsTotal, boolean allCellsFilled) {
        boolean validValue = false;

        if (!allCellsFilled) {
            if ((Integer.parseInt(eqTotal) - cellsTotal) >= Integer.parseInt(value)) {
                validValue = true;
            }
        } else {
            if ((Integer.parseInt(eqTotal) - cellsTotal) == Integer.parseInt(value)) {
                validValue = true;
            }
        }

        return validValue;
    }

    public boolean checkSubtraction(String value, String eqTotal, int cellsTotal, boolean allCellsFilled) {
        boolean validValue = false;

        if (!allCellsFilled) {
            if (Integer.parseInt(value) > cellsTotal) {
                if ((Integer.parseInt(value) - cellsTotal) >= Integer.parseInt(eqTotal)) {
                    validValue = true;
                }
            } else {
                if ((cellsTotal - Integer.parseInt(value)) >= Integer.parseInt(eqTotal)) {
                    validValue = true;
                }
            }
            if (cellsTotal == 0) {
                validValue = true;
            }
        } else {
            if (Integer.parseInt(value) > cellsTotal) {
                if ((Integer.parseInt(value) - cellsTotal) == Integer.parseInt(eqTotal)) {
                    validValue = true;
                }
            } else {
                if ((cellsTotal - Integer.parseInt(value)) == Integer.parseInt(eqTotal)) {
                    validValue = true;
                }
            }
        }

        return validValue;
    }

    public boolean checkMultiplication(String value, String eqTotal, int cellsTotal, boolean allCellsFilled) {
        boolean validValue = false;

        if (!allCellsFilled) {
            if ((Integer.parseInt(eqTotal) / cellsTotal) >= Integer.parseInt(value)) {
                validValue = true;
            }
        } else {
            if ((Integer.parseInt(eqTotal) / cellsTotal) == Integer.parseInt(value)) {
                validValue = true;
            }
        }

        return validValue;
    }

    public boolean checkDivision(String value, String eqTotal, int cellsTotal, boolean allCellsFilled) {
        boolean validValue = false;

        if (!allCellsFilled) {
            if ((Integer.parseInt(value) > cellsTotal)) {
                if ((Integer.parseInt(value) / cellsTotal) >= Integer.parseInt(eqTotal)) {
                    validValue = true;
                } else if (Integer.parseInt(value) * cellsTotal <= getBoardSize()) {
                    validValue = true;
                }
            } else {
                if ((cellsTotal / Integer.parseInt(value)) >= Integer.parseInt(eqTotal)) {
                    validValue = true;
                }
            }
            if (cellsTotal == 1) {
                validValue = true;
            }
        } else {
            if (Integer.parseInt(value) > cellsTotal) {
                if ((Integer.parseInt(value) / cellsTotal) == Integer.parseInt(eqTotal)) {
                    validValue = true;
                }
            } else {
                if ((cellsTotal / Integer.parseInt(value)) == Integer.parseInt(eqTotal)) {
                    validValue = true;
                }
            }
        }

        return validValue;
    }

    public boolean solved() {
        boolean solved = true;
        for (int row = 0; row < getBoardSize(); row++) {
            for (int col = 0; col < getBoardSize(); col++) {
                if (getKenKenArray()[row][col] == 0) {
                    solved = false;
                }
                if (!checkRowsCols("" + row, "" + col, "" + getKenKenArray()[row][col])) {
                    solved = false;
                }
            }
        }

        return solved;
    }
}
