package nukkitcoders.mobplugin.entities.animal.walking;

import cn.nukkit.entity.passive.EntityChicken;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;

@Deprecated
public class Chicken extends EntityChicken {

    public static final int NETWORK_ID = 10;

    public Chicken(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

}
