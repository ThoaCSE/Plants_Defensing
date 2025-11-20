package plantsdefense.model.entities;

import plantsdefense.util.Constants;
import plantsdefense.util.SpriteLoader;
import java.awt.image.BufferedImage;

public class Tile {
    private final int grid_x;
    private final int grid_y;
    private int type;

    public Tile(int grid_x, int grid_y, int type) {
        this.grid_x = grid_x;
        this.grid_y = grid_y;
        this.type = type;
    }

    public int getX() { return grid_x * Constants.tile_size; }
    public int getY() { return grid_y * Constants.tile_size; }
    public int getType() { return type; }
    public void setType(int type) { this.type = type; }

    public BufferedImage getSprite() {
        int col = type % Constants.atlas_cols;
        int row = type / Constants.atlas_cols;
        return SpriteLoader.getSprite(col, row);
    }

    public int getGridX() { return grid_x; }
    public int getGridY() { return grid_y; }
}