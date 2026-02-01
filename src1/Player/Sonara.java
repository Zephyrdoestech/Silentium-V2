package Player;

public class Sonara extends Character{

    private final String[] quotes = {
            "Gone. Deserved it.", // Shadow 1
            "Too slow. The truth is faster than any lie.", // Shadow 2
            "A waste of my time. Next.", // Shadow 3
            "That's for my parents. Now, who's next on the list?" // Shadow 4
    };

    public Sonara(){
        name = "Sonara";
        instrument = "The Scrouge of Echoes";
        openingNarrative = "The bell's toll shattered Sonara's peaceful delusion, confirming her family's death and drawing a wave of monstrous Shadows to her cottage.\n" +
                "\t\tSeizing her father's hidden Banjo, she instinctively played a forceful, destructive sound that repelled the initial attack,\n" +
                "\t\tvowing to use the music to hunt the source of the Shadows and avenge her loss as she fought her way out of town.";
        ps.skillName = "Body of Thorns";
        ps.skillDescription = "Upon receiving damage, The enemy receives 15% of shared true damage from the enemy’s inflicted damage.";
        as.skillName = "Melodic Impromptu";
        as.skillDescription = "Sonara can add one (1) point to the initial damage.";
        setMaxHp(150);
        setHp(getMaxHp());
        setMaxShield(35);
        setShield(getMaxShield());
        setMap(1);
        setLevel(1);

        narrative.setQuotes(quotes);
    }

}
