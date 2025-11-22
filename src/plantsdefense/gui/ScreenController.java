package plantsdefense.gui;

import plantsdefense.gui.editor.EditorPanel;
import plantsdefense.gui.menu.MenuPanel;
import plantsdefense.gui.menu.NewPlayerPanel;
import plantsdefense.gui.play.PlayPanel;
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
        if (playPanel != null) playPanel.stopGame(); // Safety stop
        layout.show(container, MENU);
    }

    public void showNewPlayer() {
        ensurePanel(new NewPlayerPanel(this), NEW_PLAYER);
        layout.show(container, NEW_PLAYER);
    }

    public void showEditor() {
        if (playPanel != null) playPanel.stopGame(); // Safety stop
        if (editorPanel == null) {
            editorPanel = new EditorPanel(this);
            container.add(editorPanel, EDITOR);
        }
        layout.show(container, EDITOR);
    }

    public void showPlay() {
        // 1. Stop old thread
        if (playPanel != null) {
            playPanel.stopGame();
            container.remove(playPanel);
        }

        // 2. Create fresh panel
        playPanel = new PlayPanel(this);
        container.add(playPanel, PLAY);
        layout.show(container, PLAY);
    }

    public void playCurrentMap() {
        showPlay();
    }

    public Tile[][] getCurrentMapFromEditor() {
        if (editorPanel != null) {
            return editorPanel.getCurrentGrid();
        }
        return plantsdefense.jdbc.MapIO.loadMap("level1.txt");
    }

    private void ensurePanel(JPanel panel, String name) {
        for (Component c : container.getComponents()) {
            if (name.equals(c.getName())) return;
        }
        panel.setName(name);
        container.add(panel, name);
    }
}