package plantsdefense.gui.play;

import plantsdefense.gamelogic.GameSession;
import plantsdefense.gamelogic.WaveManager;
import plantsdefense.gui.ScreenController;
import plantsdefense.model.entities.GameObject;
import plantsdefense.model.entities.Tile;
import plantsdefense.util.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.AffineTransform;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class PlayPanel extends JPanel {
    // --- Game State ---
    private final Tile[][] map;
    private final ScreenController controller;

    // Thread-safe list to prevent ConcurrentModificationException
    private final List<GameObject> gameObjects = new CopyOnWriteArrayList<>();

    private final WaveManager waveManager;
    private volatile boolean gameRunning = true; // 'volatile' for thread safety
    private boolean isGameOver = false;

    // --- Rendering Constants ---
    private static final int MAP_RENDER_SIZE = 960;
    private static final Color UI_BG_COLOR = new Color(20, 20, 30, 180);
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final Color GOLD_COLOR = new Color(255, 215, 0);
    private static final Color HEART_COLOR = new Color(255, 80, 80);

    // --- Game Over UI Constants ---
    private static final int BTN_W = 250;
    private static final int BTN_H = 60;
    private int retryX, menuX, btnY;
    private int hoverX = -1, hoverY = -1;

    public PlayPanel(ScreenController controller) {
        this.controller = controller;

        setPreferredSize(new Dimension(1280, 1000));
        setBackground(new Color(20, 25, 40));

        this.map = GameSession.getCurrentMap();
        this.waveManager = new WaveManager(gameObjects, map);

        // --- MOUSE INPUT ---
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (isGameOver) {
                    handleGameOverClick(e.getX(), e.getY());
                }
                // Removed empty 'else' to fix warning
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (isGameOver) {
                    hoverX = e.getX();
                    hoverY = e.getY();
                    repaint();
                }
            }
        });

        // --- GAME LOOP THREAD ---
        new Thread(this::gameLoop).start();

        // Wave Timer (Fixed unused 'e' warning)
        new Timer(3000, ignored -> {
            if (gameRunning && !isGameOver) {
                waveManager.startNextWave();
            }
        }).start();
    }

    // Clean Game Loop Method
    private void gameLoop() {
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
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

            // Small sleep to prevent 100% CPU usage (Busy-Waiting Fix)
            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restore interrupted status
            }
        }
    }

    public void stopGame() {
        gameRunning = false;
    }

    private void updateGame() {
        if (isGameOver || !gameRunning) return;

        waveManager.update();

        gameObjects.removeIf(obj -> !obj.isAlive());
        gameObjects.forEach(GameObject::update);

        if (GameSession.getLives() <= 0) {
            isGameOver = true;
        }
    }

    private void handleGameOverClick(int x, int y) {
        if (isInside(x, y, retryX, btnY, BTN_W, BTN_H)) {
            String pName = GameSession.getPlayerName();
            Tile[][] pMap = controller.getCurrentMapFromEditor();

            // NOTE: If you get "Expected 3 arguments", check if startNewGame needs extra info (like 'lives')
            GameSession.startNewGame(pName, pMap);

            controller.showPlay();
        }
        else if (isInside(x, y, menuX, btnY, BTN_W, BTN_H)) {
            controller.showMenu();
        }
    }

    // --- RENDERING ---
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        drawGameWorld(g2d);

        // Now this call works because we fixed the method definition below
        drawHUD(g2d);

        if (isGameOver) {
            drawGameOverOverlay(g2d);
        }

        g2d.dispose();
    }

    // --- FIX: Update this method to take only 1 argument ---
    private void drawHUD(Graphics2D g) {
        // We pass getWidth() and getHeight() automatically here
        drawModernHud(g, getWidth(), getHeight());
    }

    private void drawGameWorld(Graphics2D g2d) {
        int panelW = getWidth();
        int panelH = getHeight();

        int tileSize = MAP_RENDER_SIZE / Constants.cols;
        int totalGridW = tileSize * Constants.cols;
        int totalGridH = tileSize * Constants.rows;
        int mapOffsetX = (panelW - totalGridW) / 2 - (totalGridW / 9);
        int mapOffsetY = (panelH - totalGridH) / 2;

        // Draw Map
        for (int r = 0; r < Constants.rows; r++) {
            for (int c = 0; c < Constants.cols; c++) {
                Tile t = map[r][c];
                if (t != null) {
                    g2d.drawImage(t.getSprite(),
                            mapOffsetX + c * tileSize,
                            mapOffsetY + r * tileSize,
                            tileSize, tileSize, null);
                }
            }
        }

        // Draw Objects
        AffineTransform old = g2d.getTransform();
        g2d.translate(mapOffsetX, mapOffsetY);
        double scale = (double) tileSize / Constants.tile_size;
        g2d.scale(scale, scale);

        gameObjects.forEach(obj -> obj.render(g2d));

        g2d.setTransform(old);

        // Border
        g2d.setColor(new Color(255, 255, 255, 222));
        g2d.setStroke(new BasicStroke(4));
        g2d.drawRect(mapOffsetX, mapOffsetY, totalGridW, totalGridH);
    }

    private void drawGameOverOverlay(Graphics2D g2d) {
        g2d.setColor(new Color(0, 0, 0, 200));
        g2d.fillRect(0, 0, getWidth(), getHeight());

        int centerX = getWidth() / 2;
        this.btnY = getHeight() / 2;
        this.retryX = centerX - BTN_W - 20;
        this.menuX = centerX + 20;

        String title = "YOU DIED";
        g2d.setFont(new Font("Chiller", Font.BOLD, 100));
        g2d.setColor(Color.RED);
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(title, centerX - fm.stringWidth(title) / 2, btnY - 50);

        drawButton(g2d, "RETRY", retryX, btnY, new Color(0, 180, 0));
        drawButton(g2d, "MENU", menuX, btnY, new Color(60, 60, 200));
    }

    private void drawButton(Graphics2D g2d, String text, int x, int y, Color color) {
        boolean hovered = isInside(hoverX, hoverY, x, y, BTN_W, BTN_H);
        if (hovered) color = color.brighter();

        g2d.setColor(color);
        g2d.fillRoundRect(x, y, BTN_W, BTN_H, 25, 25);

        if (hovered) {
            g2d.setColor(Color.YELLOW);
            g2d.setStroke(new BasicStroke(3));
            g2d.drawRoundRect(x, y, BTN_W, BTN_H, 25, 25);
        } else {
            g2d.setColor(new Color(255, 255, 255, 50));
            g2d.setStroke(new BasicStroke(1));
            g2d.drawRoundRect(x, y, BTN_W, BTN_H, 25, 25);
        }

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 30));
        FontMetrics fm = g2d.getFontMetrics();
        int tx = x + (BTN_W - fm.stringWidth(text)) / 2;
        int ty = y + (BTN_H + fm.getAscent() - fm.getDescent()) / 2;
        g2d.drawString(text, tx, ty);
    }

    private void drawHUD(Graphics2D g, int w, int h) {
        drawModernHud(g, w, h);
    }

    private void drawModernHud(Graphics2D g, int w, int h) {
        g.setFont(new Font("Segoe UI", Font.BOLD, 20));
        FontMetrics fm = g.getFontMetrics();
        int padding = 20;
        int boxHeight = 45;

        String nameText = GameSession.getPlayerName();
        int nameBoxW = fm.stringWidth(nameText) + 50;
        drawHudBox(g, padding, padding, nameBoxW, boxHeight);
        g.setColor(Color.LIGHT_GRAY);
        g.fillOval(padding + 10, padding + 12, 20, 20);
        g.setColor(TEXT_COLOR);
        g.drawString(nameText, padding + 40, padding + 30);

        String waveText = "WAVE " + GameSession.getWave();
        int waveBoxW = fm.stringWidth(waveText) + 40;
        int waveX = w - waveBoxW - padding;
        drawHudBox(g, waveX, padding, waveBoxW, boxHeight);
        g.setColor(Color.CYAN);
        g.drawString(waveText, waveX + 20, padding + 30);

        String livesStr = String.valueOf(GameSession.getLives());
        String goldStr = String.valueOf(GameSession.getGold());
        int iconSize = 20;
        int gap = 20;
        int livesW = fm.stringWidth(livesStr);
        int goldW = fm.stringWidth(goldStr);
        int bottomBoxW = 20 + iconSize + 5 + livesW + gap + iconSize + 5 + goldW + 20;
        int bottomBoxH = 50;
        int bottomY = h - bottomBoxH - padding;

        drawHudBox(g, padding, bottomY, bottomBoxW, bottomBoxH);

        int x = padding + 20;
        int iconY = bottomY + 15;
        int textY = bottomY + 32;

        g.setColor(HEART_COLOR);
        g.fillOval(x, iconY, iconSize, iconSize);
        g.setColor(TEXT_COLOR);
        g.drawString(livesStr, x + iconSize + 5, textY);

        x += iconSize + 5 + livesW + gap;
        g.setColor(GOLD_COLOR);
        g.fillOval(x, iconY, iconSize, iconSize);
        g.setColor(TEXT_COLOR);
        g.drawString(goldStr, x + iconSize + 5, textY);
    }

    private void drawHudBox(Graphics2D g, int x, int y, int w, int h) {
        g.setColor(UI_BG_COLOR);
        g.fillRoundRect(x, y, w, h, 15, 15);
        g.setColor(new Color(255, 255, 255, 30));
        g.drawRoundRect(x, y, w, h, 15, 15);
    }

    private boolean isInside(int mx, int my, int x, int y, int w, int h) {
        return mx >= x && mx <= x + w && my >= y && my <= y + h;
    }
}