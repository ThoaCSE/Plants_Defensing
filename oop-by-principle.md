# OOP by Principle — Repository Examples

This document groups Java/OOP concepts and shows representative examples from the codebase. Each section lists files that illustrate the concept, a short code snippet, and a relative link to the source file.

## Table of contents

- [Encapsulation](#encapsulation)
- [Inheritance](#inheritance)
- [Polymorphism — Overriding](#polymorphism--overriding)
- [Polymorphism — Overloading](#polymorphism--overloading)
- [Abstraction](#abstraction)
- [Interfaces](#interfaces)
- [Enums](#enums)
- [Records](#records)
- [Static members & Utility classes](#static-members--utility-classes)
- [final / immutability](#final--immutability)
- [this keyword](#this-keyword)
- [Constructors (DI / overloads)](#constructors-dependency-injection--overloads)
- [Inner classes / nested interfaces](#inner-classes--nested-interfaces)
- [Composition / Aggregation / Association](#composition--aggregation--association)
- [Annotations](#annotations)
- [Misc / Patterns](#misc--patterns)

---

## Encapsulation

- `src/plantsdefense/model/Tile.java`

```java
private final int grid_x;
private int type;
public int getX() { return grid_x * Constants.tile_size; }
public void setType(int type) { this.type = type; }
```

[View file](src/plantsdefense/model/Tile.java)

- `src/plantsdefense/util/SpriteLoader.java`

```java
private static BufferedImage atlas;
public static BufferedImage getSprite(int col, int row) {
    return atlas.getSubimage(x, y, Constants.sprite_size, Constants.sprite_size);
}
```

[View file](src/plantsdefense/util/SpriteLoader.java)

---

## Inheritance

- `src/plantsdefense/model/plants/Plant.java`

```java
public abstract class Plant extends GameObject {
    // base for concrete plants
}
```

[View file](src/plantsdefense/model/plants/Plant.java)

- `src/plantsdefense/model/plants/TrackerPlant.java`

```java
public class TrackerPlant extends Plant {
    public TrackerPlant(int gridX, int gridY, List<GameObject> objects) { /* ... */ }
}
```

[View file](src/plantsdefense/model/plants/TrackerPlant.java)

---

## Polymorphism — Overriding

- `src/plantsdefense/util/Pathfinder.java`

```java
private record Node(Point p, int g, int h, Node parent) implements Comparable<Node> {
    @Override
    public int compareTo(Node o) { return Integer.compare(f(), o.f()); }
}
```

[View file](src/plantsdefense/util/Pathfinder.java)

- `src/plantsdefense/model/plants/shoot/Projectile.java`

```java
public abstract class Projectile extends GameObject {
    @Override
    public void update() { /* override in subclasses */ }
}
```

[View file](src/plantsdefense/model/plants/shoot/Projectile.java)

---

## Polymorphism — Overloading

- `src/plantsdefense/jdbc/MapDB.java`

```java
public static void saveMap(String name, Tile[][] grid) { /* ... */ }
public static void saveMap(String name, Tile[][] grid, int someFlag) { /* ... */ }
```

[View file](src/plantsdefense/jdbc/MapDB.java)

---

## Abstraction

- `src/plantsdefense/model/GameObject.java`

```java
public abstract class GameObject {
    public abstract void update();
    public abstract void render(Graphics2D g);
}
```

[View file](src/plantsdefense/model/GameObject.java)

- `src/plantsdefense/model/enemies/Enemy.java`

```java
public abstract class Enemy extends GameObject {
    protected List<Point> path;
}
```

[View file](src/plantsdefense/model/enemies/Enemy.java)

---

## Interfaces

- `src/plantsdefense/gui/editor/MapListPanel.java` (inner interface)

```java
public interface OnMapSelectedListener {
    void onMapSelected(String name);
    void onBack();
}
```

[View file](src/plantsdefense/gui/editor/MapListPanel.java)

- `src/plantsdefense/util/Pathfinder.java` (interface implementation)

```java
private record Node(...) implements Comparable<Node> { @Override public int compareTo(Node o) { /* ... */ } }
```

[View file](src/plantsdefense/util/Pathfinder.java)

---

## Enums

- `src/plantsdefense/model/enemies/EnemyType.java`

```java
public enum EnemyType { ZOMBIE, SKELETON, BAT, DOG }
```

[View file](src/plantsdefense/model/enemies/EnemyType.java)

- `src/plantsdefense/main/State.java`

```java
public enum State { MENU, PLAYING, PAUSED }
```

[View file](src/plantsdefense/main/State.java)

---

## Records

- `src/plantsdefense/util/Pathfinder.java`

```java
private record Node(Point p, int g, int h, Node parent) implements Comparable<Node> { /* ... */ }
```

[View file](src/plantsdefense/util/Pathfinder.java)

---

## Static members & Utility classes

- `src/plantsdefense/util/Constants.java`

```java
public final class Constants {
    public static final int window_width = 1280;
    private Constants() {}
}
```

[View file](src/plantsdefense/util/Constants.java)

- `src/plantsdefense/util/SpriteLoader.java`

```java
private static BufferedImage atlas;
public static BufferedImage getSprite(int col, int row) { /* ... */ }
```

[View file](src/plantsdefense/util/SpriteLoader.java)

---

## final / immutability

- `src/plantsdefense/util/Constants.java`

```java
public final class Constants { /* ... */ }
```

[View file](src/plantsdefense/util/Constants.java)

- `src/plantsdefense/model/Tile.java`

```java
private final int grid_x;
private final int grid_y;
```

[View file](src/plantsdefense/model/Tile.java)

---

## this keyword

- `src/plantsdefense/model/Tile.java`

```java
public void setType(int type) { this.type = type; }
```

[View file](src/plantsdefense/model/Tile.java)

---

## Constructors (dependency injection / overloads)

- `src/plantsdefense/model/plants/TrackerPlant.java`

```java
public TrackerPlant(int gridX, int gridY, List<GameObject> objects) { /* ... */ }
```

[View file](src/plantsdefense/model/plants/TrackerPlant.java)

- `src/plantsdefense/model/enemies/Zombie.java`

```java
public Zombie(List<Point> path) { /* ... */ }
```

[View file](src/plantsdefense/model/enemies/Zombie.java)

---

## Inner classes / nested interfaces

- `src/plantsdefense/gui/editor/MapListPanel.java`

```java
public interface OnMapSelectedListener { /* ... */ }
```

[View file](src/plantsdefense/gui/editor/MapListPanel.java)

- `src/plantsdefense/util/Pathfinder.java`

```java
private record Node(...) implements Comparable<Node> { /* ... */ }
```

[View file](src/plantsdefense/util/Pathfinder.java)

---

## Composition / Aggregation / Association

- `src/plantsdefense/gamelogic/GameSession.java`

```java
private Tile[][] currentMap;
private int gold;
private List<GameObject> gameObjects; // (managed collection)
```

[View file](src/plantsdefense/gamelogic/GameSession.java)

- `src/plantsdefense/model/plants/Plant.java`

```java
protected List<GameObject> objects; // plant interacts with other game objects
```

[View file](src/plantsdefense/model/plants/Plant.java)

---

## Annotations

- `src/plantsdefense/model/plants/shoot/TrackerProjectile.java`

```java
@Override
public void update() { /* ... */ }
```

[View file](src/plantsdefense/model/plants/shoot/TrackerProjectile.java)

---

## Misc / Patterns

- DAO-like wrappers: `src/plantsdefense/jdbc/*` (e.g., `HighScoreDB`, `PlayerDB`) encapsulate SQL and provide methods (encapsulation & association).
- Manager/controller classes: `GameSession`, `LevelManager`, `WaveManager` coordinate objects (composition/association).

---