package Screens;

import Entities.CharacterHero;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.github.Zephyrdoestech.*;

/**
 * CharacterHero selection screen.
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

    private com.badlogic.gdx.math.Vector3 mousePos = new com.badlogic.gdx.math.Vector3();

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
        game.gameCamera.zoom = 1.0f;
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

        int[] drawOrder = new int[3];
        int sortIndex = 0;
        for (int i = 0; i < 3; i++) {
            if (i != index) {
                drawOrder[sortIndex++] = i;
            }
        }
        drawOrder[2] = index;

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
            } else if (i == 2 && game.assets.lyronSelectAnim != null) {
                animFrame = game.assets.lyronSelectAnim.getKeyFrame(stateTime, true);
            } else {
                // Fallback to the square textures if animation is missing
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
        // --- 1. KEYBOARD INPUT ---
        int leftKey  = game.ctx.useWasd ? Input.Keys.A : Input.Keys.LEFT;
        int rightKey = game.ctx.useWasd ? Input.Keys.D : Input.Keys.RIGHT;

        if (Gdx.input.isKeyJustPressed(leftKey)) {
            index = index > 0 ? index - 1 : 2;
        }
        if (Gdx.input.isKeyJustPressed(rightKey)) {
            index = index < 2 ? index + 1 : 0;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            confirmSelection();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MainMenuScreen(game));
        }

        // --- 2. MOUSE INPUT ---
        // Get the mouse coordinates and translate them to the game's UI viewport
        mousePos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        game.uiViewport.unproject(mousePos);

        // Loop through our 3 character slots
        for (int i = 0; i < 3; i++) {
            // Create an invisible "hitbox" over their original starting positions
            float hitX = PX[i] - 20f;
            float hitY = 140f;
            float hitW = 120f;
            float hitH = 180f;

            // Check if the mouse is currently inside this hitbox
            if (mousePos.x >= hitX && mousePos.x <= hitX + hitW &&
                mousePos.y >= hitY && mousePos.y <= hitY + hitH) {

                // Set the selected index to the hovered character!
                index = i;

                // If they click the left mouse button while hovering, lock it in
                if (Gdx.input.justTouched()) {
                    confirmSelection();
                }
            }
        }
    }

    private void confirmSelection() {
        // Stops the title music right as you load into the game
        if (game.assets.titleBGM != null && game.assets.titleBGM.isPlaying()) {
            game.assets.titleBGM.stop();
        }
        game.ctx.stopTheme();
        game.ctx.selectedCharacter = GameContext.CharacterType.values()[index];
        switch (index) {
            case 0: game.ctx.activeCharacterStats = new CharacterHero("Sonara",   "Banjo", 150, 40); break;
            case 1: game.ctx.activeCharacterStats = new CharacterHero("Aurelius", "Flute", 150, 40); break;
            case 2: game.ctx.activeCharacterStats = new CharacterHero("Lyron",    "Harp",  250, 40); break;
        }
        game.setScreen(new TownOfEchoesScreen(game));
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
