package Display;

import Enemy.Monster;
import Player.Character;

public interface CharacterDialogue {
    // --- Map Introduction ---
    //void mapIntroduction(Map map);

    // --- Level Up   ---
    void firstLevelUp(Character character);
    void secondLevelUp(Character character);
    void thirdLevelUp(Character character);
    void fourthLevelUp(Character character);

    void victoryDialogue(Character character);

    // --- Final Boss Maestro Syozan Dialogue ---
    void bossPreBattleDialogue();
    void bossPostBattleDialogue();


    // --- Final Victory ---
    void finalVictory(Character character);


    /*
    // --- Randomized Post-Combat Dialogue (New) ---
    void victoryDialogue(Character player);
    // void sonaraVictoryDialogue();
    // void aureliusVictoryDialogue();
    // void lyronVictoryDialogue();

    // --- Final Boss Moments ---
    void bossPreBattleDialogue();
    void bossPostBattleDialogue();

    void finalVictory(Character player);
    //void victorySonara();
    //void victoryAurelius();
    //void victoryLyron();

    // --- Item Drops ---
    void itemDrop(Character player /*, Item item);
    //void dropCrimsonChorus();
    //void dropSilentBarrier();
    //void dropResolvedDissonance();
    //void dropMinorGrace();
    //void dropMajorBlessing();
    */
}
