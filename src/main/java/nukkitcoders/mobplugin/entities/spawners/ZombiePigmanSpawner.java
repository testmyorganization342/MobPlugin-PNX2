package nukkitcoders.mobplugin.entities.spawners;

import cn.nukkit.Player;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.entity.EntityID;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import nukkitcoders.mobplugin.AutoSpawnTask;
import nukkitcoders.mobplugin.entities.autospawn.AbstractEntitySpawner;
import nukkitcoders.mobplugin.entities.monster.walking.ZombiePigman;
import nukkitcoders.mobplugin.utils.Utils;

public class ZombiePigmanSpawner extends AbstractEntitySpawner {

    public ZombiePigmanSpawner(AutoSpawnTask spawnTask) {
        super(spawnTask);
    }

    @Override
    public void spawn(Player player, Position pos, Level level) {
        if (level.getBlockLightAt((int) pos.x, (int) pos.y + 1, (int) pos.z) <= 7) {
            for (int i = 0; i < Utils.rand(2, 4); i++) {
                EntityCreature entity = this.spawnTask.createEntity(EntityID.PIGLIN, pos.add(0.5, 1, 0.5));
                if (entity == null) return;
                if (Utils.rand(1, 20) == 1) {
                    entity.setBaby(true);
                }
            }
        }
    }

    @Override
    public final int getEntityNetworkId() {
        return ZombiePigman.NETWORK_ID;
    }
}
