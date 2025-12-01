package plantsdefense.main;

import plantsdefense.gui.GameFrame;
import plantsdefense.jdbc.DatabaseInitializer;

public class Main {
    public static void main(String[] args) {
        DatabaseInitializer.init();      
        javax.swing.SwingUtilities.invokeLater(GameFrame::new);
    }
}