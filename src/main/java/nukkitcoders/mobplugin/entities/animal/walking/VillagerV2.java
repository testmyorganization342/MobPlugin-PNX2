package nukkitcoders.mobplugin.entities.animal.walking;

import cn.nukkit.entity.passive.EntityVillager;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;

@Deprecated
public class VillagerV2 extends EntityVillager {

    public static final int NETWORK_ID = 115;

    public VillagerV2(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

}