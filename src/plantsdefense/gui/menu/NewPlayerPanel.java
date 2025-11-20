package plantsdefense.gui.menu;

import plantsdefense.gui.ScreenController;
import javax.swing.*;
import java.awt.*;

public class NewPlayerPanel extends JPanel {
    private final JTextField nameField = new JTextField(20);

    public NewPlayerPanel(ScreenController controller) {
        setLayout(new BorderLayout());
        setBackground(new Color(40, 40, 60));

        JLabel label = new JLabel("Enter Your Name:", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 32));
        label.setForeground(Color.WHITE);
        add(label, BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.add(nameField);
        add(center, BorderLayout.CENTER);

        JButton start = new JButton("START GAME");
        start.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (!name.isEmpty()) {
                // Later: Save player → start game
                System.out.println("New player: " + name);
                // controller.showPlay();
            }
        });
        add(start, BorderLayout.SOUTH);
    }
}