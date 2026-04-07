package Screens;

import Entities.Enemy;
import Entities.MapCharacter;
import com.badlogic.gdx.math.Rectangle;
import io.github.Zephyrdoestech.Main;
import io.github.Zephyrdoestech.GameContext;
import Mechanics.MapTraversalSystem.Room;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TownOfEchoesScreen extends ExploringScreen {

    public TownOfEchoesScreen(Main game) { super(game); }

    @Override
    protected void initMapData() {
        this.mapName = "Town of Echoes";
        game.ctx.rooms = new ArrayList<>();
        // 1st row
        game.ctx.rooms.add(new Room(65f,   1770f, 200f, 200f));
        game.ctx.rooms.add(new Room(637f,  1770f, 200f, 200f));
        game.ctx.rooms.add(new Room(1215f, 1770f, 200f, 200f));
        game.ctx.rooms.add(new Room(1787f, 1770f, 200f, 200f));
        // 2nd row
        game.ctx.rooms.add(new Room(65f,   1195f, 200f, 200f));
        game.ctx.rooms.add(new Room(637f,  1195f, 200f, 200f));
        game.ctx.rooms.add(new Room(1215f, 1195f, 200f, 200f));
        game.ctx.rooms.add(new Room(1787f, 1195f, 200f, 200f));
        // 3rd row
        game.ctx.rooms.add(new Room(65f,   623f,  200f, 200f));
        game.ctx.rooms.add(new Room(637f,  623f,  200f, 200f));
        game.ctx.rooms.add(new Room(1215f, 623f,  200f, 200f));
        game.ctx.rooms.add(new Room(1787f, 623f,  200f, 200f));
        // 4th row
        game.ctx.rooms.add(new Room(65f,   45f,   200f, 200f));
        game.ctx.rooms.add(new Room(637f,  45f,   200f, 200f));
        game.ctx.rooms.add(new Room(1215f, 45f,   200f, 200f));
        game.ctx.rooms.add(new Room(1787f, 45f,   200f, 200f));

        this.mapTexture = game.assets.townTex;
        this.mapDecor = game.assets.townDecorationsTex;
        this.exitTexture = game.assets.townExitTex;

        if (game.ctx.exitRoom == null) {
            game.ctx.exitRoom = game.ctx.rooms.get(RNG.nextInt(4));
        }
    }

    @Override
    protected void initWalkable() {
        walkableZones.clear();
        for (Room r : game.ctx.rooms) walkableZones.add(r.getBounds());

        walkableZones.add(new Rectangle(65f,  138f, 1900f, 30f));
        walkableZones.add(new Rectangle(65f,  717f, 1900f, 30f));
        walkableZones.add(new Rectangle(65f,  1292f,1900f, 30f));
        walkableZones.add(new Rectangle(65f,  1867f,1900f, 30f));
        walkableZones.add(new Rectangle(155f,  65f, 15f, 1900f));
        walkableZones.add(new Rectangle(730f,  65f, 15f, 1900f));
        walkableZones.add(new Rectangle(1305f, 65f, 15f, 1900f));
        walkableZones.add(new Rectangle(1880f, 65f, 15f, 1900f));
    }

    @Override
    protected void spawnEnemies() {
        game.ctx.mapEnemies.clear();

        List<Room> eligibleRooms = new ArrayList<>(game.ctx.rooms);
        eligibleRooms.remove(game.ctx.exitRoom); // not exit room and room with enemy
        Collections.shuffle(eligibleRooms, RNG);

        int count = Math.min(getEnemyCount(), eligibleRooms.size());
        for (int i = 0; i < count; i++) {
            Room room = eligibleRooms.get(i);
            float x = room.getBounds().x + RNG.nextFloat() * (room.getBounds().width  - GameContext.CHAR_SIZE);
            float y = room.getBounds().y + RNG.nextFloat() * (room.getBounds().height - GameContext.CHAR_SIZE);
            Enemy e = RNG.nextBoolean() ? Enemy.fleshFeeder(x, y) : Enemy.darrylion(x, y);
            room.addEnemy(e);
            game.ctx.mapEnemies.add(e);
        }
    }

    @Override
    protected int getEnemyCount() {
        return 6;
    }

    @Override
    protected int getRequiredKills() {
        return 2;
    }

    @Override
    protected void initPlayerPosition() {
        spawnEnemies();

        List<Room> emptyRooms = new ArrayList<>();
        for (Room r : game.ctx.rooms)
            if (r.getEnemies().isEmpty() && r != game.ctx.exitRoom)  // ← exclude exit room
                emptyRooms.add(r);

        Room spawnRoom = emptyRooms.isEmpty() ? game.ctx.rooms.get(0) : emptyRooms.get(RNG.nextInt(emptyRooms.size()));

        float x = spawnRoom.getBounds().x + (spawnRoom.getBounds().width  - GameContext.CHAR_SIZE) / 2f;
        float y = spawnRoom.getBounds().y + (spawnRoom.getBounds().height - GameContext.CHAR_SIZE) / 2f;

        game.ctx.player = new MapCharacter(x, y);
    }

    @Override
    protected void restoreInstanceFields() {
        game.ctx.MAP_SIZE = 2048f; // Town's actual map size — set yours correctly
        this.mapTexture  = game.assets.townTex;
        this.mapDecor    = game.assets.townDecorationsTex;
        this.exitTexture = game.assets.townExitTex;
    }

    @Override
    protected ExploringScreen getNextScreen() {
        return new SilentCavernsScreen(game);
    }

    @Override
    public void show() {
        super.show(); // This is critical! It runs the map-loading logic in ExploringScreen.

        // Start the town music when the screen is shown
        if (game.assets.townOfEchoesBGM != null) {
            game.assets.townOfEchoesBGM.setLooping(true);
            game.assets.townOfEchoesBGM.setVolume(0.5f); // Set this to whatever volume feels right!
            game.assets.townOfEchoesBGM.play();
        }
    }

    @Override
    public void hide() {
        super.hide();

        // Stop the town music when leaving (e.g., entering Combat or Main Menu)
        if (game.assets.townOfEchoesBGM != null) {
            game.assets.townOfEchoesBGM.stop();
            // Note: You can change .stop() to .pause() if you want the song
            // to resume from the same spot after a battle!
        }
    }
}
