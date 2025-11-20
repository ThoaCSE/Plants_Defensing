package plantsdefense.model.entities;

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
        int col = type % 10;
        int row = type / 10;
        return SpriteLoader.getSprite(col, row);
        };
}
