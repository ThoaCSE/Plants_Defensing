package plantsdefense.model.plants;

import java.util.List;
import plantsdefense.model.GameObject;
import plantsdefense.model.enemies.Enemy;
import plantsdefense.model.plants.shoot.TrackerProjectile;

public class TrackerPlant extends Plant {
    public TrackerPlant(int gridX, int gridY, List<GameObject> objects) {
        super(gridX, gridY, 200, 15, 40, 100, 1, 5, objects);
    }
    @Override
    protected Enemy findTarget() {
        Enemy best = null;
        float maxSpeed = -1;
        for (GameObject obj : objects) {
            if (obj instanceof Enemy e) {
                if (isInRange(e) && e.getSpeed() > maxSpeed) {
                    maxSpeed = e.getSpeed(); best = e;
                }
            }
        }
        return best;
    }
    @Override
    protected void shoot(Enemy target) {
        objects.add(new TrackerProjectile(x, y, target));
        objects.add(new TrackerProjectile(x + 10, y + 5, target));
        objects.add(new TrackerProjectile(x - 10, y + 5, target));
    }
}