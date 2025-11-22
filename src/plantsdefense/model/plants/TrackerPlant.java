package plantsdefense.model.plants;

import plantsdefense.model.GameObject;
import plantsdefense.model.enemies.Enemy;
import plantsdefense.model.plants.shoot.TrackerProjectile;

import java.util.List;

public class TrackerPlant extends Plant {

    public TrackerPlant(int gridX, int gridY, List<GameObject> objects) {
        // Range: 200 (Long), Dmg: 15, CD: 40 (Fast), Sprite: (1, 5)
        super(gridX, gridY, 200, 15, 40, 1, 5, objects);
    }

    @Override
    protected Enemy findTarget() {
        // Logic: Prioritize FAST enemies (Dog/Bat)
        Enemy bestTarget = null;
        float maxSpeed = -1;

        for (GameObject obj : objects) {
            if (obj instanceof Enemy) {
                Enemy e = (Enemy) obj;
                if (isInRange(e)) {
                    // Check if this enemy is faster than current best
                    if (e.getSpeed() > maxSpeed) {
                        maxSpeed = e.getSpeed();
                        bestTarget = e;
                    }
                }
            }
        }
        return bestTarget;
    }

    @Override
    protected void shoot(Enemy target) {
        // Create Tracker Ammo (Col 7)
        objects.add(new TrackerProjectile(x, y, target));
    }
}