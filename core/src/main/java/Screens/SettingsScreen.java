package Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import io.github.Zephyrdoestech.Main;

public class SettingsScreen extends BaseScreen {

    private final Vector3 mousePos = new Vector3();

    // Button Hitbox setup
    private final float btnWidth = 300f;
    private final float btnHeight = 60f;
    private final float btnX;
    private final float btnY;

    private boolean isHovering = false;

    public SettingsScreen(Main game) {
        super(game);

        // Center the button on the screen
        btnX = (Main.WORLD_WIDTH - btnWidth) / 2f;
        btnY = (Main.WORLD_HEIGHT - btnHeight) / 2f;
    }

    @Override
    public void show() {
        game.gameCamera.position.set(Main.WORLD_WIDTH / 2f, Main.WORLD_HEIGHT / 2f, 0);
        game.gameCamera.update();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1);
        Gdx.gl.glClear(com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT);

        handleInput();

        // 1. Draw the Button Background (Using ShapeRenderer so you don't need a new Texture)
        game.shapeRenderer.setProjectionMatrix(game.uiCamera.combined);
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Make the button slightly brighter if we are hovering over it
        if (isHovering) {
            game.shapeRenderer.setColor(0.3f, 0.3f, 0.4f, 1f);
        } else {
            game.shapeRenderer.setColor(0.2f, 0.2f, 0.3f, 1f);
        }

        game.shapeRenderer.rect(btnX, btnY, btnWidth, btnHeight);
        game.shapeRenderer.end();

        // 2. Draw the Text
        game.batch.setProjectionMatrix(game.uiCamera.combined);
        game.batch.begin();

        game.assets.titleFont.setColor(Color.CYAN);
        game.assets.titleFont.draw(game.batch, "SETTINGS", 310, 400);

        // Change text color to yellow on hover!
        game.assets.font.setColor(isHovering ? Color.YELLOW : Color.WHITE);

        // Check our context variable to see what text to display
        String controlText = game.ctx.useWasd ? "Controls: WASD" : "Controls: ARROWS";

        game.assets.font.draw(game.batch, controlText, btnX + 45, btnY + 40);

        game.assets.font.setColor(Color.GRAY);
        game.assets.font.draw(game.batch, "ESC to go back", 20, 40);

        game.batch.end();
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MainMenuScreen(game));
            return;
        }

        // --- MOUSE LOGIC ---
        mousePos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        game.uiCamera.unproject(mousePos);

        // Check if mouse is inside the button's boundaries
        if (mousePos.x >= btnX && mousePos.x <= btnX + btnWidth &&
            mousePos.y >= btnY && mousePos.y <= btnY + btnHeight) {

            isHovering = true; // Changes button color & text color in render()

            // If clicked, toggle the boolean!
            if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                game.ctx.useWasd = !game.ctx.useWasd; // Flips true to false, or false to true
            }
        } else {
            isHovering = false;
        }
    }

    @Override public void resize(int w, int h) { game.uiViewport.update(w, h, true); }
    @Override public void hide() {}
    @Override public void dispose() {}
}
