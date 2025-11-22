package plantsdefense.model.plants;

import plantsdefense.model.GameObject;
import plantsdefense.model.enemies.Enemy; // IMPORT NEW ENEMY PATH
import plantsdefense.util.Constants;
import plantsdefense.util.SpriteLoader;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public abstract class Plant extends GameObject {
    protected int range, damage, cooldown, cooldownTimer = 0;
    protected int spriteRow, spriteCol;
    protected List<GameObject> objects;

    public Plant(int gridX, int gridY, int range, int damage, int cooldown, int spriteRow, int spriteCol, List<GameObject> objects) {
        super(gridX * Constants.tile_size + 16, gridY * Constants.tile_size + 16);
        this.range = range;
        this.damage = damage;
        this.cooldown = cooldown;
        this.spriteRow = spriteRow;
        this.spriteCol = spriteCol;
        this.objects = objects;
    }

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
        if (sprite != null) g.drawImage(sprite, (int)x - 16, (int)y - 16, 32, 32, null);
    }
}