// src/plantsdefense/jdbc/SaveDB.java
package plantsdefense.jdbc;

import plantsdefense.gamelogic.GameSession;
import java.sql.*;

public class SaveDB {

    public static void saveGame() {
        int playerId = GameSession.getPlayerId();
        if (playerId <= 0) return;

        String sql = """
            INSERT INTO saves (player_id, level, wave, gold, lives, score, plants_data, map_name)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
            wave=VALUES(wave), gold=VALUES(gold), lives=VALUES(lives),
            score=VALUES(score), plants_data=VALUES(plants_data), map_name=VALUES(map_name)
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String currentMap = GameSession.getCurrentMapName();

            ps.setInt(1, playerId);
            ps.setInt(2, GameSession.getLevel());
            ps.setInt(3, GameSession.getWave());
            ps.setInt(4, GameSession.getGold());
            ps.setInt(5, GameSession.getLives());
            ps.setInt(6, GameSession.getScore());
            ps.setString(7, ""); // Placeholder for detailed plant data
            ps.setString(8, currentMap);

            ps.executeUpdate();

            // FIX: Pass 'currentMap' as the 3rd argument
            HighScoreDB.saveHighScore(playerId, GameSession.getLevel(), currentMap, GameSession.getScore());

            PlayerDB.updateProgress(playerId, GameSession.getLevel(), GameSession.getScore());

            System.out.println("Game saved successfully! Map: " + currentMap);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}