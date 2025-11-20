package plantsdefense.gui;

import plantsdefense.gui.menu.MenuPanel;
import plantsdefense.gui.menu.NewPlayerPanel;
import plantsdefense.gui.editor.EditorPanel;

import javax.swing.*;
import java.awt.*;

public class ScreenController {
    private final JPanel container;
    private final CardLayout layout;

    public static final String MENU = "MENU";
    public static final String NEW_PLAYER = "NEW_PLAYER";
    public static final String EDITOR = "EDITOR";

    public ScreenController(JPanel container, CardLayout layout) {
        this.container = container;
        this.layout = layout;
        initScreens();
    }

    private void initScreens() {
        container.add(new MenuPanel(this), MENU);
    }

    public void showMenu() {
        layout.show(container, MENU);
    }

    public void showNewPlayer() {
        if (container.getComponentCount() == 1) {
            container.add(new NewPlayerPanel(this), NEW_PLAYER);
        }
        layout.show(container, NEW_PLAYER);
    }

    public void showEditor() {
        container.add(new EditorPanel(this), EDITOR);
        layout.show(container, EDITOR);
    }

    public void showPlay() {
        // Later episodes
    }
}