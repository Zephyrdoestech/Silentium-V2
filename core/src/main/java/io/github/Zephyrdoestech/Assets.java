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

/**
 * Owns and manages the lifetime of every shared asset in the game.
 * Create once in Main.create(), dispose in Main.dispose().
 *
 * Screens read assets via game.assets.* — they never load or dispose shared assets themselves.
 */
public class Assets implements Disposable {

    // ── Fonts ─────────────────────────────────────────────────────────────────

    public final BitmapFont font;       // body text  (scale 1.5)
    public final BitmapFont titleFont;  // headings   (scale 2.2)
    public final BitmapFont bigFont;    // victory/defeat (scale 3.0)

    // ── UI / Map Textures ─────────────────────────────────────────────────────

    public final TextureRegion titleScreenTex;
    public final TextureRegion townTex;
    public final TextureRegion sonaraTex;
    public final TextureRegion lyronTex;
    public final TextureRegion aureliusTex;
    public final TextureRegion darknessOverlay;
    public final TextureRegion[] noteTextures;

    public final TextureRegion story1Tex;
    public final TextureRegion story2Tex;
    public final TextureRegion story3Tex;
    public final TextureRegion story4Tex;

    // ── Exploration Animations ────────────────────────────────────────────────

    public final Animation<TextureRegion> aureliusIdleRight, aureliusIdleLeft;
    public final Animation<TextureRegion> aureliusWalkRight, aureliusWalkLeft;

    public final Animation<TextureRegion> sonaraIdleRight, sonaraIdleLeft;
    public final Animation<TextureRegion> sonaraWalkRight, sonaraWalkLeft;

    public final Animation<TextureRegion> lyronIdleRight, lyronIdleLeft;
    public final Animation<TextureRegion> lyronWalkRight, lyronWalkLeft;

    // ── Combat Background Textures ────────────────────────────────────────────

    public final TextureRegion townCombatBackground;
    public final TextureRegion cavernsCombatBackground;
    public final TextureRegion abyssCombatBackground;

    // ── Combat HUD Textures ───────────────────────────────────────────────────

    public final TextureRegion healthBar;
    public final TextureRegion shieldBar;
    public final TextureRegion staticHudBackground;
    public final TextureRegion timerBackground;
    public final TextureRegion dynamicHudBackground;
    public final TextureRegion noteContainer;
    public final TextureRegion noteContainerFilled;
    public final TextureRegion turnMenuHud;
    public final TextureRegion attackHud;
    public final TextureRegion skillHud;
    public final TextureRegion inventoryHud;
    public final TextureRegion musicStaff;
    public final TextureRegion musicNote;

    public final TextureRegion cMajor;
    public final TextureRegion dMinor;
    public final TextureRegion eMinor;
    public final TextureRegion fMajor;
    public final TextureRegion gMajor;
    public final TextureRegion aMinor;
    public final TextureRegion bDim;

    public final TextureRegion cMajorUsed;
    public final TextureRegion dMinorUsed;
    public final TextureRegion eMinorUsed;
    public final TextureRegion fMajorUsed;
    public final TextureRegion gMajorUsed;
    public final TextureRegion aMinorUsed;
    public final TextureRegion bDimUsed;

    public final TextureRegion inventoryBackground;
    public final TextureRegion crimsonChorusSlotItem;
    public final TextureRegion majorsBlessingSlotItem;
    public final TextureRegion minorsGraceSlotItem;
    public final TextureRegion silentBarrierSlotItem;
    public final TextureRegion resolvedDissonanceSlotItem;
    public final TextureRegion timeOrbSlotItem;

