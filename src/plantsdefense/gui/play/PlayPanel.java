package plantsdefense.gui.play;

import plantsdefense.gamelogic.GameSession;
import plantsdefense.model.entities.Tile;
import plantsdefense.util.Constants;

import javax.swing.*;
import java.awt.*;

public class PlayPanel extends JPanel {
    private final Tile[][] map;

    // --- CONFIGURATION ---
    // Explicitly requested size for the map grid
    private static final int MAP_RENDER_SIZE = 960;

    // UI Colors
    private static final Color UI_BG_COLOR = new Color(20, 20, 30, 180); // Transparent dark box
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final Color GOLD_COLOR = new Color(255, 215, 0); // Gold
    private static final Color HEART_COLOR = new Color(255, 80, 80); // Red

    public PlayPanel() {
        // NOTE: A 960px tall map won't fit in a 720px window.
        // We set the preferred height to 1000 to accommodate the 960 map + padding.
        setPreferredSize(new Dimension(1280, 1000));
        setBackground(new Color(20, 25, 40));
        this.map = GameSession.getCurrentMap();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();

        // Enable Antialiasing for smooth circles and text
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int panelW = getWidth();
        int panelH = getHeight();

        // --- 1. CALCULATE TILE SIZE FOR 960x960 ---
        // We base the tile size on the fixed 960 dimension.
        // If your grid is 30x30, tileSize will be 960 / 30 = 32.
        int tileSize = MAP_RENDER_SIZE / Constants.cols;

        int totalGridW = tileSize * Constants.cols;
        int totalGridH = tileSize * Constants.rows;

        // Calculate offsets to CENTER the map in the window
        int mapOffsetX = (panelW - totalGridW) / 2;
        int mapOffsetY = (panelH - totalGridH) / 2;

        // --- 2. DRAW MAP ---
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

        // --- DRAW MAP BORDER ---
        // 4px thick semi-transparent white border to highlight the map area
        g2d.setColor(new Color(255, 255, 255, 100));
        g2d.setStroke(new BasicStroke(4));
        g2d.drawRect(mapOffsetX, mapOffsetY, totalGridW, totalGridH);

        // --- 3. DRAW HUD ---
        drawModernHud(g2d, panelW, panelH);

        // --- 4. DRAW GAME OVER ---
        if (GameSession.isGameOver()) {
            drawGameOverScreen(g2d, panelW, panelH);
        }

        g2d.dispose();
    }

    private void drawModernHud(Graphics2D g, int w, int h) {
        g.setFont(new Font("Segoe UI", Font.BOLD, 20));
        FontMetrics fm = g.getFontMetrics();
        int padding = 20;
        int boxHeight = 45;

        // --- TOP LEFT: PLAYER NAME ---
        String nameText = GameSession.getPlayerName();
        int nameBoxW = fm.stringWidth(nameText) + 50;

        drawHudBox(g, padding, padding, nameBoxW, boxHeight);

        // User Icon
        g.setColor(Color.LIGHT_GRAY);
        g.fillOval(padding + 10, padding + 12, 20, 20);
        // Name
        g.setColor(TEXT_COLOR);
        g.drawString(nameText, padding + 40, padding + 30);


        // --- TOP RIGHT: WAVE COUNT ---
        String waveText = "WAVE " + GameSession.getWave();
        int waveBoxW = fm.stringWidth(waveText) + 40;
        int waveX = w - waveBoxW - padding;

        drawHudBox(g, waveX, padding, waveBoxW, boxHeight);

        g.setColor(Color.CYAN);
        g.drawString(waveText, waveX + 20, padding + 30);


        // --- BOTTOM LEFT: HEARTS & GOLD ---
        // Grouped together in one box
        String livesStr = String.valueOf(GameSession.getLives());
        String goldStr = String.valueOf(GameSession.getGold());

        int iconSize = 20;
        int gapBetweenStats = 20;

        int livesTextW = fm.stringWidth(livesStr);
        int goldTextW = fm.stringWidth(goldStr);

        // Calculate total width: Padding + Icon + Text + Gap + Icon + Text + Padding
        int bottomBoxW = 20 + iconSize + 5 + livesTextW + gapBetweenStats + iconSize + 5 + goldTextW + 20;
        int bottomBoxH = 50;
        int bottomY = h - bottomBoxH - padding;

        drawHudBox(g, padding, bottomY, bottomBoxW, bottomBoxH);

        int currentX = padding + 20;
        int iconY = bottomY + 15;
        int textY = bottomY + 32;

        // Draw Heart
        g.setColor(HEART_COLOR);
        g.fillOval(currentX, iconY, iconSize, iconSize);
        g.setColor(TEXT_COLOR);
        g.drawString(livesStr, currentX + iconSize + 5, textY);

        // Move X to start of Gold section
        currentX += iconSize + 5 + livesTextW + gapBetweenStats;

        // Draw Gold
        g.setColor(GOLD_COLOR);
        g.fillOval(currentX, iconY, iconSize, iconSize);
        g.setColor(TEXT_COLOR);
        g.drawString(goldStr, currentX + iconSize + 5, textY);
    }

    // Helper to draw the semi-transparent background boxes
    private void drawHudBox(Graphics2D g, int x, int y, int w, int h) {
        g.setColor(UI_BG_COLOR);
        g.fillRoundRect(x, y, w, h, 15, 15);

        // Subtle white border
        g.setColor(new Color(255, 255, 255, 30));
        g.drawRoundRect(x, y, w, h, 15, 15);
    }

    private void drawGameOverScreen(Graphics2D g, int w, int h) {
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(0, 0, w, h);

        g.setFont(new Font("Arial", Font.BOLD, 80));
        String msg = "YOU DIED";
        FontMetrics fm = g.getFontMetrics();
        int textW = fm.stringWidth(msg);

        g.setColor(Color.RED);
        g.drawString(msg, (w - textW) / 2, h / 2);
    }
}