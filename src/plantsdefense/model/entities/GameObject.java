package plantsdefense.model.entities;

import java.awt.*;

public abstract class GameObject {
    protected double x, y;
    protected boolean alive = true;

    public GameObject(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public abstract void update();
    public abstract void render(Graphics2D g);

    public boolean isAlive() { return alive; }
    public void kill() { alive = false; }

    public double getX() { return x; }
    public double getY() { return y; }
}