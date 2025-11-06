package plantsdefense.model;

import plantsdefense.util.Constants;
import plantsdefense.util.SpriteLoader;

import java.awt.image.BufferedImage;

public class Tile {
    private final int x,y;
    private final int type;

    public Tile(int gridX, int gridY, int type){
        this.x = gridX * Constants.tile_size;
        this.y = gridY * Constants.tile_size;
        this.type = type;
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }

    public int getType(){
        return type;
    }

    public BufferedImage getSprite(){
        return switch (type){
            case Constants.tile_grass -> SpriteLoader.getSprite(9, 0);
            case Constants.tile_path -> SpriteLoader.getSprite(8, 0);
            case Constants.tile_begin -> SpriteLoader.getSprite(7, 2);
            case Constants.tile_end -> SpriteLoader.getSprite(8, 2);
            default -> SpriteLoader.getSprite(0,0);
        };
    }
}
