package Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import io.github.Zephyrdoestech.Main;

/**
 * Main menu with:
 * - Full-screen title background
 * - Floating musical note particles
 * - Pixel art image buttons
 * - Animated bracket cursor [ ] for the selected option
 * - Fade-in transition on entry
 */
public class MainMenuScreen extends BaseScreen {

    private static final String[] OPTIONS = {
        "START GAME", "HOW TO PLAY", "STORY", "CREDITS", "EXIT"
    };

    private int   selection  = 0;
    private float cursorTime = 0f;

    // Button dimensions
    private final float BTN_WIDTH = 160f;
    private final float BTN_HEIGHT = 48f;

    public MainMenuScreen(Main game) { super(game); }

    @Override
    public void show() {
        game.gameCamera.position.set(Main.WORLD_WIDTH / 2f, Main.WORLD_HEIGHT / 2f, 0);
        game.gameCamera.update();
        startFadeIn();

        if (game.assets.titleBGM != null && !game.assets.titleBGM.isPlaying()) {
            game.assets.titleBGM.setVolume(0.6f);
            game.assets.titleBGM.play();
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.gameViewport.apply();
        cursorTime += delta;
        updateFade(delta);

        game.batch.setProjectionMatrix(game.gameCamera.combined);
        game.batch.begin();

        // 1. Background
        game.batch.setColor(Color.WHITE);
        game.batch.draw(game.assets.titleScreenTex, 0, 0, Main.WORLD_WIDTH, Main.WORLD_HEIGHT);

        // 2. Floating notes
        drawFloatingNotes(delta);

        Texture[] buttons = {
            game.assets.startBtnTex,
            game.assets.tutorialBtnTex,
            game.assets.storyBtnTex,
            game.assets.creditsBtnTex,
            game.assets.exitBtnTex
        };

        // --- UPDATED LAYOUT MATH ---
        float centerX = (Main.WORLD_WIDTH / 2f) - (BTN_WIDTH / 2f);
        float padding = 15f;
        float gap = BTN_HEIGHT + padding;
        float totalBlockHeight = (buttons.length * BTN_HEIGHT) + ((buttons.length - 1) * padding);

        // Pulled the menu UP by changing the offset to -40f (was -130f)
        float startY = (Main.WORLD_HEIGHT / 2f) + (totalBlockHeight / 2f) - BTN_HEIGHT - 40f;

        // 3. Draw Buttons
        for (int i = 0; i < buttons.length; i++) {
            float drawY = startY - (i * gap);

            if (i == selection) {
                game.batch.setColor(Color.WHITE); // Bright for selected
            } else {
                game.batch.setColor(0.5f, 0.5f, 0.5f, 1f); // Dimmed for unselected
            }
            game.batch.draw(buttons[i], centerX, drawY, BTN_WIDTH, BTN_HEIGHT);
        }

        game.batch.end(); // Briefly stop the batch to draw our shapes!

        // 4. DRAW THE CORNER BRACKETS AROUND THE SELECTED BUTTON
        game.shapeRenderer.setProjectionMatrix(game.gameCamera.combined);
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Make the brackets pulse slightly to catch the eye
        float pulse = (MathUtils.sin(cursorTime * 6f) + 1f) / 2f;
        // Setting color to a nice light grey/white that matches your buttons
        game.shapeRenderer.setColor(0.7f + pulse * 0.3f, 0.7f + pulse * 0.3f, 0.75f + pulse * 0.25f, 1f);

        // Math for the brackets
        float selY = startY - (selection * gap); // The Y position of the currently selected button
        float pad = 8f;       // Distance from the button edge
        float t = 4f;         // Thickness of the bracket lines
        float l = 16f;        // Length of the bracket arms

        float boxX = centerX - pad;
        float boxY = selY - pad;
        float boxW = BTN_WIDTH + pad * 2;
        float boxH = BTN_HEIGHT + pad * 2;

        // Top Left Corner ⌜
        game.shapeRenderer.rect(boxX, boxY + boxH - t, l, t); // Horizontal
        game.shapeRenderer.rect(boxX, boxY + boxH - l, t, l); // Vertical

        // Top Right Corner ⌝
        game.shapeRenderer.rect(boxX + boxW - l, boxY + boxH - t, l, t);
        game.shapeRenderer.rect(boxX + boxW - t, boxY + boxH - l, t, l);

        // Bottom Left Corner ⌞
        game.shapeRenderer.rect(boxX, boxY, l, t);
        game.shapeRenderer.rect(boxX, boxY, t, l);

        // Bottom Right Corner ⌟
        game.shapeRenderer.rect(boxX + boxW - l, boxY, l, t);
        game.shapeRenderer.rect(boxX + boxW - t, boxY, t, l);

        game.shapeRenderer.end();

        // 5. Restart batch for the final text and fade overlays
        game.batch.begin();
        game.batch.setColor(Color.WHITE);

        // Hint Text
        game.assets.font.setColor(new Color(0.7f, 0.7f, 0.7f, 0.85f));
        game.assets.font.draw(game.batch,
            "W/S or Arrows to navigate  |  ENTER to select", 185, 40);
        game.assets.font.setColor(Color.WHITE);

        drawFadeOverlay();
        game.batch.end();

        // 6. Handle Input
        if (Gdx.input.isKeyJustPressed(Input.Keys.W) || Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            selection = selection > 0 ? selection - 1 : buttons.length - 1;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.S) || Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            selection = selection < buttons.length - 1 ? selection + 1 : 0;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            handleSelection();
        }
    }

    private void handleSelection() {
        switch (selection) {
            case 0:
                game.assets.titleBgm.stop();
                game.setScreen(new CharSelectScreen(game));
                break;
            case 1: game.setScreen(new HowToPlayScreen(game));   break;
            case 2: game.setScreen(new HowToPlayScreen(game));   break; // Temporary: use HowToPlay for Story
            case 3: game.setScreen(new HowToPlayScreen(game));   break; // Temporary: use HowToPlay for Credits
            case 4: Gdx.app.exit();                              break;
        }
    }

    @Override public void resize(int w, int h) {
        game.gameViewport.update(w, h, true);
    }
    @Override public void hide()    { clearNotes(); }
    @Override public void dispose() {}
}
