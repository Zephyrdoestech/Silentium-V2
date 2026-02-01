import java.util.Scanner;

public class Main{

    public static void upperFrame(){
        System.out.println();
        System.out.println("==============================================================================");
    }

    public static void lowerFrame(){
        System.out.println("==============================================================================");
    }
    
    public static void main(String[] args){
        boolean exploring = true;
        int x_Coordinate = 0;
        int y_Coordinate = 3;
        int movement;
        Scanner scanner = new Scanner(System.in);
        TownOfEchoes townOfEchoes = new TownOfEchoes();
        townOfEchoes.setMap();
        townOfEchoes.viewMap();

        while(exploring){

            upperFrame(); //FRAME
            System.out.println("Where do you wish to move?");
            System.out.println("1: North");
            System.out.println("2: West");
            System.out.println("3: East");
            System.out.println("4: South");
            lowerFrame(); //FRAME


            System.out.print("Enter: ");
            movement = scanner.nextInt();
            upperFrame(); //FRAME

            switch(movement){
                case 1:
                    if(y_Coordinate == 0){
                        System.out.println("Something is in the way...");
                        System.out.println("You cant go North...");

                    }
                    else{
                        y_Coordinate -= 1;
                    }
                    break;
                case 2:
                    if(x_Coordinate == 0){
                        System.out.println("Something is in the way...");
                        System.out.println("You cant go West...");

                    }
                    else{
                        x_Coordinate -= 1;
                    }
                    break;
                case 3:
                    if(x_Coordinate == 3){
                        System.out.println("Something is in the way...");
                        System.out.println("You cant go East...");
                    }
                    else{
                        x_Coordinate += 1;
                    }
                    break;
                case 4:
                    if(y_Coordinate == 0){
                        System.out.println("Something is in the way...");
                        System.out.println("You cant go North...");
                    }
                    else{
                        y_Coordinate += 1;
                    }
                    break;
                default:
                    upperFrame(); //FRAME
                    System.out.println("You remain still, You feel safe for now....");
                    break;
            }

            switch(townOfEchoes.getMapAt(x_Coordinate,y_Coordinate)){
                case 0:
                    System.out.println("Nothing Special");
                    break;
                case 1:
                    System.out.println("--Insert Enemy Battle--");
                    break;
                case 2:
                    System.out.println("--Insert Gate Condition--");
                    System.out.println("*Assuming character has met the condition*");
                    System.out.println("The gate has been opened... Exploring new map");
                    exploring = false;
                    break;
                default: break;
            }

            System.out.println("You are currently at coordinate row: " + y_Coordinate + " col: " + x_Coordinate);

            lowerFrame(); //FRAME
        }

        scanner.close();
    }
}

