// src/plantsdefense/main/Main.java
package plantsdefense.main;

import plantsdefense.gui.GameFrame;
import plantsdefense.jdbc.DatabaseInitializer;

public class Main {
    public static void main(String[] args) {
        DatabaseInitializer.init();           // Runs once → creates DB + loads maps
        javax.swing.SwingUtilities.invokeLater(GameFrame::new);
    }
}