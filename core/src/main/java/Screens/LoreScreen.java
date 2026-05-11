package Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.*;
import io.github.Zephyrdoestech.Main;

public class LoreScreen extends BaseScreen {

    private Texture[] slides;
    private int currentSlide = 0;
    private boolean fromStartGame;

    // Fade logic variables
    private enum FadeState { FADING_IN, VIEWING, FADING_OUT }
    private FadeState fadeState = FadeState.FADING_IN;
    private float slideAlpha = 0f;
    private final float FADE_SPEED = 1.5f; // Higher is faster. 1.0f takes 1 second to fade.

    // Text Render Variables
    private int loreTextIndex = 0;
    private int loreCharIndex = 0;
    private float loreTimer = 0f;
    private static final float LORE_TYPE_SPEED = 0.03f;

    private float textMarginX = px(4.0f);
    private float textMarginY = px(10.0f);
    private float textX = screenLeft + textMarginX;
    private float textY = screenTop - textMarginY;
    private float textWidth = Main.WORLD_WIDTH - (2 * textMarginX);

    // Lore Text
    private String[] loreText = {
        "The silence that had stretched on for centuries was shattered in an instant. " +
        "A single bell rang out across the land, its tone cutting through the still air with a clarity no one had known in generations. " +
        "The sound spread like fire, shaking the world from its slumber.",

        "From beneath the ground, the Shadow Beasts stirred. " +
        "What once lay dormant now awoke in frenzy. " +
        "They rushed to the surface in great numbers, their movements restless and erratic, drawn to the alien vibration that tore through their perfect quiet. " +
        "Yet with nothing else in the world capable of producing sound, the beasts attacked nothing - they only searched, " +
        "roaming in madness, desperate to find the source of the ringing.",

        "Amid this chaos, a few children stepped forward. Born in the silent era," +
        " they had never known sound - only stories, tales, and ancient depictions that spoke of a forgotten force. " +
        "To them, the bell’s toll was incomprehensible, a sensation unlike anything they had ever experienced. " +
        "And yet, it stirred something within them—a call to uncover its meaning, to understand why the silence had broken, and why the beasts had awakened."
    };

    public LoreScreen(Main game, boolean fromStartGame) {
        super(game);
        this.fromStartGame = fromStartGame;
    }

    public LoreScreen(Main game) {
        super(game);
        this.fromStartGame = false;
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
                    endLoreText();
                    fadeState = FadeState.FADING_OUT;
                }
                updateLoreText(delta);
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
            game.assets.font.draw(game.batch, "Press ENTER or Click to continue... (ESC to skip entirely)", px(0.4f), px(0.8f));
            game.assets.font.setColor(Color.WHITE);
            drawLoreText(textX, textY, textWidth, Align.left, 1.0f);
        }

        game.batch.end();
    }

    private void finishLore() {
        game.assets.stopAllMusic();

        if (game.assets.storyBGM != null) {
            game.assets.storyBGM.stop();
        }

        if (fromStartGame) {
            game.setScreen(new CharSelectScreen(game));
        } else {
            if (game.assets.titleBGM != null) {
                game.assets.titleBGM.setLooping(true);
                game.assets.titleBGM.play();
            }
            game.setScreen(new MainMenuScreen(game));
        }
    }

    // Text Rendering

    private void updateLoreText(float delta) {
        if (loreText == null || loreText.length == 0) return;
        if (loreTextIndex >= loreText.length) return;

        String currentText = loreText[loreTextIndex];

        loreTimer += delta;

        while (loreTimer >= LORE_TYPE_SPEED && loreCharIndex < currentText.length()) {
            loreTimer -= LORE_TYPE_SPEED;
            loreCharIndex++;
        }
    }

    private void endLoreText(){
        loreTextIndex++;
        loreCharIndex = 0;
        loreTimer = 0f;
    }

    private void drawLoreText(
        float x,
        float y,
        float width,
        int alignment,
        float scale
    ) {

        if (loreText == null || loreText.length == 0) return;
        if (loreTextIndex >= loreText.length) return;

        String currentText = loreText[loreTextIndex];
        int displayLen = Math.min(loreCharIndex, currentText.length());
        String textToDisplay = currentText.substring(0, displayLen);

        game.assets.font.setColor(Color.WHITE);
        game.assets.font.getData().setScale(scale);

        game.assets.font.draw(
            game.batch,
            textToDisplay,
            x,
            y,
            width,
            alignment,
            true
        );

        game.assets.font.getData().setScale(1f);
    }

    @Override public void resize(int w, int h) { game.gameViewport.update(w, h, true); }
    @Override public void hide() {}
    @Override public void dispose() {}
}
