package plantsdefense.model.plants;

import plantsdefense.model.GameObject;
import plantsdefense.model.enemies.Enemy;
import plantsdefense.util.SpriteLoader;
import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class Projectile extends GameObject {
    protected float speed;
    protected int damage;
    protected Enemy target;
    protected int spriteRow, spriteCol;

    // --- NEW: BIGGER AMMO (x2 size) ---
    protected static final int DRAW_SIZE = 32;

    public Projectile(double x, double y, Enemy target, float speed, int damage, int spriteRow, int spriteCol) {
        super(x, y);
        this.target = target;
        this.speed = speed;
        this.damage = damage;
        this.spriteRow = spriteRow;
        this.spriteCol = spriteCol;
    }

    @Override
    public void update() {
        if (!alive) return;
        move();
        checkCollision();
    }

    protected abstract void move();
    protected abstract void onHit(Enemy e);

    protected void checkCollision() {
        // Simple circle collision
        if (target != null && target.isAlive()) {
            double dist = Math.hypot(target.getX() - x, target.getY() - y);
            if (dist < 32) { // Increased hit radius slightly for bigger ammo
                onHit(target);
                kill();
            }
        } else if (target != null && !target.isAlive()) {
            kill();
        }
    }

    @Override
    public void render(Graphics2D g) {
        BufferedImage sprite = SpriteLoader.getSprite(spriteCol, spriteRow);
        if (sprite != null) {
            // Center the 32x32 ammo
            g.drawImage(sprite, (int)x - 16, (int)y - 16, DRAW_SIZE, DRAW_SIZE, null);
        }
    }
}