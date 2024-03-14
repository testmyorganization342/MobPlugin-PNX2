package nukkitcoders.mobplugin.entities.spawners;

import cn.nukkit.Player;
import cn.nukkit.entity.EntityID;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import nukkitcoders.mobplugin.AutoSpawnTask;
import nukkitcoders.mobplugin.entities.autospawn.AbstractEntitySpawner;
import nukkitcoders.mobplugin.entities.monster.flying.Blaze;
import nukkitcoders.mobplugin.utils.Utils;

/**
 * @author PikyCZ
 */
public class BlazeSpawner extends AbstractEntitySpawner {

    public BlazeSpawner(AutoSpawnTask spawnTask) {
        super(spawnTask);
    }

    @Override
    public void spawn(Player player, Position pos, Level level) {
        if (Utils.rand(1, 3) != 1) {
            this.spawnTask.createEntity(EntityID.BLAZE, pos.add(0.5, 1, 0.5));
        }
    }

    @Override
    public int getEntityNetworkId() {
        return Blaze.NETWORK_ID;
    }
}
