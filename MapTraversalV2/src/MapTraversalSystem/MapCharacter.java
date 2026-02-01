package MapTraversalSystem;

import java.util.Scanner;
public class MapCharacter {
    private int row;
    private int col;

    //Setters and Getters
    public void setRow(int row){
        this.row = row;
    }
    public int getRow(){
        return row;
    }

    public void setCol(int col){
        this.col = col;
    }
    public int getCol(){
        return col;
    }

    //Character Movements
    public void up(){
        row -= 1;
    }
    public void down(){
        row += 1;
    }
    public void left(){
        col -= 1;
    }
    public void right(){
        col += 1;
    }

    public void showPosition(){
        System.out.println("Character is at index [" + row + "] [" + col + "]");
    }

    public void displayDirections(){
        System.out.println("Which way are you moving?");
        System.out.println("1: North");
        System.out.println("2: South");
        System.out.println("3: West");
        System.out.println("4: East");
        System.out.println("--------------------------");
        System.out.println();
    }
    public void explore(Map map, CharacterMapMonologue charMonologue){
        Scanner scanner = new Scanner(System.in);
        int movement = 0;
        boolean isExploring = true;
        boolean isEnabled;

        //Disposeable
        System.out.println("Test Map:");
        map.viewMap();
        System.out.println();
        System.out.println();
        //END

        System.out.println("Exploring: " + map.getName());

        setRow(map.getStartingRow());
        setCol(map.getStartingCol());
        while(isExploring){
            isEnabled = true;
            while(isEnabled){
                try{
                    displayDirections();
                    System.out.print("\t Select: ");
                    movement = scanner.nextInt();
                    if(movement <= 0 || movement > 4){
                        charMonologue.displayMapConfused();
                    }
                    else{
                        isEnabled = false;
                    }
                }catch(Exception e){
                    charMonologue.displayMapConfused();
                    scanner.next();
                }
            }

            System.out.println();
            System.out.println("========================");
            System.out.println(charMonologue.getName() + ":");

            switch(movement){
                case 1:
                    if(getRow() == 0){
                        charMonologue.displayMapBlocked();
                    }
                    else{
                        up();
                    }
                    break;
                case 2:
                    if(getRow() >= map.getMapRow() - 1){
                        charMonologue.displayMapBlocked();
                    }
                    else{
                        down();
                    }
                    break;
                case 3:
                    if(getCol() == 0){
                        charMonologue.displayMapBlocked();
                    }
                    else{
                        left();
                    }
                    break;
                case 4:
                    if(getCol() >= map.getMapCol() - 1){
                        charMonologue.displayMapBlocked();
                    }
                    else{
                        right();
                    }
                    break;
                default:
                    break;
            }


            switch(map.getIndex(getRow(),getCol())){
                case 0:
                    charMonologue.displayEmptySpot();
                    break;
                case 1:
                    charMonologue.displayEnemyEncounter();
                    break;
                case 2:
                    charMonologue.displayEntrance();
                    System.out.println("This is where I started");
                    break;
                case 3:
                    charMonologue.displayExit();
                    System.out.println("--Assuming they met the conditions--");
                    isExploring = false;
                    break;
                default: break;
            }

            System.out.println("========================");
            System.out.println();
        }
    }
}
