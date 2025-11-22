package plantsdefense.model.plants.shoot;

import plantsdefense.model.enemies.Enemy;

public class SoldierProjectile extends Projectile {
    // We store the vector so it flies straight even if enemy moves
    private double dx, dy;

    public SoldierProjectile(double x, double y, Enemy target) {
        // Speed: 5.0, Dmg: 20, Sprite: (1, 9)
        super(x, y, target, 5.0f, 20, 1, 9);

        // Calculate vector once (Linear shot)
        double angle = Math.atan2(target.getY() - y, target.getX() - x);
        this.dx = Math.cos(angle) * speed;
        this.dy = Math.sin(angle) * speed;
    }

    @Override
    protected void move() {
        x += dx;
        y += dy;
        // Logic to die if out of bounds could go here
    }

    @Override
    protected void onHit(Enemy e) {
        e.takeDamage(damage);
    }
}