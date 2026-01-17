package trafficsimulation;

import java.util.ArrayList;
import java.util.List;

public class LogExport {
    private final List<String[]> rows = new ArrayList<>();

    //int vehicleCount ergänzen, wenn implementiert
    public void logStep(int step, double time) {
        rows.add(new String[] {
            String.valueOf(step),
            String.valueOf(time),
            //String.valueOf(vehicleCount) ergänzen, wenn vehicles angepasst ist
        });
    }

    public List<String[]> getRows() {
        return rows;
    }
}
