package Player;

public class Aurelius extends Character{
    private final String[] quotes = {
            "The fear is gone. Be safe.", // Shadow 1
            "You will not touch the vulnerable.", // Shadow 2
            "My breath will not fail. Justice prevails.", // Shadow 3
            "Go back to the apathy you came from. We are awake now." // Shadow 4
    };

    public Aurelius(){
        name = "Aurelius";
        instrument = "The Whispering Bulwark";
        openingNarrative = "When the Bell chimed and shadows attacked the wealthy districts, Aurelius saw his family's cruelty made manifest and seized the opportunity to finally act,\n" +
                "\t\tfleeing their compound with his Flute. Playing a bright, sustained note, he created a sound of temporary shelter that repelled a nearby Shadow,\n" +
                "\t\tallowing a group of refugees to escape as he vowed to use his music as a shield against the crisis.";
        ps.skillName = "Melodic Remedy";
        ps.skillDescription = "Chords heals 5% of Aurelius’ health. After a chord, grants aurelius an extra turn but is limited to use one note.";
        as.skillName = "Conservation";
        as.skillDescription = "Aurelius can choose to preserve the selected notes' current damage for next turn.";
        setMaxHp(150);
        setHp(getMaxHp());
        setMaxShield(40);
        setMap(1);
        setLevel(1);

        narrative.setQuotes(quotes);
    }
}
