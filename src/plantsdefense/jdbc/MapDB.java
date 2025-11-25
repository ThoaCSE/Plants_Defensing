package plantsdefense.jdbc;

import plantsdefense.model.Tile;
import plantsdefense.util.Constants;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MapDB {
    public static Tile[][] loadMap(String mapName) {
        String sql = "SELECT data FROM maps WHERE name = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, mapName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return parseMapData(rs.getString("data"));
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public static void saveMap(String filename, Tile[][] grid, int playerId) throws SQLException {
        String data = serializeGrid(grid);
        String sql = "INSERT INTO maps (name, data, created_by) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE data = VALUES(data), created_by = VALUES(created_by)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, filename.endsWith(".txt") ? filename : filename + ".txt");
            ps.setString(2, data);
            ps.setInt(3, playerId);
            ps.executeUpdate();
        }
    }

    public static List<String> listMaps() {
        List<String> names = new ArrayList<>();
        String sql = "SELECT name FROM maps ORDER BY created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) names.add(rs.getString("name"));
        } catch (Exception e) { e.printStackTrace(); }
        return names;
    }

    private static String serializeGrid(Tile[][] grid) {
        StringBuilder sb = new StringBuilder();
        for (int r = 0; r < Constants.rows; r++) {
            for (int c = 0; c < Constants.cols; c++) {
                sb.append(grid[r][c].getType());
                if (c < Constants.cols - 1) sb.append(",");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private static Tile[][] parseMapData(String data) {
        Tile[][] grid = new Tile[Constants.rows][Constants.cols];
        String[] lines = data.split("\n");
        for (int r = 0; r < lines.length && r < Constants.rows; r++) {
            String[] tokens = lines[r].split(",");
            for (int c = 0; c < tokens.length && c < Constants.cols; c++) {
                int type = Integer.parseInt(tokens[c].trim());
                grid[r][c] = new Tile(c, r, type);
            }
        }
        return grid;
    }
}