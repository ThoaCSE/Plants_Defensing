// src/plantsdefense/gamelogic/GameSession.java
package plantsdefense.gamelogic;

import plantsdefense.jdbc.MapDB;
import plantsdefense.jdbc.PlayerDB;
import plantsdefense.model.Tile;

public class GameSession {
    private static String currentPlayerName;
    private static int playerId = -1;
    private static Tile[][] currentMap;
    private static String currentMapName = "level1.txt";
    private static int lives = 20, gold = 5000, wave = 0, score = 0, currentLevel = 1;

    public static void startNewGame(String playerName, Tile[][] map, int level) {
        currentPlayerName = playerName;
        currentMap = map;
        lives = 20; gold = 5000; wave = 0; score = 0; currentLevel = level;
        currentMapName = LevelManager.getMapFile(level);

        try {
            playerId = PlayerDB.getPlayerId(playerName);
            if (playerId == -1) playerId = PlayerDB.createPlayer(playerName);
        } catch (Exception e) { e.printStackTrace(); }

        GameState.set(GameState.State.PLAYING);
    }

    public static void startCustomGame(String playerName, Tile[][] map, int level, String mapName) {
        startNewGame(playerName, map, level);
        currentMapName = mapName;
    }

    public static boolean loadSavedGame() {
        if (playerId == -1) return false;
        try (var conn = plantsdefense.jdbc.DBConnection.getConnection();
             var ps = conn.prepareStatement("SELECT level, wave, gold, lives, score, map_name FROM saves WHERE player_id = ? ORDER BY saved_at DESC LIMIT 1")) {
            ps.setInt(1, playerId);
            var rs = ps.executeQuery();
            if (rs.next()) {
                currentLevel = rs.getInt("level");
                wave = rs.getInt("wave");
                gold = rs.getInt("gold");
                lives = rs.getInt("lives");
                score = rs.getInt("score");
                currentMapName = rs.getString("map_name");
                currentMap = MapDB.loadMap(currentMapName);
                if (currentMap != null) {
                    GameState.set(GameState.State.PLAYING);
                    return true;
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public static String getCurrentMapName() { return currentMapName; }

    public static int getPlayerId() { return playerId; }
    public static Tile[][] getCurrentMap() { return currentMap; }
    public static String getPlayerName() { return currentPlayerName; }
    public static int getLives() { return lives; }
    public static int getGold() { return gold; }
    public static int getWave() { return wave; }
    public static int getScore() { return score; }
    public static int getLevel() { return currentLevel; }

    public static void setLives(int l) { lives = l; }
    public static void setGold(int g) { gold = g; }
    public static void setWave(int w) { wave = w; }
    public static void addScore(int s) { score += s; }
    public static void nextWave() { wave++; }
    public static void loseLife() { lives--; }
    public static void addGold(int g) { gold += g; }
    public static void removeGold(int amount) { gold -= amount; }
    public static void nextLevel() { currentLevel++; }
    public static boolean isGameOver() { return lives <= 0; }
}
