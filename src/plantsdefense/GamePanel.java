package plantsdefense;

import plantsdefense.model.Tile;
import plantsdefense.util.Constants;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel implements Runnable{
    private final Thread gameThread;
    private boolean running = false;
    private final Tile[][] grid = new Tile[Constants.rows][Constants.cols];

    public GamePanel(){
        setPreferredSize(new Dimension(Constants.window_width, Constants.window_height));
        setBackground(Color.BLACK);
        setFocusable(true);
        requestFocusInWindow();

        initTestGrid();

        gameThread = new Thread(this);
    }

    //TestGrid
    private void initTestGrid(){
        for(int row = 0; row < Constants.rows; row++){
            for (int col = 0; col < Constants.cols; col++){
                int type = Constants.tile_grass;
                if (row == 4){
                    type = Constants.tile_path;
                    if (col == 0) type = Constants.tile_begin;
                    if (col == Constants.cols -1) type = Constants.tile_end;
                }
                grid [row][col] = new Tile(col, row, type);
            }
        }
    }

    @Override
    public void addNotify(){
        super.addNotify();
        if(!running){
            running = true;
            gameThread.start();
        }
    }

    @Override
    public void removeNotify(){
        super.removeNotify();
        running = false;
    }

    @Override
    public void run() {
        final double nsPerFrame = 1000000000.0/Constants.FPS;
        long lastTime = System.nanoTime();

        while (running){
            long now = System.nanoTime();
            if (now - lastTime >= nsPerFrame){
                repaint();
                lastTime = now;
            }
            try { Thread.sleep(1);}
            catch (InterruptedException ignored) {}
        }
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2D = (Graphics2D)  g;
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        //grid Draw
        for (int row = 0; row < Constants.rows; row++){
            for (int col = 0; col < Constants.cols; col++){
                Tile tile = grid[row][col];
                g2D.drawImage(tile.getSprite(), tile.getX(), tile.getY(), null);
            }
        }
        g2D.dispose();

        //test
        /*g2D.setColor(new Color(34, 139, 34));
        g2D.fillOval(100, 100, 80, 80);=*/

        g2D.dispose();
    }
}
