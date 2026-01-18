package trafficsimulation;

import javax.swing.*;
import java.awt.*;
import java.util.*;

/**
 * Simple Filter-Panel for Vehicle Type and Street
 */
public class FilterPanel extends JPanel {

    private VehicleFilter filter;
    private GuiController controller;
    private Map<String, JCheckBox> typeBoxes = new HashMap<>();
    private Map<String, JCheckBox> edgeBoxes = new HashMap<>();
    private JPanel typePanel;
    private JPanel edgePanel;

    public FilterPanel(VehicleFilter filter, GuiController controller) {
        this.filter = filter;
        this.controller = controller;

        setPreferredSize(new Dimension(200, 600));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createTitledBorder("Filter"));

        // Reset button
        JButton clear = new JButton("Reset");
        clear.addActionListener(e -> reset());
        add(clear);

        // Vehicle type section
        add(new JLabel("Vehicle type:"));
        typePanel = new JPanel();
        typePanel.setLayout(new BoxLayout(typePanel, BoxLayout.Y_AXIS));
        add(new JScrollPane(typePanel));

        // Street section
        add(new JLabel("Street:"));
        edgePanel = new JPanel();
        edgePanel.setLayout(new BoxLayout(edgePanel, BoxLayout.Y_AXIS));
        add(new JScrollPane(edgePanel));

        update();
    }

    public void update() {
        updateTypes();
        updateEdges();
    }

    private void updateTypes() {
        java.util.List<String> types = filter.getAllTypes();

        // Remove old types
        java.util.List<String> toRemove = new ArrayList<>();
        for (String key : typeBoxes.keySet()) {
            if (!types.contains(key)) {
                toRemove.add(key);
            }
        }
        for (String key : toRemove) {
            JCheckBox box = typeBoxes.remove(key);
            typePanel.remove(box);
        }

        // Add new types
        for (String type : types) {
            if (!typeBoxes.containsKey(type)) {
                JCheckBox box = new JCheckBox(type);
                box.addActionListener(e -> {
                    if (box.isSelected()) {
                        filter.addType(type);
                    } else {
                        filter.removeType(type);
                    }
                    controller.refreshMap();
                });
                typeBoxes.put(type, box);
                typePanel.add(box);
            }
        }

        typePanel.revalidate();
        typePanel.repaint();
    }

    private void updateEdges() {
        java.util.List<String> edges = filter.getAllEdges();

        // Remove old edges
        java.util.List<String> toRemove = new ArrayList<>();
        for (String key : edgeBoxes.keySet()) {
            if (!edges.contains(key)) {
                toRemove.add(key);
            }
        }
        for (String key : toRemove) {
            JCheckBox box = edgeBoxes.remove(key);
            edgePanel.remove(box);
        }

        // Add new edges
        for (String edge : edges) {
            if (!edgeBoxes.containsKey(edge)) {
                String display = edge.length() > 20 ? edge.substring(0, 17) + "..." : edge;
                JCheckBox box = new JCheckBox(display);
                box.setToolTipText(edge);
                box.addActionListener(e -> {
                    if (box.isSelected()) {
                        filter.addEdge(edge);
                    } else {
                        filter.removeEdge(edge);
                    }
                    controller.refreshMap();
                });
                edgeBoxes.put(edge, box);
                edgePanel.add(box);
            }
        }

        edgePanel.revalidate();
        edgePanel.repaint();
    }

    private void reset() {
        filter.clear();
        for (JCheckBox box : typeBoxes.values()) {
            box.setSelected(false);
        }
        for (JCheckBox box : edgeBoxes.values()) {
            box.setSelected(false);
        }
        controller.refreshMap();
    }
}