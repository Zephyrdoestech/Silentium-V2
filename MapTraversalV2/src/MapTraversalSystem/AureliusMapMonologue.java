package MapTraversalSystem;

public class AureliusMapMonologue extends CharacterMapMonologue{
    String name = "Aurelius";
    String confused = "So... Where am I supposed to go again?";
    String blocked = "Well... Something is in the way it seems";
    String empty = "Looks like I'm alone";
    String enemy = "Forgive me for I shall put you to rest.";
    String exit = "I shall traverse through the unknown.";
    String entrance = "This is the entrance from where I got in.";

    @Override
    public String getName(){
        return name;
    }

    @Override
    public void displayMapConfused(){
        System.out.println(confused);
    }

    @Override
    public void displayMapBlocked(){
        System.out.println(blocked);
    }

    @Override
    public void displayEmptySpot(){
        System.out.println(empty);
    }

    @Override
    public void displayEnemyEncounter(){
        System.out.println(enemy);
    }

    @Override
    public void displayExit(){
        System.out.println(exit);
    }

    @Override
    public void displayEntrance(){
        System.out.println(entrance);
    }
}
