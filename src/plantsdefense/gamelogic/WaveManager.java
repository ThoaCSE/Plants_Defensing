package plantsdefense.gamelogic;

import plantsdefense.model.GameObject;
import plantsdefense.model.Tile;
import plantsdefense.model.enemy.*;
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
    // Index 0 = Wave 1, Index 1 = Wave 2, etc.
    private final int[][] wavesConfig = {
            {5, 0, 0, 0},   // Wave 1: 5 Zombies
            {5, 0, 0, 3},   // Wave 2: 5 Zombies, 3 Dogs
            {5, 2, 2, 0},   // Wave 3: 5 Zombies, 2 Skeletons, 2 Bats
            {10, 5, 5, 5}   // Wave 4: The Horde
    };

    // Temporary counter for the current running wave
    private int[] currentWaveLeft = new int[4];

    public WaveManager(List<GameObject> gameObjects, Tile[][] map) {
        this.gameObjects = gameObjects;
        this.map = map;
    }

    public void startNextWave() {
        if (waveActive) return;

        int waveIndex = GameSession.getWave(); // Assuming starts at 0 or 1
        // Safety check if we run out of defined waves
        if (waveIndex >= wavesConfig.length) {
            System.out.println("All waves cleared!");
            return;
        }

        // Load configuration for this wave
        int[] config = wavesConfig[waveIndex];
        currentWaveLeft[0] = config[0]; // Zombies
        currentWaveLeft[1] = config[1]; // Skeletons
        currentWaveLeft[2] = config[2]; // Bats
        currentWaveLeft[3] = config[3]; // Dogs

        enemiesToSpawnTotal = config[0] + config[1] + config[2] + config[3];
        enemiesSpawned = 0;
        waveActive = true;

        GameSession.nextWave(); // Increment global wave counter
        nextSpawnTime = System.currentTimeMillis() + 2000; // Start in 2 seconds
    }

    public void update() {
        if (!waveActive) return;

        // Check if we finished spawning everyone
        if (enemiesSpawned >= enemiesToSpawnTotal) {
            // Check if all enemies are dead to mark wave as "Complete" (Optional logic)
            boolean enemiesAlive = gameObjects.stream().anyMatch(o -> o instanceof Enemy);
            if (!enemiesAlive) {
                waveActive = false;
            }
            return;
        }

        long now = System.currentTimeMillis();
        if (now >= nextSpawnTime) {
            spawnNextEnemy();
            nextSpawnTime = now + 1000; // 1 second delay between enemies
        }
    }

    private void spawnNextEnemy() {
        Point start = findTile(Constants.tile_start);
        Point end = findTile(Constants.tile_end);

        if (start == null || end == null) return;

        // Calculate path
        List<Point> path = Pathfinder.findPath(map, start, end);
        if (path.isEmpty() || path.size() <= 1) return;

        Enemy newEnemy = null;

        // Prioritize spawning in order: Zombie -> Dog -> Bat -> Skeleton
        // You can change this order to mix them up
        if (currentWaveLeft[0] > 0) {
            newEnemy = new Zombie(path);
            currentWaveLeft[0]--;
        } else if (currentWaveLeft[3] > 0) {
            newEnemy = new Dog(path);
            currentWaveLeft[3]--;
        } else if (currentWaveLeft[2] > 0) {
            newEnemy = new Bat(path);
            currentWaveLeft[2]--;
        } else if (currentWaveLeft[1] > 0) {
            newEnemy = new Skeleton(path);
            currentWaveLeft[1]--;
        }

        if (newEnemy != null) {
            gameObjects.add(newEnemy);
            enemiesSpawned++;
        }
    }

    private Point findTile(int type) {
        for (int r = 0; r < Constants.rows; r++) {
            for (int c = 0; c < Constants.cols; c++) {
                if (map[r][c].getType() == type) {
                    return new Point(c, r);
                }
            }
        }
        return null;
    }
}