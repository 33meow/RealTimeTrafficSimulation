package trafficsimulation;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CsvExporter {

    public static void export(String filePath, List<String[]> rows) {
        try (FileWriter writer = new FileWriter(filePath)) {
            
            // Header
            writer.append("step;time;vehicles\n");
            
            // Rows
            for (String[] row : rows) {
                writer.append(String.join("; ", row));
                writer.append("\n");
            }

            System.out.println("CSV exported: " + filePath);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
