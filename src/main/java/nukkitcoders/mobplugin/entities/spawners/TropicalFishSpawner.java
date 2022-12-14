package nukkitcoders.mobplugin.entities.spawners;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import nukkitcoders.mobplugin.AutoSpawnTask;
import nukkitcoders.mobplugin.entities.animal.swimming.TropicalFish;
import nukkitcoders.mobplugin.entities.autospawn.AbstractEntitySpawner;
import nukkitcoders.mobplugin.utils.Utils;

public class TropicalFishSpawner extends AbstractEntitySpawner {

    public TropicalFishSpawner(AutoSpawnTask spawnTask) {
        super(spawnTask);
    }

    public void spawn(Player player, Position pos, Level level) {
        if (Utils.rand(1, 3) == 1) {
            return;
        }
        final int blockId = level.getBlockIdAt((int) pos.x, (int) pos.y, (int) pos.z);
        if (blockId == Block.WATER || blockId == Block.STILL_WATER) {
            final int biomeId = level.getBiomeId((int) pos.x, (int) pos.z);
            if (biomeId == 0 || biomeId == 7) {
                int b = level.getBlockIdAt((int) pos.x, (int) (pos.y -1), (int) pos.z);
                if (b == Block.WATER || b == Block.STILL_WATER) {
                    this.spawnTask.createEntity("TropicalFish", pos.add(0.5, -1, 0.5));
                }
            }
        }
    }

    @Override
    public final int getEntityNetworkId() {
        return TropicalFish.NETWORK_ID;
    }
}
