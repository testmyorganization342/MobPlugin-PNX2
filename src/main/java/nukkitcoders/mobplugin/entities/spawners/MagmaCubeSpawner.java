package nukkitcoders.mobplugin.entities.spawners;

import cn.nukkit.Player;
import cn.nukkit.entity.EntityID;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import nukkitcoders.mobplugin.AutoSpawnTask;
import nukkitcoders.mobplugin.entities.autospawn.AbstractEntitySpawner;
import nukkitcoders.mobplugin.entities.monster.jumping.MagmaCube;
import nukkitcoders.mobplugin.utils.Utils;

public class MagmaCubeSpawner extends AbstractEntitySpawner {

    public MagmaCubeSpawner(AutoSpawnTask spawnTask) {
        super(spawnTask);
    }

    @Override
    public void spawn(Player player, Position pos, Level level) {
        if (Utils.rand(1, 3) != 1) {
            this.spawnTask.createEntity(EntityID.MAGMA_CUBE, pos.add(0.5, 1, 0.5));
        }
    }

    @Override
    public final int getEntityNetworkId() {
        return MagmaCube.NETWORK_ID;
    }
}
