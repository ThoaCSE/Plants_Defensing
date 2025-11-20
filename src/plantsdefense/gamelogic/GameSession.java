package plantsdefense.gamelogic;

import plantsdefense.model.entities.Tile;

public class GameSession {
    private static String currentPlayerName;
    private static Tile[][] currentMap;
    private static int lives = 20;
    private static int gold = 500;
    private static int wave = 0;

    public static void startNewGame(String playerName, Tile[][] map) {
        currentPlayerName = playerName;
        currentMap = map;
        lives = 20;
        gold = 500;
        wave = 0;
        GameState.set(GameState.State.PLAYING);
    }

    public static Tile[][] getCurrentMap() { return currentMap; }
    public static String getPlayerName() { return currentPlayerName; }
    public static int getLives() { return lives; }
    public static int getGold() { return gold; }
    public static int getWave() { return wave; }

    public static void loseLife() { lives--; }
    public static void addGold(int amount) { gold += amount; }
    public static void spendGold(int amount) { gold -= amount; }
    public static void nextWave() { wave++; }

    public static boolean isGameOver() { return lives <= 0; }
}