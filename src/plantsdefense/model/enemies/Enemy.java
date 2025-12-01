package plantsdefense.model.enemies;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import plantsdefense.gamelogic.GameSession;
import plantsdefense.model.GameObject;
import plantsdefense.util.Constants;
import plantsdefense.util.SpriteLoader;

public abstract class Enemy extends GameObject {
    protected float speed;
    protected double health;
    protected double maxHealth;
    protected EnemyType type;
    protected int rewardGold;
    protected int scoreValue;

    protected int spriteRow, spriteCol;
    protected List<Point> path;
    protected int pathIndex = 0;

    protected int slowDuration = 0;
    protected int burnDuration = 0;

    protected static final int DRAW_SIZE = 48;

    public Enemy(List<Point> path, EnemyType type, float speed, int health, int rewardGold, int scoreValue, int spriteRow, int spriteCol) {
        super(0, 0);
        this.path = path;
        this.type = type;
        this.speed = speed;
        this.health = health;
        this.maxHealth = health;
        this.rewardGold = rewardGold;
        this.scoreValue = scoreValue;
        this.spriteRow = spriteRow;
        this.spriteCol = spriteCol;
        setStartPos();
    }

    private void setStartPos() {
        if (path != null && !path.isEmpty()) {
            Point start = path.get(0);
            this.x = start.x * Constants.tile_size + Constants.tile_size / 2.0;
            this.y = start.y * Constants.tile_size + Constants.tile_size / 2.0;
        }
    }

    @Override
    public void update() {
        if (!alive) return;
        if (pathIndex >= path.size()) {
            GameSession.loseLife();
            kill();
            return;
        }
        moveAlongPath();
        if (slowDuration > 0) slowDuration--;
        if (burnDuration > 0) burnDuration--;
    }

    protected void moveAlongPath() {
        if (pathIndex >= path.size()) return;
        Point target = path.get(pathIndex);
        double tx = target.x * Constants.tile_size + Constants.tile_size / 2.0;
        double ty = target.y * Constants.tile_size + Constants.tile_size / 2.0;
        double dx = tx - x;
        double dy = ty - y;
        double dist = Math.hypot(dx, dy);
        float currentSpeed = (slowDuration > 0) ? speed * 0.5f : speed;

        if (dist < currentSpeed) {
            pathIndex++;
        } else {
            x += (dx / dist) * currentSpeed;
            y += (dy / dist) * currentSpeed;
        }
    }

    public void knockBack(int tiles) {
        this.pathIndex -= tiles;
        if (this.pathIndex < 0) this.pathIndex = 0;
        if (pathIndex < path.size()) {
            Point target = path.get(pathIndex);
            this.x = target.x * Constants.tile_size + Constants.tile_size / 2.0;
            this.y = target.y * Constants.tile_size + Constants.tile_size / 2.0;
        }
    }

    public boolean takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            GameSession.addGold(rewardGold);
            GameSession.addScore(scoreValue);
            kill();
            return true;
        }
        return false;
    }

    public void applySlow(int durationFrames) {
        if (this.slowDuration > 0) this.slowDuration += durationFrames;
        else this.slowDuration = durationFrames;
    }

    public void applyBurn(int durationFrames) {
        if (this.burnDuration > 0) this.burnDuration += durationFrames;
        else this.burnDuration = durationFrames;
    }

    public EnemyType getType() { return type; }
    public float getSpeed() { return speed; }

    @Override
    public void render(Graphics2D g) {
        BufferedImage sprite = SpriteLoader.getSprite(spriteCol, spriteRow);
        if (sprite != null) {
            int drawX = (int)(x - DRAW_SIZE / 2);
            int drawY = (int)(y - DRAW_SIZE / 2);
            g.drawImage(sprite, drawX, drawY, DRAW_SIZE, DRAW_SIZE, null);
            drawDebuffEffects(g, drawX, drawY, DRAW_SIZE);
            g.setColor(Color.RED);
            g.fillRect((int)x - 10, (int)y - 28, 20, 4);
            g.setColor(Color.GREEN);
            g.fillRect((int)x - 10, (int)y - 28, (int)(20 * (health / maxHealth)), 4);
        }
    }

    protected void drawDebuffEffects(Graphics2D g, int dx, int dy, int size) {
        if (slowDuration > 0) {
            g.setColor(new Color(0, 200, 255, 100));
            g.fillRect(dx, dy, size, size);
        }
        if (burnDuration > 0) {
            g.setColor(new Color(255, 60, 0, 100));
            g.fillRect(dx, dy, size, size);
        }
    }
}
