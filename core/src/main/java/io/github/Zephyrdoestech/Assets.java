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

    // ── Fonts ──────────────────────────────────────────────────────────────────
    public final BitmapFont font;       // body text (scale 1.5)
    public final BitmapFont titleFont;  // large headings (scale 2.2)
    public final BitmapFont bigFont;    // victory / defeat (scale 3.0)

    // ── UI / Map textures ──────────────────────────────────────────────────────
    public final Texture titleScreenTex;
    public final Texture townTex;         // Dungeon map
    public final Texture sonaraTex;
    public final Texture lyronTex;
    public final Texture aureliusTex;
    public final Texture darknessOverlay; // radial light gradient
    public final Texture[] noteTextures;  // 4 pixmap-drawn note shapes

    public final Texture story1Tex;
    public final Texture story2Tex;
    public final Texture story3Tex;
    public final Texture story4Tex;

    //Character Selectin Animation
    public final Animation<TextureRegion> sonaraSelectAnim;
    public final Animation<TextureRegion> aureliusSelectAnim;

    // ── Character Exploration animations ───────────────────────────────────────────────────
    public final Animation<TextureRegion> aureliusIdleRight, aureliusIdleLeft;
    public final Animation<TextureRegion> aureliusWalkRight, aureliusWalkLeft;

    public final Animation<TextureRegion> sonaraIdleRight, sonaraIdleLeft;
    public final Animation<TextureRegion> sonaraWalkRight, sonaraWalkLeft;

    public final Animation<TextureRegion> lyronIdleRight, lyronIdleLeft;
    public final Animation<TextureRegion> lyronWalkRight, lyronWalkLeft;

    // ── Combat Textures ───────────────────────────────────────────────────
    public final Texture townCombatBackground;
    public final Texture cavernsCombatBackground;
    public final Texture abyssCombatBackground;

    public final Texture HealthBar;
    public final Texture ShieldBar;

    // ── Combat Animations ───────────────────────────────────────────────────
    public final Animation<TextureRegion> battleIntroAnim;
    public final Animation<TextureRegion> victoryAnim;
    public final Animation<TextureRegion> defeatAnim;

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

    // ── Music ──────────────────────────────────────────────────────────────────
    public final Music titleBGM;

    // Internal list so dispose() can clean up animation textures
    private final List<Texture> animationTextures = new ArrayList<>();

    // --- Enemy Animations ---
    public final Animation<TextureRegion> darryllionIdle;

    // ── Constructor ────────────────────────────────────────────────────────────

    public Assets() {
        // Fonts
        font = new BitmapFont();        font.getData().setScale(1.5f);
        titleFont = new BitmapFont();   titleFont.getData().setScale(2.2f);
        bigFont = new BitmapFont();     bigFont.getData().setScale(3.0f);

        // Static textures
        titleScreenTex = new Texture("Background/Title_Screen/Title_Screen_Placeholder.png");
        townTex        = new Texture("Background/Map/Dungeon.png");
        townTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        sonaraTex   = new Texture("sonara.png");
        lyronTex    = new Texture("lyron.png");
        aureliusTex = new Texture("aurelius.png");

        startBtnTex = new Texture("UI/start_btn.png");
        tutorialBtnTex = new Texture("UI/tutorial_btn.png");
        storyBtnTex = new Texture("UI/story_btn.png");
        creditsBtnTex = new Texture("UI/credits_btn.png");
        exitBtnTex = new Texture("UI/exit_btn.png");

        story1Tex = new Texture("Background/Story/story_panel_1.png");
        story2Tex = new Texture("Background/Story/story_panel_2.png");
        story3Tex = new Texture("Background/Story/story_panel_3.png");
        story4Tex = new Texture("Background/Story/story_panel_4.png");

        // Load the 11-frame selection animations!
        sonaraSelectAnim   = loadAnim("Sonara/Select",   "sonaraSelect",   11, 0.1f);
        aureliusSelectAnim = loadAnim("Aurelius/Select", "aureliusSelect", 11, 0.1f);

        darryllionIdle = loadAnim("Enemies/Darryllion/Idle", "darryllionIdle", 8, 0.15f);

        // Generated textures
        darknessOverlay = buildDarknessOverlay(1024, 0.12f, 0.45f);
        noteTextures    = buildNoteTextures();

        // Aurelius
        aureliusIdleRight = loadAnim("Sprites/Characters/Aurelius/Idle", "Idle", 4, 0.2f);
        aureliusIdleLeft  = flipped(aureliusIdleRight);
        aureliusWalkRight = loadAnim("Sprites/Characters/Aurelius/Walk", "Movement", 6, 0.1f);
        aureliusWalkLeft  = flipped(aureliusWalkRight);

        // Sonara
        sonaraIdleRight = loadAnim("Sprites/Characters/Sonara/Idle", "Idle", 4, 0.1f);
        sonaraIdleLeft  = flipped(sonaraIdleRight);
        sonaraWalkRight = loadAnim("Sprites/Characters/Sonara/Walk", "Movement", 6, 0.1f);
        sonaraWalkLeft  = flipped(sonaraWalkRight);

        // Lyron
        lyronIdleRight = loadAnim("Sprites/Characters/Lyron/Idle", "Idle", 4, 0.1f);
        lyronIdleLeft  = flipped(lyronIdleRight);
        lyronWalkRight = loadAnim("Sprites/Characters/Lyron/Walk", "Movement", 6, 0.1f);
        lyronWalkLeft  = flipped(lyronWalkRight);

    // ── Combat Assets ────────────────────────────────────────────────────────────────
        townCombatBackground = new Texture("Background/Combat/Town.jpg");
        cavernsCombatBackground = new Texture("Background/Combat/Cavern.jpg");
        abyssCombatBackground = new Texture("Background/Combat/Abyss.jpg");

        HealthBar = new Texture("Sprites/Combat/Interface/HealthBar.png");
        ShieldBar = new Texture("Sprites/Combat/Interface/ShieldBar.png");

        // Battle Intro
        battleIntroAnim = loadAnim("Sprites/Combat/SplashScreen/Intro", "Battle", 8, 0.2f);
        // Victory
        victoryAnim = loadAnim("Sprites/Combat/SplashScreen/Victory", "Victory", 8, 0.15f);
        // Defeat
        defeatAnim = loadAnim("Sprites/Combat/SplashScreen/Defeat", "Defeat", 8, 0.15f);

        // Character Animations
        sonaraCombatIdle = loadAnim("Sprites/Combat/Character/Sonara/Idle", "Idle", 4, 0.2f);
        sonaraCombatAttack = loadAnim("Sprites/Combat/Character/Sonara/Attack", "Attack", 6, 0.1f);
        aureliusCombatIdle = loadAnim("Sprites/Combat/Character/Aurelius/Idle", "Idle", 4, 0.2f);
        aureliusCombatAttack = loadAnim("Sprites/Combat/Character/Aurelius/Attack", "Attack", 6, 0.1f);
        lyronCombatIdle = loadAnim("Sprites/Combat/Character/Lyron/Idle", "Idle", 4, 0.2f);
        lyronCombatAttack = loadAnim("Sprites/Combat/Character/Lyron/Attack", "Attack", 6, 0.1f);

        // Monster Animations
        fleshfeederCombatIdle = flipped(loadAnim("Sprites/Combat/Monster/Fleshfeeder/Idle", "Idle", 4, 0.2f));
        fleshfeederCombatAttack = flipped(loadAnim("Sprites/Combat/Monster/Fleshfeeder/Attack", "Attack", 6, 0.2f));
        darrylionCombatIdle = flipped(loadAnim("Sprites/Combat/Monster/Darrylion/Idle", "Idle", 4, 0.2f));
        darrylionCombatAttack = flipped(loadAnim("Sprites/Combat/Monster/Darrylion/Attack", "Attack", 6, 0.2f));
        gobninilCombatIdle = flipped(loadAnim("Sprites/Combat/Monster/Gobninil/Idle", "Idle", 4, 0.2f));
        gobninilCombatAttack = flipped(loadAnim("Sprites/Combat/Monster/Gobninil/Attack", "Attack", 6, 0.2f));
        chimericksCombatIdle = flipped(loadAnim("Sprites/Combat/Monster/Chimericks/Idle", "Idle", 4, 0.2f));
        chimericksCombatAttack = flipped(loadAnim("Sprites/Combat/Monster/Chimericks/Attack", "Attack", 6, 0.2f));
        labagoliathCombatIdle = flipped(loadAnim("Sprites/Combat/Monster/Labagoliath/Idle", "Idle", 4, 0.2f));
        labagoliathCombatAttack = flipped(loadAnim("Sprites/Combat/Monster/Labagoliath/Attack", "Attack", 6, 0.2f));
        syozanCombatIdle = flipped(loadAnim("Sprites/Combat/Monster/Syozan/Idle", "Idle", 4, 0.2f));
        syozanCombatAttack = flipped(loadAnim("Sprites/Combat/Monster/Syozan/Attack", "Attack", 6, 0.2f));


    // ── Audio ────────────────────────────────────────────────────────────────

        titleBGM = Gdx.audio.newMusic(Gdx.files.internal("Audio/BGM_Title.wav"));
        titleBGM.setLooping(true);
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

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

    // ── Darkness overlay ───────────────────────────────────────────────────────

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

    // ── Note textures ──────────────────────────────────────────────────────────

    private Texture[] buildNoteTextures() {
        int S = 32;
        Texture[] out = new Texture[4];

        // 0: Quarter note
        { Pixmap p = blank(S);
            p.setColor(Color.WHITE); p.fillCircle(9, 9, 6);
            for (int y = 9; y <= 28; y++) p.drawPixel(15, y, 0xFFFFFFFF);
            out[0] = new Texture(p); p.dispose(); }

        // 1: Eighth note
        { Pixmap p = blank(S);
            p.setColor(Color.WHITE); p.fillCircle(9, 9, 6);
            for (int y = 9; y <= 28; y++) p.drawPixel(15, y, 0xFFFFFFFF);
            for (int i = 0; i < 8; i++) { p.drawPixel(15+i, 28-i, 0xFFFFFFFF); p.drawPixel(15+i, 29-i, 0xFFFFFFFF); }
            out[1] = new Texture(p); p.dispose(); }

        // 2: Beamed pair
        { Pixmap p = blank(S);
            p.setColor(Color.WHITE); p.fillCircle(7, 7, 5);
            for (int y = 7;  y <= 26; y++) p.drawPixel(12, y, 0xFFFFFFFF);
            p.fillCircle(20, 10, 5);
            for (int y = 10; y <= 26; y++) p.drawPixel(25, y, 0xFFFFFFFF);
            for (int x = 12; x <= 25; x++) { p.drawPixel(x, 26, 0xFFFFFFFF); p.drawPixel(x, 25, 0xFFFFFFFF); p.drawPixel(x, 24, 0xFFFFFFFF); }
            out[2] = new Texture(p); p.dispose(); }

        // 3: Double beamed pair
        { Pixmap p = blank(S);
            p.setColor(Color.WHITE); p.fillCircle(5, 6, 4);
            for (int y = 6;  y <= 26; y++) p.drawPixel(9,  y, 0xFFFFFFFF);
            p.fillCircle(14, 8, 4);
            for (int y = 8;  y <= 26; y++) p.drawPixel(18, y, 0xFFFFFFFF);
            for (int x = 9; x <= 18; x++) { p.drawPixel(x, 26, 0xFFFFFFFF); p.drawPixel(x, 25, 0xFFFFFFFF); p.drawPixel(x, 21, 0xFFFFFFFF); p.drawPixel(x, 20, 0xFFFFFFFF); }
            p.fillCircle(22, 6, 4); p.fillCircle(28, 9, 3);
            out[3] = new Texture(p); p.dispose(); }

        return out;
    }

    private Pixmap blank(int size) {
        Pixmap p = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        p.setBlending(Pixmap.Blending.None);
        p.setColor(1f, 1f, 1f, 0f); p.fill();
        return p;
    }

    // ── Dispose ────────────────────────────────────────────────────────────────

    @Override
    public void dispose() {
        font.dispose(); titleFont.dispose(); bigFont.dispose();
        titleScreenTex.dispose(); townTex.dispose();
        sonaraTex.dispose(); lyronTex.dispose(); aureliusTex.dispose();
        darknessOverlay.dispose();
        startBtnTex.dispose();
        tutorialBtnTex.dispose();
        storyBtnTex.dispose();
        creditsBtnTex.dispose();
        exitBtnTex.dispose();
        story1Tex.dispose();
        story2Tex.dispose();
        story3Tex.dispose();
        story4Tex.dispose();
        for (Texture t : noteTextures) t.dispose();
        for (Texture t : animationTextures) t.dispose();
        titleBGM.dispose();
    }
}
