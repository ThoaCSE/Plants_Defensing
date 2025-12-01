package plantsdefense.gamelogic;

public class LevelManager {

    private static final String LEVEL_1_MAP = "level1.txt";
    private static final int[][] LEVEL_1_WAVES = {
            {3, 0, 0, 0},
            {5, 0, 0, 2},
            {5, 2, 0, 2},
            {10, 5, 0, 5}
    };

    private static final String LEVEL_2_MAP = "level2.txt";
    private static final int[][] LEVEL_2_WAVES = {
            {5, 0, 2, 0},
            {5, 3, 3, 3},
            {10, 5, 5, 5},
            {15, 10, 10, 8}
    };

    private static final String LEVEL_3_MAP = "level3.txt";
    private static final int[][] LEVEL_3_WAVES = {
            {5, 5, 0, 0},
            {0, 0, 10, 10},
            {10, 10, 5, 5},
            {20, 10, 10, 10},
            {5, 20, 20, 20}
    };

    private static final int[][] CUSTOM_WAVES = {
            {5, 0, 0, 5},
            {10, 5, 5, 5},
            {15, 10, 10, 10},
            {20, 15, 15, 15},
            {10, 30, 30, 30}
    };

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
