package nukkitcoders.mobplugin.entities.animal.walking;

import cn.nukkit.entity.passive.EntityVillager;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;

@Deprecated
public class Villager extends EntityVillager {

    public static final int NETWORK_ID = 15;

    public Villager(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

}
