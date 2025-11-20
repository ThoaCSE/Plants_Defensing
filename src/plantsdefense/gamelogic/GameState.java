package plantsdefense.gamelogic;

public class GameState {
    public enum State { MENU, NEW_PLAYER, EDITOR, PLAYING, GAME_OVER, WIN }

    private static State current = State.MENU;

    public static void set(State state) { current = state; }
    public static State get() { return current; }
}