package io.github.Zephyrdoestech;

import Screens.TitleScreen;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Entry point. Owns the shared rendering infrastructure and delegates
 * everything else to screen classes via LibGDX's built-in Game + Screen system.
 *
 * Switching screens from anywhere:
 *   game.setScreen(new CombatScreen(game));
 */
public class Main extends Game {

    // ── Shared rendering infrastructure (created once, used by every screen) ──
    public SpriteBatch   batch;
    public ShapeRenderer shapeRenderer;

    public OrthographicCamera gameCamera;
    public OrthographicCamera uiCamera;
    public Viewport           gameViewport;
    public Viewport           uiViewport;

    public static final float WORLD_WIDTH  = 800f;
    public static final float WORLD_HEIGHT = 480f;

    // ── Shared asset manager and game state ────────────────────────────────────
    public Assets      assets;
    public GameContext ctx;

    @Override
    public void create() {
        batch         = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        gameCamera   = new OrthographicCamera();
        gameViewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, gameCamera);
        gameCamera.position.set(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f, 0);

        uiCamera   = new OrthographicCamera();
        uiViewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, uiCamera);
        uiCamera.position.set(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f, 0);
        uiCamera.update();

        assets = new Assets();
        ctx    = new GameContext();

        setScreen(new TitleScreen(this));
    }

    @Override
    public void resize(int width, int height) {
        if (width == 0 || height == 0) return;
        gameViewport.update(width, height, true);
        uiViewport.update(width, height, true);
        super.resize(width, height); // forwards to the active screen
    }

    @Override
    public void dispose() {
        super.dispose(); // disposes the active screen
        batch.dispose();
        shapeRenderer.dispose();
        assets.dispose();
    }
}
