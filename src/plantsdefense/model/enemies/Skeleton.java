package plantsdefense.model.enemies;

import java.awt.Point;
import java.util.List;

public class Skeleton extends Enemy {
    public Skeleton(List<Point> path) {
        super(path, EnemyType.Undead, 0.8f, 150, 15, 30, 1, 2);
    }

    @Override
    public boolean takeDamage(int damage) {
        return super.takeDamage((int)(damage * 0.7));
    }
}
