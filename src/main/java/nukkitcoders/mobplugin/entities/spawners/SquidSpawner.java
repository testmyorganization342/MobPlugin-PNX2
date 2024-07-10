package nukkitcoders.mobplugin.entities.spawners;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.EntityID;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import nukkitcoders.mobplugin.AutoSpawnTask;
import nukkitcoders.mobplugin.MobPlugin;
import nukkitcoders.mobplugin.entities.animal.swimming.Squid;
import nukkitcoders.mobplugin.entities.autospawn.AbstractEntitySpawner;
import nukkitcoders.mobplugin.utils.Utils;

public class SquidSpawner extends AbstractEntitySpawner {

    public SquidSpawner(AutoSpawnTask spawnTask) {
        super(spawnTask);
    }

    public void spawn(Player player, Position pos, Level level) {
        if (Utils.rand(1, 3) != 1) {
            return;
        }
        final String blockId = level.getBlockIdAt((int) pos.x, (int) pos.y, (int) pos.z);
        if (blockId == Block.WATER || blockId == Block.FLOWING_WATER) {
            final int biomeId = level.getBiomeId((int) pos.x, (int) pos.y, (int) pos.z);
            if (biomeId == 0 || biomeId == 10 || biomeId == 24 || (biomeId >= 40 && biomeId <= 47)) {
                if (MobPlugin.isAnimalSpawningAllowedByTime(level)) {
                    final String b = level.getBlockIdAt((int) pos.x, (int) (pos.y - 1), (int) pos.z);
                    if (b == Block.WATER || b == Block.FLOWING_WATER) {
                        for (int i = 0; i < Utils.rand(2, 4); i++) {
                            var entity = this.spawnTask.createEntity(EntityID.SQUID, pos.add(0, -1, 0));
                            if (entity == null) return;
                            if (Utils.rand(1, 20) == 1) {
                                entity.setBaby(true);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public final int getEntityNetworkId() {
        return Squid.NETWORK_ID;
    }

    @Override
    public boolean isWaterMob() {
        return true;
    }
}
