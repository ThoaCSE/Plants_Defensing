package plantsdefense.gui.menu;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import plantsdefense.gamelogic.GameSession;
import plantsdefense.gui.ScreenController;
import plantsdefense.jdbc.DBConnection;

public class LoadSavePanel extends JPanel {
    private final ScreenController controller;
    private final DefaultListModel<String> listModel;
    private  JList<String> playerList;

    public LoadSavePanel(ScreenController controller) {
        this.controller = controller;
        setLayout(new BorderLayout());
        setBackground(new Color(30, 30, 50));

        JLabel title = new JLabel("SELECT PLAYER TO CONTINUE", SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 48));
        title.setForeground(Color.YELLOW);
        add(title, BorderLayout.NORTH);

        listModel = new DefaultListModel<>();
        playerList = new JList<>(listModel);
        playerList.setFont(new Font("Arial", Font.PLAIN, 28));
        playerList.setBackground(new Color(50, 50, 70));
        playerList.setForeground(Color.WHITE);
        playerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scroll = new JScrollPane(playerList);
        add(scroll, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(30, 30, 50));

        JButton loadBtn = new JButton("LOAD GAME");
        loadBtn.setFont(new Font("Arial", Font.BOLD, 24));
        loadBtn.setPreferredSize(new Dimension(200, 60));
        loadBtn.addActionListener(e -> loadSelectedGame());

        JButton backBtn = new JButton("BACK");
        backBtn.setFont(new Font("Arial", Font.BOLD, 24));
        backBtn.setPreferredSize(new Dimension(200, 60));
        backBtn.addActionListener(e -> controller.showMenu());

        buttonPanel.add(loadBtn);
        buttonPanel.add(backBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        loadSavedPlayers();
    }

    private void loadSavedPlayers() {
        listModel.clear();

        String sql = """
            SELECT p.name, MAX(s.saved_at) as last_save
            FROM players p
            JOIN saves s ON p.id = s.player_id
            GROUP BY p.name
            ORDER BY last_save DESC
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                listModel.addElement(rs.getString("name"));
            }

            if (listModel.isEmpty()) {
                listModel.addElement("No saved games found");
                playerList.setEnabled(false);
            } else {
                playerList.setSelectedIndex(0);
                playerList.setEnabled(true);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            listModel.addElement("Error loading saves");
        }
    }

    private void loadSelectedGame() {
        String selected = playerList.getSelectedValue();
        if (selected == null || selected.contains("No saved") || selected.contains("Error")) return;

        GameSession.startNewGame(selected, null, 1);
        if (GameSession.loadSavedGame()) {
            controller.showPlay();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to load save for " + selected, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}