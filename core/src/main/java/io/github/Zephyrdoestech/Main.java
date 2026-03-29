package io.github.Zephyrdoestech;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main extends ApplicationAdapter {
    // =========================================================================
    // Enums
    // =========================================================================

    private enum GameState {
        TITLE_SCREEN, MAIN_MENU,
        HOW_TO_PLAY, STORY, CREDITS,
        STORY_INTRODUCTION,CHARACTER_SELECT, CHARACTER_INTRODUCTION,
        MAP_INTRODUCTION, EXPLORING, PRE_COMBAT, COMBAT, POST_COMBAT
    }

    /**
     * Sub-state within COMBAT.
     *  BATTLE_SCREEN               - briefly displaying battle splash screen
     *  ENEMY_INTRODUCTION          - briefly introducing the monster / enemy characteristics and stats
     *  TUTORIAL                    - briefly displaying how to use combat-related systems (e.g. notes, metronome, chords, items)
     *  CHARACTER_PRECOMBAT_LINE    - briefly displaying character's line before combat
     *  TURN_MENU                   - waiting for the player to select an action (This is timed)
     *  ATTACK                      - waiting for the player to type 3 notes
     *  USE_SKILL                   - choose between using active skill or no
     *  OPEN_INVENTORY              - displaying inventory
     *  USE_ITEM                    - choose what item inside inventory to use
     *  DISPLAY_STATS               - displaying both player and enemy stats
     *  DISPLAY_ATTACK_GUIDE        - displaying player attack guide / combat mechanics
     *  DISPLAY_CHORDS              - displaying available chords to use in battle
     *  DISPLAY_PLAYER_DAMAGE       - briefly displaying dealt damage
     *  ENEMY_ATTACK                - displaying enemy attack
     *  DISPLAY_ENEMY_DAMAGE        - briefly displaying dealt damage by the enemy
     *  VICTORY        – enemy defeated; press ENTER to return to the map.
     *  DEFEAT         – player defeated; press ENTER to return to main menu.
     */
    private enum CombatState {
        BATTLE_SCREEN, ENEMY_INTRODUCTION, TUTORIAL, CHARACTER_PRECOMBAT_LINE,
        TURN_MENU, ATTACK, USE_SKILL, OPEN_INVENTORY, USE_ITEM, DISPLAY_STATS, DISPLAY_ATTACK_GUIDE, DISPLAY_CHORDS,
        DISPLAY_PLAYER_DAMAGE,
        ENEMY_ATTACK, DISPLAY_ENEMY_DAMAGE, VICTORY, DEFEAT, CHARACTER_POSTCOMBAT_LINE
    }

    private enum CharacterType { SONARA, AURELIUS, LYRON }
    private enum PlayerState   { IDLE, WALK_UP, WALK_DOWN, WALK_LEFT, WALK_RIGHT }
    private enum Facing        { LEFT, RIGHT }

    // =========================================================================
    // Intro slides
    // =========================================================================

    private static class IntroSlide {
        final String  title, body;
        final float   duration;
        final Texture image;
        final float   imgX, imgY, imgW, imgH;

        IntroSlide(String title, String body, float duration,
                   Texture image, float imgX, float imgY, float imgW, float imgH) {
            this.title = title; this.body = body; this.duration = duration;
            this.image = image;
            this.imgX = imgX; this.imgY = imgY; this.imgW = imgW; this.imgH = imgH;
        }
        IntroSlide(String title, String body, float duration) {
            this(title, body, duration, null, 0, 0, 0, 0);
        }
    }

    private static final float FADE_IN  = 1.0f;
    private static final float FADE_OUT = 1.0f;

    private List<IntroSlide> introSlides;
    private int   currentSlide = 0;
    private float slideTimer   = 0f;

    // =========================================================================
    // Game state
    // =========================================================================

    private GameState     gameState;
    private CombatState   combatState;
    private CharacterType selectedCharacter;
    private PlayerState   playerState;
    private Facing        lastFacingDirection;

    private int menuSelection = 0;
    private final String[] menuOptions = {"START GAME", "HOW TO PLAY", "STORY", "CREDITS", "EXIT"};

    // =========================================================================
    // Rendering
    // =========================================================================

    private SpriteBatch   batch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont    font;
    private BitmapFont    titleFont;
    private BitmapFont    bigFont;       // used for VICTORY / DEFEAT headers

    private OrthographicCamera gameCamera;
    private OrthographicCamera uiCamera;
    private Viewport           gameViewport;
    private Viewport           uiViewport;

    private static final float WORLD_WIDTH  = 800f;
    private static final float WORLD_HEIGHT = 480f;

    // =========================================================================
    // Assets
    // =========================================================================

    private Texture caveTex;
    private Texture sonaraTex;
    private Texture lyronTex;
    private Texture aureliusTex;
    private Texture titleScreenTex;
    private Texture darknessOverlay;   // radial gradient: transparent centre → black edges

    private static final float MAP_SIZE  = 2048f;
    private static final float CHAR_SIZE = 64f;
    private static final float SPEED     = 150f;

    private final List<Texture> animationTextures = new ArrayList<>();
    private Animation<TextureRegion> aureliusIdleRightAnim, aureliusIdleLeftAnim;
    private Animation<TextureRegion> aureliusWalkLeftAnim,  aureliusWalkRightAnim;
    private Animation<TextureRegion> sonaraIdleRightAnim, sonaraIdleLeftAnim;
    private Animation<TextureRegion> sonaraWalkLeftAnim,  sonaraWalkRightAnim;
    private Animation<TextureRegion> lyronIdleRightAnim, lyronIdleLeftAnim;
    private Animation<TextureRegion> lyronWalkLeftAnim,  lyronWalkRightAnim;
    private float stateTime = 0f;

    // =========================================================================
    // Game objects
    // =========================================================================

    private MapCharacter player;
    private Character activeCharacterStats;

    /** All enemies currently alive on the map. */
    private List<Enemy> mapEnemies;

    /** All playable rooms defined on the map. */
    private List<Room> rooms;

    /** The enemy currently being fought in COMBAT state. */
    private Enemy currentEnemy;

    // =========================================================================
    // Combat fields
    // =========================================================================

    private static final Random RNG = new Random();

    // Note input
    private final char[]  noteBuffer    = new char[3];
    private       int     noteCount     = 0;
    private final int[]   noteDamages   = new int[3];   // rolled damage for each note

    // Result display
    private float  resultTimer    = 0f;
    private static final float RESULT_DISPLAY_TIME = 2.2f;

    private int    playerDamageDealt  = 0;
    private int    enemyDamageDealt   = 0;
    private String combatLog          = "";  // last line shown in the log area

    // =========================================================================
    // Lifecycle
    // =========================================================================

    @Override
    public void create() {
        batch         = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        font = new BitmapFont();
        font.getData().setScale(1.5f);

        titleFont = new BitmapFont();
        titleFont.getData().setScale(2.2f);

        bigFont = new BitmapFont();
        bigFont.getData().setScale(3.0f);

        gameCamera   = new OrthographicCamera();
        gameViewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, gameCamera);
        gameCamera.position.set(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f, 0);

        uiCamera   = new OrthographicCamera();
        uiViewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, uiCamera);
        uiCamera.position.set(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f, 0);
        uiCamera.update();

        loadAssets();
