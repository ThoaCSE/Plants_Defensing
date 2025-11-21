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

    // --- DYNAMIC LAYOUT FIELDS ---
    private int tileSize;
    private int mapOffsetX;
    private int mapOffsetY;
    private static final int MAP_RENDER_SIZE = 960;

    // --- UI CONFIGURATION ---
    private static final int ICON_SIZE = 64;
    private static final int BTN_W = 110;
    private static final int BTN_H = 50;

    // --- MOUSE STATE FOR HIGHLIGHTING ---
    private int hoverX = -1;
    private int hoverY = -1;

    public EditorPanel(ScreenController controller) {
        this.controller = controller;
        setPreferredSize(new Dimension(1280, 1000));
        setBackground(new Color(20, 25, 40));

        loadDefaultMap();

        // Mouse Listeners
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleClick(e.getX(), e.getY());
            }
            @Override
            public void mouseClicked(MouseEvent e) {
                handleClick(e.getX(), e.getY());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                // Reset hover when mouse leaves window
                hoverX = -1;
                hoverY = -1;
                repaint();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                // Update hover even while dragging
                hoverX = e.getX();
                hoverY = e.getY();
                paintTileAt(e.getX(), e.getY());
                repaint();
            }
            @Override
            public void mouseMoved(MouseEvent e) {
                // Track mouse for highlighting
                hoverX = e.getX();
                hoverY = e.getY();
                repaint();
            }
        });
    }

    private void loadDefaultMap() {
        try {
            grid = MapIO.loadMap("default_map.txt");
        } catch (Exception e) {
            grid = new Tile[Constants.rows][Constants.cols];
            for (int r = 0; r < Constants.rows; r++) {
                for (int c = 0; c < Constants.cols; c++) {
                    grid[r][c] = new Tile(c, r, Constants.tile_grass);
                }
            }
        }
    }

    private void handleClick(int mx, int my) {
        int w = getWidth();
        int h = getHeight();
        int sidebarX = w - 140;

        // --- 1. MENU BUTTON (Top Right) ---
        int menuBtnY = 20;
        if (mx >= sidebarX && mx <= sidebarX + BTN_W && my >= menuBtnY && my <= menuBtnY + BTN_H) {
            controller.showMenu();
            return;
        }

        // --- 2. TOOLS (Tile Selector) ---
        int toolStartY = 120;
        for (int i = 0; i < 4; i++) {
            int iy = toolStartY + i * 80;
            if (mx >= sidebarX && mx <= sidebarX + ICON_SIZE &&
                    my >= iy && my <= iy + ICON_SIZE) {

                int[] types = {Constants.tile_grass, Constants.tile_path, Constants.tile_start, Constants.tile_end};
                selected_type = types[i];
                repaint();
                return;
            }
        }

        // --- 3. SAVE/LOAD BUTTONS (Bottom Right) ---
        // FIXED: Logic now matches the paint coordinate (h - 65)
        int btnY = h - 65;
        int saveX = w - 260;
        int loadX = w - 140;

        if (mx >= saveX && mx <= saveX + BTN_W && my >= btnY && my <= btnY + BTN_H) {
            saveMap();
            return;
        }
        if (mx >= loadX && mx <= loadX + BTN_W && my >= btnY && my <= btnY + BTN_H) {
            loadMap();
            return;
        }

        // --- 4. MAP PAINTING ---
        paintTileAt(mx, my);
    }

    private void paintTileAt(int mx, int my) {
        if (tileSize == 0) return;

        int gx = (mx - mapOffsetX) / tileSize;
        int gy = (my - mapOffsetY) / tileSize;

        if (gx >= 0 && gx < Constants.cols && gy >= 0 && gy < Constants.rows) {
            grid[gy][gx].setType(selected_type);
            repaint();
        }
    }

    public Tile[][] getCurrentGrid() {
        return grid;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        int panelW = getWidth();
        int panelH = getHeight();

        // --- 1. CALCULATE LAYOUT ---
        tileSize = MAP_RENDER_SIZE / Constants.cols;
        int totalGridW = tileSize * Constants.cols;
        int totalGridH = tileSize * Constants.rows;

        // Center map, offset for sidebar
        mapOffsetX = (panelW - totalGridW) / 2 - (totalGridW/9);
        mapOffsetY = (panelH - totalGridH) / 2;

        // --- 2. DRAW MAP ---
        for (int r = 0; r < Constants.rows; r++) {
            for (int c = 0; c < Constants.cols; c++) {
                Tile t = grid[r][c];
                if (t != null) {
                    g2d.drawImage(t.getSprite(),
                            mapOffsetX + c * tileSize,
                            mapOffsetY + r * tileSize,
                            tileSize, tileSize, null);
                }
                g2d.setColor(new Color(255, 255, 255, 30));
                g2d.drawRect(mapOffsetX + c * tileSize, mapOffsetY + r * tileSize, tileSize, tileSize);
            }
        }

        // Map Border
        g2d.setColor(new Color(255, 255, 255, 222));
        g2d.setStroke(new BasicStroke(4));
        g2d.drawRect(mapOffsetX, mapOffsetY, totalGridW, totalGridH);

        // --- 3. DRAW UI SIDEBAR ---
        drawSidebar(g2d, panelW, panelH);
        g2d.dispose();
    }

    private void drawSidebar(Graphics2D g2d, int w, int h) {
        int sidebarX = w - 140;

        // --- MENU BUTTON ---
        int menuBtnY = 20;
        // Red button normally, brighter if hovered
        Color menuColor = new Color(200, 60, 60);
        drawButton(g2d, "MENU", sidebarX, menuBtnY, menuColor);

        // --- TOOLS ---
        int toolStartY = 120;
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString("TOOLS", sidebarX, toolStartY - 15);

        int[] types = {Constants.tile_grass, Constants.tile_path, Constants.tile_start, Constants.tile_end};
        String[] labels = {"Grass", "Path", "Start", "End"};

        for (int i = 0; i < 4; i++) {
            int y = toolStartY + i * 80;
            int id = types[i];

            BufferedImage icon = SpriteLoader.getSprite(id % Constants.atlas_cols, id / Constants.atlas_cols);

            if (icon != null) {
                g2d.drawImage(icon, sidebarX, y, ICON_SIZE, ICON_SIZE, null);
            }

            // Highlight Selected Tile
            if (selected_type == types[i]) {
                g2d.setColor(Color.YELLOW);
                g2d.setStroke(new BasicStroke(4));
                g2d.drawRoundRect(sidebarX - 5, y - 5, ICON_SIZE + 10, ICON_SIZE + 10, 15, 15);
                g2d.setStroke(new BasicStroke(1));
            } else if (isHovering(sidebarX, y, ICON_SIZE, ICON_SIZE)) {
                // Hover effect for tiles
                g2d.setColor(new Color(255, 255, 255, 100));
                g2d.drawRoundRect(sidebarX - 5, y - 5, ICON_SIZE + 10, ICON_SIZE + 10, 15, 15);
            }

            g2d.setColor(Color.LIGHT_GRAY);
            g2d.setFont(new Font("Arial", Font.PLAIN, 12));
            g2d.drawString(labels[i], sidebarX + ICON_SIZE + 10, y + 40);
        }

        // --- SAVE / LOAD BUTTONS ---
        int btnY = h - 65;
        int saveX = w - 260;
        int loadX = w - 140;

        drawButton(g2d, "SAVE", saveX, btnY, new Color(0, 180, 0));
        drawButton(g2d, "LOAD", loadX, btnY, new Color(0, 120, 255));
    }

    // Helper to check if mouse is over an area
    private boolean isHovering(int x, int y, int w, int h) {
        return hoverX >= x && hoverX <= x + w && hoverY >= y && hoverY <= y + h;
    }

    private void drawButton(Graphics2D g2d, String text, int x, int y, Color color) {
        boolean hovered = isHovering(x, y, BTN_W, BTN_H);

        // If hovered, make color brighter
        if (hovered) {
            color = color.brighter();
        }

        // Button Body
        g2d.setColor(color);
        g2d.fillRoundRect(x, y, BTN_W, BTN_H, 25, 25);

        // If hovered, add a yellow glow border
        if (hovered) {
            g2d.setColor(Color.YELLOW);
            g2d.setStroke(new BasicStroke(3));
            g2d.drawRoundRect(x, y, BTN_W, BTN_H, 25, 25);
        }

        // Text
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        FontMetrics fm = g2d.getFontMetrics();
        int tx = x + (BTN_W - fm.stringWidth(text)) / 2;
        g2d.drawString(text, tx, y + 33);
    }

    private void saveMap() {
        String name = JOptionPane.showInputDialog(this, "Save map as:", "level_0");
        if (name != null && !name.trim().isEmpty()) {
            if (!name.endsWith(".txt")) name += ".txt";
            MapIO.saveMap(name.trim(), grid);
            JOptionPane.showMessageDialog(this, "Saved: " + name);
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