package plantsdefense.gui.play;

import plantsdefense.gamelogic.GameSession;
import plantsdefense.gui.ScreenController;
import plantsdefense.util.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class GameOverPanel extends JPanel {
    private final ScreenController controller;

    // --- UI CONFIGURATION ---
    private static final int BTN_W = 250;
    private static final int BTN_H = 60;

    // Mouse State for Hover Effects
    private int hoverX = -1;
    private int hoverY = -1;

    public GameOverPanel(ScreenController controller) {
        this.controller = controller;

        // Ensure it covers the whole screen if added directly
        setPreferredSize(new Dimension(Constants.window_width, Constants.window_height));
        setBackground(new Color(0, 0, 0, 220)); // Semi-transparent background

        // --- MOUSE LISTENERS ---
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleClick(e.getX(), e.getY());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hoverX = -1;
                hoverY = -1;
                repaint();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                hoverX = e.getX();
                hoverY = e.getY();
                repaint();
            }
        });
    }

    private void handleClick(int mx, int my) {
        int w = getWidth();
        int h = getHeight();
        int centerY = h / 2 + 50; // Buttons slightly below center

        // Calculate positions (Same as in paintComponent)
        int gap = 40;
        int totalW = (BTN_W * 2) + gap;
        int startX = (w - totalW) / 2;

        int retryX = startX;
        int menuX = startX + BTN_W + gap;
        int btnY = centerY;

        // --- CHECK RETRY BUTTON ---
        if (mx >= retryX && mx <= retryX + BTN_W && my >= btnY && my <= btnY + BTN_H) {
            // Restart Game Logic
            GameSession.startNewGame(GameSession.getPlayerName(), GameSession.getCurrentMap());
            controller.showPlay();
            return;
        }

        // --- CHECK MENU BUTTON ---
        if (mx >= menuX && mx <= menuX + BTN_W && my >= btnY && my <= btnY + BTN_H) {
            controller.showMenu();
            return;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();

        // Enable Antialiasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // --- 1. BACKGROUND ---
        g2d.setColor(getBackground());
        g2d.fillRect(0, 0, w, h);

        // --- 2. TITLE "YOU DIED" ---
        g2d.setFont(new Font("Arial", Font.BOLD, 100));
        String title = "YOU DIED";
        FontMetrics fm = g2d.getFontMetrics();
        int titleW = fm.stringWidth(title);
        int titleY = h / 2 - 50; // Slightly above center

        // Title Shadow
        g2d.setColor(Color.BLACK);
        g2d.drawString(title, (w - titleW) / 2 + 5, titleY + 5);

        // Title Main Color
        g2d.setColor(Color.RED);
        g2d.drawString(title, (w - titleW) / 2, titleY);


        // --- 3. BUTTONS ---
        int centerY = h / 2 + 50;
        int gap = 40;
        int totalBtnW = (BTN_W * 2) + gap;
        int startX = (w - totalBtnW) / 2;

        int retryX = startX;
        int menuX = startX + BTN_W + gap;

        // Draw RETRY Button (Green)
        drawButton(g2d, "RETRY", retryX, centerY, new Color(0, 180, 0));

        // Draw MENU Button (Blueish)
        drawButton(g2d, "MENU", menuX, centerY, new Color(60, 60, 200));

        g2d.dispose();
    }

    // --- HELPER: Draw Button (Matches EditorPanel style) ---
    private void drawButton(Graphics2D g2d, String text, int x, int y, Color color) {
        boolean hovered = isHovering(x, y, BTN_W, BTN_H);

        if (hovered) {
            color = color.brighter();
        }

        // Button Body
        g2d.setColor(color);
        g2d.fillRoundRect(x, y, BTN_W, BTN_H, 25, 25);

        // Hover Glow Border
        if (hovered) {
            g2d.setColor(Color.YELLOW);
            g2d.setStroke(new BasicStroke(3));
            g2d.drawRoundRect(x, y, BTN_W, BTN_H, 25, 25);
        } else {
            // Normal subtle border
            g2d.setColor(new Color(255, 255, 255, 50));
            g2d.setStroke(new BasicStroke(1));
            g2d.drawRoundRect(x, y, BTN_W, BTN_H, 25, 25);
        }

        // Text
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        FontMetrics fm = g2d.getFontMetrics();
        int tx = x + (BTN_W - fm.stringWidth(text)) / 2;

        // Center text vertically roughly
        g2d.drawString(text, tx, y + 38);
    }

    private boolean isHovering(int x, int y, int w, int h) {
        return hoverX >= x && hoverX <= x + w && hoverY >= y && hoverY <= y + h;
    }
}