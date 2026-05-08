package Screens;

import Mechanics.MusicNote;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
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
    protected boolean fadingOut = false;
    protected Screen  nextScreenAfterFade = null;
    protected float   fadeSpeed = 1.2f;

    // ── Floating note particles ───────────────────────────────────────────────
    protected final List<MusicNote> musicNotes     = new ArrayList<>();
    protected       float           noteSpawnTimer = 0f;
    protected static final Random   RNG            = new Random();

    protected BaseScreen(Main game) {
        this.game = game;
    }

    // ── Fade helpers ──────────────────────────────────────────────────────────

    /** Starts a black-to-clear fade-in. Call from show() or on state entry. */
    protected void startFadeIn() {
        fadeAlpha = 1f;
        fadingIn = true;
        fadingOut = false;
    }


    /** Starts a clear-to-black fade-out and transitions to the given screen. */
    protected void startFadeOut(Screen nextScreen) {
        fadeAlpha = 0f;
        fadingOut = true;
        fadingIn = false;
        nextScreenAfterFade = nextScreen;
    }

    /** Advances the fade each frame. Call at the top of render().
     * @return true if a screen transition happened this frame, false otherwise.
     */
    protected boolean updateFade(float delta) {
        if (fadingIn) {
            fadeAlpha -= delta * fadeSpeed;
            if (fadeAlpha <= 0f) {
                fadeAlpha = 0f;
                fadingIn = false;
            }
        } else if (fadingOut) {
            fadeAlpha += delta * fadeSpeed;
            if (fadeAlpha >= 1f) {
                fadeAlpha = 1f;
                fadingOut = false;
                onFadeOutComplete();
                return true;
            }
        }
        return false;
    }

    /** Called when the fade-out animation completes. */
    protected void onFadeOutComplete() {
        if (nextScreenAfterFade != null) {
            game.setScreen(nextScreenAfterFade);
        }
    }

    /**
     * Draws a full-screen black overlay using current fadeAlpha.
     * Call this LAST inside render() so it sits on top of everything else.
     */
    protected void drawFadeOverlay() {
        if (fadeAlpha <= 0f) return;

        // Ensure batch is ended before starting shape renderer, but safely check if it's active
        boolean batchWasDrawing = game.batch.isDrawing();
        if (batchWasDrawing) {
            game.batch.end();
        }

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        game.shapeRenderer.setProjectionMatrix(game.uiCamera.combined);
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(0f, 0f, 0f, fadeAlpha);
        game.shapeRenderer.rect(0, 0, Main.WORLD_WIDTH, Main.WORLD_HEIGHT);
        game.shapeRenderer.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);

        // Resume batch if it was drawing
        if (batchWasDrawing) {
            game.batch.setProjectionMatrix(game.gameCamera.combined);
            game.batch.begin();
        }
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
