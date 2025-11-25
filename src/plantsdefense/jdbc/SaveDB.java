package plantsdefense.jdbc;

import java.sql.*;

public class SaveDB {

    public static void saveGame(int playerId, String mapName) throws SQLException {
        String sql = "INSERT INTO saves (player_id, level, wave, gold, lives, score, plants_data, map_name) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE wave=VALUES(wave), gold=VALUES(gold), lives=VALUES(lives), score=VALUES(score)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, playerId);
            ps.setInt(2, plantsdefense.gamelogic.GameSession.getLevel());
            ps.setInt(3, plantsdefense.gamelogic.GameSession.getWave());
            ps.setInt(4, plantsdefense.gamelogic.GameSession.getGold());
            ps.setInt(5, plantsdefense.gamelogic.GameSession.getLives());
            ps.setInt(6, plantsdefense.gamelogic.GameSession.getScore());
            ps.setString(7, ""); // TODO: serialize placed plants later
            ps.setString(8, mapName);
            ps.executeUpdate();
        }
    }
}
