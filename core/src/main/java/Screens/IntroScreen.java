package Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import io.github.Zephyrdoestech.Main;

/**
 * Cinematic intro sequence that plays when hitting "Start Game".
 * Fades images and text, then hands off to CharSelectScreen.
 */
public class IntroScreen extends BaseScreen {

    private final float TIME_PER_PAGE = 4.5f;
    private final float FADE_TIME = 1.0f;

    private int currentPage = 0;
    private float timer = 0f;

    private Texture[] storyImages;
    private String[] storyText;

    public IntroScreen(Main game) {
        super(game);

        // I pulled this text directly from your existing StoryScreen!
        storyText = new String[] {
            "The world fell silent to let a guardian rest.",
            "But silence birthed Shadow Beasts.",
            "A lone bell has shattered the quiet...",
            "Sound is your only weapon now."
        };

        // Placeholder array for 4 images.
        storyImages = new Texture[] {
            game.assets.story1Tex,
            game.assets.story2Tex,
            game.assets.story3Tex,
            game.assets.story4Tex
        };
    }

    @Override
    public void show() {
        game.gameCamera.position.set(Main.WORLD_WIDTH / 2f, Main.WORLD_HEIGHT / 2f, 0);
        game.gameCamera.update();
        startFadeIn(); // Using your BaseScreen fade
    }

    @Override
    public void render(float delta) {
        // 1. Clear Screen (prevents the resizing glitch!)
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT);

        updateFade(delta); // Your BaseScreen logic

        // 2. Timer and Page turning
        timer += delta;
        if (timer >= TIME_PER_PAGE) {
            timer = 0f;
            currentPage++;

            // Out of pages? Go to CharacterHero Select!
            if (currentPage >= storyImages.length) {
                game.setScreen(new CharSelectScreen(game));
                return;
            }
        }

        // 3. Fade Alpha Math
        float alpha = 1f;
        if (timer < FADE_TIME) {
            alpha = timer / FADE_TIME;
        } else if (timer > TIME_PER_PAGE - FADE_TIME) {
            alpha = (TIME_PER_PAGE - timer) / FADE_TIME;
        }

        // 4. Draw
        game.gameViewport.apply();
        game.batch.setProjectionMatrix(game.gameCamera.combined);
        game.batch.begin();

        // Draw Image (letterboxed slightly)
        game.batch.setColor(1f, 1f, 1f, alpha);
        game.batch.draw(storyImages[currentPage], 0, 0, Main.WORLD_WIDTH, Main.WORLD_HEIGHT);

        game.batch.setColor(0, 0, 0, 0.7f * alpha);

        // Draw Text centered under the image
        game.assets.font.setColor(1f, 1f, 1f, alpha);
        game.assets.font.draw(game.batch, storyText[currentPage], 150, 100);

        // Draw Skip Hint (Always visible)
        game.assets.font.setColor(Color.GRAY);
        game.assets.font.draw(game.batch, "Press ENTER to skip", 20, 30);

        // Reset colors and draw your overlay
        game.batch.setColor(Color.WHITE);
        game.assets.font.setColor(Color.WHITE);
        drawFadeOverlay();

        game.batch.end();

        // 5. Allow Skipping straight to character select
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new CharSelectScreen(game));
        }
    }

    @Override public void resize(int w, int h) {
        game.gameViewport.update(w, h, true);
    }

    @Override public void hide() {}
    @Override public void dispose() {}
}
