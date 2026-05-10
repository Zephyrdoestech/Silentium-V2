package Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import io.github.Zephyrdoestech.GameContext;
import io.github.Zephyrdoestech.Main;

import java.util.List;

public class LoadGameScreen extends BaseScreen {
    private com.badlogic.gdx.math.Vector3 mousePos = new com.badlogic.gdx.math.Vector3();

    private List<String> saveSlots;
    private int selection = 0;
    private float cursorTime = 0f;

    // Panel and slot dimensions
    private float panelWidth = 600f;
    private float panelHeight = 150f; // FORCED TO 150 TO BE A BANNER
    private float slotWidth = 450f;
    private float slotHeight = 80f;

    public LoadGameScreen(Main game) {
        super(game);
        saveSlots = game.ctx.getAllSaveSlots();
    }

    @Override
    public void show() {
        game.gameCamera.zoom = 1.0f;
        game.gameCamera.position.set(Main.WORLD_WIDTH / 2f, Main.WORLD_HEIGHT / 2f, 0);
        game.gameCamera.update();
        startFadeIn();

        if (game.assets.titleBGM != null && !game.assets.titleBGM.isPlaying()) {
            game.assets.titleBGM.setVolume(0.6f);
            game.assets.titleBGM.play();
        }

        // 1. Make the panel act like a wide header banner
        panelWidth = 400f;
        panelHeight = 100f;

        // 2. Set the save slot buttons
        slotWidth = 350f;
        slotHeight = 55f;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT);

        game.gameViewport.apply();
        cursorTime += delta;
        updateFade(delta);

        game.batch.setProjectionMatrix(game.gameCamera.combined);
        game.batch.begin();

        // 1. Background
        game.batch.setColor(Color.WHITE);
        game.batch.draw(game.assets.mainMenuBG, 0, 0, Main.WORLD_WIDTH, Main.WORLD_HEIGHT);

        // 2. Panel (Anchored to the top)
        float panelX = (Main.WORLD_WIDTH - panelWidth) / 2f;
        float panelY = Main.WORLD_HEIGHT - panelHeight - 40f;

        if (game.assets.loadGamePanelTex != null) {
            game.batch.draw(game.assets.loadGamePanelTex, panelX, panelY, panelWidth, panelHeight);
        }

        // Return button text
//        game.assets.font.setColor(Color.GRAY);
//        game.assets.font.draw(game.batch, "Press ESC to return", 20f, 40f);

        // Calculate layout properties for slots (Pushed down below the banner)
        float startY = panelY - 60f;
        float gap = slotHeight + 15f;
        float centerX = (Main.WORLD_WIDTH - slotWidth) / 2f;

        // 3. Draw Save Slots
        if (saveSlots.isEmpty()) {
            game.assets.font.setColor(Color.WHITE);
            String emptyText = "No Save Files Found";
            game.glyphLayout.setText(game.assets.font, emptyText);
            game.assets.font.draw(game.batch, emptyText, (Main.WORLD_WIDTH - game.glyphLayout.width) / 2f, Main.WORLD_HEIGHT / 2f);
        } else {
            for (int i = 0; i < saveSlots.size(); i++) {
                float drawY = startY - (i * gap);

                if (i == selection) {
                    game.batch.setColor(Color.WHITE);
                } else {
                    game.batch.setColor(0.5f, 0.5f, 0.5f, 1f);
                }

                if (game.assets.loadFileTex != null) {
                    game.batch.draw(game.assets.loadFileTex, centerX, drawY, slotWidth, slotHeight);
                }

                // Draw save info text
                String info = game.ctx.getSaveInfo(saveSlots.get(i));
                game.assets.font.setColor(Color.WHITE);

                // 1. INCREASE THIS NUMBER!
                // If it is 0.8f right now, try changing it to 1.2f, 1.5f, or even 2.0f
                game.assets.font.getData().setScale(1.2f);

                // 2. Draw the text
                // Note: You might need to adjust the '+ 10f' at the end to center it vertically perfectly
                game.assets.font.draw(game.batch, info, centerX + 20f, drawY + slotHeight / 2f + 10f);

                // 3. IMMEDIATELY RESET (Keep this at 1.0f!)
                game.assets.font.getData().setScale(1.0f);
            }
        }

        game.batch.end();

        // 4. Draw brackets for selection if we have saves
        if (!saveSlots.isEmpty()) {
            game.shapeRenderer.setProjectionMatrix(game.gameCamera.combined);
            game.shapeRenderer.begin(com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Filled);

            float pulse = (com.badlogic.gdx.math.MathUtils.sin(cursorTime * 6f) + 1f) / 2f;
            game.shapeRenderer.setColor(0.7f + pulse * 0.3f, 0.7f + pulse * 0.3f, 0.75f + pulse * 0.25f, 1f);

            float selY = startY - (selection * gap);
            float pad = 8f;
            float t = 4f;
            float l = 16f;
            float boxX = centerX - pad;
            float boxY = selY - pad;
            float boxW = slotWidth + pad * 2;
            float boxH = slotHeight + pad * 2;

            // Corners
            game.shapeRenderer.rect(boxX, boxY + boxH - t, l, t);
            game.shapeRenderer.rect(boxX, boxY + boxH - l, t, l);
            game.shapeRenderer.rect(boxX + boxW - l, boxY + boxH - t, l, t);
            game.shapeRenderer.rect(boxX + boxW - t, boxY + boxH - l, t, l);
            game.shapeRenderer.rect(boxX, boxY, l, t);
            game.shapeRenderer.rect(boxX, boxY, t, l);
            game.shapeRenderer.rect(boxX + boxW - l, boxY, l, t);
            game.shapeRenderer.rect(boxX + boxW - t, boxY, t, l);

            game.shapeRenderer.end();
        }

        game.batch.begin();
        drawFadeOverlay();
        game.batch.end();

        // 5. Input
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            startFadeOut(new MainMenuScreen(game));
        }

        if (!saveSlots.isEmpty()) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.W) || Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
                selection = selection > 0 ? selection - 1 : saveSlots.size() - 1;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.S) || Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
                selection = selection < saveSlots.size() - 1 ? selection + 1 : 0;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                loadSelectedGame();
            }

            mousePos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            game.gameViewport.unproject(mousePos);

            for (int i = 0; i < saveSlots.size(); i++) {
                float slotY = startY - (i * gap);
                if (mousePos.x >= centerX && mousePos.x <= centerX + slotWidth &&
                    mousePos.y >= slotY && mousePos.y <= slotY + slotHeight) {
                    selection = i;
                    if (Gdx.input.justTouched()) {
                        loadSelectedGame();
                    }
                }
            }
        }
    }

    private void loadSelectedGame() {
        if (saveSlots.isEmpty()) return;

        String slot = saveSlots.get(selection);
        String mapName = game.ctx.loadGame(slot);

        if (mapName != null) {
            game.assets.stopAllMusic();

            mapName = mapName.toLowerCase().replace(" ", "");

            if (mapName.contains("town")) {
                startFadeOut(new TownOfEchoesScreen(game));
            } else if (mapName.contains("cavern")) {
                startFadeOut(new SilentCavernsScreen(game));
            } else if (mapName.contains("abyss")) {
                startFadeOut(new AbyssOfDissonanceScreen(game));
            } else {
                startFadeOut(new TownOfEchoesScreen(game));
            }
        }
    }

    @Override public void resize(int w, int h) {
        game.gameViewport.update(w, h, true);
    }
    @Override public void hide() {}
    @Override public void dispose() {}
}
