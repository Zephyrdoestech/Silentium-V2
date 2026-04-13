package io.github.Zephyrdoestech;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;
import java.util.ArrayList;
import java.util.List;

public class Assets implements Disposable {
    //Buttons
    public final Texture startBtnTex;
    public final Texture tutorialBtnTex;
    public final Texture storyBtnTex;
    public final Texture creditsBtnTex;
    public final Texture exitBtnTex;

    public final com.badlogic.gdx.graphics.g2d.Animation<com.badlogic.gdx.graphics.g2d.TextureRegion> sonaraSelectAnim;
    public final com.badlogic.gdx.graphics.g2d.Animation<com.badlogic.gdx.graphics.g2d.TextureRegion> aureliusSelectAnim;

    // ── Fonts ─────────────────────────────────────────────────────────────────

    public final BitmapFont font;       // body text  (scale 1.5)
    public final BitmapFont titleFont;  // headings   (scale 2.2)
    public final BitmapFont bigFont;    // victory/defeat (scale 3.0)


    public final Texture titleScreenTex;

    // Story Slideshow
    public Texture storyPanel1;
    public Texture storyPanel2;
    public Texture storyPanel3;
    public Texture storyPanel4;

    // Maps and Decorations (These must be TextureRegions!)
    public final TextureRegion townTex;
    public final TextureRegion townExitTex;
    public final TextureRegion silentCavernsTex;
    public final TextureRegion cavernsExitTex;
    public final TextureRegion abyssOfDissonanceTex;
    public final TextureRegion townDecorationsTex;

    public final Texture sonaraTex;
    public final Texture lyronTex;
    public final Texture aureliusTex;
    public final Texture darknessOverlay;
    public final Texture[] noteTextures;

    public Music titleBGM;
    public Music storyBGM;
    public Music townOfEchoesBGM;
    public Music battleAbyssBGM;
    public Music battleBossBGM;

    public final Texture story1Tex;
    public final Texture story2Tex;
    public final Texture story3Tex;
    public final Texture story4Tex;

    public Animation<TextureRegion> darryllionIdle;// Added as per request

    // ── Exploration Animations ────────────────────────────────────────────────

    public final Animation<TextureRegion> aureliusIdleRight, aureliusIdleLeft;
    public final Animation<TextureRegion> aureliusWalkRight, aureliusWalkLeft;

    public final Animation<TextureRegion> sonaraIdleRight, sonaraIdleLeft;
    public final Animation<TextureRegion> sonaraWalkRight, sonaraWalkLeft;

    public final Animation<TextureRegion> lyronIdleRight, lyronIdleLeft;
    public final Animation<TextureRegion> lyronWalkRight, lyronWalkLeft;

    // ── Combat Background Textures ────────────────────────────────────────────

    public final Texture townCombatBackground;
    public final Texture cavernsCombatBackground;
    public final Texture abyssCombatBackground;

    // ── Combat HUD Textures ───────────────────────────────────────────────────

    public final Texture healthBar;
    public final Texture shieldBar;
    public final Texture staticHudBackground;
    public final Texture timerBackground;
    public final Texture dynamicHudBackground;
    public final Texture noteContainer;
    public final Texture noteContainerFilled;
    public final Texture turnMenuHud;
    public final Texture attackHud;
    public final Texture skillHud;
    public final Texture inventoryHud;
    public final Texture musicStaff;
    public final Texture musicNote;

    public final Texture cMajor;
    public final Texture dMinor;
    public final Texture eMinor;
    public final Texture fMajor;
    public final Texture gMajor;
    public final Texture aMinor;
    public final Texture bDim;

    public final Texture cMajorUsed;
    public final Texture dMinorUsed;
    public final Texture eMinorUsed;
    public final Texture fMajorUsed;
    public final Texture gMajorUsed;
    public final Texture aMinorUsed;
    public final Texture bDimUsed;

    // ── Item Textures ─────────────────────────────────────────────────────────

