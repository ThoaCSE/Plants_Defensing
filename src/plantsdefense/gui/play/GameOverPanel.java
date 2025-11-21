package plantsdefense.gui.play;

import plantsdefense.gui.ScreenController;
import plantsdefense.gamelogic.GameSession;

import javax.swing.*;
import java.awt.*;

public class GameOverPanel extends JPanel {
    private final ScreenController controller;

    public GameOverPanel(ScreenController controller) {
        this.controller = controller;
        setLayout(new BorderLayout());
        setBackground(new Color(0, 0, 0, 220));

        JLabel title = new JLabel("YOU DIED", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 100));
        title.setForeground(Color.RED);
        title.setBorder(BorderFactory.createEmptyBorder(150, 0, 50, 0));
        add(title, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(0, 0, 0, 0));

        JButton retry = createButton("RETRY", new Color(0, 180, 0));
        retry.addActionListener(e -> {
            controller.remove(this);
            GameSession.startNewGame(GameSession.getPlayerName(), GameSession.getCurrentMap());
            controller.showPlay(); // Restart game
        });

        JButton menu = createButton("BACK TO MENU", new Color(100, 100, 255));
        menu.addActionListener(e -> {
            controller.remove(this);
            controller.showMenu();
        });

        buttonPanel.add(retry);
        buttonPanel.add(Box.createHorizontalStrut(50));
        buttonPanel.add(menu);
        add(buttonPanel, BorderLayout.CENTER);
    }

    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 32));
        btn.setPreferredSize(new Dimension(300, 80));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        return btn;
    }
}