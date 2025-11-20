package plantsdefense.dao;

import plantsdefense.model.entities.Tile;
import plantsdefense.util.Constants;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MapIO {

    private static final String MAPS_DIR = "res/levels/";

    public static Tile[][] loadMap(String filename) {
        Tile[][] grid = new Tile[Constants.rows][Constants.cols];
        try (BufferedReader br = new BufferedReader(new FileReader(MAPS_DIR + filename))) {
            String line;
            int row = 0;
            while ((line = br.readLine()) != null && row < Constants.rows) {
                String[] tokens = line.split(",");
                for (int col = 0; col < Constants.cols && col < tokens.length; col++) {
                    int type = Integer.parseInt(tokens[col].trim());
                    grid[row][col] = new Tile(col, row, type);
                }
                row++;
            }
        } catch (Exception e) {
            return createDefaultMap();
        }
        fillMissingTiles(grid);
        return grid;
    }

    public static void saveMap(String filename, Tile[][] grid) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(MAPS_DIR + filename))) {
            for (int r = 0; r < Constants.rows; r++) {
                for (int c = 0; c < Constants.cols; c++) {
                    pw.print(grid[r][c].getType());
                    if (c < Constants.cols - 1) pw.print(",");
                }
                pw.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> listMaps() {
        List<String> names = new ArrayList<>();
        File dir = new File(MAPS_DIR);
        if (dir.exists()) {
            for (File f : dir.listFiles((d, name) -> name.endsWith(".txt"))) {
                names.add(f.getName());
            }
        }
        return names;
    }

    private static Tile[][] createDefaultMap() {
        Tile[][] grid = new Tile[Constants.rows][Constants.cols];
        for (int r = 0; r < Constants.rows; r++) {
            for (int c = 0; c < Constants.cols; c++) {
                int type = (r == 4) ? Constants.tile_path : Constants.tile_grass;
                if (r == 4 && c == 0) type = Constants.tile_start;
                if (r == 4 && c == Constants.cols - 1) type = Constants.tile_end;
                grid[r][c] = new Tile(c, r, type);
            }
        }
        return grid;
    }

    private static void fillMissingTiles(Tile[][] grid) {
        for (int r = 0; r < Constants.rows; r++) {
            for (int c = 0; c < Constants.cols; c++) {
                if (grid[r][c] == null) {
                    grid[r][c] = new Tile(c, r, Constants.tile_grass);
                }
            }
        }
    }
}