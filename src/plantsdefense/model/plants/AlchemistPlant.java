package plantsdefense.model.plants;

import plantsdefense.model.GameObject;
import plantsdefense.model.enemies.Enemy;
import plantsdefense.model.plants.shoot.AlchemistProjectile;

import java.util.List;

public class AlchemistPlant extends Plant {

    public AlchemistPlant(int gridX, int gridY, List<GameObject> objects) {
        // Range: 120, Dmg: 10, CD: 90 (Slow), Sprite: (1, 6)
        super(gridX, gridY, 120, 10, 90, 1, 6, objects);
    }

    @Override
    protected Enemy findTarget() {
        // Logic: Any target is fine, maybe closest
        for (GameObject obj : objects) {
            if (obj instanceof Enemy) {
                Enemy e = (Enemy) obj;
                if (isInRange(e)) return e;
            }
        }
        return null;
    }

    @Override
    protected void shoot(Enemy target) {
        // Create Alchemist Ammo (Col 8)
        objects.add(new AlchemistProjectile(x, y, target));
    }
}