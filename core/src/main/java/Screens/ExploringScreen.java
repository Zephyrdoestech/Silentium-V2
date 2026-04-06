package Screens;

import Mechanics.MapTraversalSystem.Room;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import Entities.Character;
import Entities.Enemy;
import io.github.Zephyrdoestech.GameContext;
import io.github.Zephyrdoestech.Main;
import Entities.MapCharacter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import Mechanics.MapTraversalSystem.Room;

/**
 * The overworld map screen.
 *
 * Responsibilities:
 *  - Renders the Dungeon map texture
 *  - Moves the player with WASD / arrows
 *  - Clamps the camera to map bounds
 *  - Draws the radial darkness/light overlay
 *  - Detects enemy collisions → hands off to CombatScreen
 *  - Draws the HUD (HP / shield bars)
 *
 * Note: no particles or character-select music here.
 */
public class ExploringScreen extends BaseScreen {

    protected static final Random RNG = new Random();
    protected final List<Rectangle> walkableZones = new ArrayList<>();

    protected TextureRegion mapTexture;
    protected TextureRegion mapDecor;
    protected String mapName = "Unknown";
    protected Room exitRoom;
    protected TextureRegion exitTexture;
    private boolean atExit = false;
    private boolean showingExitPrompt = false;
    private boolean showInventory = false;
    private float exitPromptGraceTimer = 0f;

    public ExploringScreen(Main game) {
        super(game);
        if (game.ctx.mapName == null) {
            game.ctx.mapName = GameContext.MapName.TOWN_OF_ECHOES; // Default to Town
        }
        System.out.println("Returning to map: " + game.ctx.mapName);
        switch (game.ctx.mapName) {
            case TOWN_OF_ECHOES:
                mapTexture = new TextureRegion(game.assets.townTex);
                break;
            case SILENT_CAVERNS:
                mapTexture = new TextureRegion(game.assets.silentCavernsTex);
                break;
            case ABYSS_OF_DISSONANCE:
                mapTexture = new TextureRegion(game.assets.abyssOfDissonanceTex);
                break;
            default:
                mapTexture = new TextureRegion(game.assets.townTex);
                break;
        }
        if (this.mapTexture == null) {
            System.out.println("WARNING: Defaulting to Town.");
            this.mapTexture = new TextureRegion(game.assets.townTex);
        }
        if (game.ctx.player != null) {
            game.ctx.player.resetMovementState();
        }
    }

    //overriden by map classes
    protected int getRequiredKills() { return 0; }
    protected ExploringScreen getNextScreen() { return null; }
    protected int getEnemyCount() { return 3; }
    protected void initMapData() { }
    protected void initWalkable() {
        walkableZones.clear();
        for (Room r : game.ctx.rooms) walkableZones.add(r.getBounds());
    }
    protected void spawnEnemies() { game.ctx.mapEnemies.clear(); }

    protected void initPlayerPosition() {
        spawnEnemies();

        List<Room> emptyRooms = new ArrayList<>();
        for (Room r : game.ctx.rooms)
            if (r.getEnemies().isEmpty() && r != exitRoom)
                emptyRooms.add(r);

        Room spawnRoom = emptyRooms.isEmpty() ? game.ctx.rooms.get(0) : emptyRooms.get(RNG.nextInt(emptyRooms.size()));

        float x = spawnRoom.getBounds().x + (spawnRoom.getBounds().width - GameContext.CHAR_SIZE) / 2f;
        float y = spawnRoom.getBounds().y + (spawnRoom.getBounds().height - GameContext.CHAR_SIZE) / 2f;

        game.ctx.player = new MapCharacter(x, y);
        game.ctx.player.resetMovementState();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(null);

        showingExitPrompt = false;
        atExit = false;
        showInventory = false;
        exitPromptGraceTimer = 0.35f;

        if (game.ctx.rooms.isEmpty()) {
            initMapData();
            initWalkable();
        }

        if (game.ctx.player == null || game.ctx.player.getX() == 0 && game.ctx.player.getY() == 0) {
            game.ctx.activeCharacterStats.resetStats();
            initPlayerPosition();
        } else {
            game.ctx.player.resetMovementState();
            game.ctx.player.setInCombat(false);

            for (Room r : game.ctx.rooms) {
                if (r.getBounds().contains(game.ctx.player.getX(), game.ctx.player.getY())) {
                    game.ctx.player.setX(r.getBounds().x + (r.getBounds().width - GameContext.CHAR_SIZE) / 2f);
                    game.ctx.player.setY(r.getBounds().y + (r.getBounds().height - GameContext.CHAR_SIZE) / 2f);
                    break;
                }
            }
        }

        game.ctx.stateTime = 0f;
        game.ctx.playerState = GameContext.PlayerState.IDLE;
        if (game.ctx.facing == null) {
            game.ctx.facing = GameContext.Facing.RIGHT;
        }

        updateCamera();
    }

