package Map;

import Enemy.*;

import java.util.Random;
import Display.*;
import Player.*;

public class Map {
    Random random = new Random();
    TextDisplay text = new TextDisplay();
    public String name;
    private int row;
    private int col;
    private int totalEnemies;
    private int startPos;
    private int endPos;
    private int randomRow;
    private int randomCol;
    private int i;
    private int j;
    private int[][] map;
    private boolean placingEnemies;

    public Map(int row, int col, int totalEnemies, String name){
        this.name = name;
        this.row = row;
        this.col = col;
        this.totalEnemies = totalEnemies;
        this.map = new int[row][col];
        this.startPos = random.nextInt(0,col);
        this.endPos = random.nextInt(0,col);
    }

    public void setMap(){
        for(i=0;i<row;i++){
            for(j=0;j<col;j++){
                map[i][j] = 0;
            }
        }
        //initialize starting point and end point
        map[row-1][startPos] = 2;
        map[0][endPos] = 3;

        //sets the maximum amount of enemies based on the map size
        if(totalEnemies > (row*col) / 2){
            totalEnemies = (row*col) / 2;
        }

        //placing enemies
        placingEnemies = true;
        for(i=1;i<=totalEnemies;i++){
            while(placingEnemies){
                randomRow = random.nextInt(row);
                randomCol = random.nextInt(col);
                if(map[randomRow][randomCol] == 0){
                    map[randomRow][randomCol] = 1;
                    placingEnemies = false;
                }
            }
            placingEnemies = true;
        }

    }


    //MapTraversalSystem.Map Visual Methods
    public int getIndex(int x,int y){
        return map[x][y];
    }
    public void clearAt(int x, int y){
        map[x][y] = 0;
    }


    //Starter Getters
    public int getStartingRow(){
        return row-1;
    }
    public int getStartingCol(){
        return startPos;
    }

    public int getMapRow(){
        return row;
    }
    public int getMapCol(){
        return col;
    }

    public void setMapPos(int x, int y, int pos){
        map[x][y] = pos;
    }

    public void viewMap(){
        for(i=0;i<row;i++){
            for(j=0;j<col;j++){
                text.printMap(map[i][j] + " ");
            }
            System.out.println();
        }
    }

    public void viewDisplayMap(String mapName){
        char untravelled =  '\u25A1';
        char travelled =  '\u2022';
        char playerPos = '\u2b24';
        char nextMap = '\u2605';
        char onNextMap = '\u2606';
        char killedEnemy = '\u2715';

        text.printSystemMessage("=== \t\t\t" + mapName + "\t\t\t\t===");
        System.out.println();

        if(mapName.equalsIgnoreCase("SILENT CAVERNS")){
            System.out.print("\t   ");

            for(i=0;i<row;i++){
                for(j=0;j<col;j++){
                    if(map[i][j] == 0){
                        text.printMap("" + untravelled);
                    }else if(map[i][j] == 1){
                        text.printMap("" + travelled);
                    }else if(map[i][j] == 2){
                        text.printPlayerOnMap("" + playerPos);
                    }else if(map[i][j] == 3){
                        text.printExitMap("" + nextMap);
                    }else if(map[i][j] == 4){
                        text.printExitMap("" + onNextMap);
                    }else if(map[i][j] == 5){
                        text.printEnemyMap("" + killedEnemy);
                    }

                }
                System.out.println();
                System.out.print("\t   ");
            }
        } else if(mapName.equalsIgnoreCase("Abyss of Dissonance")) {
            System.out.print("\t\t\t\t\t  ");

            for (i = 0; i < row; i++) {
                for (j = 0; j < col; j++) {
                    if (map[i][j] == 0) {
                        text.printMap("" + untravelled);
                    } else if (map[i][j] == 1) {
                        text.printMap("" + travelled);
                    } else if (map[i][j] == 2) {
                        text.printPlayerOnMap("" + playerPos);
                    } else if (map[i][j] == 3) {
                        text.printExitMap("" + nextMap);
                    } else if (map[i][j] == 4) {
                        text.printExitMap("" + onNextMap);
                    } else if (map[i][j] == 5) {
                        text.printEnemyMap("" + killedEnemy);
                    }

                }
                System.out.println();
                System.out.print("\t\t\t\t\t  ");
            }
        }
        else {
            System.out.print("\t\t\t");

            for(i=0;i<row;i++){
                for(j=0;j<col;j++){
                    if(map[i][j] == 0){
                        text.printMap("" + untravelled);
                    }else if(map[i][j] == 1){
                        text.printMap("" + travelled);
                    }else if(map[i][j] == 2){
                        text.printPlayerOnMap("" + playerPos);
                    }else if(map[i][j] == 3){
                        text.printExitMap("" + nextMap);
                    }else if(map[i][j] == 4){
                        text.printExitMap("" + onNextMap);
                    }else if(map[i][j] == 5){
                        text.printEnemyMap("" + killedEnemy);
                    }

                }
                System.out.println();
                System.out.print("\t\t\t");
            }
        }
    }

    public void initDisplayMap(Map map){
        for(i=0;i<row;i++){
            for(j=0;j<col;j++){
                this.map[i][j] = 0;
                if(map.getIndex(i,j) == 2){
                    this.map[i][j] = 1;
                }
            }
        }
    }

    public void updateMapTravel(Map map,int row,int col){
        if(map.getIndex(row,col) == 3){
            this.map[row][col] = 3;
        }else{
            this.map[row][col] = 2;
        }
    }

    public void setTravelled(Map map, int row,int col){
        if(map.getIndex(row,col) == 3){
            this.map[row][col] = 4;
        }else if(map.getIndex(row,col) == 4){
            this.map[row][col] = 5;
        }else{
            this.map[row][col] = 1;
        }
    }

    public Monster MonsterSpawn(int map){
        text.redText("\t> \t\tMONSTER FOUND! \t\t<");
        System.out.println();
        System.out.println();
        Random rd = new Random();
        int op = 1;

        if(map == 1){
            op = rd.nextInt(1, 2 + 1);
            if(op == 1) return new FleshFeeders();
            if(op == 2) return new Andrewellers();
        }else if(map == 2){
            op = rd.nextInt(1, 2 + 1);

            if(op == 1) return new Aryzachnid();
            if(op == 2) return new Chimericks();
        }
        else if(map==3){
            return new Abarquez();
        }
        return null;
    }
}
