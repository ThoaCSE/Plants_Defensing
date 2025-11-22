package plantsdefense.model.enemies;

import plantsdefense.util.SpriteLoader;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.List;

public class Dog extends Enemy {

    public Dog(List<Point> path) {
        // Path, Type, Speed, Health, Gold, Row, Col
        super(path, EnemyType.Beast, 2.0f, 60, 12, 1, 3);
    }

    @Override
    public void render(Graphics2D g) {
        BufferedImage sprite = SpriteLoader.getSprite(spriteCol, spriteRow);

        if (sprite != null) {
            // 1. Save the current "camera" state
            AffineTransform oldTransform = g.getTransform();

            // 2. Move "pivot" to the center of the enemy (x, y)
            g.translate(x, y);

            // 3. Rotate -90 degrees (Math.toRadians(-90))
            // This turns a "Down-facing" sprite to "Right-facing"
            g.rotate(Math.toRadians(-90));

            // 4. Draw the sprite centered at (0, 0) relative to the pivot
            // Since we translated to (x,y), drawing at (-16, -16) centers the 32x32 sprite
            g.drawImage(sprite, -16, -16, 32, 32, null);

            // 5. Restore the camera state (So the health bar doesn't rotate!)
            g.setTransform(oldTransform);

            // 6. Draw Health Bar (Standard, non-rotated)
            g.setColor(Color.RED);
            g.fillRect((int)x - 10, (int)y - 20, 20, 4);

            g.setColor(Color.GREEN);
            double hpRatio = health / maxHealth;
            g.fillRect((int)x - 10, (int)y - 20, (int)(20 * hpRatio), 4);
        }
    }
}