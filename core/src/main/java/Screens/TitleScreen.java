package Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import io.github.Zephyrdoestech.Main;
import com.badlogic.gdx.graphics.GL20;

/**
 * Shows the title screen image.
 * Press SPACE → transitions to MainMenuScreen.
 */
public class TitleScreen extends BaseScreen {

    public TitleScreen(Main game) { super(game); }

    @Override
    public void show() {
        Main.setMainVolume(1.0f);
        // Reset scales that might have been changed by other screens
        game.assets.font.getData().setScale(1.5f);
        game.assets.titleFont.getData().setScale(2.2f);

        // Reset cameras to centre for UI screens
        game.gameCamera.position.set(Main.WORLD_WIDTH / 2f, Main.WORLD_HEIGHT / 2f, 0);
        game.gameCamera.update();

        if (!game.assets.titleBGM.isPlaying()) {
            game.assets.titleBGM.setVolume(0.6f * Main.MainVolume);
            game.assets.titleBGM.play();
        }

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.gameViewport.apply();
        game.batch.setProjectionMatrix(game.gameCamera.combined);
        game.batch.begin();

        game.batch.setColor(Color.WHITE);
        game.batch.draw(game.assets.titleScreenTex, 0, 0, Main.WORLD_WIDTH, Main.WORLD_HEIGHT);

        drawFloatingNotes(delta);

        game.assets.font.setColor(new Color(0.85f, 0.85f, 0.85f, 0.9f));
        String prompt = "Press SPACE to continue";
        game.glyphLayout.setText(game.assets.font, prompt);
        float textX = (Main.WORLD_WIDTH - game.glyphLayout.width) / 2f;
        game.assets.font.draw(game.batch, prompt, textX, 60f);
        game.assets.font.setColor(Color.WHITE);

        game.batch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) ||
            Gdx.input.isKeyJustPressed(Input.Keys.ENTER) ||
            Gdx.input.justTouched()) {

            game.setScreen(new MainMenuScreen(game));
        }
    }

    @Override public void resize(int w, int h) {
        game.gameViewport.update(w, h, true);
    }
    @Override public void hide()    {
        clearNotes();
    }
    @Override public void dispose() {}
}
