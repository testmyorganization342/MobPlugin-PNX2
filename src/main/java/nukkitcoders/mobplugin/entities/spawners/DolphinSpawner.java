package nukkitcoders.mobplugin.entities.spawners;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockWater;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import nukkitcoders.mobplugin.AutoSpawnTask;
import nukkitcoders.mobplugin.MobPlugin;
import nukkitcoders.mobplugin.entities.animal.swimming.Dolphin;
import nukkitcoders.mobplugin.entities.autospawn.AbstractEntitySpawner;
import nukkitcoders.mobplugin.utils.Utils;

public class DolphinSpawner extends AbstractEntitySpawner {

    public DolphinSpawner(AutoSpawnTask spawnTask) {
        super(spawnTask);
    }

    public void spawn(Player player, Position pos, Level level) {
        if (Utils.rand(1, 3) == 1) {
            return;
        }
        final int biomeId = level.getBiomeId((int) pos.x, (int) pos.z);
        final int blockId = level.getBlockIdAt((int) pos.x, (int) pos.y, (int) pos.z);
        if ((blockId == Block.WATER || blockId == Block.STILL_WATER) && biomeId == 0) {
            if (MobPlugin.isAnimalSpawningAllowedByTime(level)) {
                if (level.getBlock(pos.add(0.5, -1, 0.5)) instanceof BlockWater) {
                    this.spawnTask.createEntity("Dolphin", pos.add(0.5, -1, 0.5));
                }
            }
        }
    }

    @Override
    public final int getEntityNetworkId() {
        return Dolphin.NETWORK_ID;
    }
}
