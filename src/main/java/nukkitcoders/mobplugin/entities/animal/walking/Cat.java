package nukkitcoders.mobplugin.entities.animal.walking;

import cn.nukkit.entity.passive.EntityCat;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

@Deprecated
public class Cat extends EntityCat {

    public static final int NETWORK_ID = 75;

    public Cat(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

}