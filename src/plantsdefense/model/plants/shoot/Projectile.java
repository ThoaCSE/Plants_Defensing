package plantsdefense.model.plants.shoot;

import plantsdefense.model.GameObject;
import plantsdefense.model.enemies.Enemy; // IMPORT NEW ENEMY PATH
import plantsdefense.util.SpriteLoader;
import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class Projectile extends GameObject {
    protected float speed;
    protected int damage;
    protected Enemy target;
    protected int spriteRow, spriteCol;

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
        if (target != null && target.isAlive()) {
            double dist = Math.hypot(target.getX() - x, target.getY() - y);
            if (dist < 20) {
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
        if (sprite != null) g.drawImage(sprite, (int)x - 8, (int)y - 8, 16, 16, null);
    }
}