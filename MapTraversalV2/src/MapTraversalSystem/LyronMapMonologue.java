package MapTraversalSystem;

public class LyronMapMonologue extends CharacterMapMonologue{
    String name = "Lyron";
    String confused = "Where am I supposed to go?";
    String blocked = "I wish I could break this thing that is blocking my way...";
    String empty = "No one's here...";
    String enemy = "Another kill to my name!!!";
    String exit = "I'll get off now";
    String entrance = "The scent of the beast lead me here.";

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
