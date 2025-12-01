package plantsdefense.model.enemies;
import java.awt.Point;
import java.util.List;

public class Zombie extends Enemy {
    public Zombie(List<Point> path) {
        super(path, EnemyType.Undead, 1.0f, 100, 10, 10, 1, 0);
    }
}