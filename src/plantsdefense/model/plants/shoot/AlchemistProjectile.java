package plantsdefense.model.plants.shoot;

import plantsdefense.model.enemies.Enemy;
import plantsdefense.model.plants.Projectile;

public class AlchemistProjectile extends Projectile {

    public AlchemistProjectile(double x, double y, Enemy target) {
        // Speed: 4.0, Dmg: 10, Sprite Row: 1, Col: 8
        super(x, y, target, 4.0f, 10, 1, 8);
    }

    @Override
    protected void move() {
        // Homing Logic (Alchemists don't miss potions)
        if (target != null) {
            double angle = Math.atan2(target.getY() - y, target.getX() - x);
            x += Math.cos(angle) * speed;
            y += Math.sin(angle) * speed;
        }
    }

    @Override
    protected void onHit(Enemy e) {
        // Base Damage
        e.takeDamage(damage);

        // --- LOGIC: TYPE BASED DEBUFFS ---
        switch (e.getType()) {
            case Beast: // Dog
                e.applySlow(120); // Slow (Blue) for 2 seconds
                break;

            case Undead: // Zombie/Skeleton
                e.takeDamage(damage); // Double Damage (Holy Water)
                e.applyBurn(60);      // Burn (Red) for 1 second
                break;

            case Aerial: // Bat
                e.takeDamage(damage + 5); // Extra Damage
                break;
        }
    }
}