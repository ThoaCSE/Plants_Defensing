package plantsdefense.util;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public final class SpriteLoader {
    private static final BufferedImage atlas;

    static {
        try{
            atlas = ImageIO.read(
                    Objects.requireNonNull(
                            SpriteLoader.class.getResourceAsStream("/spriteatlas.png") // redraw later.
                    )
            );
        } catch (IOException e) {
            throw new RuntimeException("Error at loading spirteatlas png", e); // change later
        }
    }

    private SpriteLoader(){}

    public static BufferedImage getSprite(int col, int row){
        if (col < 0 || col >= Constants.atlas_cols || row < 0 || row >=Constants.atlas_rows){
            throw new IllegalArgumentException("Can't adjust sprite position : col = " +col + ",  row = " + row);
        }
        return atlas.getSubimage(col*Constants.sprite_size, row*Constants.sprite_size, Constants.sprite_size, Constants.sprite_size);
    }
}
