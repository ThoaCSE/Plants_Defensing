package plantsdefense.gamelogic;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import plantsdefense.model.GameObject;
import plantsdefense.model.Tile;
import plantsdefense.model.enemies.*;
import plantsdefense.util.Constants;
import plantsdefense.util.Pathfinder;

public class WaveManager {
    private final List<GameObject> gameObjects;
    private final Tile[][] map;

    private boolean waveActive = false;
    private int enemiesSpawned = 0;
    private int enemiesToSpawnTotal = 0;
    private long nextSpawnTime = 0;

    private final int[][] wavesConfig;
    private final int[] currentWaveLeft = new int[4];

    private final List<Point> spawnPoints = new ArrayList<>();
    private final List<Point> endPoints = new ArrayList<>();
    private final Random random = new Random();

    public WaveManager(List<GameObject> gameObjects, Tile[][] map, int[][] wavesConfig) {
        this.gameObjects = gameObjects;
        this.map = map;
        this.wavesConfig = wavesConfig;

        findPathPoints();
    }

    private void findPathPoints() {
        spawnPoints.clear();
        endPoints.clear();

        for (int r = 0; r < Constants.rows; r++) {
            for (int c = 0; c < Constants.cols; c++) {
                int type = map[r][c].getType();
                if (type == Constants.tile_start) {
                    spawnPoints.add(new Point(c, r));
                } else if (type == Constants.tile_end) {
                    endPoints.add(new Point(c, r));
                }
            }
        }

        if (spawnPoints.isEmpty()) System.out.println("WARNING: No Start Tiles found!");
        if (endPoints.isEmpty()) System.out.println("WARNING: No End Tiles found!");
    }

    public void startNextWave() {
        if (waveActive) return;

        int waveIndex = GameSession.getWave();
        if (waveIndex >= wavesConfig.length) return;

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

        if (enemiesSpawned >= enemiesToSpawnTotal) {
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

    public boolean isLevelFinished() {
        if (GameSession.getWave() >= wavesConfig.length) {
            if (!waveActive) {
                return gameObjects.stream().noneMatch(o -> o instanceof Enemy);
            }
        }
        return false;
    }

    private void spawnNextEnemy() {
        if (spawnPoints.isEmpty() || endPoints.isEmpty()) return;

        Point start = spawnPoints.get(random.nextInt(spawnPoints.size()));

        List<Point> path = null;

        for (Point end : endPoints) {
            List<Point> testPath = Pathfinder.findPath(map, start, end);
            if (testPath != null && !testPath.isEmpty()) {
                path = testPath;
                break;
            }
        }

        if (path == null || path.size() <= 1) return;

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
}