    // New variables added
    public final TextureRegion crimsonChorusInvTex;
    public final TextureRegion crimsonChorusBattleTex;
    public final TextureRegion majorsBlessingInvTex;
    public final TextureRegion majorsBlessingBattleTex;
    public final TextureRegion minorsGraceInvTex;
    public final TextureRegion minorsGraceBattleTex;
    public final TextureRegion resolvedDissonanceInvTex;
    public final TextureRegion resolvedDissonanceBattleTex;
    public final TextureRegion silentBarrierInvTex;
    public final TextureRegion silentBarrierBattleTex;
    public final TextureRegion timeOrbInvTex;
    public final TextureRegion timeOrbBattleTex;
    public final TextureRegion silentCavernsTex;
    public final TextureRegion abyssOfDissonanceTex;
    public final Animation<TextureRegion> sonaraSelectAnim;
    public final Animation<TextureRegion> aureliusSelectAnim;
    public final Music titleBGM;
    public final TextureRegion startBtnTex;
    public final TextureRegion tutorialBtnTex;
    public final TextureRegion storyBtnTex;
    public final TextureRegion creditsBtnTex;
    public final TextureRegion exitBtnTex;
    public final TextureRegion townDecorationsTex;
    public final TextureRegion townExitTex;

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

    // ── Music ─────────────────────────────────────────────────────────────────

    public final Music sonaraTheme;
    public final Music aureliusTheme;
    public final Music lyronTheme;
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
        titleScreenTex = new TextureRegion(new Texture("Background/Title_Screen/Title_Screen_Placeholder.png"));
        townTex        = new TextureRegion(new Texture("Background/Map/Town_Of_Echoes.png"));
        townTex.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        sonaraTex   = new TextureRegion(new Texture("sonara.png"));
        lyronTex    = new TextureRegion(new Texture("lyron.png"));
        aureliusTex = new TextureRegion(new Texture("aurelius.png"));

        story1Tex = new TextureRegion(new Texture("Background/Story/story_panel_1.png"));
        story2Tex = new TextureRegion(new Texture("Background/Story/story_panel_2.png"));
        story3Tex = new TextureRegion(new Texture("Background/Story/story_panel_3.png"));
        story4Tex = new TextureRegion(new Texture("Background/Story/story_panel_4.png"));

        // Generated textures
        darknessOverlay = buildDarknessOverlay(1024, 0.12f, 0.45f);
        noteTextures    = buildNoteTextures();

        // ── Exploration Animations ────────────────────────────────────────────

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

        townCombatBackground    = new TextureRegion(new Texture("Background/Combat/Town.jpg"));
        cavernsCombatBackground = new TextureRegion(new Texture("Background/Combat/Cavern.jpg"));
        abyssCombatBackground   = new TextureRegion(new Texture("Background/Combat/Abyss.jpg"));

        // ── Combat HUD ────────────────────────────────────────────────────────

        healthBar          = new TextureRegion(new Texture("Sprites/Combat/Interface/HealthBar.png"));
        shieldBar          = new TextureRegion(new Texture("Sprites/Combat/Interface/ShieldBar.png"));
        staticHudBackground  = new TextureRegion(new Texture("Sprites/Combat/Interface/StaticHUD/HUDBackground.png"));
        timerBackground      = new TextureRegion(new Texture("Sprites/Combat/Interface/Timer/HUDBackground.png"));
        dynamicHudBackground = new TextureRegion(new Texture("Sprites/Combat/Interface/DynamicHUD/HUDBackground.png"));
        noteContainer        = new TextureRegion(new Texture("Sprites/Combat/Interface/DynamicHUD/NoteContainer.png"));
        noteContainerFilled  = new TextureRegion(new Texture("Sprites/Combat/Interface/DynamicHUD/NoteContainerFilled.png"));
        turnMenuHud          = new TextureRegion(new Texture("Sprites/Combat/Interface/DynamicHUD/TurnMenu.png"));
        attackHud            = new TextureRegion(new Texture("Sprites/Combat/Interface/DynamicHUD/Attack.png"));
        skillHud             = new TextureRegion(new Texture("Sprites/Combat/Interface/DynamicHUD/Skill.png"));
        inventoryHud         = new TextureRegion(new Texture("Sprites/Combat/Interface/DynamicHUD/Inventory.png"));
        musicStaff           = new TextureRegion(new Texture("Sprites/Combat/Interface/DynamicHUD/MusicStaff.png"));
        musicNote            = new TextureRegion(new Texture("Sprites/Combat/Interface/DynamicHUD/MusicNote.png"));

