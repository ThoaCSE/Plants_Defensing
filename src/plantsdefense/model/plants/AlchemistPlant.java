package plantsdefense.model.plants;

import plantsdefense.model.GameObject;
import plantsdefense.model.enemies.Enemy;
import plantsdefense.model.enemies.EnemyType;
import plantsdefense.model.plants.shoot.*;
import java.util.List;

public class AlchemistPlant extends Plant {
    public AlchemistPlant(int gridX, int gridY, List<GameObject> objects) {
        // Cost: 150
        super(gridX, gridY, 120, 10, 90, 150, 1, 6, objects);
    }
    @Override
    protected Enemy findTarget() {
        // (Priority logic remains the same as previous step)
        Enemy bestTarget = null;
        int bestPriority = -1;
        for (GameObject obj : objects) {
            if (obj instanceof Enemy) {
                Enemy e = (Enemy) obj;
                if (isInRange(e)) {
                    int p = (e.getType() == EnemyType.Aerial) ? 3 : (e.getType() == EnemyType.Beast) ? 2 : 1;
                    if (p > bestPriority) { bestPriority = p; bestTarget = e; }
                }
            }
        }
        return bestTarget;
    }
    @Override
    protected void shoot(Enemy target) {
        objects.add(new AlchemistProjectile(x, y, target));
    }
}