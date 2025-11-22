package plantsdefense.model.enemy;

import plantsdefense.gamelogic.GameSession;
import plantsdefense.model.GameObject;
import plantsdefense.util.Constants;
import plantsdefense.util.SpriteLoader;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public abstract class Enemy extends GameObject {
    // Stats defined by subclasses
    protected float speed;
    protected double health;
    protected double maxHealth;
    protected EnemyType type;
    protected int rewardGold;

    // Sprite sheet coordinates
    protected int spriteRow;
    protected int spriteCol;

    // Pathing
    protected List<Point> path;
    protected int pathIndex = 0;

    // Debuff Logic
    protected int slowDuration = 0;

    public Enemy(List<Point> path, EnemyType type, float speed, int health, int rewardGold, int spriteRow, int spriteCol) {
        super(0, 0); // Position will be set in setStartPos()
        this.path = path;
        this.type = type;
        this.speed = speed;
        this.health = health;
        this.maxHealth = health;
        this.rewardGold = rewardGold;
        this.spriteRow = spriteRow;
        this.spriteCol = spriteCol;
        setStartPos();
    }

    private void setStartPos() {
        if (path != null && !path.isEmpty()) {
            Point start = path.get(0);
            // Center the enemy on the tile
            this.x = start.x * Constants.tile_size + Constants.tile_size / 2.0;
            this.y = start.y * Constants.tile_size + Constants.tile_size / 2.0;
        }
    }

    @Override
    public void update() {
        if (!alive) return;

        // 1. Check End of Path
        if (pathIndex >= path.size()) {
            GameSession.loseLife();
            kill();
            return;
        }

        // 2. Move Logic
        moveAlongPath();

        // 3. Handle Status Effects (Debuffs)
        if (slowDuration > 0) slowDuration--;
    }

    protected void moveAlongPath() {
        if (pathIndex >= path.size()) return;

        Point target = path.get(pathIndex);
        double tx = target.x * Constants.tile_size + Constants.tile_size / 2.0;
        double ty = target.y * Constants.tile_size + Constants.tile_size / 2.0;

        double dx = tx - x;
        double dy = ty - y;
        double dist = Math.hypot(dx, dy);

        // Logic: If slowed, speed is reduced by 50%
        float currentSpeed = (slowDuration > 0) ? speed * 0.5f : speed;

        if (dist < currentSpeed) {
            pathIndex++;
        } else {
            x += (dx / dist) * currentSpeed;
            y += (dy / dist) * currentSpeed;
        }
    }

    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            GameSession.addGold(rewardGold);
            kill();
        }
    }

    // Logic to extend debuff duration (Stacking time)
    public void applySlow(int durationFrames) {
        if (this.slowDuration > 0) {
            this.slowDuration += durationFrames;
        } else {
            this.slowDuration = durationFrames;
        }
    }

    public EnemyType getType() { return type; }
    public float getSpeed() { return speed; }

    @Override
    public void render(Graphics2D g) {
        BufferedImage sprite = SpriteLoader.getSprite(spriteCol, spriteRow);
        if (sprite != null) {
            // Render at 32x32 size, centered
            int size = 32;
            g.drawImage(sprite, (int)(x - size/2), (int)(y - size/2), size, size, null);

            // Simple Health Bar
            g.setColor(Color.RED);
            g.fillRect((int)x - 10, (int)y - 20, 20, 4);
            g.setColor(Color.GREEN);
            g.fillRect((int)x - 10, (int)y - 20, (int)(20 * (health / maxHealth)), 4);
        }
    }
}