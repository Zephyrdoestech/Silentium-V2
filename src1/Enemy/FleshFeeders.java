package Enemy;

public class FleshFeeders extends Monster{
    public FleshFeeders(){
        name = "Flesh Feeders";
        setMaxHp(250);
        setHp(getMaxHp());;

        sk1Name = "Claw Through";
        sk1Damage = rd.nextInt(10, 20 + 1);
        sk2Name = "Bite";
        sk2Damage = rd.nextInt(15, 30 + 1);
        sk3Name = "Leap";
        sk3Damage = rd.nextInt(20, 45 + 1);

        monsterDescription = "These are the lowest echoes of despair—lurching figures born from sustained starvation and fear. ";
    }
}

