package plantsdefense.gamelogic;

import plantsdefense.model.GameObject;
import plantsdefense.model.Tile;
import plantsdefense.model.enemies.*;
import plantsdefense.util.Constants;
import plantsdefense.util.Pathfinder;

import java.awt.Point;
import java.util.List;

public class WaveManager {
    private final List<GameObject> gameObjects;
    private final Tile[][] map;

    // Wave State
    private boolean waveActive = false;
    private int enemiesSpawned = 0;
    private int enemiesToSpawnTotal = 0;
    private long nextSpawnTime = 0;

    // Defines the enemies for each wave: {Zombies, Skeletons, Bats, Dogs}
    private final int[][] wavesConfig = {
            {3, 0, 0, 0},   // Wave 1: Easy
            {5, 0, 0, 3},   // Wave 2: Dogs introduced
            {5, 2, 2, 0},   // Wave 3: Mixed
            {10, 5, 5, 5}   // Wave 4: Horde
    };

    private int[] currentWaveLeft = new int[4];

    public WaveManager(List<GameObject> gameObjects, Tile[][] map) {
        this.gameObjects = gameObjects;
        this.map = map;
    }

    public void startNextWave() {
        if (waveActive) return;

        int waveIndex = GameSession.getWave();

        // Check if we exceeded max waves (Victory condition handled in isLevelFinished)
        if (waveIndex >= wavesConfig.length) {
            return;
        }

        // Load configuration
        int[] config = wavesConfig[waveIndex];
        currentWaveLeft[0] = config[0];
        currentWaveLeft[1] = config[1];
        currentWaveLeft[2] = config[2];
        currentWaveLeft[3] = config[3];

        enemiesToSpawnTotal = config[0] + config[1] + config[2] + config[3];
        enemiesSpawned = 0;
        waveActive = true;

        GameSession.nextWave();
        nextSpawnTime = System.currentTimeMillis() + 2000;
    }

    public void update() {
        if (!waveActive) return;

        // Check if we finished spawning this wave
        if (enemiesSpawned >= enemiesToSpawnTotal) {
            // Wave logic ends when all enemies are spawned.
            // The GameSession checks if they are dead to potentially start next wave logic UI
            boolean enemiesAlive = gameObjects.stream().anyMatch(o -> o instanceof Enemy);
            if (!enemiesAlive) {
                waveActive = false;
            }
            return;
        }

        long now = System.currentTimeMillis();
        if (now >= nextSpawnTime) {
            spawnNextEnemy();
            nextSpawnTime = now + 1500;
        }
    }

    // NEW: Checks if the player has beaten all waves
    public boolean isLevelFinished() {
        // 1. Must have finished the last wave index
        if (GameSession.getWave() >= wavesConfig.length) {
            // 2. Wave must not be active (spawning done)
            if (!waveActive) {
                // 3. No enemies left alive on map
                boolean enemiesAlive = gameObjects.stream().anyMatch(o -> o instanceof Enemy);
                return !enemiesAlive;
            }
        }
        return false;
    }

    private void spawnNextEnemy() {
        Point start = findTile(Constants.tile_start);
        Point end = findTile(Constants.tile_end);

        if (start == null || end == null) return;

        List<Point> path = Pathfinder.findPath(map, start, end);
        if (path.isEmpty() || path.size() <= 1) return;

        Enemy newEnemy = null;

        if (currentWaveLeft[0] > 0) { newEnemy = new Zombie(path); currentWaveLeft[0]--; }
        else if (currentWaveLeft[3] > 0) { newEnemy = new Dog(path); currentWaveLeft[3]--; }
        else if (currentWaveLeft[2] > 0) { newEnemy = new Bat(path); currentWaveLeft[2]--; }
        else if (currentWaveLeft[1] > 0) { newEnemy = new Skeleton(path); currentWaveLeft[1]--; }

        if (newEnemy != null) {
            gameObjects.add(newEnemy);
            enemiesSpawned++;
        }
    }

    private Point findTile(int type) {
        for (int r = 0; r < Constants.rows; r++) {
            for (int c = 0; c < Constants.cols; c++) {
                if (map[r][c].getType() == type) return new Point(c, r);
            }
        }
        return null;
    }
}