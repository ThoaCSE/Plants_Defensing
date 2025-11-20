package plantsdefense.gui.editor;

import plantsdefense.gui.ScreenController;
import javax.swing.*;
import java.awt.*;

public class EditorPanel extends JPanel {
    public EditorPanel(ScreenController controller) {
        setBackground(Color.DARK_GRAY);
        JLabel label = new JLabel("MAP EDITOR - Coming in Ep3", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 40));
        label.setForeground(Color.CYAN);
        add(label);
    }
}