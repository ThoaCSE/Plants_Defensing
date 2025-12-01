package plantsdefense.gui.menu;

import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import plantsdefense.gui.ScreenController;
import plantsdefense.jdbc.DBConnection;

public class LeaderboardPanel extends JPanel {
    private final ScreenController controller;
    private final JComboBox<String> levelCombo;
    private final JTable table;
    private final DefaultTableModel tableModel;

    public LeaderboardPanel(ScreenController controller) {
        this.controller = controller;
        setLayout(new BorderLayout());
        setBackground(new Color(30, 30, 50));

        JLabel title = new JLabel("LEADERBOARD", SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 60));
        title.setForeground(Color.YELLOW);
        add(title, BorderLayout.NORTH);

        JPanel topPanel = new JPanel();
        topPanel.setBackground(new Color(30, 30, 50));
        JLabel levelLabel = new JLabel("Select Level:");
        levelLabel.setFont(new Font("Arial", Font.BOLD, 28));
        levelLabel.setForeground(Color.WHITE);

        levelCombo = new JComboBox<>(new String[]{"Level 1", "Level 2", "Level 3", "All/Custom"});
        levelCombo.setFont(new Font("Arial", Font.PLAIN, 24));
        levelCombo.addActionListener(e -> loadLeaderboard());

        topPanel.add(levelLabel);
        topPanel.add(levelCombo);

        JButton backBtn = new JButton("BACK");
        backBtn.setFont(new Font("Arial", Font.BOLD, 24));
        backBtn.addActionListener(e -> controller.showMenu());
        topPanel.add(backBtn);

        add(topPanel, BorderLayout.SOUTH);

        String[] columns = {"Rank", "Player", "Level/Map", "Score", "Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setFont(new Font("Arial", Font.PLAIN, 22));
        table.setRowHeight(40);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 24));
        table.getTableHeader().setBackground(Color.YELLOW);
        table.getTableHeader().setForeground(Color.BLACK);
        table.setBackground(new Color(50, 50, 70));
        table.setForeground(Color.WHITE);
        table.setGridColor(Color.GRAY);

        JScrollPane scroll = new JScrollPane(table);
        add(scroll, BorderLayout.CENTER);

        loadLeaderboard();
    }

    private void loadLeaderboard() {
        tableModel.setRowCount(0);
        int selection = levelCombo.getSelectedIndex() + 1;
        boolean showAll = (selection == 4);

        String sql = showAll ?
                "SELECT p.name, h.level, h.map_name, h.score, h.achieved_at, ROW_NUMBER() OVER (ORDER BY h.score DESC) as row_num " +
                        "FROM high_scores h JOIN players p ON h.player_id = p.id " +
                        "ORDER BY h.score DESC LIMIT 10" :
                "SELECT p.name, h.level, h.map_name, h.score, h.achieved_at, ROW_NUMBER() OVER (ORDER BY h.score DESC) as row_num " +
                        "FROM high_scores h JOIN players p ON h.player_id = p.id " +
                        "WHERE h.level = ? ORDER BY h.score DESC LIMIT 10";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (!showAll) ps.setInt(1, selection);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String player = rs.getString("name");
                String mapName = rs.getString("map_name");
                int score = rs.getInt("score");
                Timestamp ts = rs.getTimestamp("achieved_at");
                String date = (ts != null) ? sdf.format(ts) : "—";

                tableModel.addRow(new Object[]{rs.getInt("row_num"), player, mapName, score, date});
            }

            if (tableModel.getRowCount() == 0) {
                tableModel.addRow(new Object[]{"—", "No scores yet", "—", "—", "—"});
            }

        } catch (SQLException e) {
            e.printStackTrace();
            tableModel.addRow(new Object[]{"Error", "Database error", "", "", ""});
        }
    }
}
