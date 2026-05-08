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

    public CreditsScreen(Main game) {
        super(game);
    }

    @Override
    public void show() {
        startFadeIn();

        // Center the camera just like your other screens!
        game.gameCamera.position.set(Main.WORLD_WIDTH / 2f, Main.WORLD_HEIGHT / 2f, 0);
        game.gameCamera.update();

        videoPlayer = VideoPlayerCreator.createVideoPlayer();

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
    }

    @Override
    public void render(float delta) {
        updateFade(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (isLoaded && videoPlayer != null) {
            videoPlayer.update();
            Texture frame = videoPlayer.getTexture();

            if (frame != null) {
                // Tell the batch to use your centered camera
                game.batch.setProjectionMatrix(game.gameCamera.combined);
                game.batch.begin();

                // Dynamic Centering Math (Keeps aspect ratio perfect)
                float videoWidth = videoPlayer.getVideoWidth();
                float videoHeight = videoPlayer.getVideoHeight();

                if (videoWidth > 0 && videoHeight > 0) {
                    float scale = Math.min(Main.WORLD_WIDTH / videoWidth, Main.WORLD_HEIGHT / videoHeight);
                    float drawWidth = videoWidth * scale;
                    float drawHeight = videoHeight * scale;
                    float drawX = (Main.WORLD_WIDTH - drawWidth) / 2f;
                    float drawY = (Main.WORLD_HEIGHT - drawHeight) / 2f;

                    // Draw the perfectly centered frame
                    game.batch.draw(frame, drawX, drawY, drawWidth, drawHeight);
                }

                drawFadeOverlay();
                game.batch.end();
            }

            if (!videoPlayer.isPlaying()) {
                exitToMenu();
            }
        }

        // Allow skipping
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            exitToMenu();
        }
    }

    private void exitToMenu() {
        game.setScreen(new MainMenuScreen(game));
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

    // Required by BaseScreen
    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}
}
