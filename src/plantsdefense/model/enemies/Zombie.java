package plantsdefense.model.enemies;

import java.util.List;
import java.awt.Point;

public class Zombie extends Enemy {
    public Zombie(List<Point> path) {
        // Path, Type, Speed, Health, Gold, Row, Col
        super(path, EnemyType.Undead, 1.0f, 100, 10, 1, 0);
    }
}