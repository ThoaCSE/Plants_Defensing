package plantsdefense;

import plantsdefense.util.Constants;
import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel implements Runnable{
    private final Thread gameThread;
    private boolean running = false;
    public GamePanel(){
        setPreferredSize(new Dimension(Constants.window_width, Constants.window_height));
        setBackground(Color.BLACK);
        setFocusable(true);
        requestFocusInWindow();

        gameThread = new Thread(this);
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

        //test
        g2D.setColor(new Color(34, 139, 34));
        g2D.fillOval(100, 100, 80, 80);

        g2D.dispose();
    }
}
