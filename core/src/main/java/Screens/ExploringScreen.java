package Screens;

import Mechanics.Room;
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
import java.util.Random;

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

    private static final Random RNG = new Random();

    public ExploringScreen(Main game) { super(game); }

    @Override
    public void show() {
        // Only initialise the map once; re-entering from combat keeps the existing state
        if (game.ctx.player == null) {
            initMap();
        }
        game.ctx.stateTime = 0f;
    }

    private void initMap() {
        float half = GameContext.MAP_SIZE / 2f - GameContext.CHAR_SIZE / 2f;
        game.ctx.player      = new MapCharacter(half, half);
        game.ctx.playerState = GameContext.PlayerState.IDLE;
        game.ctx.facing      = GameContext.Facing.RIGHT;
        spawnEnemies();
    }

    private void spawnEnemies() {
        game.ctx.mapEnemies = new ArrayList<>();
        game.ctx.rooms      = new ArrayList<>();

        game.ctx.rooms.add(new Room(96f,   64f,   320f, 256f));
        game.ctx.rooms.add(new Room(96f,   544f,  320f, 256f));
        game.ctx.rooms.add(new Room(1088f, 96f,   448f, 384f));
        game.ctx.rooms.add(new Room(1216f, 1024f, 384f, 448f));
        game.ctx.rooms.add(new Room(768f,  512f,  384f, 384f));

        for (Room room : game.ctx.rooms) {
            if (RNG.nextInt(100) < 70) {
                int count = 1 + RNG.nextInt(3);
                for (int i = 0; i < count; i++) {
                    float x = room.getBounds().x + RNG.nextFloat() * (room.getBounds().width  - GameContext.CHAR_SIZE);
                    float y = room.getBounds().y + RNG.nextFloat() * (room.getBounds().height - GameContext.CHAR_SIZE);
                    Enemy e = RNG.nextBoolean()
                        ? Enemy.fleshFeeder(x, y)
                        : Enemy.andrewellers(x, y);
                    room.addEnemy(e);
                    game.ctx.mapEnemies.add(e);
                }
            }
        }
    }

    // ── Render ────────────────────────────────────────────────────────────────

    @Override
    public void render(float delta) {
        game.ctx.stateTime += delta;
        handleMovement(delta);
        updateCamera();

        game.batch.setProjectionMatrix(game.gameCamera.combined);
        game.batch.begin();

        // Map
        game.batch.draw(game.assets.townTex, 0, 0, GameContext.MAP_SIZE, GameContext.MAP_SIZE);

        game.batch.end();

        // Debug room outlines + enemy rects (ShapeRenderer)
        game.shapeRenderer.setProjectionMatrix(game.gameCamera.combined);
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        game.shapeRenderer.setColor(Color.GREEN);
        for (Room r : game.ctx.rooms)
            game.shapeRenderer.rect(r.getBounds().x, r.getBounds().y, r.getBounds().width, r.getBounds().height);
        game.shapeRenderer.end();

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(0.7f, 0.1f, 0.1f, 1f);
        for (Enemy e : game.ctx.mapEnemies)
            if (!e.isDefeated())
                game.shapeRenderer.rect(e.getX(), e.getY(), GameContext.CHAR_SIZE, GameContext.CHAR_SIZE);
        game.shapeRenderer.end();

        game.batch.setProjectionMatrix(game.gameCamera.combined);
        game.batch.begin();

        // Enemy name labels
        game.assets.font.setColor(Color.RED);
        for (Enemy e : game.ctx.mapEnemies)
            if (!e.isDefeated())
                game.assets.font.draw(game.batch, e.getName(),
                    e.getX() - 10, e.getY() + GameContext.CHAR_SIZE + 18);
        game.assets.font.setColor(Color.WHITE);

        // Player sprite
        drawPlayerSprite();

        // Darkness overlay (world space, centred on player)
        drawDarknessOverlay();

        game.batch.end();

        // HUD (uses fixed uiCamera)
        drawHUD();

        // ESC → main menu
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE))
            game.setScreen(new MainMenuScreen(game));
    }

    // ── Movement ──────────────────────────────────────────────────────────────

    private void handleMovement(float delta) {
        float move = GameContext.SPEED * delta;
        float prevX = game.ctx.player.getX();
        float prevY = game.ctx.player.getY();

        game.ctx.playerState = GameContext.PlayerState.IDLE;

        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
            game.ctx.player.up(move);
            game.ctx.playerState = GameContext.PlayerState.WALK_UP;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            game.ctx.player.down(move);
            game.ctx.playerState = GameContext.PlayerState.WALK_DOWN;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            game.ctx.player.left(move);
            game.ctx.playerState = GameContext.PlayerState.WALK_LEFT;
            game.ctx.facing = GameContext.Facing.LEFT;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            game.ctx.player.right(move);
            game.ctx.playerState = GameContext.PlayerState.WALK_RIGHT;
            game.ctx.facing = GameContext.Facing.RIGHT;
        }

        float S = GameContext.MAP_SIZE, C = GameContext.CHAR_SIZE;
        if (game.ctx.player.getX() < 0)     game.ctx.player.setX(0);
        if (game.ctx.player.getY() < 0)     game.ctx.player.setY(0);
        if (game.ctx.player.getX() > S - C) game.ctx.player.setX(S - C);
        if (game.ctx.player.getY() > S - C) game.ctx.player.setY(S - C);

        // Enemy collision
        Rectangle pRect = new Rectangle(game.ctx.player.getX(), game.ctx.player.getY(), C, C);
        for (Enemy e : game.ctx.mapEnemies) {
            if (e.isDefeated()) continue;
            if (pRect.overlaps(new Rectangle(e.getX(), e.getY(), C, C))) {
                game.ctx.player.setX(prevX);
                game.ctx.player.setY(prevY);
                game.ctx.currentEnemy = e;
                game.ctx.noteCount    = 0;
                game.ctx.combatLog    = "A " + e.getName() + " appears! Enter 3 notes to attack.";
                game.ctx.combatState  = GameContext.CombatState.ATTACK;
                game.setScreen(new CombatScreen(game));
                return;
            }
        }
    }

    // ── Camera ────────────────────────────────────────────────────────────────

    private void updateCamera() {
        float halfW = Main.WORLD_WIDTH  / 2f;
        float halfH = Main.WORLD_HEIGHT / 2f;
        float S     = GameContext.MAP_SIZE;
        float C     = GameContext.CHAR_SIZE;
        float camX  = MathUtils.clamp(game.ctx.player.getX() + C / 2f, halfW, S - halfW);
        float camY  = MathUtils.clamp(game.ctx.player.getY() + C / 2f, halfH, S - halfH);
        game.gameCamera.position.set(camX, camY, 0);
        game.gameCamera.update();
    }

    // ── Player sprite ─────────────────────────────────────────────────────────

    private void drawPlayerSprite() {
        switch (game.ctx.selectedCharacter) {
            case SONARA:   drawCharacter(
                game.assets.sonaraIdleRight,  game.assets.sonaraIdleLeft,
                game.assets.sonaraIdleRight,  game.assets.sonaraIdleLeft); break;
            case AURELIUS: drawCharacter(
                game.assets.aureliusIdleRight, game.assets.aureliusIdleLeft,
                game.assets.aureliusWalkRight, game.assets.aureliusWalkLeft); break;
            case LYRON:    drawCharacter(
                game.assets.lyronIdleRight,   game.assets.lyronIdleLeft,
                game.assets.lyronWalkRight,   game.assets.lyronWalkLeft); break;
        }
    }

    private void drawCharacter(
        com.badlogic.gdx.graphics.g2d.Animation<TextureRegion> idleR,
        com.badlogic.gdx.graphics.g2d.Animation<TextureRegion> idleL,
        com.badlogic.gdx.graphics.g2d.Animation<TextureRegion> walkR,
        com.badlogic.gdx.graphics.g2d.Animation<TextureRegion> walkL) {

        float t = game.ctx.stateTime;
        GameContext.PlayerState ps = game.ctx.playerState;
        GameContext.Facing      f  = game.ctx.facing;
        TextureRegion frame;

        switch (ps) {
            case WALK_LEFT:  frame = walkL.getKeyFrame(t, true); break;
            case WALK_RIGHT: frame = walkR.getKeyFrame(t, true); break;
            case WALK_UP: case WALK_DOWN:
                frame = f == GameContext.Facing.LEFT
                    ? walkL.getKeyFrame(t, true)
                    : walkR.getKeyFrame(t, true); break;
            default:
                frame = f == GameContext.Facing.LEFT
                    ? idleL.getKeyFrame(t, true)
                    : idleR.getKeyFrame(t, true); break;
        }
        game.batch.draw(frame,
            game.ctx.player.getX(), game.ctx.player.getY(),
            GameContext.CHAR_SIZE, GameContext.CHAR_SIZE);
    }

    // ── Darkness overlay ──────────────────────────────────────────────────────

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

    // ── HUD ───────────────────────────────────────────────────────────────────

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
        game.assets.font.setColor(Color.WHITE);
        game.batch.end();
    }

    @Override public void resize(int w, int h) {}
    @Override public void hide()    {}
    @Override public void dispose() {}
}
