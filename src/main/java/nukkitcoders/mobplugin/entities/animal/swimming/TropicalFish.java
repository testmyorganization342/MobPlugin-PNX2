package nukkitcoders.mobplugin.entities.animal.swimming;

import cn.nukkit.entity.passive.EntityTropicalfish;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;

@Deprecated
public class TropicalFish extends EntityTropicalfish {

    public static final int NETWORK_ID = 111;

    public TropicalFish(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

}
