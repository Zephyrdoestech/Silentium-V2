package Enemy;

public class Andrewellers extends Monster{
    public Andrewellers(){
        name = "Andrewellers";
        setMaxHp(350);
        setHp(getMaxHp());

        sk1Name = "Luminous Gaze";
        sk1Damage = rd.nextInt(10, 20 + 1);
        sk2Name = "Deafening Screech";
        sk2Damage = rd.nextInt(15, 25 + 1);
        sk3Name = "Shatter Cry";
        sk3Damage = rd.nextInt(20, 35 + 1);

        monsterDescription = "Specters of silent malice, the Andrewellers are humanoid shadows whose presence radiates subtle, persistent dread.";
    }
}
