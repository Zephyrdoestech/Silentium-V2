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
    private boolean isFadingIn = false;
    private boolean isFadingOut = false;

    private com.badlogic.gdx.math.Vector3 mousePos = new com.badlogic.gdx.math.Vector3();

    private static final String[] NAMES   = {"1: Sonara",   "2: Aurelius", "3: Lyron"};
    private static final String[] WEAPONS = {"Banjo",        "Flute",       "Harp"};
    private static final String[] HP_VALS = {"HP: 150",      "HP: 150",     "HP: 250"};
    private static final String[] DESCS   = {
        "Fierce & grief-driven.   Passive: Body of Thorns.",
        "Gentle & principled.     Passive: Melodic Remedy.",
        "Reluctant avenger.       Passive: Winner Takes All."
    };

    // Cards Rendering Measurement
    float cardGap = px(1.0f);
    float cardWidth = 135;
    float cardHeight = 177;
    float cardDisplayWidth = (cardWidth * 3) + (2 * cardGap);
    float cardDisplayX = (Main.WORLD_WIDTH - cardDisplayWidth) / 2f;
    float cardDisplayY = (Main.WORLD_HEIGHT) - px(2.4f) - cardHeight;

    public CharSelectScreen(Main game) { super(game); }

    @Override
    public void show() {
        game.gameCamera.zoom = 1.0f;
        game.gameCamera.position.set(Main.WORLD_WIDTH / 2f, Main.WORLD_HEIGHT / 2f, 0);
        game.gameCamera.update();
        startFadeIn();
        isFadingIn = true;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT);

        stateTime += delta;

        updateFade(delta);
        if(isFadingIn) {
            fadeAlpha -= delta * 1.2f;
            if(fadeAlpha <= 0f){
                fadeAlpha = 0f;
                isFadingIn = false;
            }
        }
                handleInput();

        if (isFadingOut) {
            fadeAlpha += delta * 1.2f;
            if (fadeAlpha >= 1f) {
                fadeAlpha = 1f;
                isFadingOut = false;
                // Perform the screen transition once the fade-out is complete
                game.ctx.selectedCharacter = GameContext.CharacterType.values()[index];
                switch (index) {
                    case 0: game.ctx.activeCharacterStats = new CharacterHero("Sonara",   "Banjo", 150, 40); break;
                    case 1: game.ctx.activeCharacterStats = new CharacterHero("Aurelius", "Flute", 150, 40); break;
                    case 2: game.ctx.activeCharacterStats = new CharacterHero("Lyron",    "Harp",  250, 40); break;
                }
                game.setScreen(new CharacterLoreScreen(game));
                return; // Exit render loop to prevent further drawing
            }
        }

        game.batch.setProjectionMatrix(game.uiCamera.combined);
        game.batch.begin();

        game.batch.draw(game.assets.characterSelectBG, 0, 0, Main.WORLD_WIDTH, Main.WORLD_HEIGHT);
        game.batch.setColor(Color.WHITE);

        drawCenteredText(
            "SELECT YOUR HERO", 0, Main.WORLD_HEIGHT - px(2.0f), Main.WORLD_WIDTH,
            px(1.6f), Color.CYAN, 1.5f);

        Texture[] staticTexs = {game.assets.sonaraTex, game.assets.aureliusTex, game.assets.lyronTex};

        // CARD & DESCRIPTION RENDERING
        for (int i = 0; i < 3; i++) {
            boolean sel = (i == index);

            float imageX = cardDisplayX + (i * (cardWidth + cardGap));
            float imageY = cardDisplayY;
            float textX = imageX;
            float textY = imageY - px(0.8f);

            TextureRegion animFrame = null;
            Texture staticFrame = null;

            if (i == 0 && game.assets.sonaraCardDefault != null && game.assets.sonaraCardSelected != null) {
                animFrame = sel ? game.assets.sonaraCardSelected.getKeyFrame(stateTime, true) :
                    game.assets.sonaraCardDefault.getKeyFrame(stateTime, true);
            } else if (i == 1 && game.assets.aureliusCardDefault != null && game.assets.aureliusCardSelected != null) {
                animFrame = sel ? game.assets.aureliusCardSelected.getKeyFrame(stateTime, true) :
                    game.assets.aureliusCardDefault.getKeyFrame(stateTime, true);
            } else if (i == 2 && game.assets.lyronCardDefault != null && game.assets.lyronCardSelected != null) {
                animFrame = sel ? game.assets.lyronCardSelected.getKeyFrame(stateTime, true) :
                    game.assets.lyronCardDefault.getKeyFrame(stateTime, true);
            } else {
                staticFrame = staticTexs[i];
            }

            if (animFrame != null) { game.batch.draw(animFrame, imageX, imageY, cardWidth, cardHeight);
            } else if (staticFrame != null) { game.batch.draw(staticFrame, imageX, imageY, cardWidth, cardHeight);}

            game.assets.font.getData().setScale(1.2f);
            game.assets.font.setColor(sel ? Color.YELLOW : Color.WHITE);
            game.assets.font.draw(game.batch, WEAPONS[i], textX, textY);
            game.assets.font.draw(game.batch, HP_VALS[i], textX, textY - px (1f));
            game.assets.font.setColor(Color.WHITE);
        }

        drawCenteredText(DESCS[index], 0, px(2.0f), Main.WORLD_WIDTH, px(1.6f),
            new Color(0.8f, 0.8f, 1f, 0.9f), 1.2f);

        drawCenteredText(
            "Press ESC to go back.",
            0, px(1.0f), Main.WORLD_WIDTH, px(1.6f), Color.GRAY, 1.2f);

        drawFadeOverlay();
        game.batch.end();
    }

    private void handleInput() {
        if (isFadingOut || isFadingIn) return; // Block input during fade transitions

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
        mousePos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        game.uiViewport.unproject(mousePos);

        for (int i = 0; i < 3; i++) {
            float hitX = cardDisplayX + (i * (cardWidth + cardGap));
            float hitY = cardDisplayY;
            float hitW = cardWidth;
            float hitH = cardHeight;

            if (mousePos.x >= hitX && mousePos.x <= hitX + hitW &&
                mousePos.y >= hitY && mousePos.y <= hitY + hitH) {
                index = i;
                if (Gdx.input.justTouched()) {
                    confirmSelection();
                }
            }
        }
    }

    private void confirmSelection() {
        if (isFadingOut) return;
        isFadingOut = true;

        if (game.assets.titleBGM != null && game.assets.titleBGM.isPlaying()) {
            game.assets.titleBGM.stop();
        }
        game.ctx.stopTheme();
    }

    //Centered Text Rendering
    private void drawCenteredText(
        String text, float areaX, float areaY, float areaWidth,
        float areaHeight, Color color, float scale) {

        game.assets.font.getData().setScale(scale);
        game.assets.font.setColor(color);
        float x = areaX + ((areaWidth - textWidth(text)) / 2f);
        float y = areaY + areaHeight / 2f;
        game.assets.font.draw(game.batch, text, x, y);
        game.assets.font.getData().setScale(1.0f);
    }

    private float textWidth(String text) {
        game.glyphLayout.setText(game.assets.font, text);
        return game.glyphLayout.width;
    }

    @Override public void resize(int w, int h) {
        game.uiViewport.update(w, h, true);
    }

    @Override public void hide() {
        clearNotes();
    }

    @Override public void dispose() {}
}
