package plantsdefense.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class SpriteLoader {
    private static BufferedImage atlas;

    static {
        try {
            atlas = ImageIO.read(SpriteLoader.class.getResourceAsStream("/spriteatlas.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static BufferedImage getSprite(int col, int row) {
        if (atlas == null) return null;
        int x = col * Constants.sprite_size;
        int y = row * Constants.sprite_size;
        return atlas.getSubimage(x, y, Constants.sprite_size, Constants.sprite_size);
    }
}