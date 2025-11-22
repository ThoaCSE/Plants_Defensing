package plantsdefense.model.enemy;

import java.util.List;
import java.awt.Point;

public class Bat extends Enemy {
    public Bat(List<Point> path) {
        super(path, EnemyType.Aerial, 2.5f, 40, 10, 1, 1);
    }

    // Bats fly, so they cannot be slowed by ground traps
    @Override
    public void applySlow(int duration) {
        // Immune to slow
    }
}