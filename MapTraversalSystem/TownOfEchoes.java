import java.util.Random;

public class TownOfEchoes{
    int maximumEnemies = 6;
    int i;
    int j;
    int randomizedRowPoint;
    int randomizedColPoint;
    boolean settingMap = true;
    private final int row = 4;
    private final int col = 4;
    private final int[][] map = new int[row][col];



    public void setMap(){
        Random random = new Random();
        for(i = 0; i < row; i++){
            for(j = 0; j < col; j++){
                this.map[i][j] = 0;
            }
        }
        for(i=1; i <= maximumEnemies; i++){
            settingMap = true;
            while(settingMap){
                randomizedRowPoint = random.nextInt(0,4);
                randomizedColPoint = random.nextInt(0,4);
                if(!(randomizedRowPoint == 3 && randomizedColPoint == 0) && !(randomizedRowPoint == 0 && randomizedColPoint == 3) && map[randomizedRowPoint][randomizedColPoint] != 1){
                    map[randomizedRowPoint][randomizedColPoint] = 1;
                    settingMap = false;
                }
            }

        }

        map[0][3] = 2;
    }

    public int getMapAt(int x, int y){
        return map[y][x];
    }

    public void viewMap(){
        for(int i = 0; i < row; i++){
            for(int j = 0; j < col; j++){
                System.out.print(map[i][j] + " ");
            }
            System.out.println();
        }
    }
}
