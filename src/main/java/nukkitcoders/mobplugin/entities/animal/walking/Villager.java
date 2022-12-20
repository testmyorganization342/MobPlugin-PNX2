package nukkitcoders.mobplugin.entities.animal.walking;

import cn.nukkit.entity.passive.EntityVillagerV1;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

@Deprecated
public class Villager extends EntityVillagerV1 {

    public static final int NETWORK_ID = 15;

    public Villager(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

}
