package MapTraversalSystem;

import java.util.Random;

public abstract class Map{
    Random random = new Random();
    String name;
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

    public String getName(){
        return name;
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

    public void viewMap(){
        for(i=0;i<row;i++){
            for(j=0;j<col;j++){
                System.out.print(map[i][j] + " ");
            }
            System.out.println();
        }
    }


}