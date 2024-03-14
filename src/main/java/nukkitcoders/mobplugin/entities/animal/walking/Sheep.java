package nukkitcoders.mobplugin.entities.animal.walking;

import cn.nukkit.entity.passive.EntitySheep;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;

@Deprecated
    public class Sheep extends EntitySheep {

    public static final int NETWORK_ID = 13;

    public Sheep(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

}