        cMajor = new TextureRegion(new Texture("Sprites/Combat/Interface/Chords/C_Major.png"));
        dMinor = new TextureRegion(new Texture("Sprites/Combat/Interface/Chords/D_Minor.png"));
        eMinor = new TextureRegion(new Texture("Sprites/Combat/Interface/Chords/E_Minor.png"));
        fMajor = new TextureRegion(new Texture("Sprites/Combat/Interface/Chords/F_Major.png"));
        gMajor = new TextureRegion(new Texture("Sprites/Combat/Interface/Chords/G_Major.png"));
        aMinor = new TextureRegion(new Texture("Sprites/Combat/Interface/Chords/A_Minor.png"));
        bDim = new TextureRegion(new Texture("Sprites/Combat/Interface/Chords/B_Dim.png"));

        cMajorUsed = new TextureRegion(new Texture("Sprites/Combat/Interface/Chords/C_Used.png"));
        dMinorUsed = new TextureRegion(new Texture("Sprites/Combat/Interface/Chords/D_Used.png"));
        eMinorUsed = new TextureRegion(new Texture("Sprites/Combat/Interface/Chords/E_Used.png"));
        fMajorUsed = new TextureRegion(new Texture("Sprites/Combat/Interface/Chords/F_Used.png"));
        gMajorUsed = new TextureRegion(new Texture("Sprites/Combat/Interface/Chords/G_Used.png"));
        aMinorUsed = new TextureRegion(new Texture("Sprites/Combat/Interface/Chords/A_Used.png"));
        bDimUsed = new TextureRegion(new Texture("Sprites/Combat/Interface/Chords/B_Used.png"));

        inventoryBackground = new TextureRegion(new Texture("Sprites/Combat/Interface/Inventory/InventoryBG.png"));
        crimsonChorusSlotItem = new TextureRegion(new Texture("Sprites/Combat/Interface/Inventory/CrimsonChorus.png"));
        majorsBlessingSlotItem = new TextureRegion(new Texture("Sprites/Combat/Interface/Inventory/MajorsBlessing.png"));
        minorsGraceSlotItem = new TextureRegion(new Texture("Sprites/Combat/Interface/Inventory/MinorsGrace.png"));
        silentBarrierSlotItem = new TextureRegion(new Texture("Sprites/Combat/Interface/Inventory/SilentBarrier.png"));
        resolvedDissonanceSlotItem = new TextureRegion(new Texture("Sprites/Combat/Interface/Inventory/ResolvedDissonance.png"));
        timeOrbSlotItem = new TextureRegion(new Texture("Sprites/Combat/Interface/Inventory/TimeOrb.png"));

        // ── Combat Animations ─────────────────────────────────────────────────

        battleIntroAnim = loadAnim("Sprites/Combat/SplashScreen/Intro",   "Battle",  8, 0.2f);
        victoryAnim     = loadAnim("Sprites/Combat/SplashScreen/Victory", "Victory", 8, 0.15f);
        defeatAnim      = loadAnim("Sprites/Combat/SplashScreen/Defeat",  "Defeat",  8, 0.15f);
        timerAnim       = loadAnim("Sprites/Combat/Interface/Timer/TimerAnim", "Timer", 4, 0.2f);

        sonaraCombatIdle     = loadAnim("Sprites/Combat/Character/Sonara/Idle",     "Idle",   4, 0.2f);
        sonaraCombatAttack   = loadAnim("Sprites/Combat/Character/Sonara/Attack",   "Attack", 6, 0.1f);
        aureliusCombatIdle   = loadAnim("Sprites/Combat/Character/Aurelius/Idle",   "Idle",   4, 0.2f);
        aureliusCombatAttack = loadAnim("Sprites/Combat/Character/Aurelius/Attack", "Attack", 6, 0.1f);
        lyronCombatIdle      = loadAnim("Sprites/Combat/Character/Lyron/Idle",      "Idle",   4, 0.2f);
        lyronCombatAttack    = loadAnim("Sprites/Combat/Character/Lyron/Attack",    "Attack", 6, 0.1f);

