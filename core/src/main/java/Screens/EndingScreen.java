package Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Align;
import io.github.Zephyrdoestech.GameContext;
import io.github.Zephyrdoestech.Main;

public class EndingScreen extends BaseScreen {

    private Texture endingTexture;
    private float stateTime = 0f;

    // Text Render Variables
    private int endingCharIndex = 0;
    private float endingTimer = 0f;
    private String endingText = "";
    private static final float ENDING_TYPE_SPEED = 0.03f;

    private float textMarginX = px(2.0f);
    private float textMarginY = px(10.0f);
    private float textX = screenLeft + textMarginX;
    private float textY = screenTop - textMarginY;
    private float textWidth = Main.WORLD_WIDTH - (2 * textMarginX);

    // Ending Text
    private String[] endingTexts = {
        "In that moment of glorious noise, the fierce aggression that had fueled her for so long vanishes," +
        " replaced by a profound, sudden peace. She realized she had not just taken revenge, " +
        "but had given her parents the destruction of the force that silenced them and the promise of a peaceful," +
        " noisy life for those who remained. She dedicates her life to ensuring that peace endures, " +
        "becoming a peace advocate and a traveling musician who uses her rhythmic melodies to heal the scars of the long silence," +
        " guiding humanity toward harmony in the newly loud world.",

        "A wave of restorative sound washes over the world, " +
        "bringing with it not just noise but the sounds of honest complaint, laughter, and solidarity. " +
        "Aurelius feels the burden of his old shame lift; he has paid his debt not with suffering, " +
        "but with courage. His final thought: \"The shield is down, and the world is louder for it. " +
        "The noise will protect us now, because it is ours to share.\"" +
        "Aurelius becomes the Moral Compass of the new era. He uses his Flute to lead, " +
        "playing melodies of consensus and unity, ensuring the noise-filled future is built on justice and equality, " +
        "never again allowing systemic silence to foster cruelty and apathy.",

        "Lyron defeats the Maestro Syozan, not through rage, but by accepting his guilt and embracing his flawed strength. " +
        "As sound returns in a gentle wave, he finally allows himself to grieve, placing the Harp quietly beside him. " +
        "His vengeance ends, replaced by acceptance. His final thought: " +
        "\"They are avenged. And I am no longer weak. I am the one who lived, and I will be the one who remembers.\"\n" +
        "Afterward, Lyron becomes a Custodian of Memory, traveling the restored world and using his music not for battle, " +
         "but to help others overcome fear and guilt through soft, comforting melodies.\n"
    };


    public EndingScreen(Main game) {
        super(game);

        GameContext.CharacterType character = game.ctx.selectedCharacter;
        if (character == GameContext.CharacterType.SONARA) {
            endingTexture = game.assets.sonaraEnding;
            endingText = endingTexts[0];
        } else if (character == GameContext.CharacterType.AURELIUS) {
            endingTexture = game.assets.aureliusEnding;
            endingText = endingTexts[1];
        } else if (character == GameContext.CharacterType.LYRON) {
            endingTexture = game.assets.lyronEnding;
            endingText = endingTexts[2];
        }
    }

    @Override
    public void show() {
        startFadeIn();
    }

    @Override
    public void render(float delta) {
        if (updateFade(delta)) return;

        stateTime += delta;

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(game.uiCamera.combined);
        game.batch.begin();
        if (endingTexture != null) {
            game.batch.draw(endingTexture, 0, 0, Main.WORLD_WIDTH, Main.WORLD_HEIGHT);
        }

        if (stateTime > 2f) { // wait a bit before showing prompt
            game.assets.font.setColor(new Color(1f, 1f, 1f, (float)Math.abs(Math.sin(stateTime * 2))));
            game.glyphLayout.setText(game.assets.font, "Press ANY KEY to continue");
            float x = (Main.WORLD_WIDTH - game.glyphLayout.width) / 2f;
            game.assets.font.draw(game.batch, game.glyphLayout, x, 50f);
            game.assets.font.setColor(Color.WHITE);

            updateLoreText(delta);
            drawLoreText(textX, textY, textWidth, Align.left, 1.0f);

        }
        game.batch.end();

        drawFadeOverlay();

        if (!fadingOut && stateTime > 2f && (Gdx.input.justTouched() || Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY))) {
            startFadeOut(new CreditsScreen(game, true));
        }
    }

    // Text Rendering

    private void updateLoreText(float delta) {
        endingTimer += delta;

        while (endingTimer >= ENDING_TYPE_SPEED && endingCharIndex < endingText.length()) {
            endingTimer -= ENDING_TYPE_SPEED;
            endingCharIndex++;
        }
    }

    private void drawLoreText(
        float x,
        float y,
        float width,
        int alignment,
        float scale
    ) {

        int displayLen = Math.min(endingCharIndex, endingText.length());
        String textToDisplay = endingText.substring(0, displayLen);

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


    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {}
}
