package Screens.LeaderBoard;

import Screens.BaseScreen;
import Screens.MainMenuScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import io.github.Zephyrdoestech.GameContext;
import Screens.LeaderBoard.LeaderboardEntry;
import Screens.LeaderBoard.LeaderboardManager;
import io.github.Zephyrdoestech.Main;
import java.util.List;

public class LeaderboardScreen extends BaseScreen {

    // ── State ─────────────────────────────────────────────────────────────────

    /** All entries loaded from disk, already sorted. */
    private final List<LeaderboardEntry> entries;

    /** Total number of maps in the game (for displaying "X/3" format). */
    private final int totalMaps;

    // ── Constructor ───────────────────────────────────────────────────────────

    /**
     * @param game      the {@link Main} instance
     * @param totalMaps total map count (e.g. 3)
     */
    public LeaderboardScreen(Main game, int totalMaps) {
        super(game);
        this.entries   = LeaderboardManager.loadEntries();
        this.totalMaps = totalMaps;
    }

    // ── Static entry point (call this from death or completion) ──────────────

    /**
     * Prompts the user for their username, then saves their score to the
     * leaderboard and transitions to {@link LeaderboardScreen}.
     *
     * This is the method you call from:
     *  - CombatScreen (when player dies and lives == 0)
     *  - ExploringScreen (when player clears the final map)
     *
     * @param game      the {@link Main} instance
     * @param ctx       the current {@link GameContext} (contains playtime & maps cleared)
     * @param totalMaps total map count (e.g. 3)
     */
    public static void promptForUsername(final Main game, final GameContext ctx, final int totalMaps) {
        Gdx.input.getTextInput(new Input.TextInputListener() {
            @Override
            public void input(String text) {
                // User submitted a name
                String username = text.trim().isEmpty() ? "Unknown" : text.trim();

                // Create and save the entry
                LeaderboardEntry entry = new LeaderboardEntry(
                    username,
                    ctx.mapsCleared,
                    ctx.totalPlaytime
                );
                LeaderboardManager.addEntry(entry);

                // Show the leaderboard
                game.setScreen(new LeaderboardScreen(game, totalMaps));
            }

            @Override
            public void canceled() {
                // User pressed Cancel — still show leaderboard with "Unknown" as name
                LeaderboardEntry entry = new LeaderboardEntry(
                    "Unknown",
                    ctx.mapsCleared,
                    ctx.totalPlaytime
                );
                LeaderboardManager.addEntry(entry);
                game.setScreen(new LeaderboardScreen(game, totalMaps));
            }
        }, "Enter Your Name", "", "Your name here");
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

        // ── Background (reuse title screen texture at low brightness) ─────────
        if (game.assets.titleScreenTex != null) {
            game.batch.setColor(0.1f, 0.05f, 0.15f, 1f);
            game.batch.draw(game.assets.titleScreenTex, 0, 0, Main.WORLD_WIDTH, Main.WORLD_HEIGHT);
            game.batch.setColor(Color.WHITE);
        }

        // ── Title ─────────────────────────────────────────────────────────────
        game.assets.titleFont.setColor(Color.GOLD);
        game.assets.titleFont.draw(game.batch, "LEADERBOARD", 250f, 440f);
        game.assets.titleFont.setColor(Color.WHITE);

        game.assets.font.setColor(Color.LIGHT_GRAY);
        game.assets.font.draw(game.batch, "(Top " + LeaderboardManager.getMaxEntries() + ")", 350f, 410f);

        // ── Column headers ────────────────────────────────────────────────────
        float headerY = 370f;
        game.assets.font.setColor(Color.CYAN);
        game.assets.font.draw(game.batch, "Rank", 100f, headerY);
        game.assets.font.draw(game.batch, "Name", 200f, headerY);
        game.assets.font.draw(game.batch, "Maps", 450f, headerY);
        game.assets.font.draw(game.batch, "Time", 580f, headerY);

        // ── Divider line ──────────────────────────────────────────────────────
        game.assets.font.setColor(Color.DARK_GRAY);
        game.assets.font.draw(game.batch, "──────────────────────────────────────────────────────────", 95f, 360f);

        // ── Entries ───────────────────────────────────────────────────────────
        float startY = 340f;
        float lineHeight = 28f;

        if (entries.isEmpty()) {
            game.assets.font.setColor(Color.GRAY);
            game.assets.font.draw(game.batch, "No scores yet — be the first!", 240f, startY);
        } else {
            for (int i = 0; i < entries.size(); i++) {
                LeaderboardEntry e = entries.get(i);
                float y = startY - (i * lineHeight);

                // Rank number (1, 2, 3, ...)
                game.assets.font.setColor(i < 3 ? Color.YELLOW : Color.WHITE);
                game.assets.font.draw(game.batch, (i + 1) + ".", 105f, y);

                // Username
                game.assets.font.setColor(Color.WHITE);
                game.assets.font.draw(game.batch, e.username, 200f, y);

                // Maps cleared (e.g. "1/3")
                String mapsText = e.mapsCleared + "/" + totalMaps;
                game.assets.font.draw(game.batch, mapsText, 460f, y);

                // Time (formatted)
                game.assets.font.draw(game.batch, e.formatTime(), 590f, y);
            }
        }

        // ── Footer hint ───────────────────────────────────────────────────────
        game.assets.font.setColor(Color.GRAY);
        game.assets.font.draw(game.batch, "Press ENTER to return to main menu", 230f, 60f);
        game.assets.font.setColor(Color.WHITE);

        drawFadeOverlay();
        game.batch.end();

        // ── Input ─────────────────────────────────────────────────────────────
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MainMenuScreen(game));
        }
    }

    @Override public void resize(int w, int h) {
        game.uiViewport.update(w, h, true);
    }

    @Override public void hide()    {}
    @Override public void dispose() {}
}
