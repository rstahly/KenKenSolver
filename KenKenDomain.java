import java.util.List;
import java.util.TreeMap;

/**
 * Created by Rachel Feddersen on 3/8/2016.
 */
public class KenKenDomain {
    private TreeMap<String, List<String>> kenKenDomain = new TreeMap<>();

    public void setInitialDomain(String[] values, List<String> domain) {

        int numOfCells = (int) Math.floor(values.length/2);

        for (int j = 0; j < numOfCells; j++) {
            String cell = values[j*2] + values[(j*2)+1];
            getKenKenDomain().put(cell, domain);
        }
    }

    public TreeMap<String, List<String>> getKenKenDomain() {
        return kenKenDomain;
    }
}
