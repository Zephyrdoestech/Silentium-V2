package Mechanics;

import Entities.Enemy;
import com.badlogic.gdx.math.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class Room {
    private Rectangle bounds;
    private boolean isCleared;
    private List<Enemy> enemies;

    public Room(float x, float y, float width, float height) {
        this.bounds = new Rectangle(x, y, width, height);
        this.isCleared = false;
        this.enemies = new ArrayList<>();
    }

    // --- Getters & Setters ---

    public Rectangle getBounds() {
        return bounds;
    }

    public boolean isCleared() {
        return isCleared;
    }

    public void setCleared(boolean cleared) {
        this.isCleared = cleared;
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }

    public void addEnemy(Enemy enemy) {
        this.enemies.add(enemy);
    }

    /**
     * Checks if a specific coordinate (like the player's position) is inside this room.
     */
    public boolean contains(float x, float y) {
        return bounds.contains(x, y);
    }
}
