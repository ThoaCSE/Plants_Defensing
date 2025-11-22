package plantsdefense.model.enemy;

import java.util.List;
import java.awt.Point;

public class Skeleton extends Enemy {
    public Skeleton(List<Point> path) {
        super(path, EnemyType.Undead, 0.8f, 150, 15, 1, 2);
    }

    @Override
    public void takeDamage(int damage) {
        // Armor Logic: Reduces damage by 30%
        int reducedDamage = (int)(damage * 0.7);
        super.takeDamage(reducedDamage);
    }
}