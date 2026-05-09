package Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import io.github.Zephyrdoestech.Main;

// ─────────────────────────────────────────────────────────────────────────────
// HowToPlayScreen
// ─────────────────────────────────────────────────────────────────────────────

class HowToPlayScreen extends BaseScreen {
    private int currentTutorialIndex = 0;
    private final Texture[] tutorialScreens;

    HowToPlayScreen(Main game) {
        super(game);
        // Load the tutorial screens from assets
        tutorialScreens = game.assets.tutorials;
    }

    @Override public void show() {
        game.gameCamera.position.set(Main.WORLD_WIDTH / 2f, Main.WORLD_HEIGHT / 2f, 0);
        game.gameCamera.update();
        startFadeIn();
    }

    @Override public void render(float delta) {
        updateFade(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(game.gameCamera.combined);
        game.batch.begin();

        if (tutorialScreens != null && tutorialScreens.length > 0 && currentTutorialIndex < tutorialScreens.length) {
            Texture currentTex = tutorialScreens[currentTutorialIndex];
            if (currentTex != null) {
                // Draw the tutorial image filling the screen
                game.batch.setColor(Color.WHITE);
                game.batch.draw(currentTex, 0, 0, Main.WORLD_WIDTH, Main.WORLD_HEIGHT);
            }

            // Draw navigation hints over the image
            game.assets.font.setColor(Color.GRAY);
            game.assets.font.draw(game.batch, "Press ESC to go back", 20, 40);

            if (currentTutorialIndex < tutorialScreens.length - 1) {
                game.assets.font.draw(game.batch, "Press RIGHT/D for Next", Main.WORLD_WIDTH - 250, 40);
            }
            if (currentTutorialIndex > 0) {
                game.assets.font.draw(game.batch, "Press LEFT/A for Prev", Main.WORLD_WIDTH - 500, 40);
            }
            game.assets.font.setColor(Color.WHITE);
        } else {
            // Fallback text if images are missing
            game.assets.font.setColor(Color.CYAN);
            game.assets.font.draw(game.batch, "HOW TO PLAY", 320, 420);
            game.assets.font.setColor(Color.WHITE);
            game.assets.font.draw(game.batch, "WASD / Arrow Keys  – Move on the map",             100, 340);
            game.assets.font.draw(game.batch, "Walk into an enemy – Trigger combat",              100, 300);
            game.assets.font.draw(game.batch, "In combat: press A–G to enter 3 notes",            100, 260);
            game.assets.font.draw(game.batch, "Notes resolve automatically after 3 are entered", 100, 220);
            game.assets.font.draw(game.batch, "3-note chords grant healing, shields, or buffs",  100, 180);
            game.assets.font.draw(game.batch, "ESC – Return to Menu",                              100, 140);
            game.assets.font.setColor(Color.GRAY);
            game.assets.font.draw(game.batch, "Press ESC to go back", 290, 60);
            game.assets.font.setColor(Color.WHITE);
        }

        drawFadeOverlay();
        game.batch.end();

        // Navigation Input
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MainMenuScreen(game));
        } else if ((Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) || Gdx.input.isKeyJustPressed(Input.Keys.D)) && currentTutorialIndex < tutorialScreens.length - 1) {
            currentTutorialIndex++;
        } else if ((Gdx.input.isKeyJustPressed(Input.Keys.LEFT) || Gdx.input.isKeyJustPressed(Input.Keys.A)) && currentTutorialIndex > 0) {
            currentTutorialIndex--;
        }
    }

    @Override public void resize(int w, int h) {}
    @Override public void hide()    {}
    @Override public void dispose() {}
}

// ─────────────────────────────────────────────────────────────────────────────
// StoryScreen
// ─────────────────────────────────────────────────────────────────────────────

class StoryScreen extends BaseScreen {
    private int currentStoryIndex = 0;
    private final Texture[] storyScreens;

    StoryScreen(Main game) {
        super(game);
        storyScreens = new Texture[]{
            game.assets.story1Tex,
            game.assets.story2Tex,
            game.assets.story3Tex,
            game.assets.story4Tex
        };
    }

    @Override public void show() {
        game.gameCamera.position.set(Main.WORLD_WIDTH / 2f, Main.WORLD_HEIGHT / 2f, 0);
        game.gameCamera.update();
        startFadeIn();
    }

    @Override public void render(float delta) {
        updateFade(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(game.gameCamera.combined);
        game.batch.begin();

        if (storyScreens != null && storyScreens.length > 0 && currentStoryIndex < storyScreens.length) {
            Texture currentTex = storyScreens[currentStoryIndex];
            if (currentTex != null) {
                // Draw the story image filling the screen
                game.batch.setColor(Color.WHITE);
                game.batch.draw(currentTex, 0, 0, Main.WORLD_WIDTH, Main.WORLD_HEIGHT);
            }

            // Draw navigation hints over the image
            game.assets.font.setColor(Color.GRAY);
            game.assets.font.draw(game.batch, "Press ESC to go back", 20, 40);

            if (currentStoryIndex < storyScreens.length - 1) {
                game.assets.font.draw(game.batch, "Press RIGHT/D for Next", Main.WORLD_WIDTH - 250, 40);
            }
            if (currentStoryIndex > 0) {
                game.assets.font.draw(game.batch, "Press LEFT/A for Prev", Main.WORLD_WIDTH - 500, 40);
            }
            game.assets.font.setColor(Color.WHITE);
        } else {
            game.assets.font.setColor(Color.CYAN);
            game.assets.font.draw(game.batch, "THE LORE OF SILENTIUM", 220, 420);
            game.assets.font.setColor(Color.WHITE);
            game.assets.font.draw(game.batch, "The world fell silent to let a guardian rest.", 50, 340);
            game.assets.font.draw(game.batch, "But silence birthed Shadow Beasts.",              50, 300);
            game.assets.font.draw(game.batch, "A lone bell has shattered the quiet...",         50, 260);
            game.assets.font.draw(game.batch, "Sound is your only weapon now.",                  50, 220);
            game.assets.font.setColor(Color.GRAY);
            game.assets.font.draw(game.batch, "Press ESC to go back", 290, 60);
            game.assets.font.setColor(Color.WHITE);
        }

        drawFadeOverlay();
        game.batch.end();

        // Navigation Input
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MainMenuScreen(game));
        } else if ((Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) || Gdx.input.isKeyJustPressed(Input.Keys.D)) && currentStoryIndex < storyScreens.length - 1) {
            currentStoryIndex++;
        } else if ((Gdx.input.isKeyJustPressed(Input.Keys.LEFT) || Gdx.input.isKeyJustPressed(Input.Keys.A)) && currentStoryIndex > 0) {
            currentStoryIndex--;
        }
    }

    @Override public void resize(int w, int h) {}
    @Override public void hide()    {}
    @Override public void dispose() {}
}
