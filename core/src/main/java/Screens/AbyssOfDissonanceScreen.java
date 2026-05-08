package Screens;

import Entities.Enemy;
import io.github.Zephyrdoestech.GameContext;
import io.github.Zephyrdoestech.Main;
import Mechanics.MapTraversalSystem.Room;
import com.badlogic.gdx.math.Rectangle;
import java.util.ArrayList;

public class AbyssOfDissonanceScreen extends ExploringScreen {

    public AbyssOfDissonanceScreen(Main game) { super(game); }

    @Override
    protected void initMapData() {
        this.mapName = "Abyss of Dissonance";
        game.ctx.mapName = GameContext.MapName.ABYSS_OF_DISSONANCE;
        game.ctx.MAP_SIZE = 1800f;

        game.ctx.rooms = new ArrayList<>();
        game.ctx.rooms.add(new Room(726f, 1439f, 227f, 250f));
        game.ctx.rooms.add(new Room(726f, 788f, 227f, 250f));
        //final room
        game.ctx.rooms.add(new Room(726f, 135f, 227f, 250f));

        this.mapTexture = game.assets.abyssOfDissonanceTex;
        this.mapDecor = null;
        this.exitTexture = null;

        game.ctx.exitRoom = null;
    }

    @Override
    protected void initWalkable() {
        walkableZones.clear();

        for (Room r : game.ctx.rooms) walkableZones.add(r.getBounds());

        walkableZones.add(new Rectangle(839f,  200f, 0.1f, 1300f));

    }

    @Override
    protected ExploringScreen getNextScreen() {
        // can return to main or start screen
        return null;
    }

    @Override
    protected void spawnEnemies() {
        game.ctx.mapEnemies.clear();

        Room semiBossRoom = game.ctx.rooms.get(1);
        float sbX = semiBossRoom.getBounds().x + (semiBossRoom.getBounds().width  - 64f) / 2f;
        float sbY = semiBossRoom.getBounds().y + (semiBossRoom.getBounds().height - 64f) / 2f;
        Enemy labagoliath = Enemy.labagoliath(sbX, sbY);
        semiBossRoom.addEnemy(labagoliath);
        game.ctx.mapEnemies.add(labagoliath);

        Room finalBossRoom = game.ctx.rooms.get(2);
        float fbX = finalBossRoom.getBounds().x + (finalBossRoom.getBounds().width  - 64f) / 2f;
        float fbY = finalBossRoom.getBounds().y + (finalBossRoom.getBounds().height - 64f) / 2f;
        Enemy maestroSyozan = Enemy.maestroSyozan(fbX, fbY);
        finalBossRoom.addEnemy(maestroSyozan);
        game.ctx.mapEnemies.add(maestroSyozan);
    }

    @Override
    protected void restoreInstanceFields() {
        this.mapName = "Abyss of Dissonance";
        game.ctx.mapName = GameContext.MapName.ABYSS_OF_DISSONANCE;

        game.ctx.MAP_SIZE = 1800f;
        this.mapTexture = game.assets.abyssOfDissonanceTex;
        this.mapDecor = null;
        this.exitTexture = null;
    }

    @Override
    public void show() {
        super.show();

        if (game.ctx != null) {
            // Check if all enemies are defeated. Since this is the last screen (getNextScreen() is null),
            // this means the player has won the game.
            if (game.ctx.mapEnemies.isEmpty()) {
                game.ctx.mapsCleared = 3; // Player has cleared all 3 maps.
                Screens.LeaderBoard.LeaderboardScreen.promptForUsername(game, game.ctx, 3);
                return;
            }

            // Player is on the 3rd map, so they have cleared 2 maps.
            game.ctx.mapsCleared = 2;

            // Auto-Save the game
            game.ctx.saveGame("AbyssOfDissonance", game.ctx.player.getX(), game.ctx.player.getY());
        }
    }
}
