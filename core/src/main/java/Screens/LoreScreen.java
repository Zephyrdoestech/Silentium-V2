package Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import io.github.Zephyrdoestech.Main;

public class LoreScreen extends BaseScreen {

    private Texture[] slides;
    private int currentSlide = 0;

    // Fade logic variables
    private enum FadeState { FADING_IN, VIEWING, FADING_OUT }
    private FadeState fadeState = FadeState.FADING_IN;
    private float slideAlpha = 0f;
    private final float FADE_SPEED = 1.5f; // Higher is faster. 1.0f takes 1 second to fade.

    public LoreScreen(Main game) {
        super(game);
    }

    @Override
    public void show() {
        game.assets.stopAllMusic();
        // Manage the music
        if (game.assets.titleBGM != null) {
            game.assets.titleBGM.stop(); // Stop the main menu music
        }
        if (game.assets.storyBGM != null) {
            game.assets.storyBGM.setLooping(true);
            game.assets.storyBGM.setVolume(0.7f);
            game.assets.storyBGM.play(); // Start the story music
        }

        slides = new Texture[] {
            game.assets.storyPanel1,
            game.assets.storyPanel2,
            game.assets.storyPanel3
        };

        game.gameCamera.position.set(Main.WORLD_WIDTH / 2f, Main.WORLD_HEIGHT / 2f, 0);
        game.gameCamera.zoom = 1.0f; // Ensure camera isn't zoomed in from the map!
        game.gameCamera.update();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Allow skipping the entire lore screen with ESC
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            finishLore();
            return;
        }

        // --- FADE LOGIC STATE MACHINE ---
        switch (fadeState) {
            case FADING_IN:
                slideAlpha += delta * FADE_SPEED;
                if (slideAlpha >= 1f) {
                    slideAlpha = 1f;
                    fadeState = FadeState.VIEWING;
                }
                break;

            case VIEWING:
                // Only allow skipping to the next slide if it is fully visible
                if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                    fadeState = FadeState.FADING_OUT;
                }
                break;

            case FADING_OUT:
                slideAlpha -= delta * FADE_SPEED;
                if (slideAlpha <= 0f) {
                    slideAlpha = 0f;
                    currentSlide++;

                    if (currentSlide >= slides.length) {
                        finishLore();
                        return; // Stop rendering to prevent out-of-bounds crash
                    } else {
                        fadeState = FadeState.FADING_IN; // Start fading in the next slide
                    }
                }
                break;
        }

        game.batch.setProjectionMatrix(game.gameCamera.combined);
        game.batch.begin();

        if (slides[currentSlide] != null) {
            // Apply the alpha transparency to the batch
            game.batch.setColor(1f, 1f, 1f, slideAlpha);
            game.batch.draw(slides[currentSlide], 0, 0, Main.WORLD_WIDTH, Main.WORLD_HEIGHT);

            // Reset the batch color back to solid white so text doesn't turn invisible
            game.batch.setColor(Color.WHITE);
        }

        // Only show the hint text when the slide is fully visible
        if (fadeState == FadeState.VIEWING) {
            game.assets.font.setColor(new Color(0.7f, 0.7f, 0.7f, 0.85f));
            game.assets.font.draw(game.batch, "Press ENTER or Click to continue... (ESC to skip entirely)", 20, 40);
            game.assets.font.setColor(Color.WHITE);
        }

        game.batch.end();
    }

    private void finishLore() {
        game.assets.stopAllMusic();

        if (game.assets.storyBGM != null) {
            game.assets.storyBGM.stop();
        }

        if (game.assets.titleBGM != null) {
            game.assets.titleBGM.setLooping(true);
            game.assets.titleBGM.play();
        }

        game.setScreen(new CharSelectScreen(game));
    }

    @Override public void resize(int w, int h) { game.gameViewport.update(w, h, true); }
    @Override public void hide() {}
    @Override public void dispose() {}
}
