package plantsdefense.model.enemies;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.List;
import plantsdefense.util.SpriteLoader;

public class Dog extends Enemy {

    public Dog(List<Point> path) {
        super(path, EnemyType.Beast, 2.0f, 60, 12, 25, 1, 3);
    }

    @Override
    public void render(Graphics2D g) {
        BufferedImage sprite = SpriteLoader.getSprite(spriteCol, spriteRow);

        if (sprite != null) {
            AffineTransform old = g.getTransform();

            g.translate(x, y);
            g.rotate(Math.toRadians(-90));

            int size = 48;
            int offset = -size / 2;

            g.drawImage(sprite, offset, offset, size, size, null);

            drawDebuffEffects(g, offset, offset, size);

            g.setTransform(old);

            g.setColor(Color.RED);
            g.fillRect((int)x - 12, (int)y - 30, 24, 4);
            g.setColor(Color.GREEN);
            double hpPercent = health / maxHealth;
            g.fillRect((int)x - 12, (int)y - 30, (int)(24 * hpPercent), 4);
        }
    }
}
