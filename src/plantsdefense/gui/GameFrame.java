package plantsdefense.gui;

import plantsdefense.gui.editor.EditorPanel;

import javax.swing.*;

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
}
