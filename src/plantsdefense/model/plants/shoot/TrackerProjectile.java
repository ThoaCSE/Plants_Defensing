package plantsdefense.model.plants.shoot;

import plantsdefense.model.enemies.Enemy;

public class TrackerProjectile extends Projectile {

    public TrackerProjectile(double x, double y, Enemy target) {
        // Speed: 7.0 (Very Fast), Dmg: 15, Sprite: (1, 7)
        super(x, y, target, 7.0f, 15, 1, 7);
    }

    @Override
    protected void move() {
        // Homing Logic: Re-calculate angle every frame
        if (target != null && target.isAlive()) {
            double angle = Math.atan2(target.getY() - y, target.getX() - x);
            x += Math.cos(angle) * speed;
            y += Math.sin(angle) * speed;
        } else {
            kill(); // Target dead, missile explodes
        }
    }

    @Override
    protected void onHit(Enemy e) {
        e.takeDamage(damage);
    }
}