package nukkitcoders.mobplugin.entities.spawners;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.EntityID;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import nukkitcoders.mobplugin.AutoSpawnTask;
import nukkitcoders.mobplugin.MobPlugin;
import nukkitcoders.mobplugin.entities.animal.swimming.Turtle;
import nukkitcoders.mobplugin.entities.autospawn.AbstractEntitySpawner;
import nukkitcoders.mobplugin.utils.Utils;

public class TurtleSpawner extends AbstractEntitySpawner {

    public TurtleSpawner(AutoSpawnTask spawnTask) {
        super(spawnTask);
    }

    public void spawn(Player player, Position pos, Level level) {
        if (Utils.rand(1, 3) != 1) {
            return;
        }
        final String blockId = level.getBlockIdAt((int) pos.x, (int) pos.y, (int) pos.z);
        if (blockId == Block.WATER || blockId == Block.FLOWING_WATER) {
            final int biomeId = level.getBiomeId((int) pos.x,(int) pos.y, (int) pos.z);
            if (biomeId == 0 || biomeId == 40 || biomeId == 42) {
                if (MobPlugin.isAnimalSpawningAllowedByTime(level)) {
                    final String b = level.getBlockIdAt((int) pos.x, (int) (pos.y - 1), (int) pos.z);
                    if (b == Block.WATER || b == Block.FLOWING_WATER) {
                        for (int i = 0; i < Utils.rand(2, 6); i++) {
                            var entity = this.spawnTask.createEntity(EntityID.TURTLE, pos.add(0, -1, 0));
                            if (entity == null) return;
                            if (Utils.rand(1, 10) == 1) {
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
        return Turtle.NETWORK_ID;
    }

    @Override
    public boolean isWaterMob() {
        return true;
    }
}
