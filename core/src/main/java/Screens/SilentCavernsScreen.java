package Screens;

import Entities.Enemy;
import io.github.Zephyrdoestech.Main;
import Mechanics.Room;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SilentCavernsScreen extends ExploringScreen {

    public SilentCavernsScreen(Main game) { super(game); }

    @Override
    protected void initMapData() {
        this.mapName = "Silent Caverns";
        game.ctx.MAP_SIZE = 2048f;
        game.ctx.rooms = new ArrayList<>();

        //will change this once map is complete

        // Grid Settings: 4 Columns x 5 Rows = 20 Rooms
        float roomSize = 150f;
        float spacingX = 450f; // Distance between room starts horizontally
        float spacingY = 400f; // Distance between room starts vertically
        float startX = 200f;
        float startY = 200f;
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 4; col++) {
                float x = startX + (col * spacingX);
                float y = startY + (row * spacingY);
                game.ctx.rooms.add(new Room(x, y, roomSize, roomSize));
            }
        }

        this.mapTexture  = game.assets.silentCavernsTex;
        this.mapDecor    = null;
        this.exitTexture = game.assets.townExitTex;

        // Exit spawns in one of the rooms in the top-most row (indices 16-19)
        this.exitRoom = game.ctx.rooms.get(16 + RNG.nextInt(4));
    }

    @Override
    protected void initWalkable() {
        walkableZones.clear();
        for (Room r : game.ctx.rooms) {
            walkableZones.add(r.getBounds());
        }

        walkableZones.add(new Rectangle(200f, 265f,  1500f, 20f)); // Row 0
        walkableZones.add(new Rectangle(200f, 665f,  1500f, 20f)); // Row 1
        walkableZones.add(new Rectangle(200f, 1065f, 1500f, 20f)); // Row 2
        walkableZones.add(new Rectangle(200f, 1465f, 1500f, 20f)); // Row 3
        walkableZones.add(new Rectangle(200f, 1865f, 1500f, 20f)); // Row 4

        walkableZones.add(new Rectangle(265f,  200f, 20f, 1750f)); // Col 0
        walkableZones.add(new Rectangle(715f,  200f, 20f, 1750f)); // Col 1
        walkableZones.add(new Rectangle(1165f, 200f, 20f, 1750f)); // Col 2
        walkableZones.add(new Rectangle(1615f, 200f, 20f, 1750f)); // Col 3
    }

    @Override
    protected int getEnemyCount() {
        return 10;
    }

    @Override
    protected int getRequiredKills() {
        return 3;
    }

    @Override
    protected void spawnEnemies() {
        game.ctx.mapEnemies.clear();

        List<Room> eligibleRooms = new ArrayList<>(game.ctx.rooms);
        eligibleRooms.remove(exitRoom);
        Collections.shuffle(eligibleRooms, RNG);

        int count = Math.min(getEnemyCount(), eligibleRooms.size());
        for (int i = 0; i < count; i++) {
            Room room = eligibleRooms.get(i);
            float x = room.getBounds().x + RNG.nextFloat() * (room.getBounds().width  - 64f);
            float y = room.getBounds().y + RNG.nextFloat() * (room.getBounds().height - 64f);

            Enemy e = RNG.nextBoolean() ? Enemy.aryzachnid(x, y) : Enemy.chimericks(x, y);
            room.addEnemy(e);
            game.ctx.mapEnemies.add(e);
        }
    }

    @Override
    protected ExploringScreen getNextScreen() {
        return new AbyssOfDissonanceScreen(game);
    }

    @Override
    public void render(float delta) {
        // darker tint for cave
        game.batch.setColor(Color.SLATE);
        super.render(delta);
        game.batch.setColor(Color.WHITE);
    }
}
