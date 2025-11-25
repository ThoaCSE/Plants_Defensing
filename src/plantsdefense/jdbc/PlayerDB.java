package plantsdefense.jdbc;

import java.sql.*;

public class PlayerDB {
    public static int createPlayer(String name) throws SQLException {
        String sql = "INSERT INTO players (name) VALUES (?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            return rs.next() ? rs.getInt(1) : -1;
        }
    }

    public static int getPlayerId(String name) throws SQLException {
        String sql = "SELECT id FROM players WHERE name = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt("id") : -1;
        }
    }

    public static void updateProgress(int playerId, int levelReached, int scoreToAdd) throws SQLException {
        String sql = "UPDATE players SET levels_unlocked = GREATEST(levels_unlocked, ?), total_score = total_score + ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, levelReached);
            ps.setInt(2, scoreToAdd);
            ps.setInt(3, playerId);
            ps.executeUpdate();
        }
    }
}