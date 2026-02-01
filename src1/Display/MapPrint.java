package Display;

import Player.Character;
import Map.*;

public interface MapPrint {
    public abstract void displayMapConfused(Character player);
    public abstract void displayMapBlocked(Character player);
    public abstract void displayEmptySpot(Character player);
    public abstract void displayEnemyEncounter(Character player);
    public abstract void displayExit(Character player);
    public abstract void displayEntrance(Character player);

    public abstract void displayMap(Character player, Map map, Map mapDisp);

}
