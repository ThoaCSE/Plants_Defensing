package plantsdefense;

import plantsdefense.gui.EditorPanel;
import plantsdefense.util.Constants;

import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame {
    public GameFrame(){
        setTitle("Plants Defense - CSE2023_VGU");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        add(new EditorPanel());
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(GameFrame::new);
    }
}
