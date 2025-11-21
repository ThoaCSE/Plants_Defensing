package plantsdefense.model.entities;

import plantsdefense.gamelogic.GameSession;
import plantsdefense.util.Constants;
import plantsdefense.util.SpriteLoader;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class Enemy extends GameObject {
    private static final int SIZE = 48;
    private static final int SPEED = 1;
    private final List<Point> path;
    private int pathIndex = 0;
    private int health = 100;

    public Enemy(List<Point> path) {
        super(0, 0);
        this.path = path;
        if (!path.isEmpty()) {
            Point start = path.get(0);
            this.x = start.x * Constants.tile_size + Constants.tile_size / 2.0;
            this.y = start.y * Constants.tile_size + Constants.tile_size / 2.0;
        }
    }

    @Override
    public void update() {
        if (!alive || path.isEmpty() || pathIndex >= path.size()) {
            GameSession.loseLife();
            kill();
            return;
        }

        Point target = path.get(pathIndex);
        double tx = target.x * Constants.tile_size + Constants.tile_size / 2.0;
        double ty = target.y * Constants.tile_size + Constants.tile_size / 2.0;

        double dx = tx - x;
        double dy = ty - y;
        double dist = Math.hypot(dx, dy);

        if (dist < SPEED) {
            pathIndex++;
            if (pathIndex >= path.size()) {
                GameSession.loseLife();
                kill();
            }
        } else {
            x += (dx / dist) * SPEED;
            y += (dy / dist) * SPEED;
        }
    }

    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            GameSession.addGold(25);
            kill();
        }
    }

    @Override
    public void render(Graphics2D g) {
        BufferedImage sprite = SpriteLoader.getSprite(0, 1); // Zombie sprite (row 0, col 1)
        if (sprite != null) {
            g.drawImage(sprite, (int)(x - SIZE/2), (int)(y - SIZE/2), SIZE, SIZE, null);
        }

        // Health bar
        g.setColor(Color.BLACK);
        g.fillRect((int)x - 21, (int)y - 35, 42, 7);
        g.setColor(Color.RED);
        g.fillRect((int)x - 20, (int)y - 34, 40, 5);
        g.setColor(Color.GREEN);
        g.fillRect((int)x - 20, (int)y - 34, (int)(40.0 * health / 100), 5);
    }
}