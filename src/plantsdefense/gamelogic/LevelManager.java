package plantsdefense.gamelogic;

public class LevelManager {

    // --- LEVEL 1 DATA (Standard) ---
    private static final String LEVEL_1_MAP = "level1.txt";
    private static final int[][] LEVEL_1_WAVES = {
            {3, 0, 0, 0},   // Wave 1: 3 Zombies
            {5, 0, 0, 2},   // Wave 2: 5 Zombies, 2 Dogs
            {5, 2, 0, 2},   // Wave 3: Mixed
            {10, 5, 0, 5}   // Wave 4: Boss
    };

    // --- LEVEL 2 DATA (Flying Introduced) ---
    private static final String LEVEL_2_MAP = "level2.txt";
    private static final int[][] LEVEL_2_WAVES = {
            {5, 0, 2, 0},   // Wave 1: Zombies + Bats
            {5, 3, 3, 3},   // Wave 2: Mixed
            {10, 5, 5, 5},  // Wave 3: Hard
            {15, 10, 10, 8} // Wave 4: Swarm
    };

    // --- LEVEL 3 DATA (Hard Multi-Lane) ---
    private static final String LEVEL_3_MAP = "level3.txt";
    private static final int[][] LEVEL_3_WAVES = {
            {5, 5, 0, 0},    // Wave 1: Tanky Start
            {0, 0, 10, 10},  // Wave 2: Fast Rush (Bats/Dogs)
            {10, 10, 5, 5},  // Wave 3: Heavy Defense
            {20, 10, 10, 10},// Wave 4: Horde
            {5, 20, 20, 20}  // Wave 5: Survival
    };

    // --- CUSTOM MODE (For User Maps) ---
    private static final int[][] CUSTOM_WAVES = {
            {5, 0, 0, 5},    // Wave 1
            {10, 5, 5, 5},   // Wave 2
            {15, 10, 10, 10},// Wave 3
            {20, 15, 15, 15},// Wave 4
            {10, 30, 30, 30} // Wave 5
    };

    // --- ACCESSORS ---

    public static int getMaxLevels() {
        return 3;
    }

    public static String getMapFile(int level) {
        switch (level) {
            case 1: return LEVEL_1_MAP;
            case 2: return LEVEL_2_MAP;
            case 3: return LEVEL_3_MAP;
            default: return null;
        }
    }

    public static int[][] getWaveConfig(int level) {
        // If playing Custom Mode (Level 4+), use generic waves
        if (level > getMaxLevels()) {
            return CUSTOM_WAVES;
        }

        switch (level) {
            case 1: return LEVEL_1_WAVES;
            case 2: return LEVEL_2_WAVES;
            case 3: return LEVEL_3_WAVES;
            default: return new int[][]{};
        }
    }
}