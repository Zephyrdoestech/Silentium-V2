package Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import io.github.Zephyrdoestech.Main;

/**
 * Main menu with:
 *  - Full-screen title background
 *  - Floating musical note particles
 *  - Animated (pulsing + shimmering) selected option cursor
 *  - Fade-in transition on entry
 */
public class MainMenuScreen extends BaseScreen {

    private static final String[] OPTIONS = {
        "START GAME", "HOW TO PLAY", "STORY", "CREDITS", "EXIT"
    };

    private int   selection  = 0;
    private float cursorTime = 0f;

    public MainMenuScreen(Main game) { super(game); }

    @Override
    public void show() {
        game.gameCamera.position.set(Main.WORLD_WIDTH / 2f, Main.WORLD_HEIGHT / 2f, 0);
        game.gameCamera.update();
        startFadeIn();
    }

    @Override
    public void render(float delta) {
        game.gameViewport.apply();
        cursorTime += delta;
        updateFade(delta);

        game.batch.setProjectionMatrix(game.gameCamera.combined);
        game.batch.begin();

        // Background
        game.batch.setColor(Color.WHITE);
        game.batch.draw(game.assets.titleScreenTex, 0, 0, Main.WORLD_WIDTH, Main.WORLD_HEIGHT);

        // Floating notes
        drawFloatingNotes(delta);

        // Menu options
        for (int i = 0; i < OPTIONS.length; i++) {
            if (i == selection) {
                float pulse  = (MathUtils.sin(cursorTime * 4f) + 1f) / 2f;
                float shimmerX = MathUtils.sin(cursorTime * 6f) * 3f;
                game.assets.font.setColor(1f, 0.7f + pulse * 0.3f, 0f, 1f);
                game.assets.font.draw(game.batch, "> " + OPTIONS[i] + " <",
                    310 + shimmerX, 310 - (i * 45));
            } else {
                game.assets.font.setColor(Color.WHITE);
                game.assets.font.draw(game.batch, OPTIONS[i], 310, 310 - (i * 45));
            }
        }

        // Hint
        game.assets.font.setColor(new Color(0.7f, 0.7f, 0.7f, 0.85f));
        game.assets.font.draw(game.batch,
            "W/S or Arrows to navigate  |  ENTER to select", 185, 50);
        game.assets.font.setColor(Color.WHITE);

        drawFadeOverlay();
        game.batch.end();

        // Input
        if (Gdx.input.isKeyJustPressed(Input.Keys.W) || Gdx.input.isKeyJustPressed(Input.Keys.UP))
            selection = selection > 0 ? selection - 1 : OPTIONS.length - 1;
        if (Gdx.input.isKeyJustPressed(Input.Keys.S) || Gdx.input.isKeyJustPressed(Input.Keys.DOWN))
            selection = selection < OPTIONS.length - 1 ? selection + 1 : 0;
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER))
            handleSelection();
    }

    private void handleSelection() {
        switch (selection) {
            case 0:
                game.assets.titleBGM.stop();
                game.setScreen(new CharSelectScreen(game));
                break;
            case 1: game.setScreen(new HowToPlayScreen(game));   break;
            case 2: game.setScreen(new HowToPlayScreen(game));   break; // Temporary: use HowToPlay for Story
            case 3: game.setScreen(new HowToPlayScreen(game));   break; // Temporary: use HowToPlay for Credits
            case 4: Gdx.app.exit();                              break;
        }
    }

    @Override public void resize(int w, int h) {}
    @Override public void hide()    { clearNotes(); }
    @Override public void dispose() {}
}
