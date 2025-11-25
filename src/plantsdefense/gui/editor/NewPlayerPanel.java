package plantsdefense.gui.menu;

import plantsdefense.gamelogic.GameSession;
import plantsdefense.gui.ScreenController;
import plantsdefense.jdbc.MapDB;

import javax.swing.*;
import java.awt.*;

public class NewPlayerPanel extends JPanel {
    private final JTextField nameField = new JTextField(20);
    private final ScreenController controller;

    public NewPlayerPanel(ScreenController controller) {
        this.controller = controller;
        setLayout(new GridBagLayout());
        setBackground(new Color(35, 40, 65));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 0, 15, 0);

        // Title
        JLabel title = new JLabel("ENTER YOUR NAME", SwingConstants.CENTER);
        title.setFont(new Font("Consolas", Font.BOLD, 52));
        title.setForeground(Color.CYAN);
        add(title, gbc);

        // Name field
        nameField.setFont(new Font("Arial", Font.PLAIN, 36));
        nameField.setHorizontalAlignment(JTextField.CENTER);
        nameField.setPreferredSize(new Dimension(500, 70));
        add(nameField, gbc);

        // Start button
        JButton startButton = new JButton("START GAME");
        startButton.setFont(new Font("Arial", Font.BOLD, 40));
        startButton.setPreferredSize(new Dimension(500, 90));
        startButton.setBackground(new Color(0, 200, 0));
        startButton.setForeground(Color.WHITE);
        startButton.setFocusPainted(false);
        startButton.addActionListener(e -> startGame());
        add(startButton, gbc);

        // Back button
        JButton backButton = new JButton("MENU");
        backButton.setFont(new Font("Arial", Font.BOLD, 20));
        backButton.addActionListener(e -> controller.showMenu());
        add(backButton, gbc);
    }

    private void startGame() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your name!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // --- FIXED: ADDED LEVEL 1 (int) TO THE ARGUMENTS ---
        if (MapDB.loadMap("level1.txt") != null) {
            GameSession.startNewGame(name, MapDB.loadMap("level1.txt"), 1); // <--- FIX IS HERE
            controller.showPlay();
        } else {
            JOptionPane.showMessageDialog(this, "Could not load level1.txt", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}