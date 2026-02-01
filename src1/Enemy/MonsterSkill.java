package Enemy;
import java.util.Random;

public abstract class MonsterSkill {
    public String sk1Name;
    public String sk2Name;
    public String sk3Name;
    public int sk1Damage;
    public int sk2Damage;
    public int sk3Damage;

    public abstract int attack(Monster enemy);
}
