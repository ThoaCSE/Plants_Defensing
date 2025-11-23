package plantsdefense.model.enemies;

import plantsdefense.util.SpriteLoader;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.List;

public class Dog extends Enemy {

    public Dog(List<Point> path) {
        // Path, Type, Speed, Health, Gold, Score, Row, Col
        // Speed 2.0 (Fast), Health 60 (Low)
        super(path, EnemyType.Beast, 2.0f, 60, 12, 25, 1, 3);
    }

    @Override
    public void render(Graphics2D g) {
        BufferedImage sprite = SpriteLoader.getSprite(spriteCol, spriteRow);

        if (sprite != null) {
            AffineTransform old = g.getTransform();

            // 1. Move "Pen" to the center of the Dog
            g.translate(x, y);

            // 2. Rotate -90 degrees (Face Right)
            g.rotate(Math.toRadians(-90));

            // 3. Draw Big Sprite (48x48)
            // Offset is half of size: -24
            int size = 48;
            int offset = -size / 2; // -24

            g.drawImage(sprite, offset, offset, size, size, null);

            // 4. Draw Debuffs (Rotated with Dog)
            drawDebuffEffects(g, offset, offset, size);

            // 5. Restore Rotation for Health Bar
            g.setTransform(old);

            // 6. Draw Health Bar (Floating above)
            g.setColor(Color.RED);
            g.fillRect((int)x - 12, (int)y - 30, 24, 4);
            g.setColor(Color.GREEN);
            double hpPercent = health / maxHealth;
            g.fillRect((int)x - 12, (int)y - 30, (int)(24 * hpPercent), 4);
        }
    }
}