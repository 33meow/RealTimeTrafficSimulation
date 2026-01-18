package trafficsimulation;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple filter for vehicles by type and street
 */
public class VehicleFilter {

    private VehicleRepository repo;
    private List<String> types = new ArrayList<>();
    private List<String> edges = new ArrayList<>();

    public VehicleFilter(VehicleRepository repo) {
        this.repo = repo;
    }

    // Add/Remove filters
    public void addType(String type) {
        if (!types.contains(type)) types.add(type);
    }

    public void removeType(String type) {
        types.remove(type);
    }

    public void addEdge(String edge) {
        if (!edges.contains(edge)) edges.add(edge);
    }

    public void removeEdge(String edge) {
        edges.remove(edge);
    }

    public void clear() {
        types.clear();
        edges.clear();
    }

    // Get filtered vehicles
    public List<VehicleWrap> getFiltered() {
        List<VehicleWrap> all = repo.getAllVehicles();

        // No filters = return all
        if (types.isEmpty() && edges.isEmpty()) return all;

        List<VehicleWrap> result = new ArrayList<>();
        for (VehicleWrap v : all) {
            if (matches(v)) result.add(v);
        }
        return result;
    }

    // Check if vehicle matches filters
    private boolean matches(VehicleWrap v) {
        // Check type filter
        if (!types.isEmpty()) {
            String vType = v.getImageName();
            if (vType == null || !types.contains(vType)) return false;
        }

        // Check edge filter
        if (!edges.isEmpty()) {
            String vEdge = v.getEdge();
            if (vEdge == null || !edges.contains(vEdge)) return false;
        }

        return true;
    }

    // Get all available types
    public List<String> getAllTypes() {
        return getUniqueValues(true);
    }

    // Get all available edges
    public List<String> getAllEdges() {
        return getUniqueValues(false);
    }

    // Helper method to get unique values
    private List<String> getUniqueValues(boolean isType) {
        List<String> result = new ArrayList<>();

        if (repo == null || repo.getAllVehicles() == null) return result;

        for (VehicleWrap v : repo.getAllVehicles()) {
            String value = isType ? v.getImageName() : v.getEdge();
            if (value != null && !value.isEmpty() && !result.contains(value)) {
                result.add(value);
            }
        }
        return result;
    }
}