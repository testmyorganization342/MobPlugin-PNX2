package nukkitcoders.mobplugin.entities.animal.swimming;

import cn.nukkit.entity.passive.EntitySalmon;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

@Deprecated
public class Salmon extends EntitySalmon {

    public static final int NETWORK_ID = 109;

    public Salmon(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

}
