package trafficsimulation;

import java.util.ArrayList;
import java.util.List;

public class VehicleFilter {

    private VehicleRepository repo;
    private List<String> types;
    private List<String> edges;

    public VehicleFilter(VehicleRepository repo) {
        this.repo = repo;
        this.types = new ArrayList<>();
        this.edges = new ArrayList<>();
    }

    // Filter for VehicleType
    public void addType(String type) {
        if (!types.contains(type)) {
            types.add(type);
        }
    }

    public void removeType(String type) {
        types.remove(type);
    }

    // Filter for Edge
    public void addEdge(String edge) {
        if (!edges.contains(edge)) {
            edges.add(edge);
        }
    }

    public void removeEdge(String edge) {
        edges.remove(edge);
    }

    // delete all filters
    public void clear() {
        types.clear();
        edges.clear();
    }

    // get vehicles after applying filters
    public List<VehicleWrap> getFiltered() {
        List<VehicleWrap> all = repo.getAllVehicles();

        //if no filters active return all
        if (types.isEmpty() && edges.isEmpty()) {
            return all;
        }

        List<VehicleWrap> result = new ArrayList<>();

        for (VehicleWrap v : all) {
            boolean ok = true;

            // Prüfe Typ (wenn Filter aktiv)
            if (!types.isEmpty()) {
                String vType = v.getImageName();
                if (vType == null || !types.contains(vType)) {
                    ok = false;
                }
            }

            // Prüfe Edge (wenn Filter aktiv)
            if (!edges.isEmpty()) {
                String vEdge = v.getEdge();
                if (vEdge == null || !edges.contains(vEdge)) {
                    ok = false;
                }
            }

            if (ok) {
                result.add(v);
            }
        }

        return result;
    }

    // All available Types
    public List<String> getAllTypes() {
        if (repo == null || repo.getAllVehicles() == null) {
            return new ArrayList<>();
        }

        List<String> result = new ArrayList<>();
        for (VehicleWrap v : repo.getAllVehicles()) {
            String type = v.getImageName();
            if (type != null && !type.isEmpty() && !result.contains(type)) {
                result.add(type);
            }
        }
        return result;
    }

    // All available Edges
    public List<String> getAllEdges() {
        if (repo == null || repo.getAllVehicles() == null) {
            return new ArrayList<>();
        }

        List<String> result = new ArrayList<>();
        for (VehicleWrap v : repo.getAllVehicles()) {
            String edge = v.getEdge();
            if (edge != null && !edge.isEmpty() && !result.contains(edge)) {
                result.add(edge);
            }
        }
        return result;
    }
}