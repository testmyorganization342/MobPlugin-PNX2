package nukkitcoders.mobplugin.entities.spawners;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.entity.EntityID;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import nukkitcoders.mobplugin.AutoSpawnTask;
import nukkitcoders.mobplugin.MobPlugin;
import nukkitcoders.mobplugin.entities.autospawn.AbstractEntitySpawner;
import nukkitcoders.mobplugin.utils.Utils;

public class HorseSpawner extends AbstractEntitySpawner {

    public HorseSpawner(AutoSpawnTask spawnTask) {
        super(spawnTask);
    }

    @Override
    public void spawn(Player player, Position pos, Level level) {
        if (Utils.rand(1, 3) != 1) {
            return;
        }
        String blockId = level.getBlockIdAt((int) pos.x, (int) pos.y, (int) pos.z);
            if (blockId == Block.GRASS_BLOCK || blockId == Block.SNOW_LAYER) {
            final int biomeId = level.getBiomeId((int) pos.x, (int) pos.y, (int) pos.z);
            if (biomeId == 1 || biomeId == 35 || biomeId == 128 || biomeId == 129) {
                if (MobPlugin.isAnimalSpawningAllowedByTime(level)) {
                    for (int i = 0; i < Utils.rand(2, 6); i++) {
                        EntityCreature entity = this.spawnTask.createEntity(EntityID.HORSE, pos.add(0.5, 1, 0.5));
                        if (entity == null) return;
                        if (Utils.rand(1, 20) == 1) {
                            entity.setBaby(true);
                        }
                    }
                }
            }
        }
    }

    @Override
    public final int getEntityNetworkId() {
        return 23;
    }
}
