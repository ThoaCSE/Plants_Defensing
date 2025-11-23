package plantsdefense.model.plants;

import plantsdefense.model.GameObject;
import plantsdefense.model.enemies.Enemy;
import plantsdefense.model.plants.shoot.SoldierProjectile;

import java.util.List;

public class SoldierPlant extends Plant {
    public SoldierPlant(int gridX, int gridY, List<GameObject> objects) {
        // Cost: 50
        super(gridX, gridY, 150, 20, 60, 50, 1, 4, objects);
    }
    @Override
    protected Enemy findTarget() {
        Enemy closest = null;
        double minDst = Double.MAX_VALUE;
        for (GameObject obj : objects) {
            if (obj instanceof Enemy) {
                Enemy e = (Enemy) obj;
                if (isInRange(e)) {
                    double dst = Math.hypot(e.getX() - x, e.getY() - y);
                    if (dst < minDst) { minDst = dst; closest = e; }
                }
            }
        }
        return closest;
    }
    @Override
    protected void shoot(Enemy target) {
        objects.add(new SoldierProjectile(x, y, target));
    }
}