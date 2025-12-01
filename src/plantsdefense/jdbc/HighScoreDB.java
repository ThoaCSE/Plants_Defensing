package plantsdefense.jdbc;

import java.sql.*;

public class HighScoreDB {

    public static void saveHighScore(int playerId, int level, String mapName, int score) {
        String sql = """
            INSERT INTO high_scores (player_id, level, map_name, score)
            VALUES (?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
            score = GREATEST(score, VALUES(score)),
            achieved_at = CURRENT_TIMESTAMP
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, playerId);
            ps.setInt(2, level);
            ps.setString(3, mapName);
            ps.setInt(4, score);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void initializePlayerScores(int playerId) {
        try {
            for (int i = 1; i <= 3; i++) {
                saveHighScore(playerId, i, "level" + i + ".txt", 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