    public final Texture crimsonChorusBattleTex;
    public final Texture majorsBlessingBattleTex;
    public final Texture minorsGraceBattleTex;
    public final Texture resolvedDissonanceBattleTex;
    public final Texture silentBarrierBattleTex;
    public final Texture timeOrbBattleTex;

    public final Texture inventoryBackground;
    public final Texture crimsonChorusSlotItem;
    public final Texture majorsBlessingSlotItem;
    public final Texture minorsGraceSlotItem;
    public final Texture silentBarrierSlotItem;
    public final Texture resolvedDissonanceSlotItem;
    public  final Texture timeOrbSlotItem;

    // ── Combat Animations ─────────────────────────────────────────────────────

    public final Animation<TextureRegion> battleIntroAnim;
    public final Animation<TextureRegion> victoryAnim;
    public final Animation<TextureRegion> defeatAnim;
    public final Animation<TextureRegion> timerAnim;

    public final Animation<TextureRegion> sonaraCombatIdle;
    public final Animation<TextureRegion> sonaraCombatAttack;
    public final Animation<TextureRegion> aureliusCombatIdle;
    public final Animation<TextureRegion> aureliusCombatAttack;
    public final Animation<TextureRegion> lyronCombatIdle;
    public final Animation<TextureRegion> lyronCombatAttack;

    public final Animation<TextureRegion> fleshfeederCombatIdle;
    public final Animation<TextureRegion> fleshfeederCombatAttack;
    public final Animation<TextureRegion> darrylionCombatIdle;
    public final Animation<TextureRegion> darrylionCombatAttack;
    public final Animation<TextureRegion> gobninilCombatIdle;
    public final Animation<TextureRegion> gobninilCombatAttack;
    public final Animation<TextureRegion> chimericksCombatIdle;
    public final Animation<TextureRegion> chimericksCombatAttack;
    public final Animation<TextureRegion> labagoliathCombatIdle;
    public final Animation<TextureRegion> labagoliathCombatAttack;
    public final Animation<TextureRegion> syozanCombatIdle;
    public final Animation<TextureRegion> syozanCombatAttack;

    public Animation<TextureRegion> lyronSelectAnim;

//    public final Music sonaraTheme;
//    public final Music aureliusTheme;
//    public final Music lyronTheme;
    public final Music titleBgm;

    // Internal list so dispose() can clean up animation textures
    private final List<Texture> animationTextures = new ArrayList<>();

    // ── Constructor ───────────────────────────────────────────────────────────

