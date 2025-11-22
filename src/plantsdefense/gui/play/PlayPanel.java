package plantsdefense.gui.play;

import plantsdefense.gamelogic.GameSession;
import plantsdefense.gamelogic.WaveManager;
import plantsdefense.gui.ScreenController;
import plantsdefense.model.GameObject;
import plantsdefense.model.Tile;
import plantsdefense.model.enemies.*;
import plantsdefense.model.plants.*;
import plantsdefense.util.Constants;
import plantsdefense.util.SpriteLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class PlayPanel extends JPanel {
    // --- Game State ---
    private final Tile[][] map;
    private final ScreenController controller;
    private final List<GameObject> gameObjects = new CopyOnWriteArrayList<>();
    private final WaveManager waveManager;

    private volatile boolean gameRunning = true;
    private boolean isGameOver = false;
    private boolean isVictory = false;

    // --- Threads & Timers ---
    private Thread gameThread;
    private javax.swing.Timer waveTimer;

    // --- HUD / Shop State ---
    private final List<ShopItem> shopItems = new ArrayList<>();
    private int selectedShopIndex = -1;

    // --- Layout Variables (Synced with Render Logic) ---
    private static final int MAP_RENDER_SIZE = 960; // FIXED from Previous Code
    private int renderedTileSize; // Calculated dynamically
    private int mapOffsetX;
    private int mapOffsetY;
    private int hudX, hudY, hudW, hudH;

    // --- Interaction State ---
    private int hoveredGridX = -1;
    private int hoveredGridY = -1;

    // --- UI Constants ---
    private static final Color UI_BG = new Color(30, 30, 50, 220);
    private static final int BTN_W = 200;
    private static final int BTN_H = 50;

    // Overlay Button positions
    private int retryX, menuX, nextLvlX, btnY;
    private int hoverBtnX = -1, hoverBtnY = -1;

    public PlayPanel(ScreenController controller) {
        this.controller = controller;
        setPreferredSize(new Dimension(1280, 1000));
        setBackground(new Color(20, 25, 40));

        this.map = GameSession.getCurrentMap();
        this.waveManager = new WaveManager(gameObjects, map);

        initShop();

        // Initialize default layout values to prevent divide-by-zero before first paint
        calculateLayoutDimensions();

        // 1. MOUSE INPUT
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (isGameOver) {
                    handleGameOverClick(e.getX(), e.getY());
                    return;
                }
                if (isVictory) {
                    handleVictoryClick(e.getX(), e.getY());
                    return;
                }

                int mx = e.getX();
                int my = e.getY();

                // Recalculate layout for precise clicks
                calculateLayoutDimensions();

                // Check HUD Click
                if (checkShopClick(mx, my)) return;

                // Check Map Click
                if (selectedShopIndex != -1 && hoveredGridX != -1 && hoveredGridY != -1) {
                    tryPlacePlant(hoveredGridX, hoveredGridY);
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (isGameOver || isVictory) {
                    hoverBtnX = e.getX();
                    hoverBtnY = e.getY();
                    repaint();
                    return;
                }

                calculateLayoutDimensions();

                int mx = e.getX();
                int my = e.getY();

                // Calculate using the SCALED variables
                int rawX = mx - mapOffsetX;
                int rawY = my - mapOffsetY;

                int totalMapW = renderedTileSize * Constants.cols;
                int totalMapH = renderedTileSize * Constants.rows;

                if (rawX >= 0 && rawX < totalMapW && rawY >= 0 && rawY < totalMapH) {
                    hoveredGridX = rawX / renderedTileSize;
                    hoveredGridY = rawY / renderedTileSize;
                } else {
                    hoveredGridX = -1;
                    hoveredGridY = -1;
                }
            }
            @Override
            public void mouseDragged(MouseEvent e) { mouseMoved(e); }
        });

        // 2. START LOOPS
        gameThread = new Thread(this::gameLoop);
        gameThread.start();

        waveTimer = new Timer(3000, ignored -> {
            if (gameRunning && !isGameOver && !isVictory) {
                waveManager.startNextWave();
            }
        });
        waveTimer.start();
    }

    // --- EXACT LOGIC FROM PlayPanel_previous.java ---
    private void calculateLayoutDimensions() {
        int panelW = getWidth();
        int panelH = getHeight();

        if (panelW == 0) panelW = 1280; // Fallback if not visible yet
        if (panelH == 0) panelH = 1000;

        // 1. Dynamic Tile Size based on 960px Width
        this.renderedTileSize = MAP_RENDER_SIZE / Constants.cols;

        int totalGridW = renderedTileSize * Constants.cols;
        int totalGridH = renderedTileSize * Constants.rows;

        // 2. Offset Logic from Previous Code: (Shifted Left)
        this.mapOffsetX = (panelW - totalGridW) / 2 - (totalGridW / 9);
        this.mapOffsetY = (panelH - totalGridH) / 2;

        // 3. HUD Logic: Attached to the Right of the Map
        this.hudX = mapOffsetX + totalGridW + 20;
        this.hudY = mapOffsetY;
        this.hudW = 220;
        this.hudH = 600;
    }

    public void stopGame() {
        gameRunning = false;
        if (waveTimer != null) waveTimer.stop();
        if (gameThread != null) gameThread.interrupt();
    }

    private void initShop() {
        shopItems.add(new ShopItem("Soldier", 50, 4, 1, 0));
        shopItems.add(new ShopItem("Tracker", 100, 5, 1, 1));
        shopItems.add(new ShopItem("Alchemist", 150, 6, 1, 2));
    }

    private boolean checkShopClick(int mx, int my) {
        if (mx < hudX || mx > hudX + hudW || my < hudY || my > hudY + hudH) return false;
        int itemH = 80;
        int currentY = hudY + 50;
        for (int i = 0; i < shopItems.size(); i++) {
            if (my >= currentY && my <= currentY + itemH) {
                if (selectedShopIndex == i) selectedShopIndex = -1;
                else selectedShopIndex = i;
                return true;
            }
            currentY += itemH + 10;
        }
        return false;
    }

    private void tryPlacePlant(int gx, int gy) {
        ShopItem item = shopItems.get(selectedShopIndex);

        if (map[gy][gx].getType() != Constants.tile_grass) return;

        // Occupancy Check
        for (GameObject obj : gameObjects) {
            if (obj instanceof Plant) {
                double tileCX = gx * Constants.tile_size + 16;
                double tileCY = gy * Constants.tile_size + 16;
                // Compare World Coordinates (Constants.tile_size = 32)
                if (Math.hypot(obj.getX() - tileCX, obj.getY() - tileCY) < 10) return;
            }
        }

        if (GameSession.getGold() >= item.cost) {
            GameSession.removeGold(item.cost);
            Plant p = null;
            switch (item.id) {
                case 0: p = new SoldierPlant(gx, gy, gameObjects); break;
                case 1: p = new TrackerPlant(gx, gy, gameObjects); break;
                case 2: p = new AlchemistPlant(gx, gy, gameObjects); break;
            }
            if (p != null) gameObjects.add(p);
            selectedShopIndex = -1;
        }
    }

    private void gameLoop() {
        long lastTime = System.nanoTime();
        double ns = 1000000000 / 60.0;
        double delta = 0;
        while (gameRunning) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            if (delta >= 1) {
                updateGame();
                repaint();
                delta--;
            }
            try { Thread.sleep(2); } catch (InterruptedException e) { break; }
        }
    }

    private void updateGame() {
        if (isGameOver || isVictory) return;
        waveManager.update();
        gameObjects.removeIf(obj -> !obj.isAlive());
        gameObjects.forEach(GameObject::update);
        if (GameSession.getLives() <= 0) isGameOver = true;
        if (waveManager.isLevelFinished()) isVictory = true;
    }

    // --- OVERLAYS ---
    private void handleGameOverClick(int x, int y) {
        if (isInside(x, y, retryX, btnY, BTN_W, BTN_H)) {
            stopGame();
            GameSession.startNewGame(GameSession.getPlayerName(), controller.getCurrentMapFromEditor());
            controller.showPlay();
        } else if (isInside(x, y, menuX, btnY, BTN_W, BTN_H)) {
            stopGame();
            controller.showMenu();
        }
    }

    private void handleVictoryClick(int x, int y) {
        if (isInside(x, y, nextLvlX, btnY, BTN_W, BTN_H)) {
            stopGame();
            JOptionPane.showMessageDialog(this, "Next Level (Demo)");
            GameSession.startNewGame(GameSession.getPlayerName(), controller.getCurrentMapFromEditor());
            controller.showPlay();
        } else if (isInside(x, y, menuX, btnY, BTN_W, BTN_H)) {
            stopGame();
            controller.showMenu();
        }
    }

    // --- RENDERING ---
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Update dimensions every frame (Handle Resize)
        calculateLayoutDimensions();

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawGameWorld(g2d);
        drawShopHUD(g2d);
        drawStats(g2d);

        if (isGameOver) drawOverlay(g2d, "GAME OVER", Color.RED, false);
        else if (isVictory) drawOverlay(g2d, "VICTORY!", Color.GREEN, true);
    }

    private void drawGameWorld(Graphics2D g2d) {
        AffineTransform old = g2d.getTransform();

        // 1. Translate Logic from Previous Code
        g2d.translate(mapOffsetX, mapOffsetY);

        // 2. Draw Tiles (Using Scaled 'renderedTileSize')
        for (int r = 0; r < Constants.rows; r++) {
            for (int c = 0; c < Constants.cols; c++) {
                Tile t = map[r][c];
                if (t != null) {
                    g2d.drawImage(t.getSprite(),
                            c * renderedTileSize,
                            r * renderedTileSize,
                            renderedTileSize, renderedTileSize, null);
                }
            }
        }

        // 3. Draw Objects (Using Scale Transform)
        // This ensures objects match the tile size
        double scale = (double) renderedTileSize / Constants.tile_size;
        g2d.scale(scale, scale);

        gameObjects.forEach(obj -> obj.render(g2d));

        // 4. Draw Ghost Plant (Reset Scale logic for Ghost to manually draw)
        // We need to revert scale to calculate position correctly manually or just use scale
        // It's easier to revert scale and draw normally like tiles
        g2d.setTransform(old); // Reset to (0,0)
        g2d.translate(mapOffsetX, mapOffsetY); // Move to map start

        if (selectedShopIndex != -1 && hoveredGridX != -1) {
            drawGhostPlant(g2d);
        }

        // 5. Border
        g2d.setTransform(old); // Reset again for Border
        g2d.setColor(new Color(255, 255, 255, 100));
        g2d.setStroke(new BasicStroke(2));
        // Calculate total width/height again for border
        int totalW = renderedTileSize * Constants.cols;
        int totalH = renderedTileSize * Constants.rows;
        g2d.drawRect(mapOffsetX, mapOffsetY, totalW, totalH);
    }

    private void drawGhostPlant(Graphics2D g2d) {
        ShopItem item = shopItems.get(selectedShopIndex);

        // Coordinates in "Map Space"
        int px = hoveredGridX * renderedTileSize;
        int py = hoveredGridY * renderedTileSize;

        BufferedImage sprite = SpriteLoader.getSprite(item.col, item.row);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));

        if (sprite != null) {
            g2d.drawImage(sprite, px, py, renderedTileSize, renderedTileSize, null);
        }

        boolean valid = (map[hoveredGridY][hoveredGridX].getType() == Constants.tile_grass);
        g2d.setColor(valid ? Color.GREEN : Color.RED);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRect(px, py, renderedTileSize, renderedTileSize);
    }

    private void drawShopHUD(Graphics2D g) {
        g.setColor(UI_BG);
        g.fillRect(hudX, hudY, hudW, hudH);
        g.setColor(Color.WHITE);
        g.drawRect(hudX, hudY, hudW, hudH);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.drawString("DEFENDERS", hudX + 50, hudY + 30);

        int itemY = hudY + 50;
        for (int i = 0; i < shopItems.size(); i++) {
            ShopItem item = shopItems.get(i);
            boolean selected = (i == selectedShopIndex);
            g.setColor(selected ? new Color(80, 120, 80) : new Color(50, 50, 70));
            g.fillRect(hudX + 10, itemY, hudW - 20, 80);

            BufferedImage icon = SpriteLoader.getSprite(item.col, item.row);
            if (icon != null) g.drawImage(icon, hudX + 20, itemY + 8, 64, 64, null);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 14));
            g.drawString(item.name, hudX + 90, itemY + 30);
            g.setColor(GameSession.getGold() >= item.cost ? Color.YELLOW : Color.RED);
            g.drawString("$" + item.cost, hudX + 90, itemY + 55);
            itemY += 90;
        }
    }

    private void drawStats(Graphics2D g) {
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.setColor(Color.WHITE);
        g.drawString("Lives: " + GameSession.getLives(), 50, getHeight() - 50);
        g.drawString("Gold: " + GameSession.getGold(), 200, getHeight() - 50);
        g.drawString("Wave: " + GameSession.getWave(), 400, getHeight() - 50);
    }

    private void drawOverlay(Graphics2D g, String title, Color color, boolean isWin) {
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(0, 0, getWidth(), getHeight());
        int cx = getWidth() / 2;
        int cy = getHeight() / 2;

        g.setFont(new Font("Chiller", Font.BOLD, 100));
        g.setColor(color);
        int titleW = g.getFontMetrics().stringWidth(title);
        g.drawString(title, cx - titleW / 2, cy - 100);

        this.btnY = cy + 20;
        this.menuX = cx + 20;
        if (isWin) {
            this.nextLvlX = cx - BTN_W - 20;
            drawButton(g, "NEXT LEVEL", nextLvlX, btnY);
        } else {
            this.retryX = cx - BTN_W - 20;
            drawButton(g, "RETRY", retryX, btnY);
        }
        drawButton(g, "MENU", menuX, btnY);
    }

    private void drawButton(Graphics2D g, String text, int x, int y) {
        boolean hover = isInside(hoverBtnX, hoverBtnY, x, y, BTN_W, BTN_H);
        g.setColor(hover ? Color.YELLOW : Color.WHITE);
        g.setStroke(new BasicStroke(3));
        g.drawRoundRect(x, y, BTN_W, BTN_H, 20, 20);
        g.setColor(new Color(255, 255, 255, 50));
        g.fillRoundRect(x, y, BTN_W, BTN_H, 20, 20);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        int tw = g.getFontMetrics().stringWidth(text);
        g.drawString(text, x + (BTN_W - tw)/2, y + 35);
    }

    private boolean isInside(int mx, int my, int x, int y, int w, int h) {
        return mx >= x && mx <= x + w && my >= y && my <= y + h;
    }

    private static class ShopItem {
        String name;
        int cost, col, row, id;
        public ShopItem(String name, int cost, int col, int row, int id) {
            this.name = name;
            this.cost = cost;
            this.col = col;
            this.row = row;
            this.id = id;
        }
    }
}