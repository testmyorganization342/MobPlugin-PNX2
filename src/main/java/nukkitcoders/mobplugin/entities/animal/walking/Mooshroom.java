package nukkitcoders.mobplugin.entities.animal.walking;

import cn.nukkit.entity.passive.EntityMooshroom;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;

@Deprecated
public class Mooshroom extends EntityMooshroom {

    public static final int NETWORK_ID = 16;

    public Mooshroom(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

}
