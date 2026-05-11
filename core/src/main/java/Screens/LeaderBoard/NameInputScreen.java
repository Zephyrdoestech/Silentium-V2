package Screens.LeaderBoard;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import io.github.Zephyrdoestech.GameContext;
import io.github.Zephyrdoestech.Main;

// IMPORTANT: Make sure to import your LeaderboardManager and LeaderboardEntry here!

public class NameInputScreen implements Screen {
    private final Main game;
    private final GameContext ctx;
    private final int totalMaps;

    private String currentName = "";
    private float blinkTimer = 0f;
    private boolean showCursor = true;

    public NameInputScreen(Main game, GameContext ctx, int totalMaps) {
        this.game = game;
        this.ctx = ctx;
        this.totalMaps = totalMaps;
    }

    @Override
    public void show() {
        // This listens to your raw keyboard inputs!
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyTyped(char character) {
                // If they press ENTER
                if (character == '\r' || character == '\n') {
                    submitName();
                    return true;
                }
                // If they press BACKSPACE
                if (character == '\b' && currentName.length() > 0) {
                    currentName = currentName.substring(0, currentName.length() - 1);
                    return true;
                }
                // Limit to 12 characters, accept letters, numbers, and spaces
                if (currentName.length() < 12 && (Character.isLetterOrDigit(character) || character == ' ')) {
                    currentName += character;
                    return true;
                }
                return false;
            }
        });
    }

    private void submitName() {
        // If they just hit enter without typing anything, default to Unknown
        String finalName = currentName.trim().isEmpty() ? "Unknown" : currentName.trim();

        // 1. Save it to your Leaderboard system
        // (Make sure your LeaderboardEntry and LeaderboardManager are imported at the top!)
        LeaderboardEntry entry = new LeaderboardEntry(finalName, ctx.mapsCleared, ctx.totalPlaytime);
        LeaderboardManager.addEntry(entry);

        // 2. Go to the beautiful Leaderboard Screen!
        game.setScreen(new Screens.LeaderBoard.LeaderboardScreen(game, totalMaps));
    }

    @Override
    public void render(float delta) {
        // Dark background to match the leaderboard
        Gdx.gl.glClearColor(0.02f, 0.02f, 0.08f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        blinkTimer += delta;
        if (blinkTimer > 0.5f) {
            showCursor = !showCursor;
            blinkTimer = 0f;
        }

        game.batch.setProjectionMatrix(game.uiCamera.combined);
        game.batch.begin();

        // 1. Draw your custom Input Panel
        float bgWidth = 500f;
        float bgHeight = 200f;
        float bgX = (Main.WORLD_WIDTH - bgWidth) / 2f;
        float bgY = (Main.WORLD_HEIGHT - bgHeight) / 2f;

        if (game.assets.nameInputPanelBG != null) {
            game.batch.setColor(Color.WHITE);
            game.batch.draw(game.assets.nameInputPanelBG, bgX, bgY, bgWidth, bgHeight);
        }

        // 2. Draw the typed name with scaling
        game.assets.titleFont.setColor(Color.WHITE);

        // --- START SCALE ---
        game.assets.titleFont.getData().setScale(1.15f); // Adjust this (0.5f to 1.0f) for size

        String displayString = currentName + (showCursor ? "_" : " ");
        game.glyphLayout.setText(game.assets.titleFont, displayString);

        // Centering the text inside the dark inset box of your asset
        float textY = bgY + 82f;
        game.assets.titleFont.draw(game.batch, displayString, (Main.WORLD_WIDTH - game.glyphLayout.width) / 2f, textY);

        // --- RESET SCALE (CRITICAL!) ---
        game.assets.titleFont.getData().setScale(2.2f); // Set back to 2.2f instead of 1.0f
        // -------------------------------

        // 3. Draw the Helper Footer underneath the panel
        game.assets.font.setColor(Color.GRAY);
        String hint = "Press ENTER to confirm";
        game.glyphLayout.setText(game.assets.font, hint);
        game.assets.font.draw(game.batch, hint, (Main.WORLD_WIDTH - game.glyphLayout.width) / 2f, bgY - 30f);

        game.batch.end();
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}

    @Override
    public void hide() {
        // ALWAYS clean up the input processor when leaving the screen!
        Gdx.input.setInputProcessor(null);
    }

    @Override public void dispose() {}
}
