package Player;

import Combat.Combat;
import Enemy.Monster;
import Display.*;

public class PassiveSkill {
    public CombatDisplay combDisplay = new CombatDisplay();
    public Combat combat = new Combat();
    public String skillName;
    public String skillDescription;
    TextDisplay text = new TextDisplay();

    //SONARA SKILL EFFECT
    public int skillEffect(Monster enemy){
        int damage = (combat.enemyAttack(enemy));;
        int thorn = (int) (damage * 0.15);

        text.redText("\t\tThe enemy damaged itself from Sonara's Body of Thorns and received " + thorn + " damage!\n");

        enemy.takeDamage(thorn);
        combDisplay.enemyStatsSummary(enemy);
        return damage;
    }

    //AURELIUS SKILL EFFECT
    public void skillEffect(Character player){
        int healAmount = (int)(player.getHp() * 0.05);
        int before = player.getHp();
        player.heal(healAmount);
        int after = player.getHp();
        int actualHeal = after - before;

        text.printSystemMessage("Aurelius's gains " + actualHeal + " health from his Melodic Remedy!\n");
    }


    //LYRON SKILL EFFECT
    public int skillEffect(Character player, int damage){
        text.printSystemMessage("The winner takes it all activates! Lyron gains shield equal to 25% of the damage dealt!\n");
        text.printSystemMessage("=== " + damage + " SHIELD:  " + (damage * 0.25) + " === \n\n");

        player.addShield((int)(damage * 0.25));
        return (int) (damage * 0.75);

    }
}
