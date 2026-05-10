package Screens;

import Entities.Enemy;
import io.github.Zephyrdoestech.GameContext;
import io.github.Zephyrdoestech.Main;
import Mechanics.MapTraversalSystem.Room;
import com.badlogic.gdx.math.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SilentCavernsScreen extends ExploringScreen {

    public SilentCavernsScreen(Main game) { super(game); }

    @Override
    protected void initMapData() {
        this.mapName = "Silent Caverns";
        game.ctx.mapName = GameContext.MapName.SILENT_CAVERNS;
        game.ctx.MAP_SIZE = 2048f;
        game.ctx.rooms = new ArrayList<>();

        // 2nd row
        game.ctx.rooms.add(new Room(47f,   1370f, 158f, 158f));
        game.ctx.rooms.add(new Room(496f,  1370f, 158f, 158f));
        game.ctx.rooms.add(new Room(946f, 1370f, 158f, 158f));
        game.ctx.rooms.add(new Room(1395f, 1370f, 158f, 158f));
        game.ctx.rooms.add(new Room(1843f, 1370f, 158f, 158f));
        // 3rd row
        game.ctx.rooms.add(new Room(47f,   914f,  158f, 158f));
        game.ctx.rooms.add(new Room(496f,  914f,  158f, 158f));
        game.ctx.rooms.add(new Room(946f, 914f,  158f, 158f));
        game.ctx.rooms.add(new Room(1395f, 914f,  158f, 158f));
        game.ctx.rooms.add(new Room(1843f, 914f,  158f, 158f));
        // 4th row
        game.ctx.rooms.add(new Room(47f,   465f,   158f, 158f));
        game.ctx.rooms.add(new Room(496f,  465f,   158f, 158f));
        game.ctx.rooms.add(new Room(946f, 465f,   158f, 158f));
        game.ctx.rooms.add(new Room(1395f, 465f,   158f, 158f));
        game.ctx.rooms.add(new Room(1843f, 465f,   158f, 158f));
        // 1st row
        game.ctx.rooms.add(new Room(47f,   1815f, 158f, 158f));
        game.ctx.rooms.add(new Room(496f,  1815f, 158f, 158f));
        game.ctx.rooms.add(new Room(946f,  1815f, 158f, 158f));
        game.ctx.rooms.add(new Room(1395f, 1815f, 158f, 158f));
        game.ctx.rooms.add(new Room(1843f, 1815f, 158f, 158f));

        this.mapTexture  = game.assets.silentCavernsTex;
        this.mapDecor    = null;
        this.exitTexture = game.assets.cavernsExitTex;

        // Exit spawns in one of the rooms in the top-most row (indices 16-19)
        if (game.ctx.exitRoom == null) {
            game.ctx.exitRoom = game.ctx.rooms.get(16 + RNG.nextInt(4));
        }
    }

    @Override
    protected void initWalkable() {
        super.initWalkable();

        corridorZones.add(new Rectangle(70f,  1886f, 1900f, 10f));
        corridorZones.add(new Rectangle(70f,  1437f, 1900f, 10f));
        corridorZones.add(new Rectangle(70f,  988f, 1900f, 10f));
        corridorZones.add(new Rectangle(70f,  541f, 1900f, 10f));

        corridorZones.add(new Rectangle(126f,  530f, 0.1f, 1400f));
        corridorZones.add(new Rectangle(575f,  530f, 0.1f, 1400f));
        corridorZones.add(new Rectangle(1024f, 530f, 0.1f, 1400f));
        corridorZones.add(new Rectangle(1473f, 530f, 0.1f, 1400f));
        corridorZones.add(new Rectangle(1922f, 530f, 0.1f, 1400f));
    }

    @Override
    protected int getEnemyCount() {
        return 10;
    }

    @Override
    protected int getRequiredKills() {
        return 0;
    }

    @Override
    protected void spawnEnemies() {
        game.ctx.mapEnemies.clear();

        List<Room> eligibleRooms = new ArrayList<>(game.ctx.rooms);
        eligibleRooms.remove(game.ctx.exitRoom);
        Collections.shuffle(eligibleRooms, RNG);

        int count = Math.min(getEnemyCount(), eligibleRooms.size());
        for (int i = 0; i < count; i++) {
            Room room = eligibleRooms.get(i);
            float x = room.getBounds().x + RNG.nextFloat() * (room.getBounds().width  - 64f);
            float y = room.getBounds().y + RNG.nextFloat() * (room.getBounds().height - 64f);

            Enemy e = RNG.nextBoolean() ? Enemy.gobninil(x, y) : Enemy.chimericks(x, y);
            room.addEnemy(e);
            game.ctx.mapEnemies.add(e);
        }
    }

    @Override
    protected ExploringScreen getNextScreen() {
        return new AbyssOfDissonanceScreen(game);
    }

    @Override
    protected void restoreInstanceFields() {
        this.mapName = "Silent Caverns";
        game.ctx.mapName = GameContext.MapName.SILENT_CAVERNS;

        game.ctx.MAP_SIZE = 2048f;
        this.mapTexture  = game.assets.silentCavernsTex;
        this.mapDecor    = null;
        this.exitTexture = game.assets.cavernsExitTex;
    }

    @Override
    public void render(float delta) {
        // darker tint for cave
//        game.batch.setColor(Color.SLATE);
        super.render(delta);
//        game.batch.setColor(Color.WHITE);
    }

    @Override
    public void show() {
        super.show();

        if (game.ctx != null) {
            game.ctx.mapsCleared = 0;
        }

        // 2. Auto-Save the game!
        if (game.ctx != null) {
            game.ctx.saveGame("SilentCaverns", game.ctx.player.getX(), game.ctx.player.getY());
        }
    }
}
