package Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.github.Zephyrdoestech.GameContext;
import io.github.Zephyrdoestech.Main;

public class CharacterLoreScreen extends BaseScreen {

    private Animation<TextureRegion> loreAnimation;
    private float stateTime = 0f;

    public CharacterLoreScreen(Main game) {
        super(game);

        GameContext.CharacterType character = game.ctx.selectedCharacter;
        if (character == GameContext.CharacterType.SONARA) {
            loreAnimation = game.assets.sonaraLore;
        } else if (character == GameContext.CharacterType.AURELIUS) {
            loreAnimation = game.assets.aureliusLore;
        } else if (character == GameContext.CharacterType.LYRON) {
            loreAnimation = game.assets.lyronLore;
        } else {
            loreAnimation = null; // Default or error handling
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
        if (loreAnimation != null) {
            TextureRegion currentFrame = loreAnimation.getKeyFrame(stateTime, true);
            game.batch.draw(currentFrame, 0, 0, Main.WORLD_WIDTH, Main.WORLD_HEIGHT);

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
            startFadeOut(new TownOfEchoesScreen(game));
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
