package nukkitcoders.mobplugin.entities.spawners;

import cn.nukkit.Player;
import cn.nukkit.entity.EntityID;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import nukkitcoders.mobplugin.AutoSpawnTask;
import nukkitcoders.mobplugin.MobPlugin;
import nukkitcoders.mobplugin.entities.autospawn.AbstractEntitySpawner;
import nukkitcoders.mobplugin.entities.monster.walking.Spider;

public class SpiderSpawner extends AbstractEntitySpawner {

    public SpiderSpawner(AutoSpawnTask spawnTask) {
        super(spawnTask);
    }

    public void spawn(Player player, Position pos, Level level) {
        if (level.getBlockLightAt((int) pos.x, (int) pos.y + 1, (int) pos.z) <= 7) {
            if (MobPlugin.isMobSpawningAllowedByTime(level)) {
                this.spawnTask.createEntity(EntityID.SPIDER, pos.add(0.5, 1, 0.5));
            }
        }
    }

    @Override
    public final int getEntityNetworkId() {
        return Spider.NETWORK_ID;
    }
}
