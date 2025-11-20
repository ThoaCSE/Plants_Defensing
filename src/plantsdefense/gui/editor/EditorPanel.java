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
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;

public class EditorPanel extends JPanel {
    private Tile[][] grid;
    private int selected_type = Constants.tile_path;
    private final ScreenController controller;

    private static final double MAP_SCALE = 0.75;
    private static final int TILE = (int) (Constants.tile_size * MAP_SCALE);
    private static final int GRID_W = Constants.cols * TILE;
    private static final int GRID_H = Constants.rows * TILE;

    private static final int GRID_X = 60;
    private static final int GRID_Y = 60;
    private static final int SELECTOR_X = Constants.window_width - 140;
    private static final int SELECTOR_Y = 100;
    private static final int ICON_SIZE = 64;

    private static final int SAVE_X = Constants.window_width - 260;
    private static final int LOAD_X = Constants.window_width - 140;
    private static final int BTN_Y = Constants.window_height - 100;
    private static final int BTN_W = 110;
    private static final int BTN_H = 50;

    private boolean isDragging = false;

    public EditorPanel(ScreenController controller) {
        this.controller = controller;
        setPreferredSize(new Dimension(Constants.window_width, Constants.window_height));
        setBackground(new Color(28, 32, 48));
        loadDefaultMap();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleClick(e.getX(), e.getY());
                isDragging = true;
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isDragging = false;
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (isDragging) paintTileAt(e.getX(), e.getY());
            }
        });
    }

    private void loadDefaultMap() {
        grid = MapIO.loadMap("default_map.txt");
    }

    private void handleClick(int mx, int my) {
        // Selector
        for (int i = 0; i < 4; i++) {
            int iy = SELECTOR_Y + i * 80;
            if (mx >= SELECTOR_X && mx <= SELECTOR_X + ICON_SIZE &&
                    my >= iy && my <= iy + ICON_SIZE) {
                int[] types = {Constants.tile_grass, Constants.tile_path, Constants.tile_start, Constants.tile_end};
                selected_type = types[i];
                repaint();
                return;
            }
        }

        // Buttons
        if (mx >= SAVE_X && mx <= SAVE_X + BTN_W && my >= BTN_Y && my <= BTN_Y + BTN_H) {
            saveMap();
            return;
        }
        if (mx >= LOAD_X && mx <= LOAD_X + BTN_W && my >= BTN_Y && my <= BTN_Y + BTN_H) {
            loadMap();
            return;
        }

        paintTileAt(mx, my);
    }

    private void paintTileAt(int mx, int my) {
        int gx = (mx - GRID_X) / TILE;
        int gy = (my - GRID_Y) / TILE;
        if (gx >= 0 && gx < Constants.cols && gy >= 0 && gy < Constants.rows) {
            grid[gy][gx].setType(selected_type);
            repaint();
        }
    }

    public Tile[][] getCurrentGrid() {
        return grid;
    }

    // [paintComponent, drawButton, saveMap, loadMap — same as last version]
    // ... (keep the beautiful paint code from previous message)

    @Override
    protected void paintComponent(Graphics g) {
        // ... (same beautiful code from last message)
        // Just make sure getCurrentGrid() is available
    }

    private void drawButton(Graphics2D g2d, String text, int x, int y, Color color) {
        g2d.setColor(color);
        g2d.fillRoundRect(x, y, BTN_W, BTN_H, 25, 25);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        FontMetrics fm = g2d.getFontMetrics();
        int tx = x + (BTN_W - fm.stringWidth(text)) / 2;
        g2d.drawString(text, tx, y + 33);
    }

    private void saveMap() {
        String name = JOptionPane.showInputDialog(this, "Save map as:", "my_level");
        if (name != null && !name.trim().isEmpty()) {
            MapIO.saveMap(name.trim() + ".txt", grid);
            JOptionPane.showMessageDialog(this, "Saved: " + name + ".txt");
        }
    }

    private void loadMap() {
        JFileChooser fc = new JFileChooser("res/levels/");
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            grid = MapIO.loadMap(fc.getSelectedFile().getName());
            repaint();
        }
    }
}