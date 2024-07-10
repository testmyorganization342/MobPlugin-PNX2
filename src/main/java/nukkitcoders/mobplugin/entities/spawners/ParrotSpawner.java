package nukkitcoders.mobplugin.entities.spawners;

import cn.nukkit.Player;
import cn.nukkit.block.BlockGrassBlock;
import cn.nukkit.block.BlockLeaves;
import cn.nukkit.block.BlockWood;
import cn.nukkit.entity.EntityID;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import nukkitcoders.mobplugin.AutoSpawnTask;
import nukkitcoders.mobplugin.MobPlugin;
import nukkitcoders.mobplugin.entities.animal.flying.Parrot;
import nukkitcoders.mobplugin.entities.autospawn.AbstractEntitySpawner;
import nukkitcoders.mobplugin.utils.Utils;

public class ParrotSpawner extends AbstractEntitySpawner {

    public ParrotSpawner(AutoSpawnTask spawnTask) {
        super(spawnTask);
    }

    public void spawn(Player player, Position pos, Level level) {
        if (pos.y < 70 || Utils.rand(1, 3) != 1) {
            return;
        }
        final int biomeId = level.getBiomeId((int) pos.x, (int) pos.y, (int) pos.z);
        if (biomeId == 21 || biomeId == 149 || biomeId == 23 || biomeId == 151 || biomeId == 48 || biomeId == 49) {
            final var block = level.getBlock((int) pos.x, (int) pos.y, (int) pos.z);
            if (block instanceof BlockGrassBlock || block instanceof BlockLeaves || block instanceof BlockWood) {
                if (MobPlugin.isAnimalSpawningAllowedByTime(level)) {
                    for (int i = 0; i < Utils.rand(1, 2); i++) {
                        this.spawnTask.createEntity(EntityID.PARROT, pos.add(0.5, 1, 0.5));
                    }
                }
            }
        }
    }

    @Override
    public final int getEntityNetworkId() {
        return Parrot.NETWORK_ID;
    }
}
