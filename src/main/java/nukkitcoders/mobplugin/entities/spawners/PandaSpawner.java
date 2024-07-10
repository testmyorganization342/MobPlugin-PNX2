package nukkitcoders.mobplugin.entities.spawners;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.entity.EntityID;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import nukkitcoders.mobplugin.AutoSpawnTask;
import nukkitcoders.mobplugin.MobPlugin;
import nukkitcoders.mobplugin.entities.animal.walking.Panda;
import nukkitcoders.mobplugin.entities.autospawn.AbstractEntitySpawner;
import nukkitcoders.mobplugin.utils.Utils;

public class PandaSpawner extends AbstractEntitySpawner {

    public PandaSpawner(AutoSpawnTask spawnTask) {
        super(spawnTask);
    }

    public void spawn(Player player, Position pos, Level level) {
        if (Utils.rand(1, 3) != 1) {
            return;
        }
        final int biomeId = level.getBiomeId((int) pos.x, (int) pos.y, (int) pos.z);
        if (((biomeId == 21 || biomeId == 22) && Utils.rand(1, 10) != 1) || biomeId != 48 && biomeId != 49 && biomeId != 21 && biomeId != 22) {
            return;
        }
        if (!MobPlugin.isAnimalSpawningAllowedByTime(level) || level.getBlockIdAt((int) pos.x, (int) pos.y, (int) pos.z) != Block.GRASS_BLOCK) {
            return;
        }
        for (int i = 0; i < Utils.rand(1, 2); i++) {
            EntityCreature entity = this.spawnTask.createEntity(EntityID.PANDA, pos.add(0.5, 1, 0.5));
            if (entity == null) return;
            if (Utils.rand(1, 20) == 1) {
                entity.setBaby(true);
            }
        }
    }

    @Override
    public final int getEntityNetworkId() {
        return Panda.NETWORK_ID;
    }
}