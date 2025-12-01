package plantsdefense.model.plants.shoot;

import plantsdefense.model.enemies.Enemy;
import plantsdefense.model.plants.Projectile;

public class TrackerProjectile extends Projectile {

    public TrackerProjectile(double x, double y, Enemy target) {
        super(x, y, target, 7.0f, 15, 1, 7);
    }

    @Override
    protected void move() {
        if (target != null && target.isAlive()) {
            double angle = Math.atan2(target.getY() - y, target.getX() - x);
            x += Math.cos(angle) * speed;
            y += Math.sin(angle) * speed;
        } else {
            kill();
        }
    }

    @Override
    protected void onHit(Enemy e) {
        e.takeDamage(damage);
    }
}
