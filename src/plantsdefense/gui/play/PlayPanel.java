package plantsdefense.gui.play;

import plantsdefense.gamelogic.GameSession;
import plantsdefense.gamelogic.WaveManager;
import plantsdefense.model.entities.GameObject;
import plantsdefense.model.entities.Tile;
import plantsdefense.util.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

public class PlayPanel extends JPanel {
    private final Tile[][] map;
    private final List<GameObject> gameObjects = new ArrayList<>();
    private WaveManager waveManager;
    private long lastUpdateTime = System.nanoTime();
    private boolean gameRunning = true;

    private static final int MAP_RENDER_SIZE = 960;
    private static final Color UI_BG_COLOR = new Color(20, 20, 30, 180);
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final Color GOLD_COLOR = new Color(255, 215, 0);
    private static final Color HEART_COLOR = new Color(255, 80, 80);

    public PlayPanel() {
        setPreferredSize(new Dimension(1280, 1000));
        setBackground(new Color(20, 25, 40));
        this.map = GameSession.getCurrentMap();
        this.waveManager = new WaveManager(gameObjects, map);

        new Thread(() -> {
            while (gameRunning) {
                long now = System.nanoTime();
                if (now - lastUpdateTime >= 16_666_666L) {
                    updateGame();
                    repaint();
                    lastUpdateTime = now;
                }
                try { Thread.sleep(4); } catch (Exception ignored) {}
            }
        }).start();

        new Timer(3000, e -> waveManager.startNextWave()).start();
    }

    private void updateGame() {
        if (!gameRunning) return;

        waveManager.update();
        gameObjects.removeIf(obj -> !obj.isAlive());
        gameObjects.forEach(GameObject::update);

        // Check Game Over
        if (GameSession.getLives() <= 0 && gameRunning) {
            gameRunning = false;
            showGameOver();
        }
    }

    private void showGameOver() {
        GameOverPanel gameOver = new GameOverPanel(
                ((JFrame) SwingUtilities.getWindowAncestor(this)).getRootPane().getParent().getComponent(0) instanceof plantsdefense.gui.GameFrame frame
                        ? frame.getScreenController()
                        : null
        );
        JLayeredPane layeredPane = getRootPane().getLayeredPane();
        gameOver.setBounds(0, 0, getWidth(), getHeight());
        layeredPane.add(gameOver, JLayeredPane.POPUP_LAYER);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int panelW = getWidth();
        int panelH = getHeight();

        int tileSize = MAP_RENDER_SIZE / Constants.cols;
        int totalGridW = tileSize * Constants.cols;
        int totalGridH = tileSize * Constants.rows;
        int mapOffsetX = (panelW - totalGridW) / 2 - (totalGridW / 9);
        int mapOffsetY = (panelH - totalGridH) / 2;

        // Draw map
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

        // Draw game objects (with proper scaling)
        AffineTransform old = g2d.getTransform();
        g2d.translate(mapOffsetX, mapOffsetY);
        double scale = (double) tileSize / Constants.tile_size;
        g2d.scale(scale, scale);
        gameObjects.forEach(obj -> obj.render(g2d));
        g2d.setTransform(old);

        // Map border
        g2d.setColor(new Color(255, 255, 255, 222));
        g2d.setStroke(new BasicStroke(4));
        g2d.drawRect(mapOffsetX, mapOffsetY, totalGridW, totalGridH);

        // HUD
        drawModernHud(g2d, panelW, panelH);

        g2d.dispose();
    }

    // drawModernHud(), drawHudBox() – unchanged (perfect as-is)
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
}