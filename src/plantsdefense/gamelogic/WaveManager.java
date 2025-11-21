package plantsdefense.gamelogic;

import plantsdefense.model.entities.Enemy;
import plantsdefense.model.entities.GameObject;
import plantsdefense.model.entities.Tile;
import plantsdefense.util.Constants;
import plantsdefense.util.Pathfinder;

import java.awt.Point;
import java.util.List;

public class WaveManager {
    private final List<GameObject> gameObjects;
    private final Tile[][] map;
    private int enemiesToSpawn = 0;
    private long nextSpawnTime = 0;
    private boolean waveActive = false;

    public WaveManager(List<GameObject> gameObjects, Tile[][] map) {
        this.gameObjects = gameObjects;
        this.map = map;
    }

    public void startNextWave() {
        if (waveActive) return;
        int wave = GameSession.getWave() + 1;
        enemiesToSpawn = 5 + wave * 2;
        waveActive = true;
        nextSpawnTime = System.currentTimeMillis() + 3000; // 3 sec countdown
        GameSession.nextWave();
    }

    public void update() {
        if (!waveActive || enemiesToSpawn <= 0) return;

        long now = System.currentTimeMillis();
        if (now >= nextSpawnTime) {
            spawnEnemy();
            enemiesToSpawn--;
            nextSpawnTime = now + 900; // 0.9 sec between enemies
        }

        if (enemiesToSpawn <= 0) {
            waveActive = false;
        }
    }

    private void spawnEnemy() {
        Point start = findTile(Constants.tile_start);
        Point end = findTile(Constants.tile_end);

        if (start == null || end == null) return;

        List<Point> path = Pathfinder.findPath(map, start, end);
        if (!path.isEmpty() && path.size() > 1) {
            gameObjects.add(new Enemy(path));
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

    public boolean isWaveActive() { return waveActive; }
}