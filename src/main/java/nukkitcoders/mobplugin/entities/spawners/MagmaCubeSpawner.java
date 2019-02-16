package nukkitcoders.mobplugin.entities.spawners;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.utils.Config;
import nukkitcoders.mobplugin.AutoSpawnTask;
import nukkitcoders.mobplugin.entities.autospawn.AbstractEntitySpawner;
import nukkitcoders.mobplugin.entities.autospawn.SpawnResult;
import nukkitcoders.mobplugin.entities.monster.jumping.MagmaCube;

public class MagmaCubeSpawner extends AbstractEntitySpawner {

    public MagmaCubeSpawner(AutoSpawnTask spawnTask, Config pluginConfig) {
        super(spawnTask, pluginConfig);
    }

    @Override
    public SpawnResult spawn(Player player, Position pos, Level level) {
        SpawnResult result = SpawnResult.OK;

        int biomeId = level.getBiomeId((int) pos.x, (int) pos.z);
        int blockId = level.getBlockIdAt((int) pos.x, (int) pos.y, (int) pos.z);

        if (biomeId != 8) {
            result = SpawnResult.WRONG_BIOME;
        } else if (blockId != Block.NETHERRACK) {
            result = SpawnResult.WRONG_BLOCK;
        } else if (pos.y > 127 || pos.y < 1 || blockId == Block.AIR) {
            result = SpawnResult.POSITION_MISMATCH;
        } else {
            this.spawnTask.createEntity("MagmaCube", pos.add(0, 1, 0));
        }

        return result;
    }

    @Override
    public final int getEntityNetworkId() {
        return MagmaCube.NETWORK_ID;
    }
}
