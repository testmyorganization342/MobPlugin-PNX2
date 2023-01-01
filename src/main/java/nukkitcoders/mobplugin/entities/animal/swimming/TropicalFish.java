package nukkitcoders.mobplugin.entities.animal.swimming;

import cn.nukkit.entity.passive.EntityTropicalFish;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

@Deprecated
public class TropicalFish extends EntityTropicalFish {

    public static final int NETWORK_ID = 111;

    public TropicalFish(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

}
