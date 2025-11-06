package plantsdefense.dao;

import plantsdefense.model.Tile;
import plantsdefense.util.Constants;
import plantsdefense.util.Constants;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class LevelDAO {
    private static final String Level_path = "res/levels/";

    public void saveLevel(String filename, Tile [][] grid){
        try(PrintWriter writer = new PrintWriter(new FileWriter(Level_path + filename))){
            for(int row = 0; row < Constants.rows; row++){
                for(int col = 0; col < Constants.cols; col++){
                    writer.println(grid[row][col].getType());
                    if(col < Constants.cols - 1) writer.print(",");
                }
            }
        } catch (IOException e){
            System.err.println("Can't save game at moment due to: " + e.getMessage() );
        }
    }

    public Tile[][] loadLevel(String filename){
        Tile[][] grid = new Tile[Constants.rows][Constants.cols];
        try(BufferedReader reader = new BufferedReader(new FileReader(Level_path + filename))){
            String line;
            int row = 0;
            while ((line = reader.readLine()) != null && row < Constants.rows){
                String[] types = line.split(",");
                for(int col = 0; col < types.length && col < Constants.cols; col ++){
                    int type = Integer.parseInt(types[col].trim());
                    grid [row][col] = new Tile(col, row, type);
                }
                for (int col = types.length; col < Constants.cols; col++){
                    grid[row][col] = new Tile(col, row, Constants.tile_grass);
                }
                row ++;
            }
        } catch (IOException | NumberFormatException e){
            System.err.println("Loading error!!! Using Default: " + e.getMessage());
            return createDefaultGrid();
        }
        return grid;
    }

    private Tile[][] createDefaultGrid(){
        Tile[][] grid = new Tile[Constants.rows][Constants.cols];
        for(int row = 0; row < Constants.rows; row++){
            for (int col = 0; col< Constants.cols; col++){
                int type = Constants.tile_grass;
                if(row == 4){
                    type = Constants.tile_path;
                    if (col == 0) type = Constants.tile_begin;
                    if (col == Constants.cols - 1) type = Constants.tile_end;
                }
                grid[row][col] = new Tile(col, row, type);
            }
        }
        return grid;
    }
}
