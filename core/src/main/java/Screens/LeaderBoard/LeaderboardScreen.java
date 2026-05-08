package Screens.LeaderBoard;

import Screens.BaseScreen;
import Screens.MainMenuScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import io.github.Zephyrdoestech.GameContext;
import io.github.Zephyrdoestech.Main;
import java.util.List;

public class LeaderboardScreen extends BaseScreen {

    // ── State ─────────────────────────────────────────────────────────────────

    /** All entries loaded from disk, already sorted. */
    private final List<LeaderboardEntry> entries;

    /** Total number of maps in the game (for displaying "X/3" format). */
    private final int totalMaps;

    /** Timer to prevent "Input Bleed" from the NameInputScreen */
    private float screenTimer = 0f;

    // ── Constructor ───────────────────────────────────────────────────────────

    public LeaderboardScreen(Main game, int totalMaps) {
        super(game);
        this.entries   = LeaderboardManager.loadEntries();
        this.totalMaps = totalMaps;
    }

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    @Override
    public void show() {
        game.gameCamera.zoom = 1.0f;
        game.gameCamera.position.set(Main.WORLD_WIDTH / 2f, Main.WORLD_HEIGHT / 2f, 0);
        game.gameCamera.update();
        startFadeIn();
    }

    // ── Render ────────────────────────────────────────────────────────────────

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.02f, 0.02f, 0.08f, 1f);
        Gdx.gl.glClear(com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT);

        updateFade(delta);

        game.batch.setProjectionMatrix(game.uiCamera.combined);
        game.batch.begin();

        // 1. Draw your custom background panel centered on the screen
        float bgWidth = 700f;
        float bgHeight = 400f;
        float bgX = (Main.WORLD_WIDTH - bgWidth) / 2f;
        float bgY = (Main.WORLD_HEIGHT - bgHeight) / 2f;

        if (game.assets.leaderboardPanelBG != null) {
            game.batch.setColor(Color.WHITE);
            game.batch.draw(game.assets.leaderboardPanelBG, bgX, bgY, bgWidth, bgHeight);
        }

        // 2. Draw the Title above the panel (Nudged up to +90f)
        game.assets.titleFont.setColor(Color.GOLD);
        game.glyphLayout.setText(game.assets.titleFont, "LEADERBOARD");
        game.assets.titleFont.draw(game.batch, "LEADERBOARD", (Main.WORLD_WIDTH - game.glyphLayout.width) / 2f, bgY + bgHeight + 90f);
        game.assets.titleFont.setColor(Color.WHITE);

        // 3. Draw the Entries aligned to your custom image columns
        float rankX = bgX + 120f;
        float nameX = bgX + 210f;
        float levelX = bgX + 350f;
        float timeX = bgX + 510f;

        float startY = bgY + bgHeight - 160f;
        float lineHeight = 21f; // Squished to comfortably fit 10 scores

        if (entries.isEmpty()) {
            game.assets.font.setColor(Color.GRAY);
            game.assets.font.draw(game.batch, "No scores yet — be the first!", bgX + 220f, startY);
        } else {
            for (int i = 0; i < entries.size(); i++) {
                LeaderboardEntry e = entries.get(i);
                float y = startY - (i * lineHeight);

                // Rank number (1, 2, 3, ...)
                game.assets.font.setColor(i < 3 ? Color.YELLOW : Color.WHITE);
                game.assets.font.draw(game.batch, (i + 1) + ".", rankX, y);

                // Username
                game.assets.font.setColor(Color.WHITE);
                game.assets.font.draw(game.batch, e.username, nameX, y);

                // Maps cleared
                String mapsText = e.mapsCleared + "/" + totalMaps;
                game.assets.font.draw(game.batch, mapsText, levelX, y);

                // Time (formatted)
                game.assets.font.draw(game.batch, e.formatTime(), timeX, y);
            }
        }

        // ── Footer hint (Moved dynamically below the panel) ───────────────────
        game.assets.font.setColor(Color.GRAY);
        game.glyphLayout.setText(game.assets.font, "Press ENTER to return to main menu");
        game.assets.font.draw(game.batch, "Press ENTER to return to main menu", (Main.WORLD_WIDTH - game.glyphLayout.width) / 2f, bgY - 30f);
        game.assets.font.setColor(Color.WHITE);

        drawFadeOverlay();
        game.batch.end();

        // ── Input ─────────────────────────────────────────────────────────────
        screenTimer += delta; // Count how long the screen has been open

        // ONLY allow exit if the screen has been open for half a second!
        if (screenTimer > 0.5f) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                game.assets.stopAllMusic();
                game.setScreen(new MainMenuScreen(game));
            }
        }
    }

    @Override public void resize(int w, int h) {
        game.uiViewport.update(w, h, true);
    }

    @Override public void hide() {
        game.assets.stopAllMusic();
    }

    @Override public void dispose() {}
}
