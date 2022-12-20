package nukkitcoders.mobplugin.entities.animal.walking;

import cn.nukkit.entity.passive.EntityPig;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

@Deprecated
public class Pig extends EntityPig {

    public static final int NETWORK_ID = 12;

    public Pig(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

}