    @Override
    public void render(float delta) {
        if (game.ctx.activeCharacterStats.getHp() <= 0) {
            game.ctx.lives--;

            game.ctx.enemiesDefeatedInCurrentMap = 0;
            game.ctx.rooms.clear();
            game.ctx.mapEnemies.clear();

            if (game.ctx.lives <= 0) {
                game.ctx.lives = 3;
                game.ctx.player = null;
                game.setScreen(new MainMenuScreen(game));
                return;
            } else {
                game.ctx.activeCharacterStats.resetStats();
                this.show();
                return;
            }
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT);

        game.ctx.stateTime += delta;
        handleMovement(delta);
        updateCamera();

        game.batch.setProjectionMatrix(game.gameCamera.combined);
        game.batch.begin();

        game.batch.setColor(com.badlogic.gdx.graphics.Color.WHITE);

        if (mapTexture != null) {
            game.batch.draw(mapTexture, 0, 0, game.ctx.MAP_SIZE, game.ctx.MAP_SIZE);
        } else {
            System.err.println("Warning: Map texture is null in ExploringScreen.java. Check asset loading.");
        }
        if (exitRoom != null && exitTexture != null) {
            float exitSize = 104f;
            game.batch.draw(exitTexture,
                exitRoom.getBounds().x + (exitRoom.getBounds().width - exitSize) / 2f,
                exitRoom.getBounds().y + (exitRoom.getBounds().height) / 1.16f,
                exitSize, exitSize);
        }
        game.batch.end();

        game.shapeRenderer.setProjectionMatrix(game.gameCamera.combined);

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        game.shapeRenderer.setColor(Color.GREEN);
        for (Room r : game.ctx.rooms) {
            // game.shapeRenderer.rect(r.getBounds().x, r.getBounds().y, r.getBounds().width, r.getBounds().height);
        }
        game.shapeRenderer.setColor(Color.YELLOW);
        for (Rectangle h : walkableZones) {
            // game.shapeRenderer.rect(h.x, h.y, h.width, h.height);
        }
        game.shapeRenderer.end();

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(0.7f, 0.1f, 0.1f, 1f);
        for (Enemy e : game.ctx.mapEnemies) {
            if (!e.isDefeated())
                game.shapeRenderer.rect(e.getX(), e.getY(), GameContext.CHAR_SIZE, GameContext.CHAR_SIZE);
        }
        game.shapeRenderer.end();

        game.batch.begin();
        game.assets.font.setColor(Color.RED);
        for (Enemy e : game.ctx.mapEnemies) {
            if (!e.isDefeated()) {
                game.assets.font.draw(game.batch, e.getName(),
                    e.getX() - 10, e.getY() + GameContext.CHAR_SIZE + 18);
            }
        }
        game.assets.font.setColor(Color.WHITE);
        drawPlayerSprite();
        if (mapDecor != null)
            game.batch.draw(mapDecor, 0, 0, game.ctx.MAP_SIZE, game.ctx.MAP_SIZE);

        drawDarknessOverlay();
        game.batch.end();

        drawHUD();

        if (showInventory) {
            drawInventoryOverlay();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE))
            game.setScreen(new MainMenuScreen(game));
    }

    private void handleMovement(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {
            showInventory = !showInventory;
        }

        if (showInventory) {
            game.ctx.playerState = GameContext.PlayerState.IDLE;
            return;
        }

        if (exitPromptGraceTimer > 0f) {
            exitPromptGraceTimer = Math.max(0f, exitPromptGraceTimer - delta);
        }

        float move = GameContext.SPEED * delta;
        float prevX = game.ctx.player.getX();
        float prevY = game.ctx.player.getY();

        game.ctx.playerState = GameContext.PlayerState.IDLE;

        if (!showingExitPrompt) {
            if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
                game.ctx.player.setY(game.ctx.player.getY() + move);
                game.ctx.playerState = GameContext.PlayerState.WALK_UP;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                game.ctx.player.setY(game.ctx.player.getY() - move);
                game.ctx.playerState = GameContext.PlayerState.WALK_DOWN;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                game.ctx.player.setX(game.ctx.player.getX() - move);
                game.ctx.playerState = GameContext.PlayerState.WALK_LEFT;
                game.ctx.facing = GameContext.Facing.LEFT;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                game.ctx.player.setX(game.ctx.player.getX() + move);
                game.ctx.playerState = GameContext.PlayerState.WALK_RIGHT;
                game.ctx.facing = GameContext.Facing.RIGHT;
            }
        }

        if (!isInWalkableZone(game.ctx.player.getX(), game.ctx.player.getY())) {
            game.ctx.player.setX(prevX);
            game.ctx.player.setY(prevY);
        }

        float S = game.ctx.MAP_SIZE, C = GameContext.CHAR_SIZE;
        game.ctx.player.setX(MathUtils.clamp(game.ctx.player.getX(), 0, S - C));
        game.ctx.player.setY(MathUtils.clamp(game.ctx.player.getY(), 0, S - C));

        if (exitRoom != null && exitPromptGraceTimer <= 0f) {
            float exitSize = 104f;
            Rectangle pRect = new Rectangle(game.ctx.player.getX(), game.ctx.player.getY(), C, C);
            Rectangle exitRect = new Rectangle(
                exitRoom.getBounds().x + (exitRoom.getBounds().width - exitSize) / 2f,
                exitRoom.getBounds().y + (exitRoom.getBounds().height) / 1.16f,
                exitSize, exitSize);

            if (pRect.overlaps(exitRect)) {
                if (!atExit) {
                    showingExitPrompt = true;
                    atExit = true;
                }
            } else {
                atExit = false;
            }
        }

        if (showingExitPrompt) {
            boolean canExit = game.ctx.enemiesDefeatedInCurrentMap >= getRequiredKills();
            if (canExit) {
                if (Gdx.input.isKeyJustPressed(Input.Keys.Y)) {
                    ExploringScreen next = getNextScreen();
                    if (next != null) {
                        game.ctx.player = null;
                        game.ctx.enemiesDefeatedInCurrentMap = 0;
                        game.ctx.rooms.clear();
                        game.ctx.mapEnemies.clear();

                        game.setScreen(next);
                    }
                    showingExitPrompt = false;
                } else if (Gdx.input.isKeyJustPressed(Input.Keys.N)) {
                    showingExitPrompt = false;
                    atExit = false;
                    exitPromptGraceTimer = 0.35f;
                }
            } else {
                if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                    showingExitPrompt = false;
                    atExit = false;
                    exitPromptGraceTimer = 0.35f;
                    game.ctx.player.setY(game.ctx.player.getY() - 15f);
                }
            }
        }

        Rectangle pRect = new Rectangle(game.ctx.player.getX(), game.ctx.player.getY(), C, C);
        for (Enemy e : game.ctx.mapEnemies) {
            if (e.isDefeated()) continue;
            Rectangle eRect = new Rectangle(e.getX(), e.getY(), C, C);
            if (pRect.overlaps(eRect)) {
                game.ctx.player.setX(prevX);
                game.ctx.player.setY(prevY);
                game.ctx.player.setInCombat(true);
                game.ctx.currentEnemy = e;
                game.ctx.noteHandler.noteCount = 0;

                game.ctx.combatState = GameContext.CombatState.BATTLE_SCREEN;
                game.setScreen(new CombatScreen(game));
                return;

            }
        }
    }

    private boolean isInWalkableZone(float x, float y) {
        Rectangle playerRect = new Rectangle(x, y, GameContext.CHAR_SIZE, GameContext.CHAR_SIZE);
        for (Rectangle zone : walkableZones)
            if (zone.overlaps(playerRect))
                return true;
        return false;
    }

    private void updateCamera() {
        float halfW = Main.WORLD_WIDTH / 2f;
        float halfH = Main.WORLD_HEIGHT / 2f;
        float S = game.ctx.MAP_SIZE;
        float C = GameContext.CHAR_SIZE;
        float camX = MathUtils.clamp(game.ctx.player.getX() + C / 2f, halfW, S - halfW);
        float camY = MathUtils.clamp(game.ctx.player.getY() + C / 2f, halfH, S - halfH);
        game.gameCamera.position.set(camX, camY, 0);
        game.gameCamera.update();
    }

    private void drawPlayerSprite() {
        switch (game.ctx.selectedCharacter) {
            case SONARA:
                drawCharacter(
                    game.assets.sonaraIdleRight, game.assets.sonaraIdleLeft,
                    game.assets.sonaraIdleRight, game.assets.sonaraIdleLeft);
                break;
            case AURELIUS:
                drawCharacter(
                    game.assets.aureliusIdleRight, game.assets.aureliusIdleLeft,
                    game.assets.aureliusWalkRight, game.assets.aureliusWalkLeft);
                break;
            case LYRON:
                drawCharacter(
                    game.assets.lyronIdleRight, game.assets.lyronIdleLeft,
                    game.assets.lyronWalkRight, game.assets.lyronWalkLeft);
                break;
        }
    }

    private void drawCharacter(
        com.badlogic.gdx.graphics.g2d.Animation<TextureRegion> idleR,
        com.badlogic.gdx.graphics.g2d.Animation<TextureRegion> idleL,
        com.badlogic.gdx.graphics.g2d.Animation<TextureRegion> walkR,
        com.badlogic.gdx.graphics.g2d.Animation<TextureRegion> walkL) {

        float t = game.ctx.stateTime;
        GameContext.PlayerState ps = game.ctx.playerState;
        GameContext.Facing f = game.ctx.facing;
        TextureRegion frame;

        switch (ps) {
            case WALK_LEFT:
                frame = walkL.getKeyFrame(t, true);
                break;
            case WALK_RIGHT:
                frame = walkR.getKeyFrame(t, true);
                break;
            case WALK_UP:
            case WALK_DOWN:
                frame = f == GameContext.Facing.LEFT
                    ? walkL.getKeyFrame(t, true)
                    : walkR.getKeyFrame(t, true);
                break;
            default:
                frame = f == GameContext.Facing.LEFT
                    ? idleL.getKeyFrame(t, true)
                    : idleR.getKeyFrame(t, true);
                break;
        }
        game.batch.draw(frame,
            game.ctx.player.getX(), game.ctx.player.getY(),
            GameContext.CHAR_SIZE, GameContext.CHAR_SIZE);
    }

    private void drawDarknessOverlay() {
        float drawSize = 1100f;
        float cx = game.ctx.player.getX() + GameContext.CHAR_SIZE / 2f;
        float cy = game.ctx.player.getY() + GameContext.CHAR_SIZE / 2f;
        TextureRegion blackPixel = new TextureRegion(game.assets.darknessOverlay, 0, 0, 1, 1);
        float left = cx - drawSize / 2f, right = cx + drawSize / 2f;
        float bottom = cy - drawSize / 2f, top = cy + drawSize / 2f;
        float pad = 2000f;

        game.batch.setColor(Color.WHITE);
        game.batch.draw(game.assets.darknessOverlay, left, bottom, drawSize, drawSize);
        game.batch.draw(blackPixel, left - pad, bottom - pad, pad, drawSize + pad * 2f);
        game.batch.draw(blackPixel, right, bottom - pad, pad, drawSize + pad * 2f);
        game.batch.draw(blackPixel, left, bottom - pad, drawSize, pad);
        game.batch.draw(blackPixel, left, top, drawSize, pad);
    }

    private void drawHUD() {
        Character c = game.ctx.activeCharacterStats;
        game.uiCamera.update();

        game.shapeRenderer.setProjectionMatrix(game.uiCamera.combined);
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        drawBar(game.shapeRenderer, 20, Main.WORLD_HEIGHT - 30, 200, 16,
            (float) c.getHp() / c.getMaxHp(), Color.DARK_GRAY, Color.RED);
        drawBar(game.shapeRenderer, 20, Main.WORLD_HEIGHT - 52, 200, 16,
            c.getMaxShield() > 0 ? (float) c.getShield() / c.getMaxShield() : 0f,
            Color.DARK_GRAY, Color.CYAN);
        game.shapeRenderer.end();

        game.batch.setProjectionMatrix(game.uiCamera.combined);
        game.batch.begin();
        game.assets.font.setColor(Color.WHITE);
        game.assets.font.draw(game.batch,
            c.getName() + "  HP: " + c.getHp() + "/" + c.getMaxHp(),
            230, Main.WORLD_HEIGHT - 18);
        game.assets.font.draw(game.batch,
            "Shield: " + c.getShield() + "/" + c.getMaxShield(),
            230, Main.WORLD_HEIGHT - 40);
        game.assets.font.draw(game.batch, "Lv " + c.getLevel(), 20, Main.WORLD_HEIGHT - 62);
        game.assets.font.setColor(Color.GRAY);
        game.assets.font.draw(game.batch, "ESC – Menu", 10, 20);
        game.assets.font.draw(game.batch, "I – Inventory", 10, 40);
        game.assets.font.setColor(Color.WHITE);
        game.batch.end();

        game.batch.begin();
        game.assets.font.draw(game.batch, "LIVES: " + game.ctx.lives, 20, Main.WORLD_HEIGHT - 85);
        if (getRequiredKills() > 0) {
            String progress = "Kills: " + game.ctx.enemiesDefeatedInCurrentMap + "/" + getRequiredKills();
            game.assets.font.draw(game.batch, progress, 20, Main.WORLD_HEIGHT - 110);
        }

        if (showingExitPrompt) {
            if (game.ctx.enemiesDefeatedInCurrentMap < getRequiredKills()) {
                game.assets.font.draw(game.batch, "LOCKED: Defeat " + getRequiredKills() + " enemies first!",
                    Main.WORLD_WIDTH / 2f - 150, Main.WORLD_HEIGHT / 2f + 50);
                game.assets.font.draw(game.batch, "[Press ENTER to go back]",
                    Main.WORLD_WIDTH / 2f - 100, Main.WORLD_HEIGHT / 2f + 20);
            } else {
                game.assets.font.draw(game.batch, "EXIT READY! (Y/N)",
                    Main.WORLD_WIDTH / 2f - 80, Main.WORLD_HEIGHT / 2f + 50);
            }
        }
        game.batch.end();
    }

    private void drawInventoryOverlay() {
        float overlayX = Main.WORLD_WIDTH * 0.2f;
        float overlayY = Main.WORLD_HEIGHT * 0.18f;
        float overlayW = Main.WORLD_WIDTH * 0.6f;
        float overlayH = Main.WORLD_HEIGHT * 0.64f;

        game.shapeRenderer.setProjectionMatrix(game.uiCamera.combined);
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(0f, 0f, 0f, 0.75f);
        game.shapeRenderer.rect(0, 0, Main.WORLD_WIDTH, Main.WORLD_HEIGHT);
        game.shapeRenderer.setColor(0.1f, 0.12f, 0.18f, 0.95f);
        game.shapeRenderer.rect(overlayX, overlayY, overlayW, overlayH);
        game.shapeRenderer.end();

        game.batch.setProjectionMatrix(game.uiCamera.combined);
        game.batch.begin();

        float titleX = overlayX + 24f;
        float titleY = overlayY + overlayH - 24f;
        game.assets.font.getData().setScale(1.2f);
        game.assets.font.setColor(Color.YELLOW);
        game.assets.font.draw(game.batch, "Inventory", titleX, titleY);

        game.assets.font.getData().setScale(0.9f);
        game.assets.font.setColor(Color.LIGHT_GRAY);
        game.assets.font.draw(game.batch, "[I] Close", overlayX + overlayW - 90f, titleY);

        float itemY = titleY - 36f;
        float lineGap = 26f;

        Map<String, Integer> inventory = game.ctx.activeCharacterStats.inventory;
        if (inventory == null || inventory.isEmpty()) {
            game.assets.font.setColor(Color.WHITE);
            game.assets.font.draw(game.batch, "Inventory is empty", titleX, itemY);
        } else {
            game.assets.font.setColor(Color.WHITE);
            for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
                String line = "- " + entry.getKey() + " x" + entry.getValue();
                game.assets.font.draw(game.batch, line, titleX, itemY);
                itemY -= lineGap;
                if (itemY < overlayY + 24f) break;
            }
        }

        game.assets.font.getData().setScale(1.0f);
        game.assets.font.setColor(Color.WHITE);
        game.batch.end();
    }

    @Override
    public void resize(int w, int h) {
        game.gameViewport.update(w, h, true);
        game.uiViewport.update(w, h, true);
    }

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        // Do not dispose of global assets here
    }
}