    public Assets() {

        // Fonts
        font      = new BitmapFont(); font.getData().setScale(1.5f);
        titleFont = new BitmapFont(); titleFont.getData().setScale(2.2f);
        bigFont   = new BitmapFont(); bigFont.getData().setScale(3.0f);

        // Static textures
        titleScreenTex = safeLoadTexture("Background/Title_Screen/Title_Screen_Placeholder.png");

        // Load the Story Panels
        storyPanel1 = new Texture(Gdx.files.internal("Background/Story/story_panel_1.png"));
        storyPanel2 = new Texture(Gdx.files.internal("Background/Story/story_panel_2.png"));
        storyPanel3 = new Texture(Gdx.files.internal("Background/Story/story_panel_3.png"));
        storyPanel4 = new Texture(Gdx.files.internal("Background/Story/story_panel_4.png"));

        // Map Textures
        Texture townFile = safeLoadTexture("Background/Map/Town_Of_Echoes.png");
        if (townFile != null) townTex = new TextureRegion(townFile);
        else townTex = null;

        Texture townExitFile = safeLoadTexture("Background/Map/Town_Exit.png");
        if (townExitFile != null) townExitTex = new TextureRegion(townExitFile);
        else townExitTex = null;

        Texture silentCavernsFile = safeLoadTexture("Background/Map/Silent_Caverns.png");
        if (silentCavernsFile != null) silentCavernsTex = new TextureRegion(silentCavernsFile);
        else silentCavernsTex = null;

        Texture cavernsExitFile = safeLoadTexture("Background/Map/Caverns_Exit.png");
        if (cavernsExitFile != null) cavernsExitTex = new TextureRegion(cavernsExitFile);
        else cavernsExitTex = null;

        Texture abyssFile = safeLoadTexture("Background/Map/Abyss_Of_Dissonance.png");
        if (abyssFile != null) abyssOfDissonanceTex = new TextureRegion(abyssFile);
        else abyssOfDissonanceTex = null;

        Texture townDecorFull = safeLoadTexture("Background/Map/Town_Decorations.png");
        if (townDecorFull != null) townDecorationsTex = new TextureRegion(townDecorFull);
        else townDecorationsTex = null;

//        silentCavernsDecorationsTex = null; // TODO: load "Background/Map/Silent_Caverns_Decorations.png"
//        abyssDecorationsTex         = null; // TODO: load "Background/Map/Abyss_Decorations.png"

        sonaraTex = new Texture("sonara.png");
        lyronTex = new Texture("lyron.png");
        aureliusTex = new Texture("aurelius.png");

        this.startBtnTex = new Texture("UI/start_btn.png");
        this.tutorialBtnTex = new Texture("UI/tutorial_btn.png");
        this.storyBtnTex = new Texture("UI/story_btn.png");
        this.creditsBtnTex = new Texture("UI/credits_btn.png");
        this.exitBtnTex = new Texture("UI/exit_btn.png");

        story1Tex = safeLoadTexture("Background/Story/story_panel_1.png");
        story2Tex = safeLoadTexture("Background/Story/story_panel_2.png");
        story3Tex = safeLoadTexture("Background/Story/story_panel_3.png");
        story4Tex = safeLoadTexture("Background/Story/story_panel_4.png");

        // Load the 11-frame selection animations!
        sonaraSelectAnim = loadAnim("Sonara/Select", "sonaraSelect", 11, 0.1f);
        aureliusSelectAnim = loadAnim("Aurelius/Select", "aureliusSelect", 11, 0.1f);
        lyronSelectAnim = loadAnim("Lyron/Select", "lyronSelect", 10, 0.1f);

        darryllionIdle = loadAnim("Enemies/Darryllion/Idle", "darryllionIdle", 8, 0.15f);

        // Generated textures
        darknessOverlay = buildDarknessOverlay(1024, 0.12f, 0.45f);
        noteTextures = buildNoteTextures();

        aureliusIdleRight = loadAnim("Sprites/Characters/Aurelius/Idle",   "Idle",      4, 0.2f);
        aureliusIdleLeft  = flipped(aureliusIdleRight);
        aureliusWalkRight = loadAnim("Sprites/Characters/Aurelius/Walk",   "Movement",  6, 0.1f);
        aureliusWalkLeft  = flipped(aureliusWalkRight);

        sonaraIdleRight   = loadAnim("Sprites/Characters/Sonara/Idle",     "Idle",      4, 0.1f);
        sonaraIdleLeft    = flipped(sonaraIdleRight);
        sonaraWalkRight   = loadAnim("Sprites/Characters/Sonara/Walk",     "Movement",  6, 0.1f);
        sonaraWalkLeft    = flipped(sonaraWalkRight);

        lyronIdleRight    = loadAnim("Sprites/Characters/Lyron/Idle",      "Idle",      4, 0.1f);
        lyronIdleLeft     = flipped(lyronIdleRight);
        lyronWalkRight    = loadAnim("Sprites/Characters/Lyron/Walk",      "Movement",  6, 0.1f);
        lyronWalkLeft     = flipped(lyronWalkRight);

        // ── Combat Backgrounds ────────────────────────────────────────────────

        townCombatBackground    = new Texture("Background/Combat/Town.jpg");
        cavernsCombatBackground = new Texture("Background/Combat/Cavern.jpg");
        abyssCombatBackground   = new Texture("Background/Combat/Abyss.jpg");

        // ── Combat HUD ────────────────────────────────────────────────────────

        healthBar          = new Texture("Sprites/Combat/Interface/HealthBar.png");
        shieldBar          = new Texture("Sprites/Combat/Interface/ShieldBar.png");
        staticHudBackground  = new Texture("Sprites/Combat/Interface/StaticHUD/HUDBackground.png");
        timerBackground      = new Texture("Sprites/Combat/Interface/Timer/HUDBackground.png");
        dynamicHudBackground = new Texture("Sprites/Combat/Interface/DynamicHUD/HUDBackground.png");
        noteContainer        = new Texture("Sprites/Combat/Interface/DynamicHUD/NoteContainer.png");
        noteContainerFilled  = new Texture("Sprites/Combat/Interface/DynamicHUD/NoteContainerFilled.png");
        turnMenuHud          = new Texture("Sprites/Combat/Interface/DynamicHUD/TurnMenu.png");
        attackHud            = new Texture("Sprites/Combat/Interface/DynamicHUD/Attack.png");
        skillHud             = new Texture("Sprites/Combat/Interface/DynamicHUD/Skill.png");
        inventoryHud         = new Texture("Sprites/Combat/Interface/DynamicHUD/Inventory.png");
        musicStaff           = new Texture("Sprites/Combat/Interface/DynamicHUD/MusicStaff.png");
        musicNote            = new Texture("Sprites/Combat/Interface/DynamicHUD/MusicNote.png");

        cMajor = new Texture("Sprites/Combat/Interface/Chords/C_Major.png");
        dMinor = new Texture("Sprites/Combat/Interface/Chords/D_Minor.png");
        eMinor = new Texture("Sprites/Combat/Interface/Chords/E_Minor.png");
        fMajor = new Texture("Sprites/Combat/Interface/Chords/F_Major.png");
        gMajor = new Texture("Sprites/Combat/Interface/Chords/G_Major.png");
        aMinor = new Texture("Sprites/Combat/Interface/Chords/A_Minor.png");
        bDim = new Texture("Sprites/Combat/Interface/Chords/B_Dim.png");

        cMajorUsed = new Texture("Sprites/Combat/Interface/Chords/C_Used.png");
        dMinorUsed = new Texture("Sprites/Combat/Interface/Chords/D_Used.png");
        eMinorUsed = new Texture("Sprites/Combat/Interface/Chords/E_Used.png");
        fMajorUsed = new Texture("Sprites/Combat/Interface/Chords/F_Used.png");
        gMajorUsed = new Texture("Sprites/Combat/Interface/Chords/G_Used.png");
        aMinorUsed = new Texture("Sprites/Combat/Interface/Chords/A_Used.png");
        bDimUsed = new Texture("Sprites/Combat/Interface/Chords/B_Used.png");

        // ── Item Textures ─────────────────────────────────────────────────────

        crimsonChorusBattleTex = new Texture("Sprites/Combat/Interface/Inventory/Items/CrimsonChorus.png");
        majorsBlessingBattleTex = new Texture("Sprites/Combat/Interface/Inventory/Items/MajorsBlessing.png");
        minorsGraceBattleTex = new Texture("Sprites/Combat/Interface/Inventory/Items/MinorsGrace.png");
        resolvedDissonanceBattleTex = new Texture("Sprites/Combat/Interface/Inventory/Items/ResolvedDissonance.png");
        silentBarrierBattleTex = new Texture("Sprites/Combat/Interface/Inventory/Items/SilentBarrier.png");
        timeOrbBattleTex = new Texture("Sprites/Combat/Interface/Inventory/Items/TimeOrb.png");

        inventoryBackground = new Texture("Sprites/Combat/Interface/Inventory/InventoryBG.png");
        crimsonChorusSlotItem = new Texture("Sprites/Combat/Interface/Inventory/SlotItems/CrimsonChorus.png");
        majorsBlessingSlotItem = new Texture("Sprites/Combat/Interface/Inventory/SlotItems/MajorsBlessing.png");
        minorsGraceSlotItem = new Texture("Sprites/Combat/Interface/Inventory/SlotItems/MinorsGrace.png");
        silentBarrierSlotItem = new Texture("Sprites/Combat/Interface/Inventory/SlotItems/SilentBarrier.png");
        resolvedDissonanceSlotItem = new Texture("Sprites/Combat/Interface/Inventory/SlotItems/ResolvedDissonance.png");
        timeOrbSlotItem = new Texture("Sprites/Combat/Interface/Inventory/SlotItems/TimeOrb.png");

        // ── Combat Animations ─────────────────────────────────────────────────

        battleIntroAnim = loadAnim("Sprites/Combat/SplashScreen/Intro",   "",  20, 0.05f);
        victoryAnim     = loadAnim("Sprites/Combat/SplashScreen/Victory", "", 30, 0.05f);
        defeatAnim      = loadAnim("Sprites/Combat/SplashScreen/Defeat",  "",  30, 0.05f);
        timerAnim       = loadAnim("Sprites/Combat/Interface/Timer/TimerAnim", "Timer", 4, 0.2f);

        sonaraCombatIdle     = loadAnim("Sprites/Combat/Character/Sonara/Idle",     "Idle",   4, 0.2f);
        sonaraCombatAttack   = loadAnim("Sprites/Combat/Character/Sonara/Attack",   "Attack", 6, 0.1f);
        aureliusCombatIdle   = loadAnim("Sprites/Combat/Character/Aurelius/Idle",   "Idle",   4, 0.2f);
        aureliusCombatAttack = loadAnim("Sprites/Combat/Character/Aurelius/Attack", "Attack", 6, 0.1f);
        lyronCombatIdle      = loadAnim("Sprites/Combat/Character/Lyron/Idle",      "Idle",   4, 0.2f);
        lyronCombatAttack    = loadAnim("Sprites/Combat/Character/Lyron/Attack",    "Attack", 6, 0.1f);

        fleshfeederCombatIdle    = flipped(loadAnim("Sprites/Combat/Monster/Fleshfeeder/Idle",    "Idle",   4, 0.2f));
        fleshfeederCombatAttack  = flipped(loadAnim("Sprites/Combat/Monster/Fleshfeeder/Attack",  "Attack", 6, 0.2f));
        darrylionCombatIdle      = flipped(loadAnim("Sprites/Combat/Monster/Darryllion/Idle",      "Idle",   4, 0.2f));
        darrylionCombatAttack    = flipped(loadAnim("Sprites/Combat/Monster/Darryllion/Attack",    "Attack", 6, 0.2f));
        gobninilCombatIdle       = flipped(loadAnim("Sprites/Combat/Monster/Gobninil/Idle",       "Idle",   4, 0.2f));
        gobninilCombatAttack     = flipped(loadAnim("Sprites/Combat/Monster/Gobninil/Attack",     "Attack", 6, 0.2f));
        chimericksCombatIdle     = flipped(loadAnim("Sprites/Combat/Monster/Chimericks/Idle",     "Idle",   4, 0.2f));
        chimericksCombatAttack   = flipped(loadAnim("Sprites/Combat/Monster/Chimericks/Attack",   "Attack", 6, 0.2f));
        labagoliathCombatIdle    = flipped(loadAnim("Sprites/Combat/Monster/Labagoliath/Idle",    "Idle",   4, 0.2f));
        labagoliathCombatAttack  = flipped(loadAnim("Sprites/Combat/Monster/Labagoliath/Attack",  "Attack", 6, 0.2f));
        syozanCombatIdle         = flipped(loadAnim("Sprites/Combat/Monster/Syozan/Idle",         "Idle",   4, 0.2f));
        syozanCombatAttack       = flipped(loadAnim("Sprites/Combat/Monster/Syozan/Attack",       "Attack", 6, 0.2f));

        // ── Audio ─────────────────────────────────────────────────────────────

        // Load Background Music
        storyBGM = Gdx.audio.newMusic(Gdx.files.internal("Audio/background_music/Story.wav"));
        townOfEchoesBGM = Gdx.audio.newMusic(Gdx.files.internal("Audio/background_music/TownOfEchoes.wav"));

        // Update this to match your "title_music.wav" file
        titleBgm = Gdx.audio.newMusic(Gdx.files.internal("Audio/background_music/title_music.wav"));

        // Load Battle Music
        battleAbyssBGM = Gdx.audio.newMusic(Gdx.files.internal("Audio/BGM_BATTLE_abyss.wav"));
        battleBossBGM = Gdx.audio.newMusic(Gdx.files.internal("Audio/BGM_BATTLE_boss.wav"));

        // Set Loops
        titleBgm.setLooping(true);
        storyBGM.setLooping(true);
        townOfEchoesBGM.setLooping(true);
    }

