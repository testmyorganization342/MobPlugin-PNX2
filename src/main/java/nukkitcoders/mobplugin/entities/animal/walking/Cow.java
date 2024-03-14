package nukkitcoders.mobplugin.entities.animal.walking;

import cn.nukkit.entity.passive.EntityCow;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;

@Deprecated
public class Cow extends EntityCow {

    public static final int NETWORK_ID = 11;

    public Cow(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

}
