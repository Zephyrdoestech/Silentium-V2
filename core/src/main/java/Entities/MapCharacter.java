package Entities;

public class MapCharacter {
    private float x;
    private float y;

    public MapCharacter(float startX, float startY) {
        this.x = startX;
        this.y = startY;
    }

    public void setX(float x) { this.x = x; }
    public float getX() { return x; }

    public void setY(float y) { this.y = y; }
    public float getY() { return y; }

    public void up(float amount) {
        y += amount;
    }

    public void down(float amount) {
        y -= amount;
    }

    public void left(float amount) {
        x -= amount;
    }

    public void right(float amount) {
        x += amount;
    }
}
