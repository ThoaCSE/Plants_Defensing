package plantsdefense.model;

import plantsdefense.util.Constants;
import plantsdefense.util.SpriteLoader;

import java.awt.image.BufferedImage;

public class Tile {
    private final int gridX, gridY;
    private int type;

    public Tile(int gridX, int gridY, int type){
        this.gridX = gridX;
        this.gridY = gridY;
        this.type = type;
    }

    public int getX() { return gridX * Constants.tile_size; }
    public int getY() { return gridY * Constants.tile_size; }

    public int getGridX() { return gridX; }
    public int getGridY() { return gridY; }
    public int getType() { return type; }

    public void setType(int newType) {
        this.type = newType;
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
