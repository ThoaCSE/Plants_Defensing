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

    // Scale
    private static final double MAP_SCALE = 0.75;
    private static final int TILE = (int) (Constants.tile_size * MAP_SCALE);
    private static final int GRID_W = Constants.cols * TILE;
    private static final int GRID_H = Constants.rows * TILE;

    // Positions
    private static final int GRID_X = 60;
    private static final int GRID_Y = 60;
    private static final int SELECTOR_X = Constants.window_width - 140;  // Right edge
    private static final int SELECTOR_Y = 100;
    private static final int ICON_SIZE = 64;

    // Button positions
    private static final int SAVE_X = Constants.window_width - 260;
    private static final int LOAD_X = Constants.window_width - 140;
    private static final int BTN_Y = Constants.window_height - 100;
    private static final int BTN_W = 110;
    private static final int BTN_H = 50;

    private boolean isDragging = false;

    public EditorPanel(ScreenController controller) {
        setPreferredSize(new Dimension(Constants.window_width, Constants.window_height));
        setBackground(new Color(28, 32, 48));
        loadDefaultMap();

        // Mouse click + drag for painting
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleClick(e.getX(), e.getY(), true);
                isDragging = true;
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isDragging = false;
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (!isDragging) handleClick(e.getX(), e.getY(), false);
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (isDragging) {
                    paintTileAt(e.getX(), e.getY());
                }
            }
        });
    }

    private void handleClick(int mx, int my, boolean fromPress) {
        // Tile Selector
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

        // Save Button
        if (mx >= SAVE_X && mx <= SAVE_X + BTN_W && my >= BTN_Y && my <= BTN_Y + BTN_H) {
            saveMap();
            return;
        }

        // Load Button
        if (mx >= LOAD_X && mx <= LOAD_X + BTN_W && my >= BTN_Y && my <= BTN_Y + BTN_H) {
            loadMap();
            return;
        }

        // Grid painting
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

    private void loadDefaultMap() {
        grid = MapIO.loadMap("default_map.txt");
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        // Draw map (75%)
        for (int r = 0; r < Constants.rows; r++) {
            for (int c = 0; c < Constants.cols; c++) {
                Tile t = grid[r][c];
                BufferedImage img = t.getSprite();
                if (img != null) {
                    g2d.drawImage(img, GRID_X + c * TILE, GRID_Y + r * TILE, TILE, TILE, null);
                }
            }
        }

        // Grid lines
        g2d.setColor(new Color(255, 255, 255, 40));
        for (int i = 0; i <= Constants.cols; i++) g2d.drawLine(GRID_X + i * TILE, GRID_Y, GRID_X + i * TILE, GRID_Y + GRID_H);
        for (int i = 0; i <= Constants.rows; i++) g2d.drawLine(GRID_X, GRID_Y + i * TILE, GRID_X + GRID_W, GRID_Y + i * TILE);

        // Title
        g2d.setColor(Color.CYAN);
        g2d.setFont(new Font("Consolas", Font.BOLD, 48));
        g2d.drawString("MAP EDITOR", 60, 50);

        // Tile Selector (Right edge)
        g2d.setColor(new Color(0, 0, 0, 200));
        g2d.fillRoundRect(SELECTOR_X - 20, SELECTOR_Y - 40, 100, 360, 30, 30);
        g2d.setColor(Color.CYAN);
        g2d.drawRoundRect(SELECTOR_X - 20, SELECTOR_Y - 40, 100, 360, 30, 30);

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        g2d.drawString("TILES", SELECTOR_X - 10, SELECTOR_Y - 10);

        int[] types = {Constants.tile_grass, Constants.tile_path, Constants.tile_start, Constants.tile_end};
        for (int i = 0; i < 4; i++) {
            int y = SELECTOR_Y + i * 80;
            BufferedImage icon = SpriteLoader.getSprite(types[i] % Constants.atlas_cols, types[i] / Constants.atlas_cols);
            if (icon != null) {
                g2d.drawImage(icon, SELECTOR_X, y, ICON_SIZE, ICON_SIZE, null);
            }
            if (selected_type == types[i]) {
                g2d.setColor(Color.YELLOW);
                g2d.setStroke(new BasicStroke(4));
                g2d.drawRoundRect(SELECTOR_X - 8, y - 8, ICON_SIZE + 16, ICON_SIZE + 16, 20, 20);
                g2d.setStroke(new BasicStroke(1));
            }
        }

        // Save & Load Buttons (spaced nicely)
        drawButton(g2d, "SAVE", SAVE_X, BTN_Y, new Color(0, 180, 0));
        drawButton(g2d, "LOAD", LOAD_X, BTN_Y, new Color(0, 120, 255));

        g2d.dispose();
    }

    private void drawButton(Graphics2D g2d, String text, int x, int y, Color color) {
        g2d.setColor(color);
        g2d.fillRoundRect(x, y, BTN_W, BTN_H, 25, 25);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        FontMetrics fm = g2d.getFontMetrics();
        int tx = x + (BTN_W - fm.stringWidth(text)) / 2;
        int ty = y + 33;
        g2d.drawString(text, tx, ty);
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