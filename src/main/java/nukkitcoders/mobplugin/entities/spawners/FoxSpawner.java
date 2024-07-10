package nukkitcoders.mobplugin.entities.spawners;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.entity.EntityID;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import nukkitcoders.mobplugin.AutoSpawnTask;
import nukkitcoders.mobplugin.MobPlugin;
import nukkitcoders.mobplugin.entities.animal.walking.Fox;
import nukkitcoders.mobplugin.entities.autospawn.AbstractEntitySpawner;
import nukkitcoders.mobplugin.utils.Utils;

public class FoxSpawner extends AbstractEntitySpawner {

    public FoxSpawner(AutoSpawnTask spawnTask) {
        super(spawnTask);
    }

    public void spawn(Player player, Position pos, Level level) {
        if (Utils.rand(1, 3) != 1) {
            return;
        }
        String blockId = level.getBlockIdAt((int) pos.x, (int) pos.y, (int) pos.z);
        if (blockId.equals(Block.GRASS_BLOCK) || blockId.equals(Block.SNOW_LAYER)) {
            final int biomeId = level.getBiomeId((int) pos.x, (int) pos.y, (int) pos.z);
            if (biomeId == 5 || biomeId == 160 || biomeId == 161 || biomeId == 31 || biomeId == 19 || biomeId == 30 || biomeId == 133 || biomeId == 158 || biomeId == 32 || biomeId == 33) {
                if (MobPlugin.isAnimalSpawningAllowedByTime(level)) {
                    for (int i = 0; i < Utils.rand(2, 4); i++) {
                        EntityCreature entity = this.spawnTask.createEntity(EntityID.FOX, pos.add(0.5, 1, 0.5));
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
        return Fox.NETWORK_ID;
    }
}
