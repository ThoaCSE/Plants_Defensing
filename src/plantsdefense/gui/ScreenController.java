package plantsdefense.gui;

import plantsdefense.gamelogic.GameSession;
import plantsdefense.gamelogic.LevelManager;
import plantsdefense.gui.editor.EditorPanel;
import plantsdefense.gui.menu.MenuPanel;
import plantsdefense.gui.menu.NewPlayerPanel;
import plantsdefense.gui.menu.PlayPanel;
import plantsdefense.jdbc.MapIO;
import plantsdefense.model.Tile;

import javax.swing.*;
import java.awt.*;

public class ScreenController {
    private final JPanel container;
    private final CardLayout layout;

    public static final String MENU = "MENU";
    public static final String NEW_PLAYER = "NEW_PLAYER";
    public static final String EDITOR = "EDITOR";
    public static final String PLAY = "PLAY";

    private EditorPanel editorPanel;
    private PlayPanel playPanel;

    public ScreenController(JPanel container, CardLayout layout) {
        this.container = container;
        this.layout = layout;
        initScreens();
    }

    private void initScreens() {
        container.add(new MenuPanel(this), MENU);
    }

    public void showMenu() {
        if (playPanel != null) playPanel.stopGame();
        layout.show(container, MENU);
    }

    public void showNewPlayer() {
        ensurePanel(new NewPlayerPanel(this), NEW_PLAYER);
        layout.show(container, NEW_PLAYER);
    }

    public void showEditor() {
        if (playPanel != null) playPanel.stopGame();
        if (editorPanel == null) {
            editorPanel = new EditorPanel(this);
            container.add(editorPanel, EDITOR);
        }
        layout.show(container, EDITOR);
    }

    // --- LEVEL LOADING LOGIC ---

    public void loadLevel(int levelIndex) {
        // 1. Get Filename from Manager
        String filename = LevelManager.getMapFile(levelIndex);

        // 2. Load Map
        if (filename != null) {
            Tile[][] map = MapIO.loadMap(filename);
            if (map != null) {
                String name = (GameSession.getPlayerName() == null) ? "Player" : GameSession.getPlayerName();
                GameSession.startNewGame(name, map, levelIndex);
                showPlay();
            } else {
                JOptionPane.showMessageDialog(container, "Error: Could not load " + filename);
                showMenu();
            }
        } else {
            // Fallback if level index is weird
            showMenu();
        }
    }

    public void playCustomLevel() {
        // 1. Ask user for Map Name
        String mapName = JOptionPane.showInputDialog(container, "Enter Map Name to Play:", "Load Custom Map", JOptionPane.QUESTION_MESSAGE);

        if (mapName != null && !mapName.trim().isEmpty()) {
            if (!mapName.endsWith(".txt")) mapName += ".txt";

            // 2. Load Map
            Tile[][] map = MapIO.loadMap(mapName);
            if (map != null) {
                // 3. Start Game as Level 4 (Custom)
                String name = (GameSession.getPlayerName() == null) ? "Player" : GameSession.getPlayerName();
                GameSession.startNewGame(name, map, 4);
                showPlay();
            } else {
                JOptionPane.showMessageDialog(container, "Map not found: " + mapName);
            }
        }
    }

    public void nextLevel() {
        int current = GameSession.getLevel();
        int max = LevelManager.getMaxLevels();

        if (current < max) {
            loadLevel(current + 1);
        } else {
            // If we beat the last level, let user play custom
            playCustomLevel();
        }
    }

    public void retryLevel() {
        // If playing custom (Level 4+), just restart current map
        if (GameSession.getLevel() > LevelManager.getMaxLevels()) {
            String name = GameSession.getPlayerName();
            Tile[][] map = GameSession.getCurrentMap(); // Reuse current map in memory
            GameSession.startNewGame(name, map, GameSession.getLevel());
            showPlay();
        } else {
            // Reload from file to be safe
            loadLevel(GameSession.getLevel());
        }
    }

    public void showPlay() {
        if (playPanel != null) {
            playPanel.stopGame();
            container.remove(playPanel);
        }
        playPanel = new PlayPanel(this);
        container.add(playPanel, PLAY);
        layout.show(container, PLAY);
    }

    // Helper for editor logic
    public Tile[][] getCurrentMapFromEditor() {
        if (editorPanel != null) return editorPanel.getCurrentGrid();
        return null;
    }

    private void ensurePanel(JPanel panel, String name) {
        boolean exists = false;
        for (Component comp : container.getComponents()) {
            if (comp.getClass().equals(panel.getClass())) {
                exists = true; break;
            }
        }
        if (!exists) container.add(panel, name);
    }
}