package plantsdefense.gamelogic;

import plantsdefense.model.Tile;
import plantsdefense.util.SpriteLoader;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class GameSession {
    private static String currentPlayerName;
    private static Tile[][] currentMap;
    private static int lives = 20;
    private static int gold = 99999;
    private static int wave = 0;
    private static int currentLevel = 1;

    // --- NEW: SCORE VARIABLE ---
    private static int score = 0;

    public static void startNewGame(String playerName, Tile[][] map, int level) {
        currentPlayerName = playerName;
        currentMap = map;
        lives = 20;
        gold = 5000;
        wave = 0;
        score = 0; // Reset Score
        currentLevel = level;
        GameState.set(GameState.State.PLAYING);
    }

    // --- GETTERS & SETTERS ---
    public static Tile[][] getCurrentMap() { return currentMap; }
    public static String getPlayerName() { return currentPlayerName; }
    public static int getLives() { return lives; }
    public static int getGold() { return gold; }
    public static int getWave() { return wave; }
    public static int getLevel() { return currentLevel; }

    // --- NEW SCORE METHODS ---
    public static int getScore() { return score; }
    public static void addScore(int amount) { score += amount; }

    // --- GAME ACTIONS ---
    public static void loseLife() { lives--; }
    public static void addGold(int amount) { gold += amount; }
    public static void removeGold(int amount) { gold -= amount; }
    public static void nextWave() { wave++; }

    public static boolean isGameOver() {
        return lives <= 0;
    }
}