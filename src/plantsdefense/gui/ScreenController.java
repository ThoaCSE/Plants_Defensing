// src/plantsdefense/gui/ScreenController.java
package plantsdefense.gui;

import plantsdefense.gamelogic.GameSession;
import plantsdefense.gamelogic.LevelManager;
import plantsdefense.gui.editor.EditorPanel;
import plantsdefense.gui.editor.MapListPanel;
import plantsdefense.gui.menu.*;
import plantsdefense.jdbc.MapDB;
import plantsdefense.model.Tile;

import javax.swing.*;
import java.awt.*;

public class ScreenController {
    private final JPanel container;
    private final CardLayout layout;

    public static final String MENU = "MENU";
    public static final String NEW_PLAYER = "NEW_PLAYER";
    public static final String LOAD_SAVE = "LOAD_SAVE"; // ← NEW
    public static final String LEADERBOARD = "LEADERBOARD"; // ← NEW
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

    public void showCustomMapSelector() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(container), "Custom Maps", true);
        dialog.setSize(500, 600);
        dialog.setLocationRelativeTo(container);
        dialog.setResizable(false);

        MapListPanel panel = new MapListPanel(new MapListPanel.OnMapSelectedListener() {
            @Override
            public void onMapSelected(String mapName) {
                Tile[][] map = MapDB.loadMap(mapName);
                if (map != null) {
                    GameSession.startNewGame(GameSession.getPlayerName(), map, 4); // Level 4+ = custom
                    showPlay();
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to load map!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }

            @Override
            public void onBack() {
                dialog.dispose();
                showMenu();
            }
        });

        dialog.add(panel);
        dialog.setVisible(true);
    }
    public void showLoadSave() {
        ensurePanel(new LoadSavePanel(this), LOAD_SAVE);
        layout.show(container, LOAD_SAVE);
    }

    public void showLeaderboard() {
        ensurePanel(new LeaderboardPanel(this), LEADERBOARD);
        layout.show(container, LEADERBOARD);
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
        String filename = LevelManager.getMapFile(levelIndex);
        if (filename != null) {
            Tile[][] map = MapDB.loadMap(filename);
            if (map != null) {
                String name = (GameSession.getPlayerName() == null) ? "Player" : GameSession.getPlayerName();
                GameSession.startNewGame(name, map, levelIndex);
                showPlay();
            } else {
                JOptionPane.showMessageDialog(container, "Error: Could not load " + filename);
                showMenu();
            }
        }
    }

    public void playCustomLevel() {
        String mapName = JOptionPane.showInputDialog(container, "Enter custom map name:");
        if (mapName != null && !mapName.trim().isEmpty()) {
            if (!mapName.endsWith(".txt")) mapName += ".txt";

            Tile[][] map = MapDB.loadMap(mapName);
            if (map != null) {
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
            playCustomLevel();
        }
    }

    public void retryLevel() {
        if (GameSession.getLevel() > LevelManager.getMaxLevels()) {
            String name = GameSession.getPlayerName();
            Tile[][] map = GameSession.getCurrentMap();
            GameSession.startNewGame(name, map, GameSession.getLevel());
            showPlay();
        } else {
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