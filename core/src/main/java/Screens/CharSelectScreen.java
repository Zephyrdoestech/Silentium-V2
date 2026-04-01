package Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import io.github.Zephyrdoestech.*;
import Entities.Character;

/**
 * Character selection screen.
 *
 * Features:
 * - A/D or Arrow keys to browse
 * - ENTER to confirm
 * - Selected portrait scales up massively
 * - Mix of Animated and Static Hero Sprites
 * - Floating note particles
 * - Fade-in on entry
 */
public class CharSelectScreen extends BaseScreen {

    private int   index        = 0;
    private float stateTime    = 0f;

    private static final String[] NAMES   = {"1: Sonara",   "2: Aurelius", "3: Lyron"};
    private static final String[] WEAPONS = {"Banjo",        "Flute",       "Harp"};
    private static final String[] HP_VALS = {"HP: 150",      "HP: 150",     "HP: 250"};
    private static final float[]  PX      = {80f,            355f,          620f};
    private static final String[] DESCS   = {
        "Fierce & grief-driven.   Passive: Body of Thorns.",
        "Gentle & principled.     Passive: Melodic Remedy.",
        "Reluctant avenger.       Passive: Winner Takes All."
    };

    public CharSelectScreen(Main game) { super(game); }

    @Override
    public void show() {
        game.gameCamera.position.set(Main.WORLD_WIDTH / 2f, Main.WORLD_HEIGHT / 2f, 0);
        game.gameCamera.update();
        startFadeIn();
        // Removed the code that stops titleBGM here, so the menu music keeps playing!
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT);

        stateTime += delta;

        updateFade(delta);
        handleInput();

        if (game.getScreen() != this) return;

        // Removed game.ctx.playTheme() from here!

        game.batch.setProjectionMatrix(game.uiCamera.combined);
        game.batch.begin();

        game.batch.setColor(0.35f, 0.25f, 0.45f, 1f);
        game.batch.draw(game.assets.titleScreenTex, 0, 0, Main.WORLD_WIDTH, Main.WORLD_HEIGHT);
        game.batch.setColor(Color.WHITE);

        drawFloatingNotes(delta);

        game.assets.titleFont.setColor(Color.CYAN);
        game.assets.titleFont.draw(game.batch, "SELECT YOUR HERO", 240, 450);
        game.assets.titleFont.setColor(Color.WHITE);

        Texture[] staticTexs = {game.assets.sonaraTex, game.assets.aureliusTex, game.assets.lyronTex};

        for (int i = 0; i < 3; i++) {
            boolean sel = (i == index);

            float scale = sel ? 180f : 80f;
            float imageX = sel ? PX[i] - 50f : PX[i];
            float imageY = sel ? 230f : 220f;
            float textX = PX[i] - 10f;

            TextureRegion animFrame = null;
            Texture staticFrame = null;

            if (i == 0 && game.assets.sonaraSelectAnim != null) {
                animFrame = game.assets.sonaraSelectAnim.getKeyFrame(stateTime, true);
            } else if (i == 1 && game.assets.aureliusSelectAnim != null) {
                animFrame = game.assets.aureliusSelectAnim.getKeyFrame(stateTime, true);
            } else {
                staticFrame = staticTexs[i];
            }

            if (animFrame != null) {
                game.batch.draw(animFrame, imageX, imageY, scale, scale);
            } else if (staticFrame != null) {
                game.batch.draw(staticFrame, imageX, imageY, scale, scale);
            }

            game.assets.font.setColor(sel ? Color.YELLOW : Color.WHITE);
            game.assets.font.draw(game.batch, NAMES[i],   textX, 210f);
            game.assets.font.setColor(Color.WHITE);
            game.assets.font.draw(game.batch, WEAPONS[i], textX, 185f);
            game.assets.font.draw(game.batch, HP_VALS[i], textX, 165f);
        }

        game.assets.font.setColor(new Color(0.8f, 0.8f, 1f, 0.9f));
        game.assets.font.draw(game.batch, DESCS[index], 130, 130);

        game.assets.font.setColor(Color.GRAY);
        game.assets.font.draw(game.batch,
            "A/D or Arrows to browse  |  ENTER to confirm  |  ESC to go back", 95, 60);
        game.assets.font.setColor(Color.WHITE);

        drawFadeOverlay();
        game.batch.end();
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.A) || Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            index = index > 0 ? index - 1 : 2;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.D) || Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            index = index < 2 ? index + 1 : 0;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) confirmSelection();
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            // Removed stopTheme() from here
            game.setScreen(new MainMenuScreen(game));
        }
    }

    private void confirmSelection() {
        // Stops the title music right as you load into the game
        if (game.assets.titleBGM != null && game.assets.titleBGM.isPlaying()) {
            game.assets.titleBGM.stop();
        }

        // Removed stopTheme() from here
        game.ctx.selectedCharacter = GameContext.CharacterType.values()[index];
        switch (index) {
            case 0: game.ctx.activeCharacterStats = new Character("Sonara",   "Banjo", 150, 40); break;
            case 1: game.ctx.activeCharacterStats = new Character("Aurelius", "Flute", 150, 40); break;
            case 2: game.ctx.activeCharacterStats = new Character("Lyron",    "Harp",  250, 40); break;
        }
        game.setScreen(new ExploringScreen(game));
    }

    @Override public void resize(int w, int h) {
        game.uiViewport.update(w, h, true);
    }

    @Override public void hide() {
        clearNotes();
        // Removed stopTheme() from here
        // Note: The title music is stopped in confirmSelection() when starting the game,
        // or continues playing if you back out to the Main Menu via ESC.
    }

    @Override public void dispose() {}
}
