package Display;

import Combat.ChordSystem;
import Enemy.Monster;
import Player.Character;

public interface CombatPrint {
    void battleStart();
    void battleEnd(Boolean isWin);

    void playerStatsSummary(Character player);
    void enemyStatsSummary(Monster enemy);

    void displayValidNotes(int level);
    void chordChart(ChordSystem chordSystem);
}
