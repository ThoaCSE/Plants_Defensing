package plantsdefense.model.plants;

import plantsdefense.model.GameObject;
import plantsdefense.model.enemies.Enemy;
import plantsdefense.util.Constants;
import plantsdefense.util.SpriteLoader;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public abstract class Plant extends GameObject {
    protected int range;
    protected int damage;
    protected int cooldown;
    protected int cooldownTimer = 0;
    protected int spriteRow;
    protected int spriteCol;
    protected List<GameObject> objects;

    // --- NEW: COST FIELD ---
    protected int cost;

    // --- DRAW SIZE ---
    protected static final int DRAW_SIZE = 48;

    // --- UPDATED CONSTRUCTOR (Now accepts 'cost') ---
    public Plant(int gridX, int gridY, int range, int damage, int cooldown, int cost, int spriteRow, int spriteCol, List<GameObject> objects) {
        super(gridX * Constants.tile_size + 16, gridY * Constants.tile_size + 16);
        this.range = range;
        this.damage = damage;
        this.cooldown = cooldown;
        this.cost = cost; // Save the cost here
        this.spriteRow = spriteRow;
        this.spriteCol = spriteCol;
        this.objects = objects;
    }

    public int getCost() { return cost; } // Getter for selling/refunds

    @Override
    public void update() {
        if (cooldownTimer > 0) cooldownTimer--;
        if (cooldownTimer <= 0) {
            Enemy target = findTarget();
            if (target != null) {
                shoot(target);
                cooldownTimer = cooldown;
            }
        }
    }

    protected abstract Enemy findTarget();
    protected abstract void shoot(Enemy target);

    protected boolean isInRange(Enemy e) {
        double dist = Math.hypot(e.getX() - x, e.getY() - y);
        return dist <= range;
    }

    @Override
    public void render(Graphics2D g) {
        BufferedImage sprite = SpriteLoader.getSprite(spriteCol, spriteRow);
        if (sprite != null) {
            int offset = DRAW_SIZE / 2;
            g.drawImage(sprite, (int)x - offset, (int)y - offset, DRAW_SIZE, DRAW_SIZE, null);
        }
    }
}