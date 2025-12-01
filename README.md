# Plants Defense — Project Report

## Intro

- What it is: Plants Defense is a small Java desktop tower‑defense game where players place plants to stop waves of enemies from reaching a goal.
- Why it exists: The project is intended as a learning/example codebase for game loops, simple pathfinding, sprite handling, level design, and lightweight persistence.

## Reference

- Inspired by: [KaarinGaming/TowerDefenceTutorial](https://github.com/KaarinGaming/TowerDefenceTutorial)

## Requirements

- Java: Java 8 or newer (Java 11 recommended). Ensure `javac` and `java` are available on your `PATH`.
- IDE (optional): IntelliJ IDEA or Eclipse for easy editing and running.
- Libraries: If the project uses external jars, place them in `lib/` and include them on the classpath when compiling and running.

## Structure

- `src/` — Java source code (package `plantsdefense`).
- `res/levels/` — Plain-text level files (map/level data).
- `lib/` — Third-party jars (if any).

Key files:

- `src/plantsdefense/main/Main.java` — Application entry point.
- `src/plantsdefense/gui/GameFrame.java` — Main window and UI wiring.
- `src/plantsdefense/gamelogic/` — Core game loop and session logic (`GameSession`, `GameState`).
- `src/plantsdefense/jdbc/` — Database helpers (connection, high scores, saves, maps).
- `src/plantsdefense/model/` — Game entities: plants, enemies, projectiles, tiles.

## UIs

The UI is Swing-based (window and panels). Main UI pieces:

- `GameFrame` — Creates the main application window.
- `ScreenController` — Manages which screen/panel is visible.
- `gui/editor` and `gui/menu` — contain editor panels and menu screens (e.g., `PlayPanel`, `LoadSavePanel`, `LeaderboardPanel`).

Panels are used to separate concerns (menu, editor, gameplay) and can be added or extended to introduce new screens.

## Gameplay

Game flow:

- `GameSession` and `GameState` manage the active session and the state of objects in the world.
- `LevelManager` loads level files from `res/levels/` and interprets map data.
- `WaveManager` spawns enemies according to configured waves.

Entities:

- Plants: located under `model/plants` (examples: `SoldierPlant`, `AlchemistPlant`, `TrackerPlant`).
- Enemies: under `model/enemies` (examples: `Zombie`, `Skeleton`, `Bat`, `Dog`).
- Projectiles: in `model/plants/shoot`.

Movement & logic:

- `Pathfinder` (in `util`) handles pathfinding for enemies.
- Sprite loading and animation use `Sprite` / `SpriteLoader` utilities.

## Conclusion

- Getting started: open the project in an IDE and run `src/plantsdefense/main/Main.java`, or compile and run from PowerShell. Example (PowerShell):

```powershell
javac -d out -sourcepath src -cp "lib/*" src\plantsdefense\main\Main.java
java -cp "out;lib/*" plantsdefense.main.Main
```

- Contributions: add levels in `res/levels/`, new plants/enemies in `model`, or improve UI panels under `gui`.

---
File updated: reformatted README into a report-style document.

## Screenshots & Gameplay

All example screenshots and short animated clips are available in the repository under the `ref` folder:

- Static screenshots: `ref/pic/`
- Animated clips / short GIFs: `ref/vid/`

Recommended files (already present):

- `ref/pic/Menu.png` — Main menu
- `ref/pic/New_player.png` — New player / registration
- `ref/pic/Map_editor.png` — Map editor
- `ref/pic/LeaderBoard.png` — Leaderboard panel
- `ref/pic/Continue.png` — Continue / resume
- `ref/pic/Playing.png` — In-game view
- `ref/pic/Victory.png` — Win screen
- `ref/pic/Game_over.png` — Lose screen

Short animated interactions (GIFs):

- `ref/vid/Placing.gif` — Placing a defender
- `ref/vid/Removing.gif` — Removing/selling a defender
- `ref/vid/PlayGame.gif` — Short gameplay loop
- `ref/vid/Map_Editor.gif` — Editor interaction
- `ref/vid/Victory.gif`, `ref/vid/GameOver.gif` — End states

How to embed in the `README.md` (examples):

Inline static image:

```markdown
![Main menu](ref/pic/Menu.png)
```

Inline animated GIF:

```markdown
![Place defender](ref/vid/Placing.gif)
```

Clickable thumbnail that links to an external video (YouTube/Vimeo):

```markdown
[![Watch gameplay](ref/pic/thumbnail.png)](https://youtu.be/your_video_id)
```

Notes and best practices:

- GitHub renders images and GIFs referenced by path inside the repository — using `ref/pic/` and `ref/vid/` will show them in the README when the files are committed.
- Keep GIFs short (3–6s) and limited to ~640px width for reasonable file sizes. Use `ffmpeg` palette-based conversion for better quality.
- For longer MP4 videos, host on YouTube/Vimeo and link via a thumbnail in the README rather than adding large video files to the repository.
- If you decide to store MP4s or large GIFs in the repo, enable Git LFS and track `*.mp4` and `*.gif` to avoid bloating the repo history.


---
File updated: reformatted README into a report-style document.
