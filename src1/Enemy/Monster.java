package Enemy;

import java.util.Random;


public class Monster extends MonsterSkill{
    public String name;
    public String monsterDescription;
    private int hp;
    private int maxHp;


    // MONSTER INITIALIZATION
    Random rd = new Random();

    public void setHp(int hp) {
        this.hp = hp;
    }
    public int getHp() {
        return hp;
    }

    public void setMaxHp(int maxHp) {this.maxHp = maxHp;}
    public  int getMaxHp() {
        return maxHp;
    }

    public void takeDamage(int damage) {
        hp = hp - damage;
    }

    @Override
    public int attack(Monster enemy) {
        int attack = rd.nextInt(1, 4);
        int damage = 0;

        if (attack == 1) {
            damage = enemy.sk1Damage;
        } else if (attack == 2) {
            damage = enemy.sk2Damage;
        } else if (attack == 3) {
            damage = enemy.sk2Damage;
        }

        return damage;
    }
}