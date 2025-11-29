// src/plantsdefense/gui/editor/MapListPanel.java
package plantsdefense.gui.editor;

import plantsdefense.jdbc.MapDB;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MapListPanel extends JPanel {
    private final JList<String> mapList;
    private final DefaultListModel<String> listModel;
    private final JButton loadButton;

    public interface OnMapSelectedListener {
        void onMapSelected(String mapName);
        void onBack();
    }

    public MapListPanel(OnMapSelectedListener listener) {
        setLayout(new BorderLayout());
        setBackground(new Color(30, 30, 50));

        JLabel title = new JLabel("SELECT CUSTOM MAP", SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 48));
        title.setForeground(Color.YELLOW);
        add(title, BorderLayout.NORTH);

        listModel = new DefaultListModel<>();
        mapList = new JList<>(listModel);
        mapList.setFont(new Font("Arial", Font.PLAIN, 28));
        mapList.setBackground(new Color(50, 50, 70));
        mapList.setForeground(Color.WHITE);
        mapList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scroll = new JScrollPane(mapList);
        add(scroll, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(new Color(30, 30, 50));

        loadButton = new JButton("PLAY MAP");
        loadButton.setFont(new Font("Arial", Font.BOLD, 24));
        loadButton.setPreferredSize(new Dimension(220, 60));
        loadButton.addActionListener(e -> {
            String selected = mapList.getSelectedValue();
            if (selected != null && !selected.contains("No")) {
                listener.onMapSelected(selected);
            }
        });

        JButton backButton = new JButton("BACK");
        backButton.setFont(new Font("Arial", Font.BOLD, 24));
        backButton.setPreferredSize(new Dimension(180, 60));
        backButton.addActionListener(e -> listener.onBack());

        btnPanel.add(loadButton);
        btnPanel.add(Box.createHorizontalStrut(20));
        btnPanel.add(backButton);
        add(btnPanel, BorderLayout.SOUTH);

        loadMaps();
    }

    private void loadMaps() {
        listModel.clear();
        List<String> maps = MapDB.listMaps();
        if (maps.isEmpty()) {
            listModel.addElement("No custom maps found");
            loadButton.setEnabled(false);
        } else {
            for (String map : maps) {
                listModel.addElement(map);
            }
            mapList.setSelectedIndex(0);
            loadButton.setEnabled(true);
        }
    }
}