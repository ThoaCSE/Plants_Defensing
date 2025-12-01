package plantsdefense.model.plants.shoot;

import plantsdefense.model.enemies.Enemy;
import plantsdefense.model.enemies.EnemyType;
import plantsdefense.model.plants.Projectile;

public class SoldierProjectile extends Projectile {
    private final double dx;
    private final double dy;

    public SoldierProjectile(double x, double y, Enemy target) {
        super(x, y, target, 5.0f, 20, 1, 9);
        double angle = Math.atan2(target.getY() - y, target.getX() - x);
        this.dx = Math.cos(angle) * speed;
        this.dy = Math.sin(angle) * speed;
    }

    @Override
    protected void move() {
        x += dx;
        y += dy;
        if (x < 0 || y < 0 || x > 2000 || y > 2000) kill();
    }

    @Override
    protected void onHit(Enemy e) {
        if (e.getType() == EnemyType.Aerial) return;

        e.takeDamage(damage);

        if (e.isAlive()) {
            e.knockBack(3);
        }
    }
}
