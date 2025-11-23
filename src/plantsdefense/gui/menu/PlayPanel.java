package plantsdefense.gui.menu;

import plantsdefense.gamelogic.GameSession;
import plantsdefense.gamelogic.LevelManager;
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
    private final Tile[][] map;
    private final ScreenController controller;
    private final List<GameObject> gameObjects = new CopyOnWriteArrayList<>();
    private final WaveManager waveManager;

    private volatile boolean gameRunning = true;
    private boolean isGameOver = false;
    private boolean isVictory = false;

    private Thread gameThread;
    private javax.swing.Timer waveTimer;

    private final List<ShopItem> shopItems = new ArrayList<>();
    private int selectedShopIndex = -1;

    private static final int MAP_RENDER_SIZE = 960;
    private int renderedTileSize;
    private int mapOffsetX, mapOffsetY;
    private int hudX, hudY, hudW, hudH;

    private int hoveredGridX = -1, hoveredGridY = -1;
    private static final Color UI_BG = new Color(30, 30, 50, 220);
    private static final int BTN_W = 200, BTN_H = 50;
    private int retryX, menuX, nextLvlX, btnY;
    private int hoverBtnX = -1, hoverBtnY = -1;

    public PlayPanel(ScreenController controller) {
        this.controller = controller;
        setPreferredSize(new Dimension(1280, 1000));
        setBackground(new Color(20, 25, 40));

        this.map = GameSession.getCurrentMap();
        this.waveManager = new WaveManager(gameObjects, map, LevelManager.getWaveConfig(GameSession.getLevel()));

        initShop();
        calculateLayoutDimensions();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (isGameOver) { handleGameOverClick(e.getX(), e.getY()); return; }
                if (isVictory) { handleVictoryClick(e.getX(), e.getY()); return; }

                calculateLayoutDimensions();

                // Right Click to Deselect (Cancel placing)
                if (SwingUtilities.isRightMouseButton(e)) {
                    selectedShopIndex = -1;
                    return;
                }

                if (checkShopClick(e.getX(), e.getY())) return;

                if (selectedShopIndex != -1 && hoveredGridX != -1 && hoveredGridY != -1) {
                    tryPlaceOrRemovePlant(hoveredGridX, hoveredGridY);
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (isGameOver || isVictory) {
                    hoverBtnX = e.getX(); hoverBtnY = e.getY(); repaint(); return;
                }
                calculateLayoutDimensions();
                int rawX = e.getX() - mapOffsetX;
                int rawY = e.getY() - mapOffsetY;
                int totalMapW = renderedTileSize * Constants.cols;
                int totalMapH = renderedTileSize * Constants.rows;

                if (rawX >= 0 && rawX < totalMapW && rawY >= 0 && rawY < totalMapH) {
                    hoveredGridX = rawX / renderedTileSize;
                    hoveredGridY = rawY / renderedTileSize;
                } else {
                    hoveredGridX = -1; hoveredGridY = -1;
                }
            }
            @Override public void mouseDragged(MouseEvent e) { mouseMoved(e); }
        });

        gameThread = new Thread(this::gameLoop);
        gameThread.start();
        waveTimer = new Timer(3000, ignored -> {
            if (gameRunning && !isGameOver && !isVictory) waveManager.startNextWave();
        });
        waveTimer.start();
    }

    private void initShop() {
        shopItems.add(new ShopItem("Soldier", 50, 4, 1, 0));
        shopItems.add(new ShopItem("Tracker", 100, 5, 1, 1));
        shopItems.add(new ShopItem("Alchemist", 150, 6, 1, 2));
        // --- NEW: REMOVE TOOL (ID = -1) ---
        shopItems.add(new ShopItem("Remove", 0, 0, 0, -1));
    }

    private void tryPlaceOrRemovePlant(int gx, int gy) {
        ShopItem item = shopItems.get(selectedShopIndex);

        // --- CASE 1: REMOVE PLANT ---
        if (item.id == -1) {
            Plant found = null;
            // Find plant at this location
            for (GameObject obj : gameObjects) {
                if (obj instanceof Plant) {
                    double tileCX = gx * Constants.tile_size + 16;
                    double tileCY = gy * Constants.tile_size + 16;
                    if (Math.hypot(obj.getX() - tileCX, obj.getY() - tileCY) < 10) {
                        found = (Plant) obj;
                        break;
                    }
                }
            }
            if (found != null) {
                gameObjects.remove(found);
                GameSession.addGold(found.getCost()); // Refund
            }
            return; // Don't deselect, allow multi-delete
        }

        // --- CASE 2: PLACE PLANT ---
        if (map[gy][gx].getType() != Constants.tile_grass) return;

        // Check Occupancy
        for (GameObject obj : gameObjects) {
            if (obj instanceof Plant) {
                double tileCX = gx * Constants.tile_size + 16;
                double tileCY = gy * Constants.tile_size + 16;
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

            // --- NEW: MULTI-PLACE ---
            // selectedShopIndex = -1; // Commented out to allow placing multiple
        }
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

        // --- NEW: SHOW SCORE UNDER VICTORY ---
        if (isWin) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            String scoreText = "Score: " + GameSession.getScore();
            int scoreW = g.getFontMetrics().stringWidth(scoreText);
            g.drawString(scoreText, cx - scoreW / 2, cy - 30);
        }

        this.btnY = cy + 50; // Shifted down slightly
        this.menuX = cx + 20;

        if (isWin) {
            this.nextLvlX = cx - BTN_W - 20;
            String btnText = (GameSession.getLevel() >= LevelManager.getMaxLevels()) ? "LOAD MAP" : "NEXT LEVEL";
            drawButton(g, btnText, nextLvlX, btnY);
        } else {
            this.retryX = cx - BTN_W - 20;
            drawButton(g, "RETRY", retryX, btnY);
        }
        drawButton(g, "MENU", menuX, btnY);
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

            // --- DRAW "X" IF REMOVE TOOL ---
            if (item.id == -1) {
                g.setColor(Color.RED);
                g.setFont(new Font("Arial", Font.BOLD, 40));
                g.drawString("X", hudX + 40, itemY + 55);
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 14));
                g.drawString("REMOVE", hudX + 90, itemY + 45);
            } else {
                BufferedImage icon = SpriteLoader.getSprite(item.col, item.row);
                if (icon != null) g.drawImage(icon, hudX + 20, itemY + 8, 64, 64, null);

                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 14));
                g.drawString(item.name, hudX + 90, itemY + 30);
                g.setColor(GameSession.getGold() >= item.cost ? Color.YELLOW : Color.RED);
                g.drawString("$" + item.cost, hudX + 90, itemY + 55);
            }
            itemY += 90;
        }
    }

    // ... (Keep existing methods: calculateLayoutDimensions, stopGame, checkShopClick, gameLoop, updateGame, drawGameWorld, drawStats, drawButton, isInside, etc.) ...
    // Note: Be sure to include the unchanged methods I omitted here for brevity if you are copy-pasting the whole file.

    // --- OMITTED METHODS (STANDARD) ---
    private void calculateLayoutDimensions() {
        int panelW = getWidth() == 0 ? 1280 : getWidth();
        int panelH = getHeight() == 0 ? 1000 : getHeight();
        this.renderedTileSize = MAP_RENDER_SIZE / Constants.cols;
        int totalGridW = renderedTileSize * Constants.cols;
        int totalGridH = renderedTileSize * Constants.rows;
        this.mapOffsetX = (panelW - totalGridW) / 2 - (totalGridW / 9);
        this.mapOffsetY = (panelH - totalGridH) / 2;
        this.hudX = mapOffsetX + totalGridW + 20;
        this.hudY = mapOffsetY;
        this.hudW = 220;
        this.hudH = 600;
    }
    private boolean checkShopClick(int mx, int my) {
        if (mx < hudX || mx > hudX + hudW || my < hudY || my > hudY + hudH) return false;
        int itemH = 80;
        int currentY = hudY + 50;
        for (int i = 0; i < shopItems.size(); i++) {
            if (my >= currentY && my <= currentY + itemH) {
                selectedShopIndex = (selectedShopIndex == i) ? -1 : i;
                return true;
            }
            currentY += itemH + 10;
        }
        return false;
    }
    public void stopGame() {
        gameRunning = false;
        if (waveTimer != null) waveTimer.stop();
        if (gameThread != null) gameThread.interrupt();
    }
    private void gameLoop() {
        long lastTime = System.nanoTime();
        double ns = 1000000000 / 60.0;
        double delta = 0;
        while (gameRunning) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            if (delta >= 1) { updateGame(); repaint(); delta--; }
            try { Thread.sleep(2); } catch (InterruptedException e) { break; }
        }
    }
    private void updateGame() {
        if (isGameOver || isVictory) return;
        waveManager.update();
        gameObjects.removeIf(obj -> !obj.isAlive());
        gameObjects.forEach(GameObject::update);
        if (GameSession.getLives() <= 0) isGameOver = true;
        if (waveManager.isLevelFinished()) {
            if (!isVictory) {
                int livesBonus = GameSession.getLives() * 100;
                GameSession.addScore(livesBonus);
                isVictory = true;
            }
        }
    }
    private void handleGameOverClick(int x, int y) {
        if (isInside(x, y, retryX, btnY, BTN_W, BTN_H)) { stopGame(); controller.retryLevel(); }
        else if (isInside(x, y, menuX, btnY, BTN_W, BTN_H)) { stopGame(); controller.showMenu(); }
    }
    private void handleVictoryClick(int x, int y) {
        if (isInside(x, y, nextLvlX, btnY, BTN_W, BTN_H)) {
            stopGame();
            if (GameSession.getLevel() >= LevelManager.getMaxLevels()) controller.playCustomLevel();
            else controller.nextLevel();
        } else if (isInside(x, y, menuX, btnY, BTN_W, BTN_H)) { stopGame(); controller.showMenu(); }
    }
    @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
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
        g2d.translate(mapOffsetX, mapOffsetY);
        for (int r = 0; r < Constants.rows; r++) {
            for (int c = 0; c < Constants.cols; c++) {
                Tile t = map[r][c];
                if (t != null) g2d.drawImage(t.getSprite(), c * renderedTileSize, r * renderedTileSize, renderedTileSize, renderedTileSize, null);
            }
        }
        double scale = (double) renderedTileSize / Constants.tile_size;
        g2d.scale(scale, scale);
        gameObjects.forEach(obj -> obj.render(g2d));
        g2d.setTransform(old);
        g2d.translate(mapOffsetX, mapOffsetY);
        if (selectedShopIndex != -1 && hoveredGridX != -1) drawGhostPlant(g2d);
        g2d.setTransform(old);
        g2d.setColor(new Color(255, 255, 255, 100));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRect(mapOffsetX, mapOffsetY, renderedTileSize * Constants.cols, renderedTileSize * Constants.rows);
    }
    private void drawGhostPlant(Graphics2D g2d) {
        ShopItem item = shopItems.get(selectedShopIndex);
        int px = hoveredGridX * renderedTileSize;
        int py = hoveredGridY * renderedTileSize;

        // Handle "Remove" tool ghost (Red box)
        if (item.id == -1) {
            g2d.setColor(new Color(255, 0, 0, 100));
            g2d.fillRect(px, py, renderedTileSize, renderedTileSize);
            g2d.setColor(Color.RED);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRect(px, py, renderedTileSize, renderedTileSize);
            return;
        }

        int ghostSize = (int)(renderedTileSize * 1.5);
        int offset = (ghostSize - renderedTileSize) / 2;
        BufferedImage sprite = SpriteLoader.getSprite(item.col, item.row);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        if (sprite != null) g2d.drawImage(sprite, px - offset, py - offset, ghostSize, ghostSize, null);
        boolean valid = (map[hoveredGridY][hoveredGridX].getType() == Constants.tile_grass);
        g2d.setColor(valid ? Color.GREEN : Color.RED);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRect(px, py, renderedTileSize, renderedTileSize);
    }
    private void drawStats(Graphics2D g) {
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.setColor(Color.WHITE);
        int y = getHeight() - 50;
        g.drawString("Lives: " + GameSession.getLives(), 50, y);
        g.drawString("Gold: " + GameSession.getGold(), 200, y);
        g.drawString("Wave: " + GameSession.getWave(), 350, y);
        g.drawString("Score: " + GameSession.getScore(), 500, y);
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