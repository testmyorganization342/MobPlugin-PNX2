package nukkitcoders.mobplugin.entities.spawners;

import cn.nukkit.Player;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import nukkitcoders.mobplugin.AutoSpawnTask;
import nukkitcoders.mobplugin.MobPlugin;
import nukkitcoders.mobplugin.entities.autospawn.AbstractEntitySpawner;
import nukkitcoders.mobplugin.entities.monster.walking.Enderman;
import nukkitcoders.mobplugin.utils.Utils;

public class EndermanSpawner extends AbstractEntitySpawner {

    public EndermanSpawner(AutoSpawnTask spawnTask) {
        super(spawnTask);
    }

    public void spawn(Player player, Position pos, Level level) {
        boolean nether = level.getDimension() == Level.DIMENSION_NETHER;
        boolean end = level.getDimension() == Level.DIMENSION_THE_END;

        if (Utils.rand(1, nether ? 10 : 7) != 1 && !end) {
            return;
        }

        final int biomeId = level.getBiomeId((int) pos.x, (int) pos.z);
        if (biomeId != 14 && biomeId != 15) {
            if (level.getBlockLightAt((int) pos.x, (int) pos.y, (int) pos.z) <= 7 || nether || end) {
                if (MobPlugin.isMobSpawningAllowedByTime(level) || nether || end) {
                    this.spawnTask.createEntity("Enderman", pos.add(0.5, 1, 0.5));
                }
            }
        }
    }

    @Override
    public final int getEntityNetworkId() {
        return Enderman.NETWORK_ID;
    }
}
