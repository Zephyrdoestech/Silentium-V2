package Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import io.github.Zephyrdoestech.*;
import Entities.Character;

/**
 * Character selection screen.
 *
 * Features:
 *  - A/D or Arrow keys to browse
 *  - ENTER to confirm
 *  - Instrument theme plays when a character is highlighted (non-overlapping)
 *  - Selected portrait bobs and pulses with a glow
 *  - Floating note particles
 *  - Fade-in on entry
 */
public class CharSelectScreen extends BaseScreen {

    private int   index        = 0;
    private float bounceTime   = 0f;

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
        // Reset so first highlight always plays its theme
        game.ctx.lastThemeIndex = -1;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT);

        bounceTime += delta;
        updateFade(delta);
        handleInput();

        if (game.getScreen() != this) return;

        // Audio — plays only when selection changes
        game.ctx.playTheme(index, game.assets);

        game.batch.setProjectionMatrix(game.uiCamera.combined);
        game.batch.begin();

        // Background (dimmed title screen)
        game.batch.setColor(0.35f, 0.25f, 0.45f, 1f);
        game.batch.draw(game.assets.titleScreenTex, 0, 0, Main.WORLD_WIDTH, Main.WORLD_HEIGHT);
        game.batch.setColor(Color.WHITE);

        // Floating notes
        drawFloatingNotes(delta);

        // Title
        game.assets.titleFont.setColor(Color.CYAN);
        game.assets.titleFont.draw(game.batch, "SELECT YOUR HERO", 240, 450);
        game.assets.titleFont.setColor(Color.WHITE);

        // Portraits
        Texture[] texs = {game.assets.sonaraTex, game.assets.aureliusTex, game.assets.lyronTex};
        for (int i = 0; i < 3; i++) {
            boolean sel     = (i == index);
            float   bounceY = sel ? MathUtils.sin(bounceTime * 5f) * 8f : 0f;
            float   scale   = sel ? 100f : 80f;
            float   xOff    = sel ? -10f : 0f;

            game.batch.draw(texs[i], PX[i] + xOff, 220f + bounceY, scale, scale);

            game.assets.font.setColor(sel ? Color.YELLOW : Color.WHITE);
            game.assets.font.draw(game.batch, NAMES[i],   PX[i] + xOff, 210f);
            game.assets.font.setColor(Color.WHITE);
            game.assets.font.draw(game.batch, WEAPONS[i], PX[i] + xOff, 185f);
            game.assets.font.draw(game.batch, HP_VALS[i], PX[i] + xOff, 165f);
        }

        // Description
        game.assets.font.setColor(new Color(0.8f, 0.8f, 1f, 0.9f));
        game.assets.font.draw(game.batch, DESCS[index], 130, 130);

        // Hint
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
            bounceTime = 0f;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.D) || Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            index = index < 2 ? index + 1 : 0;
            bounceTime = 0f;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) confirmSelection();
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.ctx.stopTheme();
            game.setScreen(new MainMenuScreen(game));
        }
    }

    private void confirmSelection() {
        game.assets.titleBGM.stop();
        game.ctx.stopTheme();
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
    @Override public void hide()    {
        clearNotes();
        if (game.assets.titleBGM != null && game.assets.titleBGM.isPlaying()) {
            game.assets.titleBGM.stop();
        }
        game.ctx.stopTheme();
    }
    @Override public void dispose() {}
}
