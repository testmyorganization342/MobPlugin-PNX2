package de.kniffo80.mobplugin;

import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import de.kniffo80.mobplugin.utils.Utils;

public class RandomSpawn {

    public static Position getSpawnPos(Level level) {
        return getSpawnPos(level, new Position(0, 128, 0, level), 256, 512);
    }

    public static Position getSpawnPos(Level level, Position pos, int radius, int maxTries) {
        for (int tries = 0; tries < maxTries; tries++) {
            int xPos = Utils.rand(-radius, radius + 1) + pos.getFloorX(),
                    zPos = Utils.rand(-radius, radius + 1) + pos.getFloorZ(),
                    yPos = level.getHighestBlockAt(xPos, zPos);
            int under = level.getBlockIdAt(xPos, yPos, zPos);
            pos = new Position(xPos + 0.5f, yPos + 1, zPos + 0.5f, level);
            break;
        }

        return pos;
    }
}