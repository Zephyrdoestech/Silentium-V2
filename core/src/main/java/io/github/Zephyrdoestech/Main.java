package io.github.Zephyrdoestech;

import Screens.TitleScreen;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Main extends Game {
    public SpriteBatch batch;
    public ShapeRenderer shapeRenderer;
    public OrthographicCamera gameCamera;
    public OrthographicCamera uiCamera;
    public Viewport gameViewport;
    public Viewport uiViewport;
    public static final float WORLD_WIDTH = 800.0F;
    public static final float WORLD_HEIGHT = 480.0F;
    public Assets assets;
    public GameContext ctx;
    public GlyphLayout glyphLayout;

    public void create() {
        this.batch = new SpriteBatch();
        this.shapeRenderer = new ShapeRenderer();
        this.gameCamera = new OrthographicCamera();
        this.gameViewport = new FitViewport(800.0F, 480.0F, this.gameCamera);
        this.gameCamera.position.set(400.0F, 240.0F, 0.0F);
        this.uiCamera = new OrthographicCamera();
        this.uiViewport = new FitViewport(800.0F, 480.0F, this.uiCamera);
        this.uiCamera.position.set(400.0F, 240.0F, 0.0F);
        this.uiCamera.update();
        this.assets = new Assets();
        this.ctx = new GameContext();
        this.glyphLayout = new GlyphLayout();
        this.setScreen(new TitleScreen(this));
    }

    public void resize(int width, int height) {
        if (width != 0 && height != 0) {
            this.gameViewport.update(width, height, true);
            this.uiViewport.update(width, height, true);
            super.resize(width, height);
        }
    }

    public void dispose() {
        super.dispose();
        this.batch.dispose();
        this.shapeRenderer.dispose();
        this.assets.dispose();
    }
}
