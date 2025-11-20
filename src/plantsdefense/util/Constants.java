package plantsdefense.util;

public final class Constants {
    private Constants() {}

    // Window
    public static final int window_width = 1280;
    public static final int window_height = 720;

    // Grid
    public static final int tile_size = 80;
    public static final int cols = 16;
    public static final int rows = 9;

    // Atlas
    public static final int sprite_size = 32;
    public static final int atlas_cols = 10;
    public static final int atlas_rows = 3;

    // Tile IDs (from your 10×3 atlas)
    public static final int tile_grass = 9;   // [0][9]
    public static final int tile_path  = 8;   // [0][8]
    public static final int tile_start = 27; // [2][7]
    public static final int tile_end   = 28; // [2][8]

    // Tile selector icons (row 0)
    public static final int selector_grass = 9;
    public static final int selector_path  = 8;
    public static final int selector_start = 27;
    public static final int selector_end   = 28;
}