    // ── Private Helpers ───────────────────────────────────────────────────────

    private Texture safeLoadTexture(String path) {
        if (Gdx.files.internal(path).exists()) {
            return new Texture(path);
        }
        System.out.println("Missing Art: " + path);
        return null;
    }

    private Animation<TextureRegion> loadAnim(String folder, String base, int count, float duration) {
        TextureRegion[] frames = new TextureRegion[count];
        for (int i = 0; i < count; i++) {
            String path = folder + "/" + base + (i + 1) + ".png";
            // Safety Check: If the file is missing, print a warning and cancel the animation!
            if (!Gdx.files.internal(path).exists()) {
                System.out.println("Missing Anim Frame: " + path);
                return null;
            }

            Texture tex = new Texture(path);
            animationTextures.add(tex);
            frames[i] = new TextureRegion(tex);
        }
        return new Animation<>(duration, frames);
    }

    private Animation<TextureRegion> flipped(Animation<TextureRegion> src) {
        if (src == null) return null;
        TextureRegion[] orig    = src.getKeyFrames();
        TextureRegion[] flipped = new TextureRegion[orig.length];
        for (int i = 0; i < orig.length; i++) {
            flipped[i] = new TextureRegion(orig[i]);
            flipped[i].flip(true, false);
        }
        return new Animation<>(src.getFrameDuration(), flipped);
    }

