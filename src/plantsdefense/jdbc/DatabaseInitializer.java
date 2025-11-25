package plantsdefense.jdbc;

import plantsdefense.model.Tile;
import plantsdefense.util.Constants;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DatabaseInitializer {

    private static final String[] BUILT_IN_LEVELS = {
            "level1.txt", "level2.txt", "level3.txt", "level_1.txt", "level_4.txt", "l0.txt", "3_1.txt", "3.txt"
    };

    public static void init() {
        try (Connection conn = DBConnection.getConnection()) {
            createTablesIfNotExist(conn);
            insertBuiltInMapsIfMissing(conn);
            System.out.println("Database initialized successfully!");
        } catch (Exception e) {
            System.err.println("Failed to initialize database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void createTablesIfNotExist(Connection conn) throws Exception {
        String[] sqls = {
                // Players
                "CREATE TABLE IF NOT EXISTS players (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                        "name VARCHAR(50) UNIQUE NOT NULL," +
                        "levels_unlocked INT DEFAULT 1," +
                        "total_score INT DEFAULT 0," +
                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)",

                // High Scores
                "CREATE TABLE IF NOT EXISTS high_scores (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                        "player_id INT NOT NULL," +
                        "level INT NOT NULL," +
                        "score INT NOT NULL," +
                        "achieved_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                        "FOREIGN KEY (player_id) REFERENCES players(id) ON DELETE CASCADE)",

                // Saves
                "CREATE TABLE IF NOT EXISTS saves (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                        "player_id INT NOT NULL," +
                        "level INT NOT NULL," +
                        "wave INT NOT NULL," +
                        "gold INT NOT NULL," +
                        "lives INT NOT NULL," +
                        "score INT NOT NULL," +
                        "plants_data TEXT," +
                        "map_name VARCHAR(100)," +
                        "saved_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                        "FOREIGN KEY (player_id) REFERENCES players(id) ON DELETE CASCADE)",

                // Maps
                "CREATE TABLE IF NOT EXISTS maps (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                        "name VARCHAR(100) UNIQUE NOT NULL," +
                        "data TEXT NOT NULL," +
                        "created_by INT," +
                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                        "FOREIGN KEY (created_by) REFERENCES players(id) ON DELETE SET NULL)"
        };

        for (String sql : sqls) {
            conn.createStatement().execute(sql);
        }
    }

    private static void insertBuiltInMapsIfMissing(Connection conn) throws Exception {
        for (String filename : BUILT_IN_LEVELS) {
            // Check if already exists
            String check = "SELECT COUNT(*) FROM maps WHERE name = ?";
            try (PreparedStatement ps = conn.prepareStatement(check)) {
                ps.setString(1, filename);
                ResultSet rs = ps.executeQuery();
                rs.next();
                if (rs.getInt(1) > 0) continue; // already exists
            }

            // Load from file and insert
            String data = loadMapFile("res/levels/" + filename);
            if (data != null) {
                String insert = "INSERT INTO maps (name, data, created_by) VALUES (?, ?, NULL)";
                try (PreparedStatement ps = conn.prepareStatement(insert)) {
                    ps.setString(1, filename);
                    ps.setString(2, data);
                    ps.executeUpdate();
                    System.out.println("Inserted built-in map: " + filename);
                }
            }
        }
    }

    private static String loadMapFile(String path) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        } catch (Exception e) {
            System.err.println("Could not read built-in map: " + path);
            return null;
        }
    }
