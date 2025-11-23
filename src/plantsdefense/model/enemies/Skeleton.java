package plantsdefense.model.enemies;

import java.util.List;
import java.awt.Point;

public class Skeleton extends Enemy {
    public Skeleton(List<Point> path) {
        // Path, Type, Speed, Health, Gold, Score, Row, Col
        super(path, EnemyType.Undead, 0.8f, 150, 15, 30, 1, 2);
    }

    // FIX: Change return type to 'boolean' and return the result of super.takeDamage
    @Override
    public boolean takeDamage(int damage) {
        // Apply armor (30% reduction) and pass to parent
        return super.takeDamage((int)(damage * 0.7));
    }
}