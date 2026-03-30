package Screens;

import Mechanics.MusicNote;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import io.github.Zephyrdoestech.Main;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Abstract base for all game screens.
 *
 * Provides:
 *  - Reference to Main (and therefore game.assets, game.ctx, game.batch, etc.)
 *  - Fade-in/out overlay
 *  - Floating musical note particles
 *  - Progress-bar drawing helper
 *
 * Subclasses must implement show(), render(float), resize(int,int), hide(), dispose().
 * pause() and resume() have empty defaults.
 */
public abstract class BaseScreen implements Screen {

    protected final Main game;

    // ── Fade overlay ──────────────────────────────────────────────────────────
    protected float   fadeAlpha = 0f;
    protected boolean fadingIn  = false;

    // ── Floating note particles ───────────────────────────────────────────────
    protected final List<MusicNote> musicNotes     = new ArrayList<>();
    protected       float           noteSpawnTimer = 0f;
    protected static final Random   RNG            = new Random();

    protected BaseScreen(Main game) {
        this.game = game;
    }

    // ── Fade helpers ──────────────────────────────────────────────────────────

    /** Starts a black-to-clear fade-in. Call from show() or on state entry. */
    protected void startFadeIn() { fadeAlpha = 1f; fadingIn = true; }

    /** Advances the fade each frame. Call at the top of render(). */
    protected void updateFade(float delta) {
        if (!fadingIn) return;
        fadeAlpha -= delta * 1.2f;
        if (fadeAlpha <= 0f) { fadeAlpha = 0f; fadingIn = false; }
    }

    /**
     * Draws a full-screen black overlay using current fadeAlpha.
     * Call this LAST inside render() so it sits on top of everything else.
     */
    protected void drawFadeOverlay() {
        if (fadeAlpha <= 0f) return;
        game.batch.end();
        game.shapeRenderer.setProjectionMatrix(game.uiCamera.combined);
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(0f, 0f, 0f, fadeAlpha);
        game.shapeRenderer.rect(0, 0, Main.WORLD_WIDTH, Main.WORLD_HEIGHT);
        game.shapeRenderer.end();
        game.batch.setProjectionMatrix(game.gameCamera.combined);
        game.batch.begin();
    }

    // ── Floating note particles ───────────────────────────────────────────────

    /**
     * Spawns, updates, and draws musical note particles.
     * Only call this from screens where notes should be visible (menus, char-select).
     * Do NOT call from ExploringScreen or CombatScreen.
     */
    protected void drawFloatingNotes(float delta) {
        noteSpawnTimer += delta;
        if (noteSpawnTimer >= 0.35f) {
            noteSpawnTimer = 0f;
            musicNotes.add(new MusicNote(RNG.nextFloat() * Main.WORLD_WIDTH, -20f));
        }
        Iterator<MusicNote> it = musicNotes.iterator();
        while (it.hasNext()) {
            MusicNote n = it.next();
            n.update(delta);
            if (n.isDead(Main.WORLD_HEIGHT)) { it.remove(); continue; }
            float size = 24f * n.scale;
            game.batch.setColor(0.95f, 0.85f, 0.35f, n.alpha);
            game.batch.draw(game.assets.noteTextures[n.symbolIndex],
                n.x, n.y + size, size, -size);
        }
        game.batch.setColor(Color.WHITE);
    }

    /** Clears all live particles — call from hide() if switching to gameplay. */
    protected void clearNotes() { musicNotes.clear(); noteSpawnTimer = 0f; }

    // ── Bar drawing helper ────────────────────────────────────────────────────

    protected void drawBar(ShapeRenderer sr, float x, float y, float w, float h,
                           float fraction, Color bg, Color fill) {
        sr.setColor(bg);
        sr.rect(x, y - h, w, h);
        sr.setColor(fill);
        sr.rect(x, y - h, w * MathUtils.clamp(fraction, 0f, 1f), h);
    }

    // ── Default no-op lifecycle ───────────────────────────────────────────────

    @Override public void pause()  {}
    @Override public void resume() {}
}
