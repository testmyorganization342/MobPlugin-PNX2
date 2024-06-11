package nukkitcoders.mobplugin.entities.spawners;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.EntityID;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import nukkitcoders.mobplugin.AutoSpawnTask;
import nukkitcoders.mobplugin.MobPlugin;
import nukkitcoders.mobplugin.entities.autospawn.AbstractEntitySpawner;
import nukkitcoders.mobplugin.utils.Utils;

public class WolfSpawner extends AbstractEntitySpawner {

    public WolfSpawner(AutoSpawnTask spawnTask) {
        super(spawnTask);
    }

    @Override
    public void spawn(Player player, Position pos, Level level) {
        if (Utils.rand(1, 3) != 1) {
            return;
        }
        final int biomeId = level.getBiomeId((int) pos.x, (int) pos.y, (int) pos.z);
        if (MobPlugin.isAnimalSpawningAllowedByTime(level) && ((biomeId == 4 || biomeId == 5 || biomeId == 20 || biomeId == 27 || biomeId == 30 || biomeId == 32 || biomeId == 133 || biomeId == 158))) {
            String blockId = level.getBlockIdAt((int) pos.x, (int) pos.y, (int) pos.z);
            if (blockId.equals(Block.GRASS_BLOCK) || blockId.equals(Block.SNOW_LAYER)) {
                for (int i = 0; i < 4; i++) {
                    var entity = this.spawnTask.createEntity(EntityID.WOLF, pos.add(0.5, 1, 0.5));
                    if (entity == null) return;
                    if (Utils.rand(1, 10) == 1) {
                        entity.setBaby(true);
                    }
                }
            }
        }
    }

    @Override
    public final int getEntityNetworkId() {
        return 14;
    }
}
