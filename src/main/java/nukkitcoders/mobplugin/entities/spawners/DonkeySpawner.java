package nukkitcoders.mobplugin.entities.spawners;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.utils.Config;
import nukkitcoders.mobplugin.AutoSpawnTask;
import nukkitcoders.mobplugin.entities.animal.walking.Donkey;
import nukkitcoders.mobplugin.entities.autospawn.AbstractEntitySpawner;
import nukkitcoders.mobplugin.entities.autospawn.SpawnResult;
import nukkitcoders.mobplugin.entities.BaseEntity;
import nukkitcoders.mobplugin.utils.Utils;

public class DonkeySpawner extends AbstractEntitySpawner {

    public DonkeySpawner(AutoSpawnTask spawnTask, Config pluginConfig) {
        super(spawnTask, pluginConfig);
    }

    @Override
    public SpawnResult spawn(Player player, Position pos, Level level) {
        SpawnResult result = SpawnResult.OK;

        int blockId = level.getBlockIdAt((int) pos.x, (int) pos.y, (int) pos.z);
        int biomeId = level.getBiomeId((int) pos.x, (int) pos.z);

        if (blockId != Block.GRASS) {
            result = SpawnResult.WRONG_BLOCK;
        } else if (biomeId != 1 && biomeId != 35 && biomeId != 128 && biomeId != 129) {
            result = SpawnResult.WRONG_BIOME;
        } else if (pos.y > 127 || pos.y < 1 || blockId == Block.AIR) {
            result = SpawnResult.POSITION_MISMATCH;
        } else {
            BaseEntity entity = this.spawnTask.createEntity("Donkey", pos.add(0, 1, 0));
            if (Utils.rand(0, 500) > 480) {
                entity.setBaby(true);
            }
        }

        return result;
    }

    @Override
    public final int getEntityNetworkId() {
        return Donkey.NETWORK_ID;
    }
}
