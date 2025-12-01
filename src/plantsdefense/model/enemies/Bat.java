package plantsdefense.model.enemies;
import java.awt.Point;
import java.util.List;

public class Bat extends Enemy {
    public Bat(List<Point> path){
        super(path, EnemyType.Aerial, 2.5f, 40, 10, 20, 1, 1);
    }
    @Override
    public void applySlow(int duration) {}
}