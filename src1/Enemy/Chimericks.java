package Enemy;

public class Chimericks extends Monster {
    public Chimericks(){
        name = "Chimericks";
        setMaxHp(700);
        setHp(getMaxHp());

        sk1Name = "Strike";
        sk1Damage = rd.nextInt(15, 35 + 1);
        sk2Name = "Venomous Bite";
        sk2Damage = rd.nextInt(20, 40 + 1);
        sk3Name = "Leech On";
        sk3Damage = rd.nextInt(40, 60 + 1);

        monsterDescription = "A fearsome, corrupted chimeric beast that represents pain and leeching corruption, making it highly self-sustaining. ";
    }
}