    private Texture buildDarknessOverlay(int size, float inner, float outer) {
        Pixmap p  = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        p.setBlending(Pixmap.Blending.None);
        float cx = size / 2f, cy = size / 2f, maxDist = size / 2f;
        for (int py = 0; py < size; py++) {
            for (int px = 0; px < size; px++) {
                float dx   = px - cx, dy = py - cy;
                float dist = (float) Math.sqrt(dx * dx + dy * dy) / maxDist;
                float alpha;
                if      (dist <= inner) alpha = 0f;
                else if (dist >= outer) alpha = 1f;
                else { float t = (dist - inner) / (outer - inner); alpha = t * t * (3f - 2f * t); }
                p.drawPixel(px, py, Math.min(255, (int)(alpha * 255)));
            }
        }
        Texture tex = new Texture(p);
        tex.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
        p.dispose();
        return tex;
    }

    private Texture[] buildNoteTextures() {
        int S = 32;
        Texture[] out = new Texture[4];

        // 0: Quarter note
        { Pixmap p = blankPixmap(S);
            p.setColor(Color.WHITE); p.fillCircle(9, 9, 6);
            for (int y = 9; y <= 28; y++) p.drawPixel(15, y, 0xFFFFFFFF);
            out[0] = new Texture(p); p.dispose(); }

        // 1: Eighth note
        { Pixmap p = blankPixmap(S);
            p.setColor(Color.WHITE); p.fillCircle(9, 9, 6);
            for (int y = 9; y <= 28; y++) p.drawPixel(15, y, 0xFFFFFFFF);
            for (int i = 0; i < 8; i++) { p.drawPixel(15 + i, 28 - i, 0xFFFFFFFF); p.drawPixel(15 + i, 29 - i, 0xFFFFFFFF); }
            out[1] = new Texture(p); p.dispose(); }

        // 2: Beamed pair
        { Pixmap p = blankPixmap(S);
            p.setColor(Color.WHITE); p.fillCircle(7, 7, 5);
            for (int y = 7;  y <= 26; y++) p.drawPixel(12, y, 0xFFFFFFFF);
            p.fillCircle(20, 10, 5);
            for (int y = 10; y <= 26; y++) p.drawPixel(25, y, 0xFFFFFFFF);
            for (int x = 12; x <= 25; x++) { p.drawPixel(x, 26, 0xFFFFFFFF); p.drawPixel(x, 25, 0xFFFFFFFF); p.drawPixel(x, 24, 0xFFFFFFFF); }
            out[2] = new Texture(p); p.dispose(); }

        // 3: Double beamed pair
        { Pixmap p = blankPixmap(S);
            p.setColor(Color.WHITE); p.fillCircle(5, 6, 4);
            for (int y = 6;  y <= 26; y++) p.drawPixel(9,  y, 0xFFFFFFFF);
            p.fillCircle(14, 8, 4);
            for (int y = 8;  y <= 26; y++) p.drawPixel(18, y, 0xFFFFFFFF);
            for (int x = 9; x <= 18; x++) { p.drawPixel(x, 26, 0xFFFFFFFF); p.drawPixel(x, 25, 0xFFFFFFFF); p.drawPixel(x, 21, 0xFFFFFFFF); p.drawPixel(x, 20, 0xFFFFFFFF); }
            p.fillCircle(22, 6, 4); p.fillCircle(28, 9, 3);
            out[3] = new Texture(p); p.dispose(); }

        return out;
    }

