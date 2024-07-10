package nukkitcoders.mobplugin.entities.spawners;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.EntityID;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import nukkitcoders.mobplugin.AutoSpawnTask;
import nukkitcoders.mobplugin.MobPlugin;
import nukkitcoders.mobplugin.entities.autospawn.AbstractEntitySpawner;
import nukkitcoders.mobplugin.entities.monster.walking.Drowned;

public class DrownedSpawner extends AbstractEntitySpawner {

    public DrownedSpawner(AutoSpawnTask spawnTask) {
        super(spawnTask);
    }

    public void spawn(Player player, Position pos, Level level) {
        final String blockId = level.getBlockIdAt((int) pos.x, (int) pos.y, (int) pos.z);
        if (blockId == Block.WATER || blockId == Block.FLOWING_WATER) {
            final int biomeId = level.getBiomeId((int) pos.x, (int) pos.y, (int) pos.z);
            if (biomeId == 0 || biomeId == 7 || biomeId == 10 || biomeId == 11 || biomeId == 24 || (biomeId >= 40 && biomeId <= 47)) {
                if (level.getBlockLightAt((int) pos.x, (int) pos.y + 1, (int) pos.z) <= 7) {
                    if (MobPlugin.isMobSpawningAllowedByTime(level)) {
                        final String b = level.getBlockIdAt((int) pos.x, (int) (pos.y -1), (int) pos.z);
                        if (b == Block.WATER || b == Block.FLOWING_WATER) {
                            this.spawnTask.createEntity(EntityID.DROWNED, pos.add(0.5, -1, 0.5));
                        }
                    }
                }
            }
        }
    }

    @Override
    public final int getEntityNetworkId() {
        return Drowned.NETWORK_ID;
    }

    @Override
    public boolean isWaterMob() {
        return true;
    }
}
