package io.github.Zephyrdoestech;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
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

    public final Texture pauseMenuBG;
    public final Texture pauseContinueBtn;
    public final Texture pauseChordInfoBtn;
    public final Texture pauseItemInfoBtn;
    public final Texture pauseExitBtn;

    public final Texture inventoryBtnTex;
    public final Texture pauseBtnTex;
    public final Texture menuBtnTex;

    public final com.badlogic.gdx.graphics.g2d.Animation<com.badlogic.gdx.graphics.g2d.TextureRegion> sonaraSelectAnim;
    public final com.badlogic.gdx.graphics.g2d.Animation<com.badlogic.gdx.graphics.g2d.TextureRegion> aureliusSelectAnim;

    // ── Fonts ─────────────────────────────────────────────────────────────────

    public final BitmapFont font;       // body text  (scale 1.5)
    public final BitmapFont titleFont;  // headings   (scale 2.2)
    public final BitmapFont bigFont;    // victory/defeat (scale 3.0)
    public final BitmapFont loreFont;   // lore text  (scale 1.5)


    public final Texture titleScreenTex;
    public final Texture characterSelectBG;
    public final Texture mainMenuBG;

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

    public final Animation<TextureRegion> sonaraCardDefault;
    public final Animation<TextureRegion> sonaraCardSelected;
    public final Animation<TextureRegion> aureliusCardDefault;
    public final Animation<TextureRegion> aureliusCardSelected;
    public final Animation<TextureRegion> lyronCardDefault;
    public final Animation<TextureRegion> lyronCardSelected;

    public final Animation<TextureRegion> sonaraMonologueBox;
    public final Animation<TextureRegion> aureliusMonologueBox;
    public final Animation<TextureRegion> lyronMonologueBox;

    public final Texture sonaraTex;
    public final Texture lyronTex;
    public final Texture aureliusTex;
    public final Texture darknessOverlay;
    public final Texture[] noteTextures;

    public Music storyBGM;
    public Music townOfEchoesBGM;
    public Music battleTownBGM;
    public Music battleCavernsBGM;
    public Music battleAbyssBGM;
    public Music battleBossBGM;

    public Sound victory;
    public Sound defeat;
    public Sound enemyEncounter;
    public Sound stateTransition;

    public Sound noteAttackBanjoA;
    public Sound noteAttackBanjoB;
    public Sound noteAttackBanjoC;
    public Sound noteAttackBanjoD;
    public Sound noteAttackBanjoE;
    public Sound noteAttackBanjoF;
    public Sound noteAttackBanjoG;
    public Sound chordAttackBanjoAmin;
    public Sound chordAttackBanjoBdim;
    public Sound chordAttackBanjoCmaj;
    public Sound chordAttackBanjoDmin;
    public Sound chordAttackBanjoEmin;
    public Sound chordAttackBanjoFmaj;
    public Sound chordAttackBanjoGmaj;

    public Sound noteAttackFluteA;
    public Sound noteAttackFluteB;
    public Sound noteAttackFluteC;
    public Sound noteAttackFluteD;
    public Sound noteAttackFluteE;
    public Sound noteAttackFluteF;
    public Sound noteAttackFluteG;
    public Sound chordAttackFluteAmin;
    public Sound chordAttackFluteBdim;
    public Sound chordAttackFluteCmaj;
    public Sound chordAttackFluteDmin;
    public Sound chordAttackFluteEmin;
    public Sound chordAttackFluteFmaj;
    public Sound chordAttackFluteGmaj;

    public Sound noteAttackHarpA;
    public Sound noteAttackHarpB;
    public Sound noteAttackHarpC;
    public Sound noteAttackHarpD;
    public Sound noteAttackHarpE;
    public Sound noteAttackHarpF;
    public Sound noteAttackHarpG;
    public Sound chordAttackHarpAmin;
    public Sound chordAttackHarpBdim;
    public Sound chordAttackHarpCmaj;
    public Sound chordAttackHarpDmin;
    public Sound chordAttackHarpEmin;
    public Sound chordAttackHarpFmaj;
    public Sound chordAttackHarpGmaj;

    public final Texture story1Tex;
    public final Texture story2Tex;
    public final Texture story3Tex;
    public final Texture story4Tex;


    // ── Exploration Animations ────────────────────────────────────────────────

    public final Animation<TextureRegion> aureliusIdleRight, aureliusIdleLeft;
    public final Animation<TextureRegion> aureliusWalkRight, aureliusWalkLeft;

    public final Animation<TextureRegion> sonaraIdleRight, sonaraIdleLeft;
    public final Animation<TextureRegion> sonaraWalkRight, sonaraWalkLeft;

    public final Animation<TextureRegion> lyronIdleRight, lyronIdleLeft;
    public final Animation<TextureRegion> lyronWalkRight, lyronWalkLeft;

    // ── Combat Background Textures ────────────────────────────────────────────

    public final Texture[] tutorials;

    public final Texture noteTutorial;
    public final Texture metronomeTutorial;
    public final Texture chordTutorial;

    public final Texture chordInfoPage1;
    public final Texture chordInfoPage2;
    public final Texture itemInfoPage1;
    public final Texture itemInfoPage2;
    public final Texture[] chordInfo;
    public final Texture[] itemInfo;

    // ── Tutorial Screen Textures ────────────────────────────────────────────

    public final Texture townCombatBackground;
    public final Texture cavernsCombatBackground;
    public final Texture abyssCombatBackground;

    // ── Combat HUD Textures ───────────────────────────────────────────────────

    public final Texture playerHeaderSonara;
    public final Texture playerHeaderAurelius;
    public final Texture playerHeaderLyron;

    public final Texture enemyHeaderFleshFeeder;
    public final Texture enemyHeaderDarryllion;
    public final Texture enemyHeaderGobninil;
    public final Texture enemyHeaderChimericks;
    public final Texture enemyHeaderLabagoliath;
    public final Texture enemyHeaderSyozan;

    public final Texture mapHeaderTownOfEchoes;
    public final Texture mapHeaderSilentCaverns;
    public final Texture mapHeaderAbyssOfDissonance;

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

    public final Texture continueBtnTex;

    // ── Item Textures ─────────────────────────────────────────────────────────

    public final Texture crimsonChorusBattleTex;
    public final Texture majorsBlessingBattleTex;
    public final Texture minorsGraceBattleTex;
    public final Texture resolvedDissonanceBattleTex;
    public final Texture silentBarrierBattleTex;
    public final Texture timeOrbBattleTex;

    public final Texture inventoryPanelBackground;
    public final Texture inventoryBackground;
    public final Texture crimsonChorusSlotItem;
    public final Texture majorsBlessingSlotItem;
    public final Texture minorsGraceSlotItem;
    public final Texture silentBarrierSlotItem;
    public final Texture resolvedDissonanceSlotItem;
    public final Texture timeOrbSlotItem;
    public final Texture emptySlotItem;
    public final Texture selectedSlotItem;

    // ── Combat Animations ─────────────────────────────────────────────────────

    public final Animation<TextureRegion> battleIntroAnim;
    public final Animation<TextureRegion> victoryAnim;
    public final Animation<TextureRegion> defeatAnim;
    public final Animation<TextureRegion> timerAnim;

    public final Animation<TextureRegion> sonaraCombatIdle;
    public final Animation<TextureRegion> sonaraCombatAttack;
    public final Animation<TextureRegion> sonaraCombatDeath;
    public final Animation<TextureRegion> sonaraCombatDamaged;
    public final Animation<TextureRegion> aureliusCombatIdle;
    public final Animation<TextureRegion> aureliusCombatAttack;
    public final Animation<TextureRegion> aureliusCombatDeath;
    public final Animation<TextureRegion> aureliusCombatDamaged;
    public final Animation<TextureRegion> lyronCombatIdle;
    public final Animation<TextureRegion> lyronCombatAttack;
    public final Animation<TextureRegion> lyronCombatDeath;
    public final Animation<TextureRegion> lyronCombatDamaged;

    public final Animation<TextureRegion> fleshfeederCombatIdle;
    public final Animation<TextureRegion> fleshfeederCombatAttack;
    public final Animation<TextureRegion> fleshfeederCombatDamaged;

    public final Animation<TextureRegion> darryllionCombatIdle;
    public final Animation<TextureRegion> darryllionCombatAttack1;
    public final Animation<TextureRegion> darryllionCombatAttack2;
    public final Animation<TextureRegion> darryllionCombatDamaged;

    public final Animation<TextureRegion> gobninilCombatIdle;
    public final Animation<TextureRegion> gobninilCombatAttack;
    public final Animation<TextureRegion> gobninilCombatDamaged;

    public final Animation<TextureRegion> chimericksCombatIdle;
    public final Animation<TextureRegion> chimericksCombatAttack;
    public final Animation<TextureRegion> chimericksCombatDamaged;

    public final Animation<TextureRegion> labagoliathCombatIdle;
    public final Animation<TextureRegion> labagoliathCombatAttack;
    public final Animation<TextureRegion> labagoliathCombatDamaged;

    public final Animation<TextureRegion> syozanCombatIdle;
    public final Animation<TextureRegion> syozanCombatAttack;
    public final Animation<TextureRegion> syozanCombatDamaged;

    public Animation<TextureRegion> lyronSelectAnim;

    public final Music titleBGM;

    // Internal list so dispose() can clean up animation textures
    private final List<Texture> animationTextures = new ArrayList<>();

    // ── Constructor ───────────────────────────────────────────────────────────

    public Assets() {
        // Fonts
        font      = new BitmapFont(); font.getData().setScale(1.5f);
        titleFont = new BitmapFont(); titleFont.getData().setScale(2.2f);
        bigFont   = new BitmapFont(); bigFont.getData().setScale(3.0f);

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Fonts/HTOWERT.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

            // Set the base size of the font you want
        parameter.size = 24;
            // You can also easily add borders, shadows, and colors here!
        parameter.borderWidth = 1;

        loreFont = generator.generateFont(parameter);
        loreFont.getData().setScale(1.5f);

        // Static textures
        titleScreenTex = safeLoadTexture("Background/Title_Screen/Title_Screen_Placeholder.png");
        characterSelectBG = safeLoadTexture("Background/Texture/Cobblestone.png");
        mainMenuBG = safeLoadTexture("Background/Texture/Cobblestone.png");

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

        sonaraCardDefault = loadAnim("Sprites/Characters/Sonara/PlayerCard/default",   "",  10, 0.12f);
        sonaraCardSelected = loadAnim("Sprites/Characters/Sonara/PlayerCard/selected",   "",  10, 0.12f);
        aureliusCardDefault = loadAnim("Sprites/Characters/Aurelius/PlayerCard/default",   "",  10, 0.12f);
        aureliusCardSelected = loadAnim("Sprites/Characters/Aurelius/PlayerCard/selected",   "",  10, 0.12f);
        lyronCardDefault = loadAnim("Sprites/Characters/Lyron/PlayerCard/default",   "",  10, 0.12f);
        lyronCardSelected = loadAnim("Sprites/Characters/Lyron/PlayerCard/selected",   "",  10, 0.12f);

        sonaraMonologueBox = loadAnim("Sprites/Characters/Sonara/MonologueBox",   "",  10, 0.12f);
        aureliusMonologueBox = loadAnim("Sprites/Characters/Aurelius/MonologueBox",   "",  10, 0.12f);
        lyronMonologueBox = loadAnim("Sprites/Characters/Lyron/MonologueBox",   "",  10, 0.12f);

        sonaraTex = new Texture("sonara.png");
        lyronTex = new Texture("lyron.png");
        aureliusTex = new Texture("aurelius.png");

        this.startBtnTex = new Texture("UI/start_btn.png");
        continueBtnTex = new Texture("UI/continue_btn.png");
        this.tutorialBtnTex = new Texture("UI/tutorial_btn.png");
        this.storyBtnTex = new Texture("UI/story_btn.png");
        this.creditsBtnTex = new Texture("UI/credits_btn.png");
        this.exitBtnTex = new Texture("UI/exit_btn.png");

        pauseMenuBG = new Texture("UI/Pause/pause.png");
        pauseContinueBtn = new Texture("UI/Pause/btn_continue.png");
        pauseChordInfoBtn = new Texture("UI/Pause/btn_chordinfo.png");
        pauseItemInfoBtn = new Texture("UI/Pause/btn_iteminfo.png");
        pauseExitBtn = new Texture("UI/Pause/btn_exit.png");

        inventoryBtnTex = new Texture("UI/Buttons/btn_inventory.png");
        pauseBtnTex = new Texture("UI/Buttons/btn_pause.png");
        menuBtnTex = new Texture("UI/Buttons/btn_menu.png");

        story1Tex = safeLoadTexture("Background/Story/story_panel_1.png");
        story2Tex = safeLoadTexture("Background/Story/story_panel_2.png");
        story3Tex = safeLoadTexture("Background/Story/story_panel_3.png");
        story4Tex = safeLoadTexture("Background/Story/story_panel_4.png");

        // Load the 11-frame selection animations!
        sonaraSelectAnim = loadAnim("Sonara/Select", "sonaraSelect", 11, 0.1f);
        aureliusSelectAnim = loadAnim("Aurelius/Select", "aureliusSelect", 11, 0.1f);
        lyronSelectAnim = loadAnim("Lyron/Select", "lyronSelect", 10, 0.1f);


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

        noteTutorial = new Texture("Sprites/Combat/Tutorial/NoteTutorial.png");
        metronomeTutorial = new Texture("Sprites/Combat/Tutorial/MetronomeTutorial.png");
        chordTutorial = new Texture("Sprites/Combat/Tutorial/ChordTutorial.png");
        tutorials = new Texture[] {noteTutorial, metronomeTutorial, chordTutorial};

        chordInfoPage1 = new Texture("Sprites/Combat/Pause/ChordInfo/1.png");
        chordInfoPage2 = new Texture("Sprites/Combat/Pause/ChordInfo/2.png");
        itemInfoPage1 = new Texture("Sprites/Combat/Pause/ItemInfo/1.png");
        itemInfoPage2 = new Texture("Sprites/Combat/Pause/ItemInfo/2.png");
        chordInfo = new Texture[] {chordInfoPage1, chordInfoPage2};
        itemInfo = new Texture[] {itemInfoPage1, itemInfoPage2};

        // ── Tutorial Screens ────────────────────────────────────────────────

        townCombatBackground    = new Texture("Background/Combat/Town.png");
        cavernsCombatBackground = new Texture("Background/Combat/Cavern.png");
        abyssCombatBackground   = new Texture("Background/Combat/Abyss.png");

        // ── Combat HUD ────────────────────────────────────────────────────────

        playerHeaderSonara          = new Texture("Sprites/Combat/Interface/BattleHeader/Character/Sonara.png");
        playerHeaderAurelius        = new Texture("Sprites/Combat/Interface/BattleHeader/Character/Aurelius.png");
        playerHeaderLyron           = new Texture("Sprites/Combat/Interface/BattleHeader/Character/Lyron.png");

        enemyHeaderFleshFeeder      = new Texture("Sprites/Combat/Interface/BattleHeader/Monster/FleshFeeder.png");
        enemyHeaderDarryllion       = new Texture("Sprites/Combat/Interface/BattleHeader/Monster/Darryllion.png");
        enemyHeaderGobninil         = new Texture("Sprites/Combat/Interface/BattleHeader/Monster/Gobninil.png");
        enemyHeaderChimericks       = new Texture("Sprites/Combat/Interface/BattleHeader/Monster/Chimericks.png");
        enemyHeaderLabagoliath      = new Texture("Sprites/Combat/Interface/BattleHeader/Monster/Labagoliath.png");
        enemyHeaderSyozan           = new Texture("Sprites/Combat/Interface/BattleHeader/Monster/Syozan.png");

        mapHeaderTownOfEchoes       = new Texture("Sprites/Combat/Interface/BattleHeader/Map/TownOfEchoes.png");
        mapHeaderSilentCaverns      = new Texture("Sprites/Combat/Interface/BattleHeader/Map/SilentCaverns.png");
        mapHeaderAbyssOfDissonance  = new Texture("Sprites/Combat/Interface/BattleHeader/Map/AbyssOfDissonance.png");

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

        inventoryPanelBackground = new Texture("UI/Panels/Inventory.png");

        inventoryBackground = new Texture("Sprites/Combat/Interface/Inventory/InventoryBG.png");
        crimsonChorusSlotItem = new Texture("Sprites/Combat/Interface/Inventory/SlotItems/CrimsonChorus.png");
        majorsBlessingSlotItem = new Texture("Sprites/Combat/Interface/Inventory/SlotItems/MajorsBlessing.png");
        minorsGraceSlotItem = new Texture("Sprites/Combat/Interface/Inventory/SlotItems/MinorsGrace.png");
        silentBarrierSlotItem = new Texture("Sprites/Combat/Interface/Inventory/SlotItems/SilentBarrier.png");
        resolvedDissonanceSlotItem = new Texture("Sprites/Combat/Interface/Inventory/SlotItems/ResolvedDissonance.png");
        timeOrbSlotItem = new Texture("Sprites/Combat/Interface/Inventory/SlotItems/TimeOrb.png");
        emptySlotItem = new Texture("Sprites/Combat/Interface/Inventory/SlotItems/EmptySlot.png");
        selectedSlotItem = new Texture("Sprites/Combat/Interface/Inventory/SlotItems/SelectedSlot.png");

        // ── Combat Animations ─────────────────────────────────────────────────

        battleIntroAnim = loadAnim("Sprites/Combat/SplashScreen/Intro",   "",  20, 0.05f);
        victoryAnim     = loadAnim("Sprites/Combat/SplashScreen/Victory", "", 30, 0.05f);
        defeatAnim      = loadAnim("Sprites/Combat/SplashScreen/Defeat",  "",  30, 0.05f);
        timerAnim       = loadAnim("Sprites/Combat/Interface/Timer/TimerAnim", "Timer", 4, 0.2f);

        sonaraCombatIdle     = loadAnim("Sprites/Combat/CharacterHero/Sonara/Idle",     "Idle",   4, 0.2f);
        sonaraCombatAttack   = loadAnim("Sprites/Combat/CharacterHero/Sonara/Attack",   "", 13, 0.12f);
        sonaraCombatDeath    = loadAnim("Sprites/Combat/CharacterHero/Sonara/Death",   "", 16, 0.1f);
        sonaraCombatDamaged  = loadAnim("Sprites/Combat/CharacterHero/Sonara/Damaged",   "", 8, 0.1f);
        aureliusCombatIdle   = loadAnim("Sprites/Combat/CharacterHero/Aurelius/Idle",   "Idle",   4, 0.2f);
        aureliusCombatAttack = loadAnim("Sprites/Combat/CharacterHero/Aurelius/Attack", "", 12, 0.12f);
        aureliusCombatDeath  = loadAnim("Sprites/Combat/CharacterHero/Aurelius/Death",   "", 16, 0.1f);
        aureliusCombatDamaged= loadAnim("Sprites/Combat/CharacterHero/Aurelius/Damaged",   "", 8, 0.1f);
        lyronCombatIdle      = loadAnim("Sprites/Combat/CharacterHero/Lyron/Idle",      "Idle",   4, 0.2f);
        lyronCombatAttack    = loadAnim("Sprites/Combat/CharacterHero/Lyron/Attack",    "", 13, 0.12f);
        lyronCombatDeath     = loadAnim("Sprites/Combat/CharacterHero/Lyron/Death",   "", 16, 0.1f);
        lyronCombatDamaged   = loadAnim("Sprites/Combat/CharacterHero/Lyron/Damaged",   "", 8, 0.1f);

        fleshfeederCombatIdle    = flipped(loadAnim("Sprites/Combat/Monster/Fleshfeeder/Idle",    "",   4, 0.2f));
        fleshfeederCombatAttack  = flipped(loadAnim("Sprites/Combat/Monster/Fleshfeeder/Attack",  "", 9, 0.12f));
        fleshfeederCombatDamaged = loadAnim("Sprites/Combat/Monster/Fleshfeeder/Damaged",  "", 4, 0.2f);

        darryllionCombatIdle     = flipped(loadAnim("Sprites/Combat/Monster/Darryllion/Idle",      "",   8, 0.2f));
        darryllionCombatAttack1  = loadAnim("Sprites/Combat/Monster/Darryllion/Attack1",    "", 9, 0.1f);
        darryllionCombatAttack2  = flipped(loadAnim("Sprites/Combat/Monster/Darryllion/Attack2",    "", 10, 0.16f));
        darryllionCombatDamaged  = loadAnim("Sprites/Combat/Monster/Darryllion/Damaged",    "", 4, 0.2f);

        gobninilCombatIdle       = flipped(loadAnim("Sprites/Combat/Monster/Gobninil/Idle",       "",   4, 0.2f));
        gobninilCombatAttack     = (loadAnim("Sprites/Combat/Monster/Gobninil/Attack",     "", 8, 1.2f));
        gobninilCombatDamaged     = loadAnim("Sprites/Combat/Monster/Gobninil/Damaged",     "", 4, 0.2f);

        chimericksCombatIdle     = flipped(loadAnim("Sprites/Combat/Monster/Chimericks/Idle",     "",   16, 0.12f));
        chimericksCombatAttack   = (loadAnim("Sprites/Combat/Monster/Chimericks/Attack",   "", 9, 0.1f));
        chimericksCombatDamaged   = loadAnim("Sprites/Combat/Monster/Chimericks/Damaged",   "", 6, 0.2f);

        labagoliathCombatIdle    = flipped(loadAnim("Sprites/Combat/Monster/Labagoliath/Idle",    "",   8, 0.2f));
        labagoliathCombatAttack  = loadAnim("Sprites/Combat/Monster/Labagoliath/Attack",  "", 9, 0.1f);
        labagoliathCombatDamaged = loadAnim("Sprites/Combat/Monster/Labagoliath/Damaged",  "", 4, 0.2f);

        syozanCombatIdle         = flipped(loadAnim("Sprites/Combat/Monster/Syozan/Idle",         "Idle",   4, 0.2f));
        syozanCombatAttack       = flipped(loadAnim("Sprites/Combat/Monster/Syozan/Attack",       "Attack", 6, 0.2f));
        syozanCombatDamaged       = loadAnim("Sprites/Combat/Monster/Syozan/Damaged",       "", 4, 0.2f);

        // ── Audio ─────────────────────────────────────────────────────────────

        // Load Background Music
        storyBGM = Gdx.audio.newMusic(Gdx.files.internal("Audio/background_music/Story.mp3"));
        townOfEchoesBGM = Gdx.audio.newMusic(Gdx.files.internal("Audio/background_music/TownOfEchoes.mp3"));

        // Update this to match your "title_music.wav" file
        titleBGM = Gdx.audio.newMusic(Gdx.files.internal("Audio/background_music/title_music.wav"));

        // Load Battle Music
        battleTownBGM = Gdx.audio.newMusic(Gdx.files.internal("Audio/Combat/BGM/battle_town.mp3"));
        battleCavernsBGM = Gdx.audio.newMusic(Gdx.files.internal("Audio/Combat/BGM/battle_caverns.mp3"));
        battleAbyssBGM = Gdx.audio.newMusic(Gdx.files.internal("Audio/Combat/BGM/battle_abyss.mp3"));
        battleBossBGM = Gdx.audio.newMusic(Gdx.files.internal("Audio/Combat/BGM/battle_boss.mp3"));

        // Battle SFX
        // State Transition
        stateTransition = Gdx.audio.newSound(Gdx.files.internal("Audio/Combat/SFX/state_transition.mp3"));
        // Splash Screen
        victory = Gdx.audio.newSound(Gdx.files.internal("Audio/Combat/SFX/victory.wav"));
        defeat = Gdx.audio.newSound(Gdx.files.internal("Audio/Combat/SFX/defeat.wav"));
        enemyEncounter = Gdx.audio.newSound(Gdx.files.internal("Audio/Combat/SFX/enemy_encounter.wav"));
        // Notes
        // Banjo (Sonara)
        noteAttackBanjoA = Gdx.audio.newSound(Gdx.files.internal("Audio/Combat/SFX/Notes/Banjo/A.wav"));
        noteAttackBanjoB = Gdx.audio.newSound(Gdx.files.internal("Audio/Combat/SFX/Notes/Banjo/B.wav"));
        noteAttackBanjoC = Gdx.audio.newSound(Gdx.files.internal("Audio/Combat/SFX/Notes/Banjo/C.wav"));
        noteAttackBanjoD = Gdx.audio.newSound(Gdx.files.internal("Audio/Combat/SFX/Notes/Banjo/D.wav"));
        noteAttackBanjoE = Gdx.audio.newSound(Gdx.files.internal("Audio/Combat/SFX/Notes/Banjo/E.wav"));
        noteAttackBanjoF = Gdx.audio.newSound(Gdx.files.internal("Audio/Combat/SFX/Notes/Banjo/F.wav"));
        noteAttackBanjoG = Gdx.audio.newSound(Gdx.files.internal("Audio/Combat/SFX/Notes/Banjo/G.wav"));
        chordAttackBanjoAmin = Gdx.audio.newSound(Gdx.files.internal("Audio/Combat/SFX/Notes/Banjo/Amin.wav"));
        chordAttackBanjoBdim = Gdx.audio.newSound(Gdx.files.internal("Audio/Combat/SFX/Notes/Banjo/Bdim.wav"));
        chordAttackBanjoCmaj = Gdx.audio.newSound(Gdx.files.internal("Audio/Combat/SFX/Notes/Banjo/Cmaj.wav"));
        chordAttackBanjoDmin = Gdx.audio.newSound(Gdx.files.internal("Audio/Combat/SFX/Notes/Banjo/Dmin.wav"));
        chordAttackBanjoEmin = Gdx.audio.newSound(Gdx.files.internal("Audio/Combat/SFX/Notes/Banjo/Emin.wav"));
        chordAttackBanjoFmaj = Gdx.audio.newSound(Gdx.files.internal("Audio/Combat/SFX/Notes/Banjo/Fmaj.wav"));
        chordAttackBanjoGmaj = Gdx.audio.newSound(Gdx.files.internal("Audio/Combat/SFX/Notes/Banjo/Gmaj.wav"));

        // Flute (Lyron)
        noteAttackFluteA = Gdx.audio.newSound(Gdx.files.internal("Audio/Combat/SFX/Notes/Flute/flute_A.wav"));
        noteAttackFluteB = Gdx.audio.newSound(Gdx.files.internal("Audio/Combat/SFX/Notes/Flute/flute_B.wav"));
        noteAttackFluteC = Gdx.audio.newSound(Gdx.files.internal("Audio/Combat/SFX/Notes/Flute/flute_C.wav"));
        noteAttackFluteD = Gdx.audio.newSound(Gdx.files.internal("Audio/Combat/SFX/Notes/Flute/flute_D.mp3"));
        noteAttackFluteE = Gdx.audio.newSound(Gdx.files.internal("Audio/Combat/SFX/Notes/Flute/flute_E.mp3"));
        noteAttackFluteF = Gdx.audio.newSound(Gdx.files.internal("Audio/Combat/SFX/Notes/Flute/flute_F.wav"));
        noteAttackFluteG = Gdx.audio.newSound(Gdx.files.internal("Audio/Combat/SFX/Notes/Flute/flute_G.wav"));
        chordAttackFluteAmin = Gdx.audio.newSound(Gdx.files.internal("Audio/Combat/SFX/Notes/Flute/flute_A_Minor.mp3"));
        chordAttackFluteBdim = Gdx.audio.newSound(Gdx.files.internal("Audio/Combat/SFX/Notes/Flute/flute_B_Diminished.mp3"));
        chordAttackFluteCmaj = Gdx.audio.newSound(Gdx.files.internal("Audio/Combat/SFX/Notes/Flute/flute_C_Major.mp3"));
        chordAttackFluteDmin = Gdx.audio.newSound(Gdx.files.internal("Audio/Combat/SFX/Notes/Flute/flute_D_Minor.mp3"));
        chordAttackFluteEmin = Gdx.audio.newSound(Gdx.files.internal("Audio/Combat/SFX/Notes/Flute/flute_E_Minor.mp3"));
        chordAttackFluteFmaj = Gdx.audio.newSound(Gdx.files.internal("Audio/Combat/SFX/Notes/Flute/flute_F_Major.mp3"));
        chordAttackFluteGmaj = Gdx.audio.newSound(Gdx.files.internal("Audio/Combat/SFX/Notes/Flute/flute_G_Major.mp3"));

        // Harp (Aurelius)
        noteAttackHarpA = Gdx.audio.newSound(Gdx.files.internal("Audio/Combat/SFX/Notes/Harp/harp - a.wav"));
        noteAttackHarpB = Gdx.audio.newSound(Gdx.files.internal("Audio/Combat/SFX/Notes/Harp/harp - b.wav"));
        noteAttackHarpC = Gdx.audio.newSound(Gdx.files.internal("Audio/Combat/SFX/Notes/Harp/harp - c.wav"));
        noteAttackHarpD = Gdx.audio.newSound(Gdx.files.internal("Audio/Combat/SFX/Notes/Harp/harp - d.wav"));
        noteAttackHarpE = Gdx.audio.newSound(Gdx.files.internal("Audio/Combat/SFX/Notes/Harp/harp - e.wav"));
        noteAttackHarpF = Gdx.audio.newSound(Gdx.files.internal("Audio/Combat/SFX/Notes/Harp/harp - f.wav"));
        noteAttackHarpG = Gdx.audio.newSound(Gdx.files.internal("Audio/Combat/SFX/Notes/Harp/harp - g.wav"));
        chordAttackHarpAmin = Gdx.audio.newSound(Gdx.files.internal("Audio/Combat/SFX/Notes/Harp/harp - A MINOR.wav"));
        chordAttackHarpBdim = Gdx.audio.newSound(Gdx.files.internal("Audio/Combat/SFX/Notes/Harp/harp - B DIM.wav"));
        chordAttackHarpCmaj = Gdx.audio.newSound(Gdx.files.internal("Audio/Combat/SFX/Notes/Harp/harp - C MAJOR.wav"));
        chordAttackHarpDmin = Gdx.audio.newSound(Gdx.files.internal("Audio/Combat/SFX/Notes/Harp/harp - D MINOR.wav"));
        chordAttackHarpEmin = Gdx.audio.newSound(Gdx.files.internal("Audio/Combat/SFX/Notes/Harp/harp - E MINOR.wav"));
        chordAttackHarpFmaj = Gdx.audio.newSound(Gdx.files.internal("Audio/Combat/SFX/Notes/Harp/harp - F MAJOR.wav"));
        chordAttackHarpGmaj = Gdx.audio.newSound(Gdx.files.internal("Audio/Combat/SFX/Notes/Harp/harp - G MAJOR.wav"));

        // Set Loops
        titleBGM.setLooping(true);
        storyBGM.setLooping(true);
        townOfEchoesBGM.setLooping(true);

        battleTownBGM.setLooping(true);
        battleCavernsBGM.setLooping(true);
        battleAbyssBGM.setLooping(true);
        battleBossBGM.setLooping(true);
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
        if (titleBGM != null) titleBGM.stop();
        if (storyBGM != null) storyBGM.stop();
        if (townOfEchoesBGM != null) townOfEchoesBGM.stop();
        if (battleTownBGM != null) battleTownBGM.stop();
        if (battleCavernsBGM != null) battleCavernsBGM.stop();
        if (battleAbyssBGM != null) battleAbyssBGM.stop();
        if (battleBossBGM != null) battleBossBGM.stop();
    }

    private void disposeTexture(Texture tex) {
        if (tex != null) {
            tex.dispose();
        }
    }

    private void disposeTextureRegion(TextureRegion reg) {
        if (reg != null && reg.getTexture() != null) {
            reg.getTexture().dispose();
        }
    }

    private void disposeSound(Sound sound) {
        if (sound != null) {
            sound.dispose();
        }
    }

    // ── Dispose ───────────────────────────────────────────────────────────────

    @Override
    public void dispose() {
        font.dispose();
        titleFont.dispose();
        bigFont.dispose();

        disposeTexture(titleScreenTex);

        disposeTexture(storyPanel1);
        disposeTexture(storyPanel2);
        disposeTexture(storyPanel3);
        disposeTexture(storyPanel4);

        disposeTextureRegion(townTex);
        disposeTextureRegion(townExitTex);
        disposeTextureRegion(silentCavernsTex);
        disposeTextureRegion(cavernsExitTex);
        disposeTextureRegion(abyssOfDissonanceTex);
        disposeTextureRegion(townDecorationsTex);

        disposeTexture(sonaraTex);
        disposeTexture(lyronTex);
        disposeTexture(aureliusTex);

        disposeTexture(startBtnTex);
        disposeTexture(tutorialBtnTex);
        disposeTexture(storyBtnTex);
        disposeTexture(creditsBtnTex);
        disposeTexture(exitBtnTex);
        disposeTexture(continueBtnTex);

        disposeTexture(pauseMenuBG);
        disposeTexture(pauseContinueBtn);
        disposeTexture(pauseChordInfoBtn);
        disposeTexture(pauseItemInfoBtn);
        disposeTexture(pauseExitBtn);

        disposeTexture(inventoryBtnTex);
        disposeTexture(pauseBtnTex);
        disposeTexture(menuBtnTex);

        disposeTexture(story1Tex);
        disposeTexture(story2Tex);
        disposeTexture(story3Tex);
        disposeTexture(story4Tex);

        disposeTexture(darknessOverlay);
        if (noteTextures != null) {
            for (Texture t : noteTextures) {
                disposeTexture(t);
            }
        }

        if (tutorials != null) {
            for (Texture t : tutorials) {
                disposeTexture(t);
            }
        }

        disposeTexture(noteTutorial);
        disposeTexture(metronomeTutorial);
        disposeTexture(chordTutorial);

        disposeTexture(townCombatBackground);
        disposeTexture(cavernsCombatBackground);
        disposeTexture(abyssCombatBackground);

        disposeTexture(mapHeaderTownOfEchoes);
        disposeTexture(mapHeaderSilentCaverns);
        disposeTexture(mapHeaderAbyssOfDissonance);

        disposeTexture(healthBar);
        disposeTexture(shieldBar);
        disposeTexture(staticHudBackground);
        disposeTexture(timerBackground);
        disposeTexture(dynamicHudBackground);
        disposeTexture(noteContainer);
        disposeTexture(noteContainerFilled);
        disposeTexture(turnMenuHud);
        disposeTexture(attackHud);
        disposeTexture(skillHud);
        disposeTexture(inventoryHud);
        disposeTexture(musicStaff);
        disposeTexture(musicNote);

        disposeTexture(cMajor);
        disposeTexture(dMinor);
        disposeTexture(eMinor);
        disposeTexture(fMajor);
        disposeTexture(gMajor);
        disposeTexture(aMinor);
        disposeTexture(bDim);

        disposeTexture(cMajorUsed);
        disposeTexture(dMinorUsed);
        disposeTexture(eMinorUsed);
        disposeTexture(fMajorUsed);
        disposeTexture(gMajorUsed);
        disposeTexture(aMinorUsed);
        disposeTexture(bDimUsed);

        disposeTexture(crimsonChorusBattleTex);
        disposeTexture(majorsBlessingBattleTex);
        disposeTexture(minorsGraceBattleTex);
        disposeTexture(resolvedDissonanceBattleTex);
        disposeTexture(silentBarrierBattleTex);
        disposeTexture(timeOrbBattleTex);

        disposeTexture(inventoryBackground);
        disposeTexture(crimsonChorusSlotItem);
        disposeTexture(majorsBlessingSlotItem);
        disposeTexture(minorsGraceSlotItem);
        disposeTexture(silentBarrierSlotItem);
        disposeTexture(resolvedDissonanceSlotItem);
        disposeTexture(timeOrbSlotItem);
        disposeTexture(emptySlotItem);
        disposeTexture(selectedSlotItem);

        for (Texture t : animationTextures) {
            disposeTexture(t);
        }
        animationTextures.clear();

        if (titleBGM != null) titleBGM.dispose();
        if (storyBGM != null) storyBGM.dispose();
        if (townOfEchoesBGM != null) townOfEchoesBGM.dispose();
        if (battleAbyssBGM != null) battleAbyssBGM.dispose();
        if (battleBossBGM != null) battleBossBGM.dispose();

        disposeSound(noteAttackBanjoA);
        disposeSound(noteAttackBanjoB);
        disposeSound(noteAttackBanjoC);
        disposeSound(noteAttackBanjoD);
        disposeSound(noteAttackBanjoE);
        disposeSound(noteAttackBanjoF);
        disposeSound(noteAttackBanjoG);
        disposeSound(chordAttackBanjoAmin);
        disposeSound(chordAttackBanjoBdim);
        disposeSound(chordAttackBanjoCmaj);
        disposeSound(chordAttackBanjoDmin);
        disposeSound(chordAttackBanjoEmin);
        disposeSound(chordAttackBanjoFmaj);
        disposeSound(chordAttackBanjoGmaj);

        disposeSound(noteAttackFluteA);
        disposeSound(noteAttackFluteB);
        disposeSound(noteAttackFluteC);
        disposeSound(noteAttackFluteD);
        disposeSound(noteAttackFluteE);
        disposeSound(noteAttackFluteF);
        disposeSound(noteAttackFluteG);
        disposeSound(chordAttackFluteAmin);
        disposeSound(chordAttackFluteBdim);
        disposeSound(chordAttackFluteCmaj);
        disposeSound(chordAttackFluteDmin);
        disposeSound(chordAttackFluteEmin);
        disposeSound(chordAttackFluteFmaj);
        disposeSound(chordAttackFluteGmaj);

        disposeSound(noteAttackHarpA);
        disposeSound(noteAttackHarpB);
        disposeSound(noteAttackHarpC);
        disposeSound(noteAttackHarpD);
        disposeSound(noteAttackHarpE);
        disposeSound(noteAttackHarpF);
        disposeSound(noteAttackHarpG);
        disposeSound(chordAttackHarpAmin);
        disposeSound(chordAttackHarpBdim);
        disposeSound(chordAttackHarpCmaj);
        disposeSound(chordAttackHarpDmin);
        disposeSound(chordAttackHarpEmin);
        disposeSound(chordAttackHarpFmaj);
        disposeSound(chordAttackHarpGmaj);
    }
}