    private Pixmap blankPixmap(int size) {
        Pixmap p = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        p.setBlending(Pixmap.Blending.None);
        p.setColor(1f, 1f, 1f, 0f);
        p.fill();
        return p;
    }

    public void stopAllMusic() {
        if (titleBgm != null) titleBgm.stop();
        if (townOfEchoesBGM != null) townOfEchoesBGM.stop();
        if (battleAbyssBGM != null) battleAbyssBGM.stop();
        if (battleBossBGM != null) battleBossBGM.stop();
    }

    // ── Dispose ───────────────────────────────────────────────────────────────

    @Override
    public void dispose() {
        font.dispose();
        titleFont.dispose();
        bigFont.dispose();

        titleScreenTex.dispose();
        if (townTex != null && townTex.getTexture() != null) {
            townTex.getTexture().dispose();
        }
        if (sonaraTex != null) {
            sonaraTex.dispose();
        }
        if (lyronTex != null) {
            lyronTex.dispose();
        }
        if (aureliusTex != null) {
            aureliusTex.dispose();
        }
        if (darknessOverlay != null) {
            darknessOverlay.dispose();
        }

        story1Tex.dispose();
        story2Tex.dispose();
        story3Tex.dispose();
        story4Tex.dispose();

        for (Texture t : noteTextures) {
            if (t != null) {
                t.dispose();
            }
        }
        for (Texture t : animationTextures) {
            if (t != null) {
                t.dispose();
            }
        }

        if (townCombatBackground != null) {
            townCombatBackground.dispose();
        }
        if (cavernsCombatBackground != null) {
            cavernsCombatBackground.dispose();
        }
        if (abyssCombatBackground != null) {
            abyssCombatBackground.dispose();
        }

        if (healthBar != null) {
            healthBar.dispose();
        }
        if (shieldBar != null) {
            shieldBar.dispose();
        }
        if (staticHudBackground != null) {
            staticHudBackground.dispose();
        }
        if (timerBackground != null) {
            timerBackground.dispose();
        }
        if (dynamicHudBackground != null) {
            dynamicHudBackground.dispose();
        }
        if (noteContainer != null) {
            noteContainer.dispose();
        }
        if (turnMenuHud != null) {
            turnMenuHud.dispose();
        }
        if (attackHud != null) {
            attackHud.dispose();
        }
        if (skillHud != null) {
            skillHud.dispose();
        }
        if (inventoryHud != null) {
            inventoryHud.dispose();
        }

        if (titleBgm != null) {
            titleBgm.dispose();
        }
    }
}
