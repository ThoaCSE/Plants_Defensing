package plantsdefense.model.plants.shoot;

import plantsdefense.model.enemies.Enemy;

public class AlchemistProjectile extends Projectile {

    public AlchemistProjectile(double x, double y, Enemy target) {
        // Speed: 4.0, Dmg: 10, Sprite: (1, 8)
        super(x, y, target, 4.0f, 10, 1, 8);
    }

    @Override
    protected void move() {
        // Standard homing or linear. Let's do Homing for accuracy.
        if (target != null) {
            double angle = Math.atan2(target.getY() - y, target.getX() - x);
            x += Math.cos(angle) * speed;
            y += Math.sin(angle) * speed;
        }
    }

    @Override
    protected void onHit(Enemy e) {
        e.takeDamage(damage);

        // UNIQUE LOGIC: Apply Debuff based on Type
        switch (e.getType()) {
            case Beast: // Dog
                // Apply Slowness (Duration: 120 frames = 2 seconds)
                e.applySlow(120);
                break;

            case Undead: // Zombie/Skeleton
                // Apply "Holy Burn" (Damage multiplier or just extra damage)
                e.takeDamage(damage * 2); // Critical hit!
                break;

            case Aerial: // Bat
                // Logic: Clip their wings (Stun or massive slow)
                // Since Bat.applySlow() is empty/immune, we deal extra damage instead
                e.takeDamage(damage + 10);
                break;
        }
    }
}