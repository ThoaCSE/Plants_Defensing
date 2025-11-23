package plantsdefense.model.enemies;
import java.util.List;
import java.awt.Point;

public class Bat extends Enemy {
    public Bat(List<Point> path) {
        // ... rewardGold=10, scoreValue=20 ...
        super(path, EnemyType.Aerial, 2.5f, 40, 10, 20, 1, 1);
    }
    @Override
    public void applySlow(int duration) {}
}