        fleshfeederCombatIdle    = flipped(loadAnim("Sprites/Combat/Monster/Fleshfeeder/Idle",    "Idle",   4, 0.2f));
        fleshfeederCombatAttack  = flipped(loadAnim("Sprites/Combat/Monster/Fleshfeeder/Attack",  "Attack", 6, 0.2f));
        darrylionCombatIdle      = flipped(loadAnim("Sprites/Combat/Monster/Darrylion/Idle",      "Idle",   4, 0.2f));
        darrylionCombatAttack    = flipped(loadAnim("Sprites/Combat/Monster/Darrylion/Attack",    "Attack", 6, 0.2f));
        gobninilCombatIdle       = flipped(loadAnim("Sprites/Combat/Monster/Gobninil/Idle",       "Idle",   4, 0.2f));
        gobninilCombatAttack     = flipped(loadAnim("Sprites/Combat/Monster/Gobninil/Attack",     "Attack", 6, 0.2f));
        chimericksCombatIdle     = flipped(loadAnim("Sprites/Combat/Monster/Chimericks/Idle",     "Idle",   4, 0.2f));
        chimericksCombatAttack   = flipped(loadAnim("Sprites/Combat/Monster/Chimericks/Attack",   "Attack", 6, 0.2f));
        labagoliathCombatIdle    = flipped(loadAnim("Sprites/Combat/Monster/Labagoliath/Idle",    "Idle",   4, 0.2f));
        labagoliathCombatAttack  = flipped(loadAnim("Sprites/Combat/Monster/Labagoliath/Attack",  "Attack", 6, 0.2f));
        syozanCombatIdle         = flipped(loadAnim("Sprites/Combat/Monster/Syozan/Idle",         "Idle",   4, 0.2f));
        syozanCombatAttack       = flipped(loadAnim("Sprites/Combat/Monster/Syozan/Attack",       "Attack", 6, 0.2f));

        // ── Audio ─────────────────────────────────────────────────────────────

        sonaraTheme   = Gdx.audio.newMusic(Gdx.files.internal("Audio/banjo.wav"));
        aureliusTheme = Gdx.audio.newMusic(Gdx.files.internal("Audio/flute.wav"));
        lyronTheme    = Gdx.audio.newMusic(Gdx.files.internal("Audio/harp.wav"));
        titleBgm      = Gdx.audio.newMusic(Gdx.files.internal("Audio/BGM_Title.wav"));

        sonaraTheme.setLooping(true);
        aureliusTheme.setLooping(true);
        lyronTheme.setLooping(true);
        titleBgm.setLooping(true);
    }

    // ── Private Helpers ───────────────────────────────────────────────────────

    private Animation<TextureRegion> loadAnim(String folder, String base,
                                              int count, float duration) {
        TextureRegion[] frames = new TextureRegion[count];
        for (int i = 0; i < count; i++) {
            Texture tex = new Texture(folder + "/" + base + (i + 1) + ".png");
            animationTextures.add(tex);
            frames[i] = new TextureRegion(tex);
        }
        return new Animation<>(duration, frames);
    }

    private Animation<TextureRegion> flipped(Animation<TextureRegion> src) {
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

    // ── Dispose ───────────────────────────────────────────────────────────────

    @Override
    public void dispose() {
        font.dispose();
        titleFont.dispose();
        bigFont.dispose();

        titleScreenTex.getTexture().dispose();
        townTex.getTexture().dispose();
        sonaraTex.getTexture().dispose();
        lyronTex.getTexture().dispose();
        aureliusTex.getTexture().dispose();
        darknessOverlay.dispose();

        story1Tex.getTexture().dispose();
        story2Tex.getTexture().dispose();
        story3Tex.getTexture().dispose();
        story4Tex.getTexture().dispose();

        for (Texture t : noteTextures)      t.dispose();
        for (Texture t : animationTextures) t.dispose();

        townCombatBackground.getTexture().dispose();
        cavernsCombatBackground.getTexture().dispose();
        abyssCombatBackground.getTexture().dispose();

        healthBar.getTexture().dispose();
        shieldBar.getTexture().dispose();
        staticHudBackground.getTexture().dispose();
        timerBackground.getTexture().dispose();
        dynamicHudBackground.getTexture().dispose();
        noteContainer.getTexture().dispose();
        turnMenuHud.getTexture().dispose();
        attackHud.getTexture().dispose();
        skillHud.getTexture().dispose();
        inventoryHud.getTexture().dispose();

        sonaraTheme.dispose();
        aureliusTheme.dispose();
        lyronTheme.dispose();
        titleBgm.dispose();
    }
}
