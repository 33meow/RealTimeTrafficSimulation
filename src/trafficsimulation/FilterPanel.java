package trafficsimulation;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 * Einfaches Filter-Panel für Fahrzeugtyp und Edge
 */
public class FilterPanel extends JPanel {

    private VehicleFilter filter;
    private GuiController controller;
    private Map<String, JCheckBox> typeBoxes;
    private Map<String, JCheckBox> edgeBoxes;
    private JPanel typePanel;
    private JPanel edgePanel;

    public FilterPanel(VehicleFilter filter, GuiController controller) {
        this.filter = filter;
        this.controller = controller;
        this.typeBoxes = new HashMap<>();
        this.edgeBoxes = new HashMap<>();

        setPreferredSize(new Dimension(250, 600));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createTitledBorder("Filter"));

        // Button for update
        JButton refresh = new JButton("Update");
        refresh.addActionListener(e -> update());
        add(refresh);

        // Button for reset
        JButton clear = new JButton("Reset");
        clear.addActionListener(e -> clearAll());
        add(clear);

        // Vehicle type
        add(new JLabel("Vehicle type:"));
        typePanel = new JPanel();
        typePanel.setLayout(new BoxLayout(typePanel, BoxLayout.Y_AXIS));
        JScrollPane typeScroll = new JScrollPane(typePanel);
        typeScroll.setPreferredSize(new Dimension(230, 200));
        add(typeScroll);

        // Edge-Filter
        add(new JLabel("Street:"));
        edgePanel = new JPanel();
        edgePanel.setLayout(new BoxLayout(edgePanel, BoxLayout.Y_AXIS));
        JScrollPane edgeScroll = new JScrollPane(edgePanel);
        edgeScroll.setPreferredSize(new Dimension(230, 200));
        add(edgeScroll);

        update();
    }

    // update the filter options
    public void update() {
        updateTypes();
        updateEdges();
    }

    private void updateTypes() {
        List<String> types = filter.getAllTypes();

        // Füge nur neue hinzu
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
        List<String> edges = filter.getAllEdges();

        // Entferne alte Edges die nicht mehr existieren
        List<String> toRemove = new ArrayList<>();
        for (String oldEdge : edgeBoxes.keySet()) {
            if (!edges.contains(oldEdge)) {
                toRemove.add(oldEdge);
            }
        }
        for (String edge : toRemove) {
            JCheckBox box = edgeBoxes.remove(edge);
            edgePanel.remove(box);
        }


        // Füge nur neue hinzu
        for (String edge : edges) {
            if (!edgeBoxes.containsKey(edge)) {
                String displayName = edge.length() > 20 ?
                        edge.substring(0, 17) + "..." : edge;

                JCheckBox box = new JCheckBox(displayName);
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

    private void clearAll() {
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