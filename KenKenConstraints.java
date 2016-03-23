import java.util.*;

/**
 * Created by Rachel Feddersen on 3/8/2016.
 */
public class KenKenConstraints {
    KenKenDomain domain;

    private TreeMap<String, String[]> mathConstraints = new TreeMap<>();
    private int boardSize;

    public KenKenConstraints(KenKenDomain d, int b) {
        domain = d;
        boardSize = b;
    }

    public void createMathConst(String[] values) {
        String keyConst = values[0] + values[1];

        int numOfCells = (int) Math.floor(values.length/2);
        String[] valueConst = new String[numOfCells];

        for (int i = 1; i < numOfCells; i++) {
            valueConst[i-1] = values[i*2] + values[(i*2)+1];
        }

        valueConst[numOfCells-1] = values[(numOfCells*2)].substring(1, values[(numOfCells*2)].length());

        getMathConstraints().put(keyConst, valueConst);
    }

    public void removeEqualsDomain() {
        for(Map.Entry<String,String[]> entry : getMathConstraints().entrySet()) {
            if (entry.getValue().length == 1) {
                List<String> values = new ArrayList<>();
                values.add(entry.getValue()[0].substring(0, 1));
                domain.getKenKenDomain().replace(entry.getKey(), values);
            }
        }
    }

    public void removeAddValues(TreeSet<String> cellGroup, int[][] kenKenArray) {
        List<String> newDomain;

        for (String cells: cellGroup) {
            newDomain = new LinkedList<>();
            for (String domainList: domain.getKenKenDomain().get(cells)) {
                if (domain.getKenKenDomain().get(cells).size() != 1 &&
                kenKenArray[Integer.parseInt(cells.substring(0, 1))][Integer.parseInt(cells.substring(1, 2))] == 0) {
                    if (Integer.parseInt(domainList) <= boardSize) {
                        newDomain.add(domainList);
                    }
                }
            }
            if (newDomain.size() > 0) {
                domain.getKenKenDomain().replace(cells, newDomain);
            }
        }
    }

    public void removeSubValues(TreeSet<String> cellGroup, String value, int[][] kenKenArray) {
        List<String> newDomain;

        for (String cells: cellGroup) {
            newDomain = new LinkedList<>();
            for (String domainList: domain.getKenKenDomain().get(cells)) {
                if (domain.getKenKenDomain().size() != 1 &&
                kenKenArray[Integer.parseInt(cells.substring(0, 1))][Integer.parseInt(cells.substring(1, 2))] == 0) {
                    int v = Integer.parseInt(value),
                            d = Integer.parseInt(domainList);

                    if ((d <= v) && ((d + v) <= boardSize)) {
                        newDomain.add(domainList);
                    } else if ((d > v) && ((d - v) >= 1)) {
                        newDomain.add(domainList);
                    }
                }
            }
            if (newDomain.size() > 0) {
                domain.getKenKenDomain().replace(cells, newDomain);
            }
        }
    }

    public void removeMultValues(TreeSet<String> cellGroup, String value, int[][] kenKenArray) {
        List<String> newDomain;

        for (String cells: cellGroup) {
            newDomain = new LinkedList<>();
            for (String domainList: domain.getKenKenDomain().get(cells)) {
                if (domain.getKenKenDomain().size() != 1 &&
                kenKenArray[Integer.parseInt(cells.substring(0, 1))][Integer.parseInt(cells.substring(1, 2))] == 0) {
                    if ((Integer.parseInt(value) % Integer.parseInt(domainList)) == 0) {
                        newDomain.add(domainList);
                    }
                }
            }
            if (newDomain.size() > 0) {
                domain.getKenKenDomain().replace(cells, newDomain);
            }
        }
    }

    public void removeDivValues(TreeSet<String> cellGroup, String value, int[][] kenKenArray) {
        List<String> newDomain;

        for (String cells: cellGroup) {
            newDomain = new LinkedList<>();
            for (String domainList: domain.getKenKenDomain().get(cells)) {
                if (domain.getKenKenDomain().size() != 1 &&
                kenKenArray[Integer.parseInt(cells.substring(0, 1))][Integer.parseInt(cells.substring(1, 2))] == 0) {
                    int v = Integer.parseInt(value),
                            d = Integer.parseInt(domainList);

                    if ((d <= v) && ((v % d) == 0)) {
                        newDomain.add(domainList);
                    } else if ((d <= v) && ((v * d) <= boardSize)) {
                        newDomain.add(domainList);
                    } else if ((d > v) && ((d % v) == 0)) {
                        newDomain.add(domainList);
                    } else if ((d > v) && ((v * d) <= boardSize)) {
                        newDomain.add(domainList);
                    }
                }
            }
            if (newDomain.size() > 0) {
                domain.getKenKenDomain().replace(cells, newDomain);
            }
        }
    }

    public TreeMap<String, String[]> getMathConstraints() {
        return mathConstraints;
    }
}
