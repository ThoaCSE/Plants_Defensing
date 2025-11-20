package plantsdefense.gui.menu;

import plantsdefense.gui.ScreenController;
import javax.swing.*;
import java.awt.*;

public class MenuPanel extends JPanel {
    public MenuPanel(ScreenController controller) {
        setLayout(new GridBagLayout());
        setBackground(new Color(30, 30, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 100, 15, 100);

        JLabel title = new JLabel("PLANTS DEFENSE", SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 64));
        title.setForeground(Color.YELLOW);
        add(title, gbc);

        addButton("NEW GAME", e -> controller.showNewPlayer(), gbc);
        addButton("CONTINUE", e -> System.out.println("Continue clicked"), gbc);
        addButton("MAP EDITOR", e -> controller.showEditor(), gbc);
        addButton("QUIT", e -> System.exit(0), gbc);
    }

    private void addButton(String text, java.awt.event.ActionListener action, GridBagConstraints gbc) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 28));
        btn.setPreferredSize(new Dimension(400, 80));
        btn.addActionListener(action);
        add(btn, gbc);
    }
}