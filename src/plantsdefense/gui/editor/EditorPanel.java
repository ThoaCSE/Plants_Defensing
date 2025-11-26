package plantsdefense.gui.editor;

import plantsdefense.gamelogic.GameSession;
import plantsdefense.gui.ScreenController;
import plantsdefense.jdbc.MapDB;
import plantsdefense.model.Tile;
import plantsdefense.util.Constants;
import plantsdefense.util.SpriteLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.sql.SQLException;
import java.util.List;

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

    // Inside EditorPanel.java
    private void loadDefaultMap() {
        // First try to load from database (recommended)
        Tile[][] loaded = MapDB.loadMap("level1.txt");

        if (loaded != null) {
            grid = loaded;
            System.out.println("Editor: Loaded default map from database (level1.txt)");
        } else {
            // Fallback: create a clean empty map manually (very rare case)
            System.out.println("Editor: Database map not found → creating empty default grid");
            grid = createDefaultGrid();
        }

        repaint();
    }

    // --- Hardcoded fallback grid (safe, clean, standard) ---
    private Tile[][] createDefaultGrid() {
        Tile[][] newGrid = new Tile[Constants.rows][Constants.cols];

        for (int row = 0; row < Constants.rows; row++) {
            for (int col = 0; col < Constants.cols; col++) {
                int type = Constants.tile_grass; // Default = grass

                // Optional: make a simple path for testing
                if (row == 4) {
                    type = Constants.tile_path;
                    if (col == 0) type = Constants.tile_start;
                    if (col == Constants.cols - 1) type = Constants.tile_end;
                }

                newGrid[row][col] = new Tile(col, row, type);
            }
        }

        return newGrid;
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

    // Add this public method to EditorPanel class
    public void setGrid(Tile[][] newGrid) {
        this.grid = newGrid;
        repaint();
    }

    private void saveMap() {
        String name = JOptionPane.showInputDialog(this, "Save map as:", "level_0");
        if (name == null || name.trim().isEmpty()) {
            return;
        }

        if (!name.endsWith(".txt")) {
            name += ".txt";
        }

        int creatorId = GameSession.getPlayerId();
        if (creatorId == -1) {
            creatorId = 0; // anonymous
        }

        try {
            MapDB.saveMap(name.trim(), grid, creatorId);
            JOptionPane.showMessageDialog(this,
                    "Map saved successfully!\nName: " + name,
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Failed to save map to database:\n" + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // src/plantsdefense/gui/editor/EditorPanel.java
    private void loadMap() {
        // Get all maps from DB
        List<String> allMaps = MapDB.listMaps();
        if (allMaps.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No maps found in database!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create dialog with list of maps
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Load Map", true);
        dialog.setSize(400, 400);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(this);

        // List of maps
        DefaultListModel<String> model = new DefaultListModel<>();
        for (String mapName : allMaps) {
            model.addElement(mapName);
        }
        JList<String> mapList = new JList<>(model);
        mapList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        mapList.setSelectedIndex(0);  // Select first by default
        JScrollPane scroll = new JScrollPane(mapList);

        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton loadBtn = new JButton("LOAD");
        JButton cancelBtn = new JButton("CANCEL");

        loadBtn.addActionListener(e -> {
            String selected = mapList.getSelectedValue();
            if (selected != null) {
                try {
                    Tile[][] loadedGrid = MapDB.loadMap(selected);
                    if (loadedGrid != null) {
                        setGrid(loadedGrid);
                        JOptionPane.showMessageDialog(dialog, "Loaded: " + selected, "Success", JOptionPane.INFORMATION_MESSAGE);
                        dialog.dispose();
                    } else {
                        JOptionPane.showMessageDialog(dialog, "Failed to load map data!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(dialog, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(loadBtn);
        buttonPanel.add(cancelBtn);

        dialog.add(scroll, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
}