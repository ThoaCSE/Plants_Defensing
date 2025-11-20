package plantsdefense.gui.editor;

import plantsdefense.dao.MapIO;
import plantsdefense.gui.ScreenController;
import plantsdefense.model.entities.Tile;
import plantsdefense.util.Constants;
import plantsdefense.util.SpriteLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class EditorPanel extends JPanel {
    private Tile[][] grid;
    private int selected_type = Constants.tile_path;
    private final ScreenController controller;

    public EditorPanel(ScreenController controller) {
        this.controller = controller;
        setPreferredSize(new Dimension(Constants.window_width, Constants.window_height));
        setBackground(Color.DARK_GRAY);
        loadDefaultMap();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int gx = e.getX() / Constants.tile_size;
                int gy = e.getY() / Constants.tile_size;
                if (gx < Constants.cols && gy < Constants.rows) {
                    grid[gy][gx].setType(selected_type);
                    repaint();
                }
            }
        });
    }

    private void loadDefaultMap() {
        grid = MapIO.loadMap("default_map.txt");
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw grid
        for (int r = 0; r < Constants.rows; r++) {
            for (int c = 0; c < Constants.cols; c++) {
                Tile t = grid[r][c];
                g.drawImage(t.getSprite(), t.getX(), t.getY(), null);
            }
        }

        // Draw selector
        g.setColor(new Color(255, 255, 255, 100));
        g.fillRect(10, 10, 200, 300);
        g.setColor(Color.BLACK);
        g.drawRect(10, 10, 200, 300);

        String[] names = {"Grass", "Path", "Start", "End"};
        int[] types = {Constants.tile_grass, Constants.tile_path, Constants.tile_start, Constants.tile_end};
        for (int i = 0; i < 4; i++) {
            int y = 50 + i * 60;
            g.drawImage(SpriteLoader.getSprite(types[i] % Constants.atlas_cols,
                    types[i] / Constants.atlas_cols), 30, y, null);
            g.drawString(names[i], 80, y + 30);
            if (selected_type == types[i]) {
                g.setColor(Color.YELLOW);
                g.drawRect(25, y - 10, 50, 50);
                g.setColor(Color.BLACK);
            }
        }

        // Instructions
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        g.drawString("Click tile to place", 20, 350);
        g.drawString("Press S to Save, L to Load", 20, 380);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(Constants.window_width, Constants.window_height);
    }
}