//        buildIntroSlides();

        gameState = GameState.TITLE_SCREEN;
    }

    // ─────────────────────────────────────────────────────────────────────────
    private void loadAssets() {
        caveTex     = new Texture("Background/Map/Dungeon.png");
        sonaraTex   = new Texture("sonara.png");
        lyronTex    = new Texture("lyron.png");
        aureliusTex = new Texture("aurelius.png");
        titleScreenTex = new Texture("Background/Title_Screen/Title_Screen_Placeholder.png");

        darknessOverlay = createDarknessOverlay(1024, 0.12f, 0.45f);

        //AURELIUS ANIMATION ASSETS
        aureliusIdleRightAnim = loadAnimation("Sprites/Characters/Aurelius/Idle", "Idle",  4, 0.2f);
        aureliusIdleLeftAnim = reverseAnimation(aureliusIdleRightAnim);

        aureliusWalkRightAnim  = loadAnimation("Sprites/Characters/Aurelius/Walk", "Movement",  4, 0.1f);
        aureliusWalkLeftAnim = reverseAnimation(aureliusWalkRightAnim);

        //SONARA ANIMATION ASSETS
        sonaraIdleRightAnim = loadAnimation("Sprites/Characters/Sonara/Idle", "Idle",  4, 0.1f);
        sonaraIdleLeftAnim = reverseAnimation(sonaraIdleRightAnim);

//        sonaraWalkRightAnim  = loadAnimation("Sprites/Character/Sonara/Walk", "Movement",  4, 0.1f);
//        sonaraWalkLeftAnim = reverseAnimation(sonaraWalkRightAnim);

        //LYRON ANIMATION ASSETS
        lyronIdleRightAnim = loadAnimation("Sprites/Characters/Lyron/Idle", "Idle",  4, 0.1f);
        lyronIdleLeftAnim = reverseAnimation(lyronIdleRightAnim);

        lyronWalkRightAnim  = loadAnimation("Sprites/Characters/Lyron/Walk", "Movement",  4, 0.1f);
        lyronWalkLeftAnim = reverseAnimation(lyronWalkRightAnim);
    }

    private Animation<TextureRegion> reverseAnimation(Animation<TextureRegion> anim) {
        TextureRegion[] frames = anim.getKeyFrames();
        TextureRegion[] flippedFrames = new TextureRegion[frames.length];
        for (int i = 0; i < frames.length; i++){
            flippedFrames[i] = new TextureRegion(frames[i]);
            flippedFrames[i].flip(true, false);
        }
        return new Animation<>(anim.getFrameDuration(), flippedFrames);
    }

    // ─────────────────────────────────────────────────────────────────────────
    private void buildIntroSlides() {
        introSlides = new ArrayList<>();
        introSlides.add(new IntroSlide("Before the Silence",
            "Long ago, every town echoed with song.\n" +
                "Streets rang with instruments, and the air itself\n" +
                "carried the joy of life and harmony.\n\n" +
                "All this music pleased a great entity —\n" +
                "a guardian who watched over the people.", 6f));

        introSlides.add(new IntroSlide("The Guardian's Request",
            "One day the guardian spoke its only wish:\n\n" +
                "   \"I shall not be woken from my slumber.\"\n\n" +
                "Believing their peace was the guardian's gift,\n" +
                "the people laid down their instruments\n" +
                "and wrapped the world in reverent quiet.", 6.5f));

        introSlides.add(new IntroSlide("The Darkness Below",
            "But it was never the guardian that kept them safe —\n" +
                "it was the music itself.\n\n" +
                "Without it, dark forces stirred in the depths.\n" +
                "From the stillness, creatures took shape.\n" +
                "The Shadow Beasts were born.",
            6f, caveTex, 550, 80, 200, 200));

        introSlides.add(new IntroSlide("The Bell Falls",
            "Then one night, an old bell broke free\n" +
                "from its rusted chains and crashed to earth.\n\n" +
                "Its thunderous toll shattered years of quiet.\n" +
                "The Shadow Beasts surged to the surface\n" +
                "and descended upon the town.", 6.5f));

        introSlides.add(new IntroSlide("Eternal Silence",
            "The survivors clung to quiet,\n" +
                "knowing any sound would draw the beasts.\n\n" +
                "Voices faded. Footsteps vanished.\n" +
                "Even breath seemed forbidden.\n\n" +
                "Centuries passed in an emptiness\n" +
                "that drained the very soul from the living.", 7f));

        introSlides.add(new IntroSlide("A Single Bell Rings",
            "Then — without warning — a lone bell rang.\n\n" +
                "The Shadow Beasts stirred in frenzy.\n" +
                "Three children, born in the silence,\n" +
                "felt something stir within them.\n\n" +
                "They reached for instruments hidden long ago.\n" +
                "Sound is their only weapon now.", 7f));
    }

    // =========================================================================
    // Render loop
    // =========================================================================

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.03f, 0.03f, 0.07f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (gameState != GameState.EXPLORING && gameState != GameState.COMBAT)
            gameCamera.position.set(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f, 0);

        gameCamera.update();
        batch.setProjectionMatrix(gameCamera.combined);
        batch.begin();

        switch (gameState) {
            case TITLE_SCREEN:      renderTitleScreen();            break;
            case MAIN_MENU:         renderMainMenu();               break;
            case HOW_TO_PLAY:       renderHowToPlay();              break;
            case STORY:             renderStory();                  break;
            case CREDITS:           renderCredits();                break;
            case CHARACTER_SELECT:  renderCharacterSelectScreen();  break;
            case EXPLORING:       renderExploringScreen();         break;
//            case PRE_COMBAT:        renderPreCombat();             break;
            case COMBAT:            renderCombatScreen();           break;
//            case POST_COMBAT:        renderPostCombat();           break;
        }

        batch.end();
    }

    // =========================================================================
    // TITLE SCREEN
    // =========================================================================

    private void renderTitleScreen(){
        batch.draw(titleScreenTex, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) gameState = GameState.MAIN_MENU;
    }

    // =========================================================================
    // INTRO
    // =========================================================================

    private void renderIntro() {
        float delta = Gdx.graphics.getDeltaTime();
        slideTimer += delta;
        IntroSlide slide = introSlides.get(currentSlide);

        float alpha;
        if      (slideTimer < FADE_IN)                        alpha = slideTimer / FADE_IN;
        else if (slideTimer > slide.duration - FADE_OUT)      alpha = (slide.duration - slideTimer) / FADE_OUT;
        else                                                   alpha = 1f;
        alpha = MathUtils.clamp(alpha, 0f, 1f);

        if (slide.image != null) {
            batch.setColor(0.25f, 0.25f, 0.35f, alpha * 0.55f);
            batch.draw(slide.image, slide.imgX, slide.imgY, slide.imgW, slide.imgH);
            batch.setColor(1f, 1f, 1f, 1f);
        }
        if (currentSlide == introSlides.size() - 1) {
            float pSize = 96f, pY = 55f;
            batch.setColor(1f, 1f, 1f, alpha);
            batch.draw(sonaraTex,   140f, pY, pSize, pSize);
            batch.draw(aureliusTex, 352f, pY, pSize, pSize);
            batch.draw(lyronTex,    564f, pY, pSize, pSize);
            batch.setColor(1f, 1f, 1f, 1f);
        }

        titleFont.setColor(0.9f, 0.85f, 0.5f, alpha);
        titleFont.draw(batch, slide.title, 50f, WORLD_HEIGHT - 38f);
        font.setColor(0.5f, 0.5f, 0.6f, alpha);
        font.draw(batch, "──────────────────────────────────────────────", 50f, WORLD_HEIGHT - 68f);

        font.setColor(0.92f, 0.92f, 0.92f, alpha);
        String[] lines = slide.body.split("\n", -1);
        float textY = WORLD_HEIGHT - 102f;
        for (String line : lines) { font.draw(batch, line, 60f, textY); textY -= 32f; }

        font.setColor(0.45f, 0.45f, 0.55f, Math.min(alpha, 0.8f));
        font.draw(batch,
            currentSlide == introSlides.size() - 1
                ? "SPACE / ENTER  –  Continue"
                : "SPACE / ENTER  –  Next          ESC  –  Skip all",
            50f, 28f);

        font.setColor(0.4f, 0.4f, 0.5f, alpha);
        font.draw(batch, (currentSlide + 1) + " / " + introSlides.size(), 720f, 28f);
        font.setColor(Color.WHITE);
        titleFont.setColor(Color.WHITE);

        boolean advance = slideTimer >= slide.duration
            || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)
            || Gdx.input.isKeyJustPressed(Input.Keys.ENTER);

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) goToMainMenu();
        else if (advance) {
            currentSlide++;
            slideTimer = 0f;
            if (currentSlide >= introSlides.size()) goToMainMenu();
        }
    }

    private void goToMainMenu() {
        currentSlide = 0; slideTimer = 0f; menuSelection = 0;
        gameState = GameState.MAIN_MENU;
    }

    // =========================================================================
    // MENUS
    // =========================================================================

    private void renderMainMenu() {
        // 1. Full-screen title background
        batch.setColor(Color.WHITE);
        batch.draw(titleScreenTex, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);

        // 2. Menu options
        for (int i = 0; i < menuOptions.length; i++) {
            font.setColor(i == menuSelection ? Color.YELLOW : Color.WHITE);
            String label = i == menuSelection ? "> " + menuOptions[i] + " <" : menuOptions[i];
            font.draw(batch, label, 310, 310 - (i * 45));
        }
        font.setColor(Color.WHITE);

        // 4. Subtle "press enter" hint at the bottom
        font.setColor(new Color(0.7f, 0.7f, 0.7f, 0.85f));
        font.draw(batch, "W/S or Arrows to navigate  |  ENTER to select", 185, 50);
        font.setColor(Color.WHITE);

        if (Gdx.input.isKeyJustPressed(Input.Keys.W) || Gdx.input.isKeyJustPressed(Input.Keys.UP))
            menuSelection = menuSelection > 0 ? menuSelection - 1 : menuOptions.length - 1;
        if (Gdx.input.isKeyJustPressed(Input.Keys.S) || Gdx.input.isKeyJustPressed(Input.Keys.DOWN))
            menuSelection = menuSelection < menuOptions.length - 1 ? menuSelection + 1 : 0;
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER))
            handleMenuSelection();
    }

    private void handleMenuSelection() {
        switch (menuSelection) {
            case 0: gameState = GameState.CHARACTER_SELECT; break;
            case 1: gameState = GameState.HOW_TO_PLAY;      break;
            case 2: gameState = GameState.STORY;             break;
            case 3: gameState = GameState.CREDITS;           break;
            case 4: Gdx.app.exit();                          break;
        }
    }

    private void renderHowToPlay() {
        font.setColor(Color.CYAN);  font.draw(batch, "HOW TO PLAY", 320, 420);
        font.setColor(Color.WHITE);
        font.draw(batch, "WASD / Arrow Keys  – Move on the map",           100, 340);
        font.draw(batch, "Walk into an enemy – Trigger combat",            100, 300);
        font.draw(batch, "In combat: press A–G to enter your 3 notes",     100, 260);
        font.draw(batch, "After 3 notes your attack resolves automatically",100, 220);
        font.draw(batch, "Chords (3 matching notes) grant special effects", 100, 180);
        font.draw(batch, "ESC – Return to Menu",                            100, 140);
        font.setColor(Color.GRAY); font.draw(batch, "Press ESC to go back", 290, 60);
        font.setColor(Color.WHITE);
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) gameState = GameState.MAIN_MENU;
    }

    private void renderStory() {
        font.setColor(Color.CYAN);  font.draw(batch, "THE LORE OF SILENTIUM", 220, 420);
        font.setColor(Color.WHITE);
        font.draw(batch, "The world fell silent to let a guardian rest.",  50, 340);
        font.draw(batch, "But silence birthed Shadow Beasts.",              50, 300);
        font.draw(batch, "A lone bell has shattered the quiet...",          50, 260);
        font.draw(batch, "Sound is your only weapon now.",                  50, 220);
        font.setColor(Color.GRAY); font.draw(batch, "Press ESC to go back", 290, 60);
        font.setColor(Color.WHITE);
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) gameState = GameState.MAIN_MENU;
    }

    private void renderCredits() {
        font.setColor(Color.CYAN);  font.draw(batch, "CREDITS", 340, 420);
        font.setColor(Color.WHITE);
        font.draw(batch, "Lead Developer  : Zephyrdoestech", 180, 340);
        font.draw(batch, "Art & Story     : Silentium Team",  180, 300);
        font.draw(batch, "Engine          : LibGDX",          180, 260);
        font.setColor(Color.GRAY); font.draw(batch, "Press ESC to go back", 290, 60);
        font.setColor(Color.WHITE);
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) gameState = GameState.MAIN_MENU;
    }

    private void renderCharacterSelectScreen() {
        font.setColor(Color.CYAN); font.draw(batch, "SELECT YOUR HERO", 280, 440);
        font.setColor(Color.WHITE);

        batch.draw(sonaraTex,    80, 220, 80, 80); font.draw(batch, "1: Sonara",   70, 210);
        font.draw(batch, "Banjo", 70, 185);        font.draw(batch, "HP: 150",     70, 165);

        batch.draw(aureliusTex, 355, 220, 80, 80); font.draw(batch, "2: Aurelius", 340, 210);
        font.draw(batch, "Flute", 340, 185);       font.draw(batch, "HP: 150",    340, 165);

        batch.draw(lyronTex,    620, 220, 80, 80); font.draw(batch, "3: Lyron",    610, 210);
        font.draw(batch, "Harp",  610, 185);       font.draw(batch, "HP: 250",     610, 165);

        font.setColor(Color.GRAY);
        font.draw(batch, "Press 1, 2, or 3  |  ESC to go back", 200, 60);
        font.setColor(Color.WHITE);

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            selectedCharacter = CharacterType.SONARA;
            activeCharacterStats = new Character("Sonara",   "Banjo", 150, 40); startGame();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            selectedCharacter = CharacterType.AURELIUS;
            activeCharacterStats = new Character("Aurelius", "Flute", 150, 40); startGame();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            selectedCharacter = CharacterType.LYRON;
            activeCharacterStats = new Character("Lyron",    "Harp",  250, 40); startGame();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) gameState = GameState.MAIN_MENU;
    }

    // =========================================================================
    // Game start / map setup
    // =========================================================================

    private void startGame() {
        player              = new MapCharacter(MAP_SIZE / 2f - CHAR_SIZE / 2f,
            MAP_SIZE / 2f - CHAR_SIZE / 2f);
        playerState         = PlayerState.IDLE;
        lastFacingDirection = Facing.RIGHT;
        stateTime           = 0f;
        spawnMapEnemies();
        gameState = GameState.EXPLORING;
    }

    /**
     * Place enemies at fixed positions spread across the 512×512 map.
     * Avoid the centre tile where the player spawns.
     */
    private void spawnMapEnemies() {
        mapEnemies = new ArrayList<>();
        rooms = new ArrayList<>();

        // Note: Coordinates are (X, Y, Width, Height) from the bottom-left corner
        rooms.add(new Room(96f, 64f, 320f, 256f));     // Bottom-left area
        rooms.add(new Room(96f, 544f, 320f, 256f));    // Mid-left area
        rooms.add(new Room(1088f, 96f, 448f, 384f));   // Bottom-right area
        rooms.add(new Room(1216f, 1024f, 384f, 448f)); // Top-right area
        rooms.add(new Room(768f, 512f, 384f, 384f));   // Center-right area

        // Dynamically spawn enemies within these rooms
        for (Room room : rooms) {
            // 70% chance for a room to have enemies
            if (RNG.nextInt(100) < 70) {
                // Spawn 1 to 3 enemies per room
                int numEnemies = 1 + RNG.nextInt(3);
                for (int i = 0; i < numEnemies; i++) {
                    // Pick random coordinates strictly inside the room
                    float spawnX = room.getBounds().x + (RNG.nextFloat() * (room.getBounds().width - CHAR_SIZE));
                    float spawnY = room.getBounds().y + (RNG.nextFloat() * (room.getBounds().height - CHAR_SIZE));

                    Enemy e = RNG.nextBoolean() ? Enemy.fleshFeeder(spawnX, spawnY) : Enemy.andrewellers(spawnX, spawnY);
                    room.addEnemy(e);
                    mapEnemies.add(e);
                }
            }
        }
    }

    // =========================================================================
    // EXPLORING SCREEN
    // =========================================================================

    private void renderExploringScreen() {
        float delta = Gdx.graphics.getDeltaTime();
        stateTime += delta;
        handleInput(delta);
        updateGameCamera();

        // ── Draw map ─────────────────────────────────────────────────────────
        batch.draw(caveTex, 0, 0, MAP_SIZE, MAP_SIZE);

        // ── [DEBUG] Draw Room Boundaries ─────────────────────────────────────
        batch.end();
        shapeRenderer.setProjectionMatrix(gameCamera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.GREEN);
        if (rooms != null) {
            for (Room r : rooms) {
                shapeRenderer.rect(r.getBounds().x, r.getBounds().y, r.getBounds().width, r.getBounds().height);
            }
        }
        shapeRenderer.end();

        // ── Draw enemies as coloured rectangles (placeholder; swap for sprites later) ──
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Enemy e : mapEnemies) {
            if (!e.isDefeated()) {
                shapeRenderer.setColor(0.7f, 0.1f, 0.1f, 1f);
                shapeRenderer.rect(e.getX(), e.getY(), CHAR_SIZE, CHAR_SIZE);
            }
        }
        shapeRenderer.end();
        batch.begin();
        batch.setProjectionMatrix(gameCamera.combined);

        // Enemy name labels above each enemy
        font.setColor(Color.RED);
        for (Enemy e : mapEnemies) {
            if (!e.isDefeated())
                font.draw(batch, e.getName(), e.getX() - 10, e.getY() + CHAR_SIZE + 18);
        }
        font.setColor(Color.WHITE);

        // ── Draw player ───────────────────────────────────────────────────────
        drawPlayerSprite();

        // ── Darkness overlay (drawn in game space, tracking player) ─────────────
        drawDarknessOverlay();

        // ── HUD ───────────────────────────────────────────────────────────────
        batch.end();
        drawHUD();
        batch.setProjectionMatrix(gameCamera.combined);
        batch.begin();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) gameState = GameState.MAIN_MENU;
    }

    // ── Camera follow ─────────────────────────────────────────────────────────
    private void updateGameCamera() {
        float halfW = WORLD_WIDTH / 2f, halfH = WORLD_HEIGHT / 2f;
        float camX  = MathUtils.clamp(player.getX() + CHAR_SIZE / 2f, halfW, MAP_SIZE - halfW);
        float camY  = MathUtils.clamp(player.getY() + CHAR_SIZE / 2f, halfH, MAP_SIZE - halfH);
        gameCamera.position.set(camX, camY, 0);
        gameCamera.update();
        batch.setProjectionMatrix(gameCamera.combined);
    }

    // ── Player sprite ─────────────────────────────────────────────────────────
    private void drawPlayerSprite() {
        switch (selectedCharacter) {
            case AURELIUS: drawAurelius(); break;
            case SONARA:   drawSonara(); break;
            case LYRON:    drawLyron(); break;
        }
    }

    private void drawAurelius() {
        TextureRegion frame;
        switch (playerState) {
            case WALK_LEFT:  frame = aureliusWalkLeftAnim.getKeyFrame(stateTime, true);  break;
            case WALK_RIGHT: frame = aureliusWalkRightAnim.getKeyFrame(stateTime, true); break;
            case WALK_UP: case WALK_DOWN:
                frame = lastFacingDirection == Facing.LEFT
                    ? aureliusWalkLeftAnim.getKeyFrame(stateTime, true)
                    : aureliusWalkRightAnim.getKeyFrame(stateTime, true); break;
            default:
                frame = lastFacingDirection == Facing.LEFT
                    ? aureliusIdleLeftAnim.getKeyFrame(stateTime, true)
                    : aureliusIdleRightAnim.getKeyFrame(stateTime, true); break;
        }
        batch.draw(frame, player.getX(), player.getY(), CHAR_SIZE, CHAR_SIZE);
    }

    private void drawSonara() {
        TextureRegion frame;
        switch (playerState) {
            case WALK_LEFT:  frame = aureliusWalkLeftAnim.getKeyFrame(stateTime, true);  break;
            case WALK_RIGHT: frame = aureliusWalkRightAnim.getKeyFrame(stateTime, true); break;
            case WALK_UP: case WALK_DOWN:
                frame = lastFacingDirection == Facing.LEFT
                    ? aureliusWalkLeftAnim.getKeyFrame(stateTime, true)
                    : aureliusWalkRightAnim.getKeyFrame(stateTime, true); break;
            default:
                frame = lastFacingDirection == Facing.LEFT
                    ? aureliusIdleLeftAnim.getKeyFrame(stateTime, true)
                    : aureliusIdleRightAnim.getKeyFrame(stateTime, true); break;
        }
        batch.draw(frame, player.getX(), player.getY(), CHAR_SIZE, CHAR_SIZE);
    }

    private void drawLyron() {
        TextureRegion frame;
        switch (playerState) {
            case WALK_LEFT:  frame = lyronWalkLeftAnim.getKeyFrame(stateTime, true);  break;
            case WALK_RIGHT: frame = lyronWalkRightAnim.getKeyFrame(stateTime, true); break;
            case WALK_UP: case WALK_DOWN:
                frame = lastFacingDirection == Facing.LEFT
                    ? lyronWalkLeftAnim.getKeyFrame(stateTime, true)
                    : lyronWalkRightAnim.getKeyFrame(stateTime, true); break;
            default:
                frame = lastFacingDirection == Facing.LEFT
                    ? lyronIdleLeftAnim.getKeyFrame(stateTime, true)
                    : lyronIdleRightAnim.getKeyFrame(stateTime, true); break;
        }
        batch.draw(frame, player.getX(), player.getY(), CHAR_SIZE, CHAR_SIZE);
    }

    // =========================================================================
    // COMBAT SCREEN
    // =========================================================================

    /**
     * Layout (800 × 480):
     *
     *  ┌─────────────────────────────────────────┐
     *  │  [Enemy name]            HP: xxx / xxx  │  ← top strip
     *  │  [Enemy HP bar ──────────────]          │
     *  │                                         │
     *  │  [big Enemy "sprite" block, centre]     │  ← mid
     *  │                                         │
     *  │  Notes: A  B  _     damage so far       │  ← note input row
     *  │  [Combat log line]                      │
     *  │─────────────────────────────────────────│
     *  │  [Player HP bar]   [Player shield bar]  │  ← bottom HUD
     *  └─────────────────────────────────────────┘
     */
    private void renderCombatScreen() {
        float delta = Gdx.graphics.getDeltaTime();
        handleCombatInput(); // ← must be called here; renderPlayingScreen() is not active during combat

        // ── Dark background panel ─────────────────────────────────────────────
        batch.end();
        shapeRenderer.setProjectionMatrix(uiCamera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.05f, 0.05f, 0.1f, 1f);
        shapeRenderer.rect(0, 0, WORLD_WIDTH, WORLD_HEIGHT);

        // Enemy HP bar (top-left area)
        float eHpFrac = currentEnemy.getMaxHp() > 0
            ? (float) currentEnemy.getHp() / currentEnemy.getMaxHp() : 0f;
        drawBar(shapeRenderer, 30, WORLD_HEIGHT - 50, 340, 18,
            eHpFrac, Color.DARK_GRAY, Color.valueOf("cc2222"));

        // Player HP bar (bottom-left)
        float pHpFrac = activeCharacterStats.getMaxHp() > 0
            ? (float) activeCharacterStats.getHp() / activeCharacterStats.getMaxHp() : 0f;
        drawBar(shapeRenderer, 30, 115, 250, 16, pHpFrac, Color.DARK_GRAY, Color.RED);

        // Player shield bar (bottom-left, below HP)
        float pShFrac = activeCharacterStats.getMaxShield() > 0
            ? (float) activeCharacterStats.getShield() / activeCharacterStats.getMaxShield() : 0f;
        drawBar(shapeRenderer, 30, 94, 250, 14, pShFrac, Color.DARK_GRAY, Color.CYAN);

        // Enemy "sprite" — large coloured block in the centre-right area
        shapeRenderer.setColor(0.55f, 0.1f, 0.1f, 1f);
        shapeRenderer.rect(500, 150, 200, 200);

        // Note slot boxes at the bottom centre
        for (int i = 0; i < 3; i++) {
            boolean filled = i < noteCount;
            shapeRenderer.setColor(filled ? new Color(0.2f,0.5f,0.9f,1f) : new Color(0.15f,0.15f,0.25f,1f));
            shapeRenderer.rect(290 + i * 70, 125, 55, 55);
        }

        shapeRenderer.end();

        // ── Text ─────────────────────────────────────────────────────────────
        batch.setProjectionMatrix(uiCamera.combined);
        batch.begin();

        // Enemy name + HP numbers
        titleFont.setColor(Color.valueOf("ff6666"));
        titleFont.draw(batch, currentEnemy.getName(), 30, WORLD_HEIGHT - 20);
        font.setColor(Color.WHITE);
        font.draw(batch,
            "HP: " + currentEnemy.getHp() + " / " + currentEnemy.getMaxHp(),
            380, WORLD_HEIGHT - 20);

        // Enemy label inside block
        font.setColor(Color.valueOf("ff9999"));
        font.draw(batch, "[" + currentEnemy.getName() + "]", 508, 265);
        font.setColor(Color.WHITE);

        // Player stats (bottom-left)
        font.setColor(Color.WHITE);
        font.draw(batch, activeCharacterStats.getName()
            + "  HP: " + activeCharacterStats.getHp() + "/" + activeCharacterStats.getMaxHp(), 30, 132);
        font.draw(batch, "Shield: " + activeCharacterStats.getShield()
            + "/" + activeCharacterStats.getMaxShield(), 30, 110);

        // Note slot labels
        font.setColor(Color.CYAN);
        font.draw(batch, "Notes:", 190, 166);
        for (int i = 0; i < 3; i++) {
            if (i < noteCount) {
                font.setColor(Color.WHITE);
                font.draw(batch, String.valueOf(java.lang.Character.toUpperCase(noteBuffer[i])),
                    310 + i * 70, 165);
            } else {
                font.setColor(new Color(0.4f, 0.4f, 0.55f, 1f));
                font.draw(batch, "_", 310 + i * 70, 165);
            }
        }

        // Damage preview while typing
        if (noteCount > 0) {
            int preview = 0;
            for (int i = 0; i < noteCount; i++) preview += noteDamages[i];
            font.setColor(Color.YELLOW);
            font.draw(batch, "Dmg so far: " + preview, 510, 166);
        }
        font.setColor(Color.WHITE);

        // Combat log
        font.setColor(Color.valueOf("ddddaa"));
        font.draw(batch, combatLog, 30, 88);

        // State-specific overlays / prompts
        switch (combatState) {
            case BATTLE_SCREEN:
                break;
            case ENEMY_INTRODUCTION:
                break;
            case TUTORIAL:
                break;
            case CHARACTER_PRECOMBAT_LINE:
                break;
            case TURN_MENU:
                break;
            case ATTACK:
                font.setColor(Color.GRAY);
                font.draw(batch, "Press A–G to enter notes. BACKSPACE to undo.", 30, 60);
                break;
            case USE_SKILL:
                break;
            case OPEN_INVENTORY:
                break;
            case USE_ITEM:
                break;
            case DISPLAY_STATS:
                break;
            case DISPLAY_ATTACK_GUIDE:
                break;
            case DISPLAY_CHORDS:
                break;
            case DISPLAY_PLAYER_DAMAGE:
                break;
            case ENEMY_ATTACK:
                break;
            case DISPLAY_ENEMY_DAMAGE:
                break;

//            case SHOW_RESULT:
//                resultTimer += delta;
//                font.setColor(Color.GREEN);
//                font.draw(batch, "You dealt " + playerDamageDealt + " damage!", 30, 60);
//                if (resultTimer >= RESULT_DISPLAY_TIME) {
//                    resultTimer = 0f;
//                    doEnemyAttack();
//                }
//                break;

//            case ENEMY_TURN:
//                resultTimer += delta;
//                font.setColor(Color.valueOf("ff6666"));
//                font.draw(batch, currentEnemy.getName()
//                    + " used " + currentEnemy.getLastAttackName()
//                    + " for " + currentEnemy.getLastAttackDmg() + " damage!", 30, 60);
//                if (resultTimer >= RESULT_DISPLAY_TIME) {
//                    resultTimer = 0f;
//                    nextPlayerTurn();
//                }
//                break;

            case VICTORY:
                bigFont.setColor(Color.GOLD);
                bigFont.draw(batch, "VICTORY!", 290, 260);
                font.setColor(Color.WHITE);
                font.draw(batch, "Press ENTER to return to the map.", 255, 200);
                if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) endCombat(true);
                break;

            case DEFEAT:
                bigFont.setColor(Color.RED);
                bigFont.draw(batch, "DEFEATED", 255, 260);
                font.setColor(Color.WHITE);
                font.draw(batch, "Press ENTER to return to the main menu.", 215, 200);
                if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                    gameState = GameState.MAIN_MENU;
                }
                break;
        }

        font.setColor(Color.WHITE);
        titleFont.setColor(Color.WHITE);
    }

    // ── Note input (called from handleInput — see below) ──────────────────────
    private void handleCombatInput() {
        if (combatState != CombatState.ATTACK) return;

        // Backspace removes last note
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE) && noteCount > 0) {
            noteCount--;
            return;
        }

        // Map A–G keys to note characters
        int[] noteKeys = {
            Input.Keys.A, Input.Keys.B, Input.Keys.C,
            Input.Keys.D, Input.Keys.E, Input.Keys.F, Input.Keys.G
        };
        char[] noteChars = {'a', 'b', 'c', 'd', 'e', 'f', 'g'};

        for (int i = 0; i < noteKeys.length; i++) {
            if (Gdx.input.isKeyJustPressed(noteKeys[i]) && noteCount < 3) {
                noteBuffer[noteCount]  = noteChars[i];
                noteDamages[noteCount] = rollNoteDamage(noteChars[i]);
                noteCount++;
                break;
            }
        }

        // Auto-resolve after 3 notes
        if (noteCount == 3) resolvePlayerAttack();
    }

    /**
     * Returns a random damage value for a given note based on GDD ranges:
     * A 1–10 | B 5–13 | C 9–16 | D 12–18 | E 16–21 | F 19–23 | G 21–24
     */
    private int rollNoteDamage(char note) {
        switch (note) {
            case 'a': return 1  + RNG.nextInt(10);
            case 'b': return 5  + RNG.nextInt(9);
            case 'c': return 9  + RNG.nextInt(8);
            case 'd': return 12 + RNG.nextInt(7);
            case 'e': return 16 + RNG.nextInt(6);
            case 'f': return 19 + RNG.nextInt(5);
            case 'g': return 21 + RNG.nextInt(4);
            default:  return 0;
        }
    }

    /** Totals the three note damages, applies damage buff, then transitions to SHOW_RESULT. */
    private void resolvePlayerAttack() {
        int total = 0;
        for (int i = 0; i < 3; i++) total += noteDamages[i];

        // Apply hero's damage buff (e.g. from chords)
        total = (int)(total * (1.0 + activeCharacterStats.getDamageBuff()));

        // Check chord bonus (C-E-G heal, D-F-A buff, etc.) — simplified for now
        String chord = "" + noteBuffer[0] + noteBuffer[1] + noteBuffer[2];
        String chordMsg = checkChord(chord);

        playerDamageDealt = total;
        currentEnemy.takeDamage(total);

        combatLog = chordMsg.isEmpty()
            ? "You played " + displayNotes() + " for " + total + " damage."
            : "CHORD: " + displayNotes() + " — " + chordMsg;

        combatState = CombatState.DISPLAY_PLAYER_DAMAGE;
        resultTimer = 0f;
    }

    /**
     * Checks if the three notes form a chord from the GDD and applies its effect.
     * Returns a description string (empty if no chord matched).
     *
     * Chords:
     *  C–E–G  → Heal 20% max HP
     *  D–F–A  → +20% damage buff
     *  E–G–B  → Heal 10% HP + 10% damage buff
     *  F–A–C  → Gain 25 shield
     *  G–B–D  → Heal 15% HP + gain 15 shield
     *  A–C–E  → Gain 35 shield
     *  B–D–F  → +30% damage, lose 10% HP
     */
    private String checkChord(String chord) {
        // Normalise order for detection
        char n1 = noteBuffer[0], n2 = noteBuffer[1], n3 = noteBuffer[2];
        String sorted = sortChord(n1, n2, n3);
        switch (sorted) {
            case "ceg":
                int healCEG = (int)(activeCharacterStats.getMaxHp() * 0.20f);
                activeCharacterStats.heal(healCEG);
                return "C Major! Healed " + healCEG + " HP.";
            case "adf":
                activeCharacterStats.setDamageBuff(activeCharacterStats.getDamageBuff() + 0.20);
                return "D Minor! +20% damage buff applied.";
            case "beg":
                int healEGB = (int)(activeCharacterStats.getMaxHp() * 0.10f);
                activeCharacterStats.heal(healEGB);
                activeCharacterStats.setDamageBuff(activeCharacterStats.getDamageBuff() + 0.10);
                return "E Minor! Healed " + healEGB + " HP + 10% damage buff.";
            case "acf":
                activeCharacterStats.gainShield(25);
                return "F Major! Gained 25 shield.";
            case "bdg":
                int healGBD = (int)(activeCharacterStats.getMaxHp() * 0.15f);
                activeCharacterStats.heal(healGBD);
                activeCharacterStats.gainShield(15);
                return "G Major! Healed " + healGBD + " HP + 15 shield.";
            case "ace":
                activeCharacterStats.gainShield(35);
                return "A Minor! Gained 35 shield.";
            case "bdf":
                activeCharacterStats.setDamageBuff(activeCharacterStats.getDamageBuff() + 0.30);
                int selfDmg = (int)(activeCharacterStats.getMaxHp() * 0.10f);
                activeCharacterStats.takeDamage(selfDmg);
                return "B Diminished! +30% dmg, but lost " + selfDmg + " HP.";
            default:
                return "";
        }
    }

    /** Returns sorted 3-char chord string for matching (e.g. "gce" → "ceg"). */
    private String sortChord(char a, char b, char c) {
        char[] ch = {a, b, c};
        java.util.Arrays.sort(ch);
        return new String(ch);
    }

    /** Friendly display of the typed notes. */
    private String displayNotes() {
        return java.lang.Character.toUpperCase(noteBuffer[0]) + "-"
            + java.lang.Character.toUpperCase(noteBuffer[1]) + "-"
            + java.lang.Character.toUpperCase(noteBuffer[2]);
    }

    /** Enemy counter-attacks; called after SHOW_RESULT timer expires. */
    private void doEnemyAttack() {
        if (currentEnemy.isDefeated()) {
            combatState = CombatState.VICTORY;
            return;
        }
        int dmg = currentEnemy.performAttack();
        activeCharacterStats.takeDamage(dmg);
        enemyDamageDealt = dmg;
        combatState = CombatState.ENEMY_ATTACK;
        resultTimer = 0f;
    }

    /** After enemy turn display, check defeat or reset for next player turn. */
    private void nextPlayerTurn() {
        if (!activeCharacterStats.isAlive()) {
            combatState = CombatState.DEFEAT;
            return;
        }
        // Reset for next player turn
        noteCount = 0;
        combatLog = "";
        combatState = CombatState.ATTACK;
    }

    /** Called when the player wins combat (ENTER pressed on VICTORY screen). */
    private void endCombat(boolean won) {
        mapEnemies.remove(currentEnemy);

        // Remove from room and check if room is cleared
        if (rooms != null) {
            for (Room room : rooms) {
                if (room.getEnemies().contains(currentEnemy)) {
                    room.getEnemies().remove(currentEnemy);
                    if (room.getEnemies().isEmpty()) {
                        room.setCleared(true);
                    }
                    break;
                }
            }
        }

        currentEnemy = null;
        noteCount    = 0;
        combatLog    = "";
        gameState    = GameState.EXPLORING;
    }

    /** Trigger combat with a given enemy. */
    private void startCombat(Enemy enemy) {
        currentEnemy = enemy;
        noteCount    = 0;
        combatLog    = "A " + enemy.getName() + " appears! Enter 3 notes to attack.";
        combatState  = CombatState.ATTACK;
        gameState    = GameState.COMBAT;
    }

    // =========================================================================
    // HUD (playing screen)
    // =========================================================================

    private void drawHUD() {
        uiCamera.update();
        shapeRenderer.setProjectionMatrix(uiCamera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        drawBar(shapeRenderer, 20, WORLD_HEIGHT - 30, 200, 16,
            (float) activeCharacterStats.getHp() / activeCharacterStats.getMaxHp(),
            Color.DARK_GRAY, Color.RED);
        drawBar(shapeRenderer, 20, WORLD_HEIGHT - 52, 200, 16,
            activeCharacterStats.getMaxShield() > 0
                ? (float) activeCharacterStats.getShield() / activeCharacterStats.getMaxShield() : 0f,
            Color.DARK_GRAY, Color.CYAN);
        shapeRenderer.end();

        batch.setProjectionMatrix(uiCamera.combined);
        batch.begin();
        font.setColor(Color.WHITE);
        font.draw(batch, activeCharacterStats.getName()
                + "  HP: " + activeCharacterStats.getHp() + "/" + activeCharacterStats.getMaxHp(),
            230, WORLD_HEIGHT - 18);
        font.draw(batch, "Shield: " + activeCharacterStats.getShield()
            + "/" + activeCharacterStats.getMaxShield(), 230, WORLD_HEIGHT - 40);
        font.draw(batch, "Lv " + activeCharacterStats.getLevel(), 20, WORLD_HEIGHT - 62);
        font.setColor(Color.GRAY);
        font.draw(batch, "ESC – Menu", 10, 20);
        font.setColor(Color.WHITE);
        batch.end();
    }

    private void drawBar(ShapeRenderer sr, float x, float y, float w, float h,
                         float fraction, Color bg, Color fill) {
        sr.setColor(bg);  sr.rect(x, y - h, w, h);
        sr.setColor(fill); sr.rect(x, y - h, w * MathUtils.clamp(fraction, 0f, 1f), h);
    }

    // =========================================================================
    // Input
    // =========================================================================

    private void handleInput(float delta) {
        if (gameState == GameState.COMBAT) return; // combat input handled in renderCombatScreen()

        float move  = SPEED * delta;
        playerState = PlayerState.IDLE;

        float prevX = player.getX(), prevY = player.getY();

        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
            player.up(move);   playerState = PlayerState.WALK_UP;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            player.down(move); playerState = PlayerState.WALK_DOWN;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            player.left(move); playerState = PlayerState.WALK_LEFT;
            lastFacingDirection = Facing.LEFT;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            player.right(move); playerState = PlayerState.WALK_RIGHT;
            lastFacingDirection = Facing.RIGHT;
        }

        // Map boundary clamp
        if (player.getX() < 0)                   player.setX(0);
        if (player.getY() < 0)                    player.setY(0);
        if (player.getX() > MAP_SIZE - CHAR_SIZE) player.setX(MAP_SIZE - CHAR_SIZE);
        if (player.getY() > MAP_SIZE - CHAR_SIZE) player.setY(MAP_SIZE - CHAR_SIZE);

        // ── Enemy collision ───────────────────────────────────────────────────
        Rectangle playerRect = new Rectangle(player.getX(), player.getY(), CHAR_SIZE, CHAR_SIZE);
        for (Enemy e : mapEnemies) {
            if (e.isDefeated()) continue;
            Rectangle enemyRect = new Rectangle(e.getX(), e.getY(), CHAR_SIZE, CHAR_SIZE);
            if (playerRect.overlaps(enemyRect)) {
                // Push player back to where they were, then trigger combat
                player.setX(prevX);
                player.setY(prevY);
                startCombat(e);
                return;
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) gameState = GameState.MAIN_MENU;
    }

    // =========================================================================
    // Asset helpers
    // =========================================================================

    private Animation<TextureRegion> loadAnimation(String folder, String baseName,
                                                   int frameCount, float frameDuration) {
        TextureRegion[] frames = new TextureRegion[frameCount];
        for (int i = 0; i < frameCount; i++) {
            Texture tex = new Texture(folder + "/" + baseName + (i + 1) + ".png");
            animationTextures.add(tex);
            frames[i] = new TextureRegion(tex);
        }
        return new Animation<>(frameDuration, frames);
    }

    // =========================================================================
    // Lifecycle
    // =========================================================================

    @Override
    public void resize(int width, int height) {
        gameViewport.update(width, height, true);
        uiViewport.update(width, height, true);
    }

    // =========================================================================
    // Darkness overlay
    // =========================================================================

    /**
     * Generates a square Pixmap-based texture:
     *   - fully transparent at the centre (the "light" zone)
     *   - smoothly fading to solid black towards the edges
     *
     * @param size        pixel size of the square Pixmap (e.g. 512)
     * @param innerRadius fraction of half-size where light is 100% (0–1)
     * @param outerRadius fraction of half-size where darkness is 100% (0–1)
     */
    private Texture createDarknessOverlay(int size, float innerRadius, float outerRadius) {
        Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        pixmap.setBlending(Pixmap.Blending.None);
        float cx = size / 2f;
        float cy = size / 2f;
        float maxDist = size / 2f;
        for (int py = 0; py < size; py++) {
            for (int px = 0; px < size; px++) {
                float dx   = px - cx;
                float dy   = py - cy;
                float dist = (float) Math.sqrt(dx * dx + dy * dy) / maxDist;
                float alpha;
                if (dist <= innerRadius) {
                    alpha = 0f;                                          // fully transparent — lit area
                } else if (dist >= outerRadius) {
                    alpha = 1f;                                          // fully black — dark area
                } else {
                    float t = (dist - innerRadius) / (outerRadius - innerRadius);
                    alpha = t * t * (3f - 2f * t);                      // smoothstep for natural falloff
                }
                int a = Math.min(255, (int)(alpha * 255));
                // RGBA8888 packed int: 0xRRGGBBAA — black = R=0,G=0,B=0, alpha=a
                pixmap.drawPixel(px, py, a);
            }
        }
        Texture tex = new Texture(pixmap);
        tex.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
        pixmap.dispose();
        return tex;
    }

    /**
     * Draws the darkness overlay relative to the camera.
     * The overlay covers the screen and its UV coordinates are shifted
     * so the light gradient tracks the player.
     */
    private void drawDarknessOverlay() {
        float drawSize = 1100f; // Restore the normal size for the light circle
        float centreX  = player.getX() + CHAR_SIZE / 2f;
        float centreY  = player.getY() + CHAR_SIZE / 2f;

        float left = centreX - drawSize / 2f;
        float right = centreX + drawSize / 2f;
        float bottom = centreY - drawSize / 2f;
        float top = centreY + drawSize / 2f;

        batch.setColor(1f, 1f, 1f, 1f);
        // Draw the main gradient circle centered perfectly on the player
        batch.draw(darknessOverlay, left, bottom, drawSize, drawSize);

        // Grab a 1x1 solid black pixel from the corner of the darkness texture
        TextureRegion blackPixel = new TextureRegion(darknessOverlay, 0, 0, 1, 1);

        // Fill the rest of the world with massive solid black rectangles to ensure the camera never sees the edge
        float pad = 2000f;

        // Left
        batch.draw(blackPixel, left - pad, bottom - pad, pad, drawSize + pad * 2f);
        // Right
        batch.draw(blackPixel, right, bottom - pad, pad, drawSize + pad * 2f);
        // Bottom
        batch.draw(blackPixel, left, bottom - pad, drawSize, pad);
        // Top
        batch.draw(blackPixel, left, top, drawSize, pad);
    }

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        font.dispose();
        titleFont.dispose();
        bigFont.dispose();
        caveTex.dispose();
        sonaraTex.dispose();
        lyronTex.dispose();
        aureliusTex.dispose();
        titleScreenTex.dispose();
        darknessOverlay.dispose();
        for (Texture tex : animationTextures) tex.dispose();
    }
}
