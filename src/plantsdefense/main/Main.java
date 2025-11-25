package plantsdefense.main;

import plantsdefense.gui.GameFrame;
import plantsdefense.jdbc.DatabaseInitializer;

public class Main {
    public static void main(String[] args) {
        // This runs ONCE — creates tables + inserts all your .txt levels
        DatabaseInitializer.init();

        javax.swing.SwingUtilities.invokeLater(GameFrame::new);
    }
}