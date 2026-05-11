package Screens;

import Entities.CharacterHero;
import Inventory.Inventory;
import Screens.LeaderBoard.NameInputScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import Entities.Enemy;
import io.github.Zephyrdoestech.GameContext;
import io.github.Zephyrdoestech.Main;
import Entities.MapCharacter;

import java.util.ArrayList;
import java.util.List;
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
    protected final List<Rectangle> corridorZones = new ArrayList<>();
    protected Room lockedRoom = null;

    protected TextureRegion mapTexture;
    protected TextureRegion mapDecor;
    protected String mapName = "Unknown";
    protected TextureRegion exitTexture;
    private boolean atExit = false;
    private boolean showInventory = false;
    private boolean showingExitPrompt = false;
    private Rectangle yesButtonRect = new Rectangle();
    private Rectangle noButtonRect = new Rectangle();
    private Rectangle okButtonRect = new Rectangle();

    // ── Scale / Helpers ────────────────────────────────────────────────────

    private static Random rd = new Random();

    private float textWidth(String text) {
        game.glyphLayout.setText(game.assets.font, text);
        return game.glyphLayout.width;
    }

    // ── PostCombat Variables ────────────────────────────────────────────────────

    private boolean showVictoryPopup  = false;
    private final List<String> droppedItemNames = new ArrayList<>();
    private final List<Texture> droppedItemIcons = new ArrayList<>();
    private boolean leveledUp         = false;
    private int     newLevel          = 0;

    // --- Monologue Variables ---
    private boolean isMonologueActive = false;
    private boolean wasMonologueActive = false;
    private boolean pendingExit = false;
    private boolean pendingCombat = false;
    private boolean pendingBossDialogue = false;
    private boolean currentMonologueRightAligned = false;
    private String[] mapEntry;
    private String[] mapExit;
    private String[] currentMonologue = {};
    private int currentMonologueIndex = 0;

    private boolean pendingFinalBossVictoryLines = false;
    private boolean pendingFinalBossDefeatLines = false;
    private boolean pendingFinalBossRecordScreen = false;
    private boolean pendingFinalBossDeathReset = false;

    // --- Typewriter Effect Variables ---
    private float monologueTimer = 0f;
    private int monologueCharIndex = 0;
    private float lineDelayTimer = 0f;
    private final float TYPEWRITER_SPEED = 0.05f; // Seconds per character
    private final float LINE_DELAY = 1.0f; // Seconds to wait after line is fully displayed

    private long monologueSfxId = -1L;
    private boolean monologueSfxPlaying = false;

    // --- Pause Menu ---
    private int             pauseMenuSelection   = 0;
    private Texture[]       pauseButtons;
    private boolean         showChordList        = false;
    private boolean         showItemInfo         = false;
    private com.badlogic.gdx.math.Vector3 mousePos = new com.badlogic.gdx.math.Vector3();

    public ExploringScreen(Main game) {
        super(game);
        if (game.ctx.mapName == null) {
            game.ctx.mapName = GameContext.MapName.TOWN_OF_ECHOES; // Default to Town
        }
        // Set the map texture based on the map name
        switch (game.ctx.mapName) {
            case TOWN_OF_ECHOES:
                mapTexture = game.assets.townTex;
                break;
            case SILENT_CAVERNS:
                mapTexture = game.assets.silentCavernsTex;
                break;
            case ABYSS_OF_DISSONANCE:
                mapTexture = game.assets.abyssOfDissonanceTex;
                break;
            default:
                mapTexture = game.assets.townTex; // Default to Town
                break;
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
    protected void restoreInstanceFields() { }
    protected void spawnEnemies() { game.ctx.mapEnemies.clear(); }


    protected void initPlayerPosition() {
        spawnEnemies();

        List<Room> emptyRooms = new ArrayList<>();
        for (Room r : game.ctx.rooms)
            if (r.getEnemies().isEmpty() && r != game.ctx.exitRoom)
                emptyRooms.add(r);

        Room spawnRoom = emptyRooms.isEmpty() ? game.ctx.rooms.get(0) : emptyRooms.get(RNG.nextInt(emptyRooms.size()));

        float x = spawnRoom.getBounds().x + (spawnRoom.getBounds().width  - GameContext.CHAR_SIZE) / 2f;
        float y = spawnRoom.getBounds().y + (spawnRoom.getBounds().height - GameContext.CHAR_SIZE) / 2f;

        game.ctx.player = new MapCharacter(x, y);
    }

    protected List<Rectangle> getActiveWalkableZones() {
        if (isInEnemyRoom() && lockedRoom != null) {
            List<Rectangle> lockdown = new ArrayList<>();
            lockdown.add(lockedRoom.getBounds());
            return lockdown;
        }
        List<Rectangle> all = new ArrayList<>(walkableZones);
        all.addAll(corridorZones);
        return all;
    }

    protected boolean isInEnemyRoom() {
        if (game.ctx.player == null) return false;

        float cx = game.ctx.player.getX() + GameContext.CHAR_SIZE / 2f;
        float cy = game.ctx.player.getY() + GameContext.CHAR_SIZE / 2f;

        if (lockedRoom != null) {
            if (lockedRoom.getEnemies().isEmpty()) {
                lockedRoom = null;
                return false;
            }
            return true;
        }

        for (Room r : game.ctx.rooms) {
            if (r.getBounds().contains(cx, cy) && !r.getEnemies().isEmpty()) {
                lockedRoom = r;
                return true;
            }
        }
        return false;
    }


    @Override
    public void show() {
        game.assets.font.getData().setScale(1.0f);
        game.assets.titleFont.getData().setScale(1.0f);
        game.ctx.currentMapScreen = this;

        startFadeIn();

        pauseButtons = new Texture[]{
            game.assets.pauseContinueBtn,
            game.assets.pauseChordInfoBtn,
            game.assets.pauseItemInfoBtn,
            game.assets.pauseExitBtn
        };

        boolean hasFinalBossVictoryPending = game.ctx.finalBossVictoryPending;
        boolean hasFinalBossDefeatPending = game.ctx.finalBossDefeatPending;

        if (!hasFinalBossVictoryPending && !hasFinalBossDefeatPending && game.ctx.playerDefeated) {
            game.ctx.playerDefeated = false;
            handlePlayerDeath();
            return;
        }

        boolean returningFromCombatVictory = game.ctx.playerWon;

        if (game.ctx.rooms.isEmpty()) {
            initMapData();
            initWalkable();

            switch (game.ctx.mapName) {
                case TOWN_OF_ECHOES:
                    mapEntry = game.ctx.activeCharacterStats.getMonologues().firstMapEntry;
                    mapExit = game.ctx.activeCharacterStats.getMonologues().firstMapExit;
                    break;
                case SILENT_CAVERNS:
                    mapEntry = game.ctx.activeCharacterStats.getMonologues().secondMapEntry;
                    mapExit = game.ctx.activeCharacterStats.getMonologues().secondMapExit;
                    break;
                case ABYSS_OF_DISSONANCE:
                    mapEntry = game.ctx.activeCharacterStats.getMonologues().thirdMapEntry;
                    mapExit = game.ctx.activeCharacterStats.getMonologues().thirdMapExit;
                    break;
            }

            if (mapEntry != null && mapEntry.length > 0) { startMonologue(mapEntry, false); }
        } else {
            restoreInstanceFields();
            initWalkable();
        }

        if (game.ctx.savedPlayerX != -1f && game.ctx.savedPlayerY != -1f) {
            if (game.ctx.player == null) {
                initPlayerPosition();
            }

            game.ctx.player.setX(game.ctx.savedPlayerX);
            game.ctx.player.setY(game.ctx.savedPlayerY);

            game.ctx.savedPlayerX = -1f;
            game.ctx.savedPlayerY = -1f;
        } else if (game.ctx.player == null || (game.ctx.player.getX() == 0 && game.ctx.player.getY() == 0)) {
            game.ctx.activeCharacterStats.resetStats();
            initPlayerPosition();
        } else if (!returningFromCombatVictory) {
            boolean placed = false;

            float playerCenterX = game.ctx.player.getX() + GameContext.CHAR_SIZE / 2f;
            float playerCenterY = game.ctx.player.getY() + GameContext.CHAR_SIZE / 2f;

            for (Room r : game.ctx.rooms) {
                if (r.getBounds().contains(playerCenterX, playerCenterY)) {
                    game.ctx.player.setX(r.getBounds().x + (r.getBounds().width - GameContext.CHAR_SIZE) / 2f);
                    game.ctx.player.setY(r.getBounds().y + (r.getBounds().height - GameContext.CHAR_SIZE) / 2f);
                    placed = true;
                    break;
                }
            }

            if (!placed && !game.ctx.rooms.isEmpty()) {
                Room fallback = game.ctx.rooms.get(0);
                game.ctx.player.setX(fallback.getBounds().x + (fallback.getBounds().width - GameContext.CHAR_SIZE) / 2f);
                game.ctx.player.setY(fallback.getBounds().y + (fallback.getBounds().height - GameContext.CHAR_SIZE) / 2f);
            }
        }

        if (game.ctx.finalBossVictoryPending) {
            game.ctx.finalBossVictoryPending = false;
            game.ctx.playerWon = false;

            pendingFinalBossVictoryLines = true;
            startMonologue(game.ctx.activeCharacterStats.getMonologues().postFinalBattleVictory, false);

            game.ctx.stateTime = 0f;
            this.lockedRoom = null;
            updateCamera();
            return;
        }
        if (game.ctx.finalBossDefeatPending) {
            game.ctx.finalBossDefeatPending = false;
            game.ctx.playerDefeated = false;

            pendingFinalBossDefeatLines = true;
            startMonologue(game.ctx.activeCharacterStats.getMonologues().postFinalBattleDefeat, false);

            game.ctx.stateTime = 0f;
            this.lockedRoom = null;
            updateCamera();
            return;
        }
        if (game.ctx.playerWon) {
            game.ctx.playerWon = false;
            triggerVictoryPopup();
        }

        game.ctx.stateTime = 0f;

        this.lockedRoom = null;
        isInEnemyRoom();

        updateCamera();
    }

    // Helper class to store a texture region and its dimensions
    private static class EnemyDrawData {
        TextureRegion region;
        float width = 64f;
        float height = 64f;

        EnemyDrawData(TextureRegion region) {
            this.region = region;
        }
    }

    private EnemyDrawData getEnemyFrame(Enemy e) {
        com.badlogic.gdx.graphics.g2d.Animation<TextureRegion> anim;
        EnemyDrawData data = new EnemyDrawData(null);

        switch (e.getName()) {
            case "Flesh Feeder":
                anim = game.assets.fleshfeederCombatIdle;
                break;
            case "Darryllion":
                anim = game.assets.darryllionCombatIdle;
                break;
            case "Gobninil":
                anim = game.assets.gobninilCombatIdle;
                data.width = 60f;
                data.height = 60f;
                break;
            case "Chimericks":
                anim = game.assets.chimericksCombatIdle;
                data.width = 80f;
                data.height = 80f;
                break;
            case "Labagoliath the Void Shaker":
                anim = game.assets.labagoliathCombatIdle;
                data.width = 96f;
                data.height = 96f;
                break;
            case "Maestro Syozan":
                anim = game.assets.syozanCombatIdle;
                data.width = 112f;
                data.height = 112f;
                break;
            default:
                // Fallback if name doesn't match
                anim = game.assets.fleshfeederCombatIdle;
                break;
        }

        if (anim != null) {
            data.region = anim.getKeyFrame(game.ctx.stateTime, true);
        }
        return data.region != null ? data : null;
    }

    // ── Render ────────────────────────────────────────────────────────────────
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT);

        // If a fade out finishes this frame, don't do anything else.
        // BaseScreen handles the actual screen transition.
        if (updateFade(delta)) return;

        if (game.ctx.player == null) return;

        if (game.ctx.activeCharacterStats.getHp() <= 0
            && !isMonologueActive
            && !pendingFinalBossDefeatLines
            && !pendingFinalBossDeathReset) {
            handlePlayerDeath();
            return;
        }
        game.ctx.totalPlaytime += delta;
        game.ctx.stateTime += delta;

        if (isMonologueActive) {
            game.ctx.playerState = GameContext.PlayerState.IDLE;
            handleMonologueInput(delta);
        } else if (!fadingOut) {
            handleMovement(delta);
        }

        if (game.ctx.player == null) return;

        updateCamera();

        // 1. DRAW ALL WORLD SPRITES
        game.batch.setProjectionMatrix(game.gameCamera.combined);
        game.batch.begin();
        game.batch.setColor(Color.WHITE);

        // Map
        if (mapTexture != null) {
            game.batch.draw(mapTexture, 0, 0, game.ctx.MAP_SIZE, game.ctx.MAP_SIZE);
        }

        // Exit Portal
        if (game.ctx.exitRoom != null && exitTexture != null) {
            float exitSize = 104f;
            float exitY = game.ctx.exitRoom.getBounds().y + (game.ctx.exitRoom.getBounds().height) / 1.16f;
            if (game.ctx.mapName == GameContext.MapName.SILENT_CAVERNS) exitY -= 50f;

            game.batch.draw(exitTexture,
                game.ctx.exitRoom.getBounds().x + (game.ctx.exitRoom.getBounds().width - exitSize) / 2f,
                exitY, exitSize, exitSize);
        }

        // Enemies
        for (Enemy e : game.ctx.mapEnemies) {
            if (!e.isDefeated()) {
                EnemyDrawData drawData = getEnemyFrame(e);
                if (drawData != null && drawData.region != null) {
                    game.batch.draw(drawData.region, e.getX(), e.getY(), drawData.width, drawData.height);
                }
            }
        }

        game.assets.font.setColor(Color.WHITE);

        // Player sprite
        drawPlayerSprite();

        // Map Decor
        if (mapDecor != null) {
            game.batch.draw(mapDecor, 0, 0, game.ctx.MAP_SIZE, game.ctx.MAP_SIZE);
        }

        // Darkness overlay
        drawDarknessOverlay();
        game.batch.end();

        // 2. DRAW SHAPES (Debug Outlines) FOR TESTING PLS DONT REMOVE <3
//        game.shapeRenderer.setProjectionMatrix(game.gameCamera.combined);
//        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
//        game.shapeRenderer.setColor(Color.GREEN);
//        for (Room r : game.ctx.rooms)
//            game.shapeRenderer.rect(r.getBounds().x, r.getBounds().y, r.getBounds().width, r.getBounds().height);
//        game.shapeRenderer.setColor(Color.YELLOW);
//        for (Rectangle h : getActiveWalkableZones())
//            game.shapeRenderer.rect(h.x, h.y, h.width, h.height);
//        game.shapeRenderer.end();

        if (isMonologueActive) {
            drawMonologueOverlay(delta);
        } else {
            drawHUD();
        }

        if (showInventory) {
            drawInventoryOverlay();
        }

        if (showVictoryPopup) {
            drawVictoryPopup();
        }

        drawFadeOverlay();

        // ESC → Save Game and return to main menu
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) && !fadingOut) {
            game.ctx.saveGame(this.mapName, game.ctx.player.getX(), game.ctx.player.getY());
            startFadeOut(new MainMenuScreen(game));
        }
    }

    private void startMonologue(String[] lines, boolean rightAligned) {
        stopMonologueSfx();

        currentMonologue = lines != null ? lines : new String[0];
        currentMonologueRightAligned = rightAligned;
        isMonologueActive = true;
        prepareMonologue();
    }

    private void handlePlayerDeath() {
        game.ctx.lives--;
        game.ctx.enemiesDefeatedInCurrentMap = 0;
        game.ctx.rooms.clear();
        game.ctx.mapEnemies.clear();
        game.ctx.activeCharacterStats.resetStats();
        game.ctx.player = null;

        if (game.ctx.lives <= 0) {
            game.ctx.lives = 1;
            game.setScreen(new NameInputScreen(game, game.ctx, 1));
        } else {
            // Player lost a life but has more, retry current map
            switch (game.ctx.mapName) {
                case TOWN_OF_ECHOES:
                    game.setScreen(new TownOfEchoesScreen(game));
                    break;
                case SILENT_CAVERNS:
                    game.setScreen(new SilentCavernsScreen(game));
                    break;
                case ABYSS_OF_DISSONANCE:
                    game.setScreen(new AbyssOfDissonanceScreen(game));
                    break;
                default:
                    game.setScreen(new TownOfEchoesScreen(game));
                    break;
            }
        }
    }

    private com.badlogic.gdx.Screen getDeathDestinationScreen() {
        game.ctx.lives--;
        game.ctx.enemiesDefeatedInCurrentMap = 0;
        game.ctx.rooms.clear();
        game.ctx.mapEnemies.clear();
        game.ctx.activeCharacterStats.resetStats();
        game.ctx.player = null;

        if (game.ctx.lives <= 0) {
            game.ctx.lives = 1;
            return new NameInputScreen(game, game.ctx, 1);
        }

        switch (game.ctx.mapName) {
            case TOWN_OF_ECHOES:
                return new TownOfEchoesScreen(game);
            case SILENT_CAVERNS:
                return new SilentCavernsScreen(game);
            case ABYSS_OF_DISSONANCE:
                return new AbyssOfDissonanceScreen(game);
            default:
                return new TownOfEchoesScreen(game);
        }
    }


    // ── Movement ──────────────────────────────────────────────────────────────
    private void handleMovement(float delta) {
        if (showVictoryPopup) return;

        if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {
            showInventory = !showInventory;
        }

        if (showInventory) {
            game.ctx.playerState = GameContext.PlayerState.IDLE;
            return;
        }

        if (showingExitPrompt) {
            game.ctx.playerState = GameContext.PlayerState.IDLE;
            return;
        }

        float move = GameContext.SPEED * delta;
        float prevX = game.ctx.player.getX();
        float prevY = game.ctx.player.getY();

        game.ctx.playerState = GameContext.PlayerState.IDLE;

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

        if (!isInWalkableZone(game.ctx.player.getX(), game.ctx.player.getY())) {
            game.ctx.player.setX(prevX);
            game.ctx.player.setY(prevY);
        }

        float S = game.ctx.MAP_SIZE, C = GameContext.CHAR_SIZE;
        game.ctx.player.setX(MathUtils.clamp(game.ctx.player.getX(), 0, S - C));
        game.ctx.player.setY(MathUtils.clamp(game.ctx.player.getY(), 0, S - C));

        // Exit Logic
        if (game.ctx.exitRoom != null) {
            float exitSize = 104f;
            Rectangle pRect = new Rectangle(game.ctx.player.getX(), game.ctx.player.getY(), C, C);
            Rectangle exitRect = new Rectangle(
                game.ctx.exitRoom.getBounds().x + (game.ctx.exitRoom.getBounds().width - exitSize) / 2f,
                game.ctx.exitRoom.getBounds().y + (game.ctx.exitRoom.getBounds().height) / 1.16f,
                exitSize, exitSize);

            if (pRect.overlaps(exitRect)) {
                if (!atExit) { showingExitPrompt = true; atExit = true; }
            } else atExit = false;
        }

        if (showingExitPrompt) {
            com.badlogic.gdx.math.Vector3 touch = new com.badlogic.gdx.math.Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            game.uiCamera.unproject(touch);

            if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                boolean canExit = game.ctx.enemiesDefeatedInCurrentMap >= getRequiredKills();

                if (canExit) {
                    if (yesButtonRect.contains(touch.x, touch.y)) {
                        ExploringScreen next = getNextScreen();
                        if (next != null) {
                            if (mapExit != null && mapExit.length > 0) {
                                // EXIT MONOLOGUE
                                currentMonologue = mapExit;
                                isMonologueActive = true;
                                pendingExit = true; // Wait for monologue to finish
                            } else {
                                // Reset state before transitioning to new map
                                game.ctx.player = null;
                                game.ctx.enemiesDefeatedInCurrentMap = 0;
                                game.ctx.rooms.clear();
                                game.ctx.mapEnemies.clear();
                                game.ctx.exitRoom = null;
                                startFadeOut(next);
                            }
                        }
                        showingExitPrompt = false;
                    } else if (noButtonRect.contains(touch.x, touch.y)) {
                        showingExitPrompt = false;
                        atExit = false;
                        game.ctx.player.setY(game.ctx.player.getY() - 15f);
                    }
                } else {
                    if (okButtonRect.contains(touch.x, touch.y)) {
                        showingExitPrompt = false;
                        atExit = false;
                        game.ctx.player.setY(game.ctx.player.getY() - 30f);
                    }
                }
            }
            return;
        }

        // Enemy collision
        Rectangle pRect = new Rectangle(game.ctx.player.getX(), game.ctx.player.getY(), C, C);
        for (Enemy e : game.ctx.mapEnemies) {
            if (e.isDefeated()) continue;

            Rectangle eRect = new Rectangle(e.getX(), e.getY(), C, C);
            if (pRect.overlaps(eRect)) {
                game.ctx.player.setX(prevX);
                game.ctx.player.setY(prevY);
                game.ctx.currentEnemy = e;
                game.ctx.noteHandler.noteCount = 0;

                // FINAL BOSS ENCOUNTER
                // Sequence:
                // 1. Player pre-final battle monologue
                // 2. Maestro Syozan dialogue, right-aligned
                // 3. Combat proper
                if (e.getName().equals("Maestro Syozan")) {
                    startMonologue(game.ctx.activeCharacterStats.getMonologues().preFinalBattle, false);

                    pendingBossDialogue = true;
                    pendingCombat = false;
                    return;
                }

                // NORMAL ENEMY ENCOUNTER MONOLOGUE
                String[][] encounters = {
                    game.ctx.activeCharacterStats.getMonologues().enemyEncounterV1,
                    game.ctx.activeCharacterStats.getMonologues().enemyEncounterV2,
                    game.ctx.activeCharacterStats.getMonologues().enemyEncounterV3,
                    game.ctx.activeCharacterStats.getMonologues().enemyEncounterV4
                };

                startMonologue(encounters[RNG.nextInt(encounters.length)], false);

                pendingCombat = true;
                return;
            }
        }
    }

    private void performExit(ExploringScreen nextScreen) {
        if (nextScreen != null) {
            game.ctx.player = null;
            game.ctx.enemiesDefeatedInCurrentMap = 0;
            game.ctx.rooms.clear();
            game.ctx.mapEnemies.clear();
            game.ctx.exitRoom = null;
            game.setScreen(nextScreen);
        }
    }

    private void drawExitOverlay() {
        if (!showingExitPrompt) return;

        Texture promptBg = game.assets.exitPromptBg;
        Texture yesBtn = game.assets.exitPromptYesBtn;
        Texture noBtn = game.assets.exitPromptNoBtn;

        boolean canExit = game.ctx.enemiesDefeatedInCurrentMap >= getRequiredKills();

        float boxW = promptBg.getWidth();
        float boxH = promptBg.getHeight();
        float boxX = screenLeft + ((Main.WORLD_WIDTH - boxW) / 2f);
        float boxY = screenBottom + ((Main.WORLD_HEIGHT - boxH) / 2f);
        float boxTop = boxY + boxH;

        // Dim background
        Gdx.gl.glEnable(com.badlogic.gdx.graphics.GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(
            com.badlogic.gdx.graphics.GL20.GL_SRC_ALPHA,
            com.badlogic.gdx.graphics.GL20.GL_ONE_MINUS_SRC_ALPHA);

        game.shapeRenderer.setProjectionMatrix(game.uiCamera.combined);
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(0f, 0f, 0f, 0.6f);
        game.shapeRenderer.rect(0, 0, Main.WORLD_WIDTH, Main.WORLD_HEIGHT);
        game.shapeRenderer.end();

        Gdx.gl.glDisable(com.badlogic.gdx.graphics.GL20.GL_BLEND);

        game.batch.setProjectionMatrix(game.uiCamera.combined);
        game.batch.begin();
        game.batch.setColor(Color.WHITE);

        // Prompt background container. Header "Leave Room" is assumed to be part of this asset.
        game.batch.draw(promptBg, boxX, boxY, boxW, boxH);

        if (canExit) {
            // Player can leave: show confirmation text and Yes/No buttons.
            game.assets.font.getData().setScale(1.1f);
            game.assets.font.setColor(Color.WHITE);

            String prompt = "Do you want to explore the next area?";
            float promptX = screenLeft + ((Main.WORLD_WIDTH - textWidth(prompt)) / 2f);
            float promptY = boxTop - px(7f);
            game.assets.font.draw(game.batch, prompt, promptX, promptY);

            float btnW = yesBtn.getWidth();
            float btnH = yesBtn.getHeight();
            float gap = px(1.0f);
            float totalBtnW = btnW + gap + noBtn.getWidth();

            float yesX = screenLeft + ((Main.WORLD_WIDTH - totalBtnW) / 2f);
            float noX = yesX + btnW + gap;
            float btnY = promptY - px(1.6f) - btnH;

            yesButtonRect.set(yesX, btnY, btnW, btnH);
            noButtonRect.set(noX, btnY, noBtn.getWidth(), noBtn.getHeight());

            game.batch.draw(yesBtn, yesButtonRect.x, yesButtonRect.y, yesButtonRect.width, yesButtonRect.height);
            game.batch.draw(noBtn, noButtonRect.x, noButtonRect.y, noButtonRect.width, noButtonRect.height);
        } else {
            // Player cannot leave yet: show locked message and an OK/continue hint.
            game.assets.font.getData().setScale(1.1f);
            game.assets.font.setColor(Color.RED);

            String lockedText = "The path ahead is still sealed.";
            float lockedX = screenLeft + ((Main.WORLD_WIDTH - textWidth(lockedText)) / 2f);
            float lockedY = boxTop - px(7f);
            game.assets.font.draw(game.batch, lockedText, lockedX, lockedY);

            game.assets.font.getData().setScale(0.8f);
            game.assets.font.setColor(Color.LIGHT_GRAY);

            String hint = "Press ENTER to continue";
            float hintX = screenLeft + ((Main.WORLD_WIDTH - textWidth(hint)) / 2f);
            float hintY = boxY + px(0.8f);
            game.assets.font.draw(game.batch, hint, hintX, hintY);

            // Keep an OK rect around the whole prompt input area for mouse support too.
            okButtonRect.set(boxX, boxY, boxW, boxH);

            // Optional: draw an OK button asset if you want one later.
            // game.batch.draw(okBtn, okX, okY, okBtn.getWidth(), okBtn.getHeight());
        }

        game.assets.font.getData().setScale(1.0f);
        game.assets.font.setColor(Color.WHITE);
        game.batch.setColor(Color.WHITE);
        game.batch.end();

        handleExitOverlayInput(canExit);
    }

    private void handleExitOverlayInput(boolean canExit) {
        com.badlogic.gdx.math.Vector3 touch =
            new com.badlogic.gdx.math.Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        game.uiCamera.unproject(touch);

        if (canExit) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)
                || Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && yesButtonRect.contains(touch.x, touch.y)) {
                confirmExitToNextMap();
                return;
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)
                || Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && noButtonRect.contains(touch.x, touch.y)) {
                cancelExitPrompt();
                return;
            }
        } else {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)
                || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)
                || Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                showingExitPrompt = false;
                atExit = false;
                game.ctx.player.setY(game.ctx.player.getY() - 30f);
            }
        }
    }

    private void confirmExitToNextMap() {
        ExploringScreen next = getNextScreen();
        if (next != null) {
            if (mapExit != null && mapExit.length > 0) {
                // EXIT MONOLOGUE
                startMonologue(mapExit, false);
                pendingExit = true;
            } else {
                // Reset state before transitioning to new map
                game.ctx.player = null;
                game.ctx.enemiesDefeatedInCurrentMap = 0;
                game.ctx.rooms.clear();
                game.ctx.mapEnemies.clear();
                game.ctx.exitRoom = null;

                game.assets.traversalSFX.play(3.0f);
                startFadeOut(next);
            }
        }

        showingExitPrompt = false;
    }

    private void cancelExitPrompt() {
        showingExitPrompt = false;
        atExit = false;
        game.ctx.player.setY(game.ctx.player.getY() - 15f);
    }


    private boolean isInWalkableZone(float x, float y) {
        Rectangle playerRect = new Rectangle(x, y, GameContext.CHAR_SIZE, GameContext.CHAR_SIZE);
        for (Rectangle zone : getActiveWalkableZones())
            if (zone.overlaps(playerRect))
                return true;
        return false;
    }

    private void startMonologueSfx() {
        if (monologueSfxPlaying) return;

        if (game.assets.monologueSFX != null) {
            monologueSfxId = game.assets.monologueSFX.loop(3.0f);
            monologueSfxPlaying = true;
        }
    }

    private void stopMonologueSfx() {
        if (!monologueSfxPlaying) return;

        if (game.assets.monologueSFX != null) {
            game.assets.monologueSFX.stop(monologueSfxId);
        }

        monologueSfxId = -1L;
        monologueSfxPlaying = false;
    }

    // ── Camera ────────────────────────────────────────────────────────────────

    private void updateCamera() {
        if (game.ctx.player == null || fadingOut) return;
        game.gameCamera.zoom = 0.6f;

        float effectiveHalfW = (Main.WORLD_WIDTH * game.gameCamera.zoom) / 2f;
        float effectiveHalfH = (Main.WORLD_HEIGHT * game.gameCamera.zoom) / 2f;

        float S = game.ctx.MAP_SIZE;
        float C = GameContext.CHAR_SIZE;

        float camX = MathUtils.clamp(game.ctx.player.getX() + C / 2f, effectiveHalfW, S - effectiveHalfW);
        float camY = MathUtils.clamp(game.ctx.player.getY() + C / 2f, effectiveHalfH, S - effectiveHalfH);

        game.gameCamera.position.set(camX, camY, 0);
        game.gameCamera.update();
    }

    // ── Player sprite ─────────────────────────────────────────────────────────

    private void drawPlayerSprite() {
        switch (game.ctx.selectedCharacter) {
            case SONARA:   drawCharacter(
                game.assets.sonaraIdleRight,  game.assets.sonaraIdleLeft,
                game.assets.sonaraWalkRight,  game.assets.sonaraWalkLeft); break;
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
        CharacterHero c = game.ctx.activeCharacterStats;
        game.uiCamera.update();

        // Stats
        renderPlayerStats();

        // Draw new HUD buttons vertically
        float btnWidth = 100f; // Adjusted width for larger horizontal text-based buttons to fit the texture well
        float btnHeight = 27f; // Adjusted height
        float spacing = 10f;
        float currentX = 5f;

        // Calculate starting Y so they stack upwards from the bottom
        // Inventory is at the bottom, Menu is on top
        float startY = 15f;

        game.batch.begin();
        if (game.assets.inventoryBtnTex != null) {
            game.batch.draw(game.assets.inventoryBtnTex, currentX, startY, btnWidth, btnHeight);
        } else {
            game.assets.font.setColor(Color.GRAY);
            game.assets.font.draw(game.batch, "I – Inventory", currentX, startY + btnHeight);
        }

        startY += btnHeight + spacing;

        if (game.assets.menuBtnTex != null) {
            game.batch.draw(game.assets.menuBtnTex, currentX, startY, btnWidth, btnHeight);
        } else {
            game.assets.font.setColor(Color.GRAY);
            game.assets.font.draw(game.batch, "ESC – Menu", currentX, startY + btnHeight);
        }

        // MAP HEADER
        Texture mapHeader = null;
        switch (game.ctx.mapName) {
            case TOWN_OF_ECHOES: mapHeader = game.assets.mapHeaderTownOfEchoes; break;
            case SILENT_CAVERNS: mapHeader = game.assets.mapHeaderSilentCaverns; break;
            case ABYSS_OF_DISSONANCE: mapHeader = game.assets.mapHeaderAbyssOfDissonance; break;
        }
        game.batch.draw(mapHeader,
            screenRight - px(1.0f) - mapHeader.getWidth() * 1.6f,
            screenTop - px(1.0f) - mapHeader.getHeight()  * 1.6f,
            mapHeader.getWidth()  * 1.6f, mapHeader.getHeight()  * 1.6f);

        game.batch.end();

        drawExitOverlay();

        // Handle HUD button clicks
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && !fadingOut) {
            com.badlogic.gdx.math.Vector3 mousePos = new com.badlogic.gdx.math.Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            game.uiCamera.unproject(mousePos);

            float checkX = 10f;
            float checkY = 10f; // Start checking from bottom again

            // Inventory button logic
            if (game.assets.inventoryBtnTex != null) {
                if (mousePos.x >= checkX && mousePos.x <= checkX + btnWidth &&
                    mousePos.y >= checkY && mousePos.y <= checkY + btnHeight) {
                    showInventory = !showInventory;
                }
            }
            checkY += btnHeight + spacing;

            // Menu button logic
            if (game.assets.menuBtnTex != null) {
                if (mousePos.x >= checkX && mousePos.x <= checkX + btnWidth &&
                    mousePos.y >= checkY && mousePos.y <= checkY + btnHeight) {
                    game.ctx.saveGame(this.mapName, game.ctx.player.getX(), game.ctx.player.getY());
                    startFadeOut(new MainMenuScreen(game));
                }
            }
        }
    }

    private void renderPlayerStats(){
        float cardScale = 0.64f;
        float cardWidth = 135 * cardScale;
        float cardHeight = 177 * cardScale;
        float cardX = screenLeft + px(1.0f);
        float cardY = screenTop - px(1.0f) - cardHeight;
        float statsOffsetX = px(0.8f);
        float playerStatsX = cardX + cardWidth + statsOffsetX;
        float playerStatsY = cardY;

        // Player Card
        TextureRegion animFrame = null;
        switch(game.ctx.selectedCharacter){
            case SONARA:  animFrame = game.assets.sonaraCardSelected.getKeyFrame(game.ctx.stateTime, true); break;
            case AURELIUS: animFrame = game.assets.aureliusCardSelected.getKeyFrame(game.ctx.stateTime, true); break;
            case LYRON: animFrame = game.assets.lyronCardSelected.getKeyFrame(game.ctx.stateTime, true); break;
        }

        game.batch.setProjectionMatrix(game.uiCamera.combined);
        game.batch.begin();
        if (animFrame != null) { game.batch.draw(animFrame, cardX, cardY, cardWidth, cardHeight);}
        game.batch.end();

        // Status Bars
        renderStatsBars(playerStatsX, playerStatsY);

        game.batch.setProjectionMatrix(game.uiCamera.combined);
        game.batch.begin();

        // Name
        game.assets.font.setColor(Color.WHITE);
        game.assets.font.getData().setScale(1.2f);
        String text = game.ctx.activeCharacterStats.getName();
        float textX = playerStatsX;
        float textY = playerStatsY + cardHeight - px(0.16f);
        game.assets.font.draw(game.batch, text, textX, textY);

        // Level
        game.assets.font.setColor(Color.YELLOW);
        game.assets.font.getData().setScale(1.0f);
        text = "Level " + game.ctx.activeCharacterStats.getLevel();
        textX = playerStatsX + px(4.0f);
        textY = playerStatsY + cardHeight - px(0.2f);
        game.assets.font.draw(game.batch, text, textX, textY);

        // Lives
        game.assets.font.setColor(Color.WHITE);
        game.assets.font.getData().setScale(1.0f);
        text = "Lives: ";
        textX = playerStatsX;
        textY = playerStatsY + px(0.4f);
        game.assets.font.draw(game.batch, text, textX, textY);

        // Hearts
        int heartsRender = game.ctx.lives;
        Texture heart = null;

        float heartX        = textX + textWidth(text) + px(0.2f);
        float heartY        = playerStatsY;
        float heartWidth    = px(0.6f);
        float heartHeight   = px(0.6f);
        float heartGap      = px(0.2f);

        for(int i = 0; i < game.ctx.maxLives; i++){
            heart = (i < heartsRender ? game.assets.heart : game.assets.heartEmpty);
            game.batch.draw(heart,
                heartX + i * (heartWidth + heartGap), heartY,
                heartWidth, heartHeight);
        }

        boolean isAbyssOfDissonance = false;
        switch (game.ctx.mapName) {
            case ABYSS_OF_DISSONANCE: isAbyssOfDissonance = true;
                text = "Defeat: " + (game.ctx.isLabagoliathDefeated ? "Maestro Syozan!" : " Labagoliath the Void Shaker!"); break;
            default: text = "Eliminate monsters: " + game.ctx.enemiesDefeatedInCurrentMap + "   /   " + getRequiredKills();
        }
        float textWidth = textWidth(text);
        float textHeight = px(1.6f);
        textX = screenRight - textWidth - px(1.6f);
        textY = screenTop - textHeight - px(2.0f);
        game.assets.font.setColor(Color.RED);
        if(game.ctx.enemiesDefeatedInCurrentMap >= getRequiredKills() && !isAbyssOfDissonance) game.assets.font.setColor(Color.GREEN);
        game.assets.font.getData().setScale(1.0f);
        game.assets.font.draw(game.batch, text, textX, textY);

        text = "Objectives: ";
        textX = textX;
        textY += px(0.48f);
        game.assets.font.setColor(Color.YELLOW);
        game.assets.font.getData().setScale(0.8f);
        game.assets.font.draw(game.batch, text, textX, textY);


        // IF ROOM IS LOCKED
        if(isInEnemyRoom()){
            text = "Room Locked! You must defeat the monster.";
            textWidth = textWidth(text);
            textX = screenRight - textWidth - px(4.0f);
            textY = screenBottom + px(1.0f); // Changed Y position to be visible at the bottom of the screenen
            game.assets.font.setColor(Color.RED);
            game.assets.font.getData().setScale(1.2f); // Increased scale
            game.assets.font.draw(game.batch, text, textX, textY);
        }
        game.batch.end();
    }

    private void renderStatsBars(float baseX, float baseY) {
        CharacterHero player = game.ctx.activeCharacterStats;

        final float barWidth           = 144f;
        final float barHeight          = 11.8f;
        final float containerWidth     = 180f;
        final float containerHeight    = 32f;
        final float barOffsetX         = px(0.4f);
        final float hpBarOffsetY       = px(2.4f);
        final float shieldBarOffsetY   = px(1.6f);
        final float containerOffsetX   = -27f;
        final float containerOffsetY   = -21f;
        final float textOffsetX        = barWidth + px(0.4f);

        float playerHpBarX     = baseX + barOffsetX;
        float playerHpBarY     = baseY + hpBarOffsetY;
        float playerShieldBarX = baseX + barOffsetX;
        float playerShieldBarY = baseY + shieldBarOffsetY;

        game.shapeRenderer.setProjectionMatrix(game.uiCamera.combined);
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        drawBar(game.shapeRenderer,
            playerHpBarX, playerHpBarY, barWidth, barHeight,
            (float) player.getHp() / player.getMaxHp(),
            Color.DARK_GRAY, Color.RED);

        drawBar(game.shapeRenderer,
            playerShieldBarX, playerShieldBarY, barWidth, barHeight,
            player.getMaxShield() > 0 ? (float) player.getShield() / player.getMaxShield() : 0f,
            Color.DARK_GRAY, new Color(0.07f, 0.58f, 0.93f, 1));
        game.shapeRenderer.end();

        // Stats Bar Border
        game.batch.setProjectionMatrix(game.uiCamera.combined);
        game.batch.begin();
        game.batch.draw(game.assets.healthBar,
            playerHpBarX + containerOffsetX, playerHpBarY + containerOffsetY,
            containerWidth, containerHeight);
        game.batch.draw(game.assets.shieldBar,
            playerShieldBarX + containerOffsetX, playerShieldBarY + containerOffsetY,
            containerWidth, containerHeight);
        game.assets.font.getData().setScale(1.0f);
        game.batch.end();
    }

    private void drawVictoryPopup() {
        if (!showVictoryPopup) return;

        Texture popupBg = game.assets.victoryPopupBg; // Placeholder asset name

        float popupW = popupBg.getWidth();
        float popupH = popupBg.getHeight();
        float popupX = screenLeft + ((Main.WORLD_WIDTH - popupW) / 2f);
        float popupY = screenBottom + ((Main.WORLD_HEIGHT - popupH) / 2f);
        float popupTop = popupY + popupH;
        float popupBottom = popupY;

        // Dim background
        Gdx.gl.glEnable(com.badlogic.gdx.graphics.GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(
            com.badlogic.gdx.graphics.GL20.GL_SRC_ALPHA,
            com.badlogic.gdx.graphics.GL20.GL_ONE_MINUS_SRC_ALPHA);

        game.shapeRenderer.setProjectionMatrix(game.uiCamera.combined);
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(0f, 0f, 0f, 0.6f);
        game.shapeRenderer.rect(0, 0, Main.WORLD_WIDTH, Main.WORLD_HEIGHT);
        game.shapeRenderer.end();

        Gdx.gl.glDisable(com.badlogic.gdx.graphics.GL20.GL_BLEND);

        game.batch.setProjectionMatrix(game.uiCamera.combined);
        game.batch.begin();
        game.batch.setColor(Color.WHITE);

        // Victory popup container
        game.batch.draw(popupBg, popupX, popupY, popupW, popupH);

        // Level / progress text
        game.assets.font.getData().setScale(1.1f);
        game.assets.font.setColor(Color.YELLOW);

        String levelText = resolveVictoryLevelText();
        float levelTextX = screenLeft + ((Main.WORLD_WIDTH - textWidth(levelText)) / 2f);
        float levelTextY = popupTop - px(7.0f);
        game.assets.font.draw(game.batch, levelText, levelTextX, levelTextY);

        // Items section header
        game.assets.font.getData().setScale(0.8f);
        game.assets.font.setColor(Color.WHITE);

        String itemsHeader = "Items Obtained";
        float itemsHeaderX = screenLeft + ((Main.WORLD_WIDTH - textWidth(itemsHeader)) / 2f);
        float itemsHeaderY = levelTextY - px(1.2f);
        game.assets.font.draw(game.batch, itemsHeader, itemsHeaderX, itemsHeaderY);

        // Item icons in one centered row
        if (!droppedItemIcons.isEmpty()) {
            float itemW = droppedItemIcons.get(0).getWidth();
            float itemH = droppedItemIcons.get(0).getHeight();
            float itemScale = 1.0f;
            float displayItemW = itemW * itemScale;
            float displayItemH = itemH * itemScale;
            float gap = px(0.4f);

            int itemCount = droppedItemIcons.size();
            float itemDropsDisplayWidth = (itemCount * displayItemW) + ((itemCount - 1) * gap);

            float itemX = screenLeft + ((Main.WORLD_WIDTH - itemDropsDisplayWidth) / 2f);
            float itemY = itemsHeaderY - px(0.8f) - itemH;

            int i = 0;
            while (i < itemCount) {
                Texture icon = droppedItemIcons.get(i);
                if (icon != null) {
                    game.batch.draw(icon,
                        itemX + i * (displayItemW + gap),
                        itemY,
                        displayItemW,
                        displayItemH);
                }
                i++;
            }
        }

        // Bottom hint
        game.assets.font.getData().setScale(0.8f);
        game.assets.font.setColor(Color.LIGHT_GRAY);

        String hint = "Press ENTER to continue";
        float hintX = screenLeft + ((Main.WORLD_WIDTH - textWidth(hint)) / 2f);
        float hintY = popupBottom + px(0.8f);
        game.assets.font.draw(game.batch, hint, hintX, hintY);

        game.assets.font.getData().setScale(1.0f);
        game.assets.font.setColor(Color.WHITE);
        game.batch.setColor(Color.WHITE);
        game.batch.end();

        // Input
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)
            || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            showVictoryPopup = false;

            if (leveledUp && currentMonologue != null) {
                startMonologue(currentMonologue, false);
            }
        }
    }

    private String resolveVictoryLevelText() {
        CharacterHero player = game.ctx.activeCharacterStats;

        int currentLevel = player.getLevel();
        int maxLevel = 5;

        if (leveledUp) {
            return "Level Up!  Level " + (newLevel - 1) + "  ->  " + newLevel;
        }

        if (currentLevel >= maxLevel) {
            return "You reached the MAX level";
        }

        int kills = player.getMonstersDefeated();
        int nextLevelRequirement;

        if      (currentLevel == 1) nextLevelRequirement = 1;
        else if (currentLevel == 2) nextLevelRequirement = 2;
        else if (currentLevel == 3) nextLevelRequirement = 4;
        else                       nextLevelRequirement = 7;

        int killsNeeded = Math.max(0, nextLevelRequirement - kills);

        int mapLevelCap = 5;
        if (game.ctx.mapName != null) {
            switch (game.ctx.mapName) {
                case TOWN_OF_ECHOES:      mapLevelCap = 3; break;
                case SILENT_CAVERNS:      mapLevelCap = 5; break;
                case ABYSS_OF_DISSONANCE: mapLevelCap = 5; break;
            }
        }

        if (currentLevel >= mapLevelCap) {
            return "Enter next map and eliminate:   [ " + killsNeeded + " ] monsters.";
        }

        return "Eliminate " + killsNeeded + " more monsters to level up.";
    }

    private void triggerVictoryPopup() {
        CharacterHero player = game.ctx.activeCharacterStats;

        // Level-up check
        int kills        = player.getMonstersDefeated();
        int currentLevel = player.getLevel();
        int targetLevel  = currentLevel;

        if      (kills >= 7) targetLevel = 5;
        else if (kills >= 4) targetLevel = 4;
        else if (kills >= 2) targetLevel = 3;
        else if (kills >= 1) targetLevel = 2;

        int mapLevelCap = 5;
        if (game.ctx.mapName != null) {
            switch (game.ctx.mapName) {
                case TOWN_OF_ECHOES:      mapLevelCap = 3; break;
                case SILENT_CAVERNS:      mapLevelCap = 5; break;
                case ABYSS_OF_DISSONANCE: mapLevelCap = 5; break;
            }
        }

        leveledUp = false;
        newLevel  = 0;
        if (targetLevel > currentLevel && targetLevel <= mapLevelCap) {
            player.levelUp(targetLevel);
            leveledUp = true;
            newLevel  = targetLevel;
        }

        // Item drops are guaranteed.
        // Town of Echoes: 1-2 items
        // Silent Caverns: 1-3 items
        // Abyss of Dissonance: 3-5 items
        droppedItemNames.clear();
        droppedItemIcons.clear();

        int dropCount = 1;
        if (game.ctx.mapName != null) {
            switch (game.ctx.mapName) {
                case TOWN_OF_ECHOES:
                    dropCount = 1 + RNG.nextInt(2);
                    break;
                case SILENT_CAVERNS:
                    dropCount = 1 + RNG.nextInt(3);
                    break;
                case ABYSS_OF_DISSONANCE:
                    dropCount = 3 + RNG.nextInt(6);
                    break;
            }
        }

        Inventory inv = player.getPlayerInventory();
        for (int i = 0; i < dropCount; i++) { addRandomDrop(inv); }

        game.ctx.enemiesDefeatedInCurrentMap++;
        showVictoryPopup = true;

        if (leveledUp) {
            game.assets.levelUpSFX.play(1.0f);

            switch (newLevel) {
                case 2: currentMonologue = game.ctx.activeCharacterStats.getMonologues().firstLevelUp;  break;
                case 3: currentMonologue = game.ctx.activeCharacterStats.getMonologues().secondLevelUp; break;
                case 4: currentMonologue = game.ctx.activeCharacterStats.getMonologues().thirdLevelUp;  break;
                case 5: currentMonologue = game.ctx.activeCharacterStats.getMonologues().fourthLevelUp; break;
            }
        }
    }

    private void addRandomDrop(Inventory inv) {
        int lastDropType = -1;

        if (!droppedItemNames.isEmpty()) {
            switch(droppedItemNames.get(droppedItemNames.size() - 1)){
                case "Crimson Chorus":      lastDropType = 0; break;
                case "Major's Blessing":    lastDropType = 1; break;
                case "Minor's Grace":       lastDropType = 2; break;
                case "Resolved Dissonance": lastDropType = 3; break;
                case "Silent Barrier":      lastDropType = 4; break;
                case "Time Orb":            lastDropType = 5; break;
            }
        }

        int itemType;

        // Unique Item Drops
        do { itemType = RNG.nextInt(6);
        } while (itemType == lastDropType);

        switch (itemType) {
            case 0:
                inv.gainCrimsonChorus(game.assets);
                droppedItemNames.add("Crimson Chorus");
                break;
            case 1:
                inv.gainMajorBlessing(game.assets);
                droppedItemNames.add("Major's Blessing");
                break;
            case 2:
                inv.gainMinorsGrace(game.assets);
                droppedItemNames.add("Minor's Grace");
                break;
            case 3:
                inv.gainResolvedDissonance(game.assets);
                droppedItemNames.add("Resolved Dissonance");
                break;
            case 4:
                inv.gainSilentBarrier(game.assets);
                droppedItemNames.add("Silent Barrier");
                break;
            case 5:
                inv.gainTimeOrb(game.assets);
                droppedItemNames.add("Time Orb");
                break;
        }

        lastDropType = itemType;
        droppedItemIcons.add( inv.getItem(inv.getInventorySize() - 1).getSlotIcon());
    }

    // ── Inventory Overlay ─────────────────────────────────────────────────────
    private void drawInventoryOverlay() {
        Gdx.gl.glEnable(com.badlogic.gdx.graphics.GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(com.badlogic.gdx.graphics.GL20.GL_SRC_ALPHA, com.badlogic.gdx.graphics.GL20.GL_ONE_MINUS_SRC_ALPHA);

        game.shapeRenderer.setProjectionMatrix(game.uiCamera.combined);
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(0f, 0f, 0f, 0.7f); // Low opacity black background
        game.shapeRenderer.rect(0, 0, Main.WORLD_WIDTH, Main.WORLD_HEIGHT);
        game.shapeRenderer.end();
        Gdx.gl.glDisable(com.badlogic.gdx.graphics.GL20.GL_BLEND);

        Texture inventoryPanelBackground = game.assets.inventoryPanelBackground;
        Inventory inventory = game.ctx.activeCharacterStats.getPlayerInventory();

        float inventoryPanelWidth = inventoryPanelBackground.getWidth();
        float inventoryPanelHeight =  inventoryPanelBackground.getHeight();
        float inventoryPanelX = (Main.WORLD_WIDTH - inventoryPanelWidth)/ 2f;
        float inventoryPanelY = (Main.WORLD_HEIGHT - inventoryPanelHeight) /2f;

        game.batch.setProjectionMatrix(game.uiCamera.combined);
        game.batch.begin();
        game.batch.draw(inventoryPanelBackground,
            inventoryPanelX, inventoryPanelY, inventoryPanelWidth, inventoryPanelHeight);
        game.batch.end();

        float Xgap = px(0.1f);
        float Ygap = px(0.1f);
        float itemSlotWidth = px(2.0f);
        float itemSlotHeight = px(2.0f);
        float itemSlotsWidth = itemSlotWidth * 5 + (4 * Xgap);
        float itemSlotsHeight = itemSlotHeight * 2 + Ygap;

        // Render Item Slots
        game.batch.setProjectionMatrix(game.uiCamera.combined);
        game.batch.begin();
        float itemXPosition = inventoryPanelX + ((inventoryPanelWidth - itemSlotsWidth) / 2f);
        float itemYPosition = (inventoryPanelY + inventoryPanelHeight) - ((inventoryPanelHeight - itemSlotsHeight) / 2f) - px(0.6f);
        int capacity = inventory.getCapacity();
        int cols = capacity / 2; // 5
        Texture item = game.assets.emptySlotItem;

        for(int i = 0; i < inventory.getCapacity(); i++){
            if(i < inventory.getInventorySize()){ item = inventory.getItem(i).getSlotIcon(); }
            else{ item = game.assets.emptySlotItem; }

            int col = i % cols;
            int row = i / cols;
            game.batch.draw(item,
                itemXPosition + col * (itemSlotWidth + Xgap),
                itemYPosition - itemSlotHeight - row * (itemSlotHeight + Ygap),
                itemSlotWidth,
                itemSlotHeight
            );
        }
        game.batch.end();
    }

    private void prepareMonologue(){
        currentMonologueIndex = 0;
        monologueCharIndex = 0;
        monologueTimer = 0f;
        lineDelayTimer = 0f;
    }

    private void handleMonologueInput(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            stopMonologueSfx();
            finishCurrentMonologue();
            return;
        }

        if (currentMonologue == null || currentMonologue.length == 0) {
            stopMonologueSfx();
            finishCurrentMonologue();
            return;
        }

        if (currentMonologueIndex >= currentMonologue.length) {
            stopMonologueSfx();
            finishCurrentMonologue();
            return;
        }

        if (currentMonologueIndex < currentMonologue.length) {
            String currentLine = currentMonologue[currentMonologueIndex];

            if (monologueCharIndex < currentLine.length()) {
                startMonologueSfx();

                monologueTimer += delta;
                while (monologueTimer >= TYPEWRITER_SPEED && monologueCharIndex < currentLine.length()) {
                    monologueTimer -= TYPEWRITER_SPEED;
                    monologueCharIndex++;
                }

                if (monologueCharIndex >= currentLine.length()) {
                    stopMonologueSfx();
                }
            } else {
                stopMonologueSfx();

                lineDelayTimer += delta;
                if (lineDelayTimer >= LINE_DELAY) {
                    lineDelayTimer = 0f;
                    monologueCharIndex = 0;
                    monologueTimer = 0f;
                    currentMonologueIndex++;

                    if (currentMonologueIndex >= currentMonologue.length) {
                        finishCurrentMonologue();
                    }
                }
            }
        }
    }

    private void finishCurrentMonologue() {
        stopMonologueSfx();
        isMonologueActive = false;

        if (pendingFinalBossVictoryLines) {
            pendingFinalBossVictoryLines = false;
            pendingFinalBossRecordScreen = true;

            startMonologue(game.ctx.bossVictoryLines, true);
            return;
        }

        if (pendingFinalBossDefeatLines) {
            pendingFinalBossDefeatLines = false;
            pendingFinalBossDeathReset = true;

            startMonologue(game.ctx.bossDefeatLines, true);
            return;
        }

        if (pendingFinalBossRecordScreen) {
            pendingFinalBossRecordScreen = false;
            currentMonologueRightAligned = false;

            game.ctx.mapsCleared = 3;

            startFadeOut(new EndingScreen(game));
            return;
        }

        if (pendingFinalBossDeathReset) {
            pendingFinalBossDeathReset = false;
            currentMonologueRightAligned = false;

            startFadeOut(getDeathDestinationScreen());
            return;
        }

        if (pendingExit) {
            pendingExit = false;

            game.ctx.player = null;
            game.ctx.enemiesDefeatedInCurrentMap = 0;
            game.ctx.rooms.clear();
            game.ctx.mapEnemies.clear();
            game.ctx.exitRoom = null;

            currentMonologueRightAligned = false;
            startFadeOut(getNextScreen());
            return;
        }

        if (pendingBossDialogue) {
            pendingBossDialogue = false;
            pendingCombat = true;

            startMonologue(game.ctx.bossPreCombatLines, true);
            return;
        }

        if (pendingCombat) {
            pendingCombat = false;
            game.ctx.combatState = GameContext.CombatState.BATTLE_SCREEN;
            game.ctx.playerWon = false;

            currentMonologueRightAligned = false;
            startFadeOut(new CombatScreen(game));
        }
    }

    private void drawMonologueOverlay(float delta) {
        TextureRegion animFrame = null;

        if (currentMonologueRightAligned) {
            // Syozan dialogue uses Syozan's own monologue box.
            // If the asset is missing, fall back to the selected player's box.
            if (game.assets.syozanMonologueBox != null) {
                animFrame = game.assets.syozanMonologueBox.getKeyFrame(game.ctx.stateTime, true);
            }
        }

        if (animFrame == null && !currentMonologueRightAligned) {
            switch(game.ctx.selectedCharacter){
                case SONARA:
                    animFrame = game.assets.sonaraMonologueBox.getKeyFrame(game.ctx.stateTime, true);
                    break;
                case AURELIUS:
                    animFrame = game.assets.aureliusMonologueBox.getKeyFrame(game.ctx.stateTime, true);
                    break;
                case LYRON:
                    animFrame = game.assets.lyronMonologueBox.getKeyFrame(game.ctx.stateTime, true);
                    break;
            }
        }

        if(animFrame == null) return;

        float boxX = 0f;
        float boxY = 0f;
        float boxWidth = animFrame.getRegionWidth();
        float boxHeight = animFrame.getRegionHeight();

        game.batch.setProjectionMatrix(game.uiCamera.combined);
        game.batch.begin();
        game.batch.draw(animFrame, boxX, boxY, boxWidth, boxHeight);

        float textY = boxY + px(3.2f);

        float playerTextLeft = boxX + px(7.2f);
        float playerTextRightPadding = px(3.0f);

        float textX = playerTextLeft;
        float wrapWidth = boxWidth - playerTextLeft - playerTextRightPadding;
        int alignment = com.badlogic.gdx.utils.Align.left;

        if (currentMonologueRightAligned) {
            float syozanTextLeft = boxX + px(2.0f);
            float enemyCardRightPadding = px(4.8f);
            float syozanTextRightPadding = px(2.0f);

            textX = syozanTextLeft;
            wrapWidth = boxWidth - syozanTextLeft - enemyCardRightPadding - syozanTextRightPadding;
            alignment = com.badlogic.gdx.utils.Align.right;
        }

        game.assets.font.setColor(Color.WHITE);
        game.assets.font.getData().setScale(1.2f);

        if (currentMonologueIndex < currentMonologue.length) {
            String currentLine = currentMonologue[currentMonologueIndex];
            int displayLen = Math.min(monologueCharIndex, currentLine.length());
            String textToDisplay = currentLine.substring(0, displayLen);

            game.assets.font.draw(game.batch, textToDisplay, textX, textY, wrapWidth, alignment, true);
        }

        game.assets.font.setColor(Color.GRAY);
        game.assets.font.getData().setScale(0.8f);
        game.assets.font.draw(game.batch, "Press ENTER to skip.",
            (!currentMonologueRightAligned ? Main.WORLD_WIDTH - px(5.0f) : px(1.2f)),
            px(1.6f));

        game.assets.font.getData().setScale(1.0f);
        game.batch.end();
    }

    @Override public void resize(int w, int h) {
        game.gameViewport.update(w, h, true);
        game.uiViewport.update(w, h, true);
    }
    @Override public void hide()    {stopMonologueSfx();}
    @Override
    public void dispose() {
        // Do not dispose of global assets here
        stopMonologueSfx();
    }
}
