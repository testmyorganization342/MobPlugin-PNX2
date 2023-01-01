package nukkitcoders.mobplugin.entities.animal.swimming;

import cn.nukkit.entity.passive.EntityCod;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

@Deprecated
public class Cod extends EntityCod {

    public static final int NETWORK_ID = 112;

    public Cod(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

}
