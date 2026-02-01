package Enemy;

public class Syozan extends Monster {
    public Syozan(){
        name = "Maestro Syozan";
        setMaxHp(2000);
        setHp(getMaxHp());

        sk1Name = "Abyssal Echo Dirge";
        sk1Damage = rd.nextInt(20, 75 + 1);
        sk2Name = "Dirge of the Shattered Moon";
        sk2Damage = rd.nextInt(30, 100 + 1);
        sk3Name = "Ebon Symphony of Consuming Night";
        sk3Damage = rd.nextInt(40, 125 + 1);

        monsterDescription = "The architect of the Eternal Silence.\n" +
                "\t\tMaestro Syozan is not a beast, but a shattered, celestial musician who wields fragmented, complex noise as a weapon.\n" +
                "\t\tHer objective is the absolute, permanent stillness of the world, and his attacks are symphonies of pure despair designed to test the limits of the child's music and will.";
    }
}
