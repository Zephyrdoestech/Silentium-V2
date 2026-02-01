package Enemy;

public class Aryzachnid extends Monster{
    public Aryzachnid(){
        name = "Aryzachnid";
        setMaxHp(500);
        setHp(getMaxHp());

        sk1Name = "Binding Webs";
        sk1Damage = rd.nextInt(20, 40 + 1);
        sk2Name = "Paralyzing Fangs";
        sk2Damage = rd.nextInt(25, 50 + 1);
        sk3Name = "Wrap-up";
        sk3Damage = rd.nextInt(50, 75 + 1);

        monsterDescription = "A grotesque horror woven from deep, creeping apprehension, the Aryzachnid embodies the fear of being trapped and consumed. ";
    }
}
