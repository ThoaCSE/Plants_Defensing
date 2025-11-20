package plantsdefense.gui;

import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame {
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel mainPanel = new JPanel(cardLayout);
    private final ScreenController screenController;

    public GameFrame() {
        setTitle("Plants Defense - Tower Defense OOP Project");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1280, 720);
        setLocationRelativeTo(null);
        setResizable(false);

        screenController = new ScreenController(mainPanel, cardLayout);
        add(mainPanel);

        // Start with Menu
        screenController.showMenu();

        setVisible(true);
    }

    public ScreenController getScreenController() {
        return screenController;
    }
}