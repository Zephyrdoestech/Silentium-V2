package Mechanics;

import com.badlogic.gdx.math.MathUtils;

/**
 * MusicNote — a single floating musical note particle.
 *
 * Responsibilities:
 *  - Stores position, velocity, alpha, scale, and which symbol to draw.
 *  - Updates itself each frame (call {@link #update(float)}).
 *  - Reports when it should be removed (call {@link #isDead()}).
 *
 * Rendering is left to Main.java so it can use the shared SpriteBatch / BitmapFont.
 */
public class MusicNote {

    // ── Note type (0–3 maps to the 4 textures in Main.noteTextures) ──────────
    // 0=quarter note  1=eighth note  2=beamed pair  3=double beamed pair

    // ── Particle state ────────────────────────────────────────────────────────
    public float  x;
    public float  y;
    public float  alpha;
    public float  scale;      // BitmapFont scale to apply when drawing
    public int    symbolIndex; // index into Main.noteTextures (0–3)

    private float vy;          // upward speed (pixels / second)
    private float vx;          // gentle horizontal drift
    private float fadeRate;    // alpha lost per second

    // ── Constructor ───────────────────────────────────────────────────────────

    /**
     * Creates a new note at (startX, startY) with randomised movement and appearance.
     *
     * @param startX   Horizontal spawn position (usually random across screen width).
     * @param startY   Vertical  spawn position (usually just below the bottom edge).
     */
    public MusicNote(float startX, float startY) {
        x           = startX;
        y           = startY;
        vy          = 35f  + MathUtils.random(55f);     // 35–90 px/s upward
        vx          = MathUtils.random(-18f, 18f);      // slight left/right drift
        alpha       = 0.5f + MathUtils.random(0.4f);    // 0.5–0.9 starting opacity
        scale       = 1.0f + MathUtils.random(0.9f);    // 1.0–1.9× font scale
        fadeRate    = 0.12f + MathUtils.random(0.10f);  // fades out at varying speeds
        symbolIndex = MathUtils.random(3);
    }

    // ── Per-frame update ──────────────────────────────────────────────────────

    /**
     * Moves the note upward, applies drift, and reduces opacity.
     * Call once per frame before checking {@link #isDead()}.
     *
     * @param delta Seconds since the last frame (from {@code Gdx.graphics.getDeltaTime()}).
     */
    public void update(float delta) {
        y     += vy * delta;
        x     += vx * delta;
        alpha -= fadeRate * delta;
    }

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    /**
     * Returns {@code true} when this note should be removed from the active list.
     * A note is dead when it has faded out or floated off the top of the screen.
     *
     * @param screenHeight The viewport height used to detect off-screen notes.
     */
    public boolean isDead(float screenHeight) {
        return alpha <= 0f || y > screenHeight + 40f;
    }
}
