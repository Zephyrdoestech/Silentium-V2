package Player;

public class Op extends Character{
    private final String[] quotes = {
            "Tester: The fear is gone. Be safe.", // Shadow 1
            "Tester: You will not touch the vulnerable.", // Shadow 2
            "Tester: My breath will not fail. Justice prevails.", // Shadow 3
            "Tester: Go back to the apathy you came from. We are awake now." // Shadow 4
    };

    public Op(){
        name = "Op";
        instrument = "Tester Instrument";
        ps.skillName = "Tester Passive Skill Name";
        ps.skillDescription = "Tester Passive Skill Description";
        as.skillName = "Tester Active Skill Name";
        as.skillDescription = "Tester Active Skill Description";
        setMaxHp(999999);
        setHp(getMaxHp());
        setMaxShield(999999);
        setMap(1);
        setLevel(5);

        narrative.setQuotes(quotes);
    }
}
