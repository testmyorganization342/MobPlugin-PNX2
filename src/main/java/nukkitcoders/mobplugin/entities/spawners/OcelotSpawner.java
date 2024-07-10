package nukkitcoders.mobplugin.entities.spawners;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.entity.EntityID;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import nukkitcoders.mobplugin.AutoSpawnTask;
import nukkitcoders.mobplugin.MobPlugin;
import nukkitcoders.mobplugin.entities.animal.walking.Ocelot;
import nukkitcoders.mobplugin.entities.autospawn.AbstractEntitySpawner;
import nukkitcoders.mobplugin.utils.Utils;

public class OcelotSpawner extends AbstractEntitySpawner {

    public OcelotSpawner(AutoSpawnTask spawnTask) {
        super(spawnTask);
    }

    public void spawn(Player player, Position pos, Level level) {
        if (Utils.rand(1, 3) != 1) {
            return;
        }
        final int biomeId = level.getBiomeId((int) pos.x, (int) pos.y, (int) pos.z);
        if (biomeId == 21 || biomeId == 149 || biomeId == 23 || biomeId == 151 || biomeId == 48 || biomeId == 49) {
            final String blockId = level.getBlockIdAt((int) pos.x, (int) pos.y, (int) pos.z);
            if (blockId.equals(Block.GRASS_BLOCK)) {
                if (MobPlugin.isAnimalSpawningAllowedByTime(level)) {
                    for (int i = 0; i < Utils.rand(1, 2); i++) {
                        EntityCreature entity = this.spawnTask.createEntity(EntityID.OCELOT, pos.add(0.5, 1, 0.5));
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
        return Ocelot.NETWORK_ID;
    }
}
