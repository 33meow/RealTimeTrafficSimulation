package trafficsimulation;

import java.util.ArrayList;
import java.util.List;

public class LogExport {
    private final List<String[]> rows = new ArrayList<>();

    //int vehicleCount ergänzen, wenn implementiert
    public void logStep(int step, double time, int vehicleCounter) {
        rows.add(new String[] {
            String.valueOf(step),
            String.valueOf(time),
            String.valueOf(vehicleCounter),
        });
    }

    public List<String[]> getRows() {
        return rows;
    }
}
