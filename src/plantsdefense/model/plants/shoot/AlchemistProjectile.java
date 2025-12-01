package plantsdefense.model.plants.shoot;

import plantsdefense.model.enemies.Enemy;
import plantsdefense.model.plants.Projectile;

public class AlchemistProjectile extends Projectile {

    public AlchemistProjectile(double x, double y, Enemy target) {
        super(x, y, target, 4.0f, 10, 1, 8);
    }

    @Override
    protected void move() {
        if (target != null) {
            double angle = Math.atan2(target.getY() - y, target.getX() - x);
            x += Math.cos(angle) * speed;
            y += Math.sin(angle) * speed;
        }
    }

    @Override
    protected void onHit(Enemy e) {
        e.takeDamage(damage);

        switch (e.getType()) {
            case Beast:
                e.applySlow(120);
                break;

            case Undead:
                e.takeDamage(damage);
                e.applyBurn(60);
                break;

            case Aerial:
                e.takeDamage(damage + 5);
                break;
        }
    }
}
