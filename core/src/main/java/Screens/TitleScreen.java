package Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import io.github.Zephyrdoestech.Main;

/**
 * Shows the title screen image.
 * Press SPACE → transitions to MainMenuScreen.
 */
public class TitleScreen extends BaseScreen {

    public TitleScreen(Main game) { super(game); }

    @Override
    public void show() {
        // Reset cameras to centre for UI screens
        game.gameCamera.position.set(Main.WORLD_WIDTH / 2f, Main.WORLD_HEIGHT / 2f, 0);
        game.gameCamera.update();
    }

    @Override
    public void render(float delta) {
        game.batch.setProjectionMatrix(game.gameCamera.combined);
        game.batch.begin();

        game.batch.setColor(Color.WHITE);
        game.batch.draw(game.assets.titleScreenTex, 0, 0, Main.WORLD_WIDTH, Main.WORLD_HEIGHT);

        // Prompt
        game.assets.font.setColor(new Color(0.85f, 0.85f, 0.85f, 0.9f));
        game.assets.font.draw(game.batch, "Press SPACE to continue",
            290, 60);
        game.assets.font.setColor(Color.WHITE);

        game.batch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            game.setScreen(new MainMenuScreen(game));
        }
    }

    @Override public void resize(int w, int h) {}
    @Override public void hide()    {}
    @Override public void dispose() {}
}
