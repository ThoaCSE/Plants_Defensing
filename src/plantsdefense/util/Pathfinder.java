package plantsdefense.util;

import plantsdefense.model.entities.Tile;

import java.awt.Point;
import java.util.*;

public class Pathfinder {

    private record Node(Point p, int g, int h, Node parent) implements Comparable<Node> {
        int f() { return g + h; }
        @Override public int compareTo(Node o) { return Integer.compare(f(), o.f()); }
    }

    public static List<Point> findPath(Tile[][] grid, Point start, Point end) {
        PriorityQueue<Node> open = new PriorityQueue<>();
        Set<Point> closed = new HashSet<>();
        open.add(new Node(start, 0, manhattan(start, end), null));

        while (!open.isEmpty()) {
            Node current = open.poll();
            if (current.p.equals(end)) {
                return reconstructPath(current);
            }

            closed.add(current.p);

            for (Point neighbor : getNeighbors(grid, current.p)) {
                if (closed.contains(neighbor)) continue;

                int newG = current.g + 1;
                Node existing = open.stream()
                        .filter(n -> n.p.equals(neighbor))
                        .findFirst().orElse(null);

                if (existing == null || newG < existing.g) {
                    open.add(new Node(neighbor, newG, manhattan(neighbor, end), current));
                }
            }
        }
        return List.of(); // No path
    }

    private static List<Point> reconstructPath(Node node) {
        List<Point> path = new ArrayList<>();
        while (node != null) {
            path.add(0, node.p);
            node = node.parent;
        }
        return path;
    }

    private static int manhattan(Point a, Point b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }

    private static List<Point> getNeighbors(Tile[][] grid, Point p) {
        List<Point> neighbors = new ArrayList<>();
        int[][] dirs = {{0,1},{1,0},{0,-1},{-1,0}};

        for (int[] d : dirs) {
            int nx = p.x + d[0];
            int ny = p.y + d[1];

            if (nx >= 0 && nx < Constants.cols && ny >= 0 && ny < Constants.rows) {
                int type = grid[ny][nx].getType();
                // ONLY allow movement on path (8), start (27), end (28)
                if (type == Constants.tile_path || type == Constants.tile_start || type == Constants.tile_end) {
                    neighbors.add(new Point(nx, ny));
                }
            }
        }
        return neighbors;
    }
}