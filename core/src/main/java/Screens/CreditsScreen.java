package Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.video.VideoPlayer;
import com.badlogic.gdx.video.VideoPlayerCreator;
import io.github.Zephyrdoestech.Main;
import java.io.FileNotFoundException;

public class CreditsScreen extends BaseScreen {
    private VideoPlayer videoPlayer;
    private boolean isLoaded = false;
    private boolean fromEnding = false;
    private boolean loadingStarted = false;

    public CreditsScreen(Main game) {
        super(game);
    }

    public CreditsScreen(Main game, boolean fromEnding) {
        super(game);
        this.fromEnding = fromEnding;
    }

    @Override
    public void show() {
        startFadeIn();

        // Make the fade transition nice and fast
        this.fadeSpeed = 4.0f;

        // CRITICAL FIX: Reset the zoom! ExploringScreen sets this to 0.6f,
        // which causes the extreme stretching if not reset to 1.0f!
        game.gameCamera.zoom = 1.0f;
        game.gameCamera.position.set(Main.WORLD_WIDTH / 2f, Main.WORLD_HEIGHT / 2f, 0);
        game.gameCamera.update();

        videoPlayer = VideoPlayerCreator.createVideoPlayer();

        // Load asynchronously so the screen transitions immediately without a harsh freeze
        Gdx.app.postRunnable(() -> {
            try {
                videoPlayer.load(Gdx.files.internal("credits.webm"));
                videoPlayer.play();

                // MUTE THE VIDEO! (0f is 0%, 1f is 100%)
                videoPlayer.setVolume(0f);

                isLoaded = true;
            } catch (FileNotFoundException e) {
                Gdx.app.error("CREDITS", "Video file not found! Skipping to menu.");
                exitToMenu();
            }
        });
    }

    @Override
    public void render(float delta) {
        updateFade(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.gameViewport.apply();
        game.batch.setProjectionMatrix(game.gameCamera.combined);

        if (isLoaded && videoPlayer != null) {
            videoPlayer.update();
            Texture frame = videoPlayer.getTexture();

            if (frame != null) {
                game.batch.begin();

                // IMPORTANT: Reset the batch color to WHITE!
                // ExploringScreen might have left the batch color darkened (e.g. for fading/overlays)
                // which causes the entire video to be drawn with a dark tint if we don't reset it.
                game.batch.setColor(com.badlogic.gdx.graphics.Color.WHITE);

                // EXACTLY COPYING THE ORIGINAL MATH FROM GIT COMMIT:
                // Dynamic Centering Math (Keeps aspect ratio perfect)
                float videoWidth = videoPlayer.getVideoWidth();
                float videoHeight = videoPlayer.getVideoHeight();

                if (videoWidth > 0 && videoHeight > 0) {
                    float scale = Math.min(Main.WORLD_WIDTH / videoWidth, Main.WORLD_HEIGHT / videoHeight);
                    float drawWidth = videoWidth * scale;
                    float drawHeight = videoHeight * scale;
                    float drawX = (Main.WORLD_WIDTH - drawWidth) / 2f;
                    float drawY = (Main.WORLD_HEIGHT - drawHeight) / 2f;

                    // Draw the perfectly centered frame exactly as it originally was
                    game.batch.draw(frame, drawX, drawY, drawWidth, drawHeight);
                }

                drawFadeOverlay();
                game.batch.end();
            }

            if (!videoPlayer.isPlaying()) {
                exitToMenu();
            }
        } else {
            // Fallback drawing if video isn't ready
            game.batch.begin();
            drawFadeOverlay();
            game.batch.end();
        }

        // Allow skipping
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            exitToMenu();
        }
    }

    private void exitToMenu() {
        if (fromEnding) {
            game.setScreen(new Screens.LeaderBoard.NameInputScreen(game, game.ctx, 3));
        } else {
            game.setScreen(new MainMenuScreen(game));
        }
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        if (videoPlayer != null) {
            videoPlayer.dispose();
            videoPlayer = null;
        }
    }

    @Override
    public void resize(int width, int height) {
        game.gameViewport.update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}
}
