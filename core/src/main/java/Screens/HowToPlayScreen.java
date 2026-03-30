package Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import io.github.Zephyrdoestech.Main;

// ─────────────────────────────────────────────────────────────────────────────
// HowToPlayScreen
// ─────────────────────────────────────────────────────────────────────────────

class HowToPlayScreen extends BaseScreen {
    HowToPlayScreen(Main game) { super(game); }

    @Override public void show() {
        game.gameCamera.position.set(Main.WORLD_WIDTH / 2f, Main.WORLD_HEIGHT / 2f, 0);
        game.gameCamera.update();
        startFadeIn();
    }

    @Override public void render(float delta) {
        updateFade(delta);
        game.batch.setProjectionMatrix(game.gameCamera.combined);
        game.batch.begin();

        game.assets.font.setColor(Color.CYAN);
        game.assets.font.draw(game.batch, "HOW TO PLAY", 320, 420);
        game.assets.font.setColor(Color.WHITE);
        game.assets.font.draw(game.batch, "WASD / Arrow Keys  – Move on the map",            100, 340);
        game.assets.font.draw(game.batch, "Walk into an enemy – Trigger combat",             100, 300);
        game.assets.font.draw(game.batch, "In combat: press A–G to enter 3 notes",           100, 260);
        game.assets.font.draw(game.batch, "Notes resolve automatically after 3 are entered", 100, 220);
        game.assets.font.draw(game.batch, "3-note chords grant healing, shields, or buffs",  100, 180);
        game.assets.font.draw(game.batch, "ESC – Return to Menu",                             100, 140);
        game.assets.font.setColor(Color.GRAY);
        game.assets.font.draw(game.batch, "Press ESC to go back", 290, 60);
        game.assets.font.setColor(Color.WHITE);

        drawFadeOverlay();
        game.batch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE))
            game.setScreen(new MainMenuScreen(game));
    }

    @Override public void resize(int w, int h) {}
    @Override public void hide()    {}
    @Override public void dispose() {}
}

// ─────────────────────────────────────────────────────────────────────────────
// StoryScreen
// ─────────────────────────────────────────────────────────────────────────────

class StoryScreen extends BaseScreen {
    StoryScreen(Main game) { super(game); }

    @Override public void show() {
        game.gameCamera.position.set(Main.WORLD_WIDTH / 2f, Main.WORLD_HEIGHT / 2f, 0);
        game.gameCamera.update();
        startFadeIn();
    }

    @Override public void render(float delta) {
        updateFade(delta);
        game.batch.setProjectionMatrix(game.gameCamera.combined);
        game.batch.begin();

        game.assets.font.setColor(Color.CYAN);
        game.assets.font.draw(game.batch, "THE LORE OF SILENTIUM", 220, 420);
        game.assets.font.setColor(Color.WHITE);
        game.assets.font.draw(game.batch, "The world fell silent to let a guardian rest.", 50, 340);
        game.assets.font.draw(game.batch, "But silence birthed Shadow Beasts.",             50, 300);
        game.assets.font.draw(game.batch, "A lone bell has shattered the quiet...",         50, 260);
        game.assets.font.draw(game.batch, "Sound is your only weapon now.",                 50, 220);
        game.assets.font.setColor(Color.GRAY);
        game.assets.font.draw(game.batch, "Press ESC to go back", 290, 60);
        game.assets.font.setColor(Color.WHITE);

        drawFadeOverlay();
        game.batch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE))
            game.setScreen(new MainMenuScreen(game));
    }

    @Override public void resize(int w, int h) {}
    @Override public void hide()    {}
    @Override public void dispose() {}
}

// ─────────────────────────────────────────────────────────────────────────────
// CreditsScreen
// ─────────────────────────────────────────────────────────────────────────────

class CreditsScreen extends BaseScreen {
    CreditsScreen(Main game) { super(game); }

    @Override public void show() {
        game.gameCamera.position.set(Main.WORLD_WIDTH / 2f, Main.WORLD_HEIGHT / 2f, 0);
        game.gameCamera.update();
        startFadeIn();
    }

    @Override public void render(float delta) {
        updateFade(delta);
        game.batch.setProjectionMatrix(game.gameCamera.combined);
        game.batch.begin();

        game.assets.font.setColor(Color.CYAN);
        game.assets.font.draw(game.batch, "CREDITS", 340, 420);
        game.assets.font.setColor(Color.WHITE);
        game.assets.font.draw(game.batch, "Lead Developer  : Zephyrdoestech", 180, 340);
        game.assets.font.draw(game.batch, "Art & Story     : Silentium Team",  180, 300);
        game.assets.font.draw(game.batch, "Engine          : LibGDX",          180, 260);
        game.assets.font.setColor(Color.GRAY);
        game.assets.font.draw(game.batch, "Press ESC to go back", 290, 60);
        game.assets.font.setColor(Color.WHITE);

        drawFadeOverlay();
        game.batch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE))
            game.setScreen(new MainMenuScreen(game));
    }

    @Override public void resize(int w, int h) {}
    @Override public void hide()    {}
    @Override public void dispose() {}
}
