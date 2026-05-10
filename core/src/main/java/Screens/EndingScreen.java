package Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import io.github.Zephyrdoestech.GameContext;
import io.github.Zephyrdoestech.Main;

public class EndingScreen extends BaseScreen {

    private Texture endingTexture;
    private float stateTime = 0f;

    public EndingScreen(Main game) {
        super(game);

        GameContext.CharacterType character = game.ctx.selectedCharacter;
        if (character == GameContext.CharacterType.SONARA) {
            endingTexture = game.assets.sonaraEnding;
        } else if (character == GameContext.CharacterType.AURELIUS) {
            endingTexture = game.assets.aureliusEnding;
        } else if (character == GameContext.CharacterType.LYRON) {
            endingTexture = game.assets.lyronEnding;
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
        }
        game.batch.end();

        drawFadeOverlay();

        if (!fadingOut && stateTime > 2f && (Gdx.input.justTouched() || Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY))) {
            startFadeOut(new CreditsScreen(game, true));
        }
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
