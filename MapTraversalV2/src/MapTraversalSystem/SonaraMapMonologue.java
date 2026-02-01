package MapTraversalSystem;

public class SonaraMapMonologue extends CharacterMapMonologue{
    String name = "Sonara";
    String confused = "I know exploring is my expertise but I gotta have some directions....";
    String blocked = "Even I can't climb that wall...";
    String empty = "No one is here";
    String enemy = "Well, This sucks...";
    String exit = "Where does this lead to?";
    String entrance = "Hey, this is where I got in here";

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
