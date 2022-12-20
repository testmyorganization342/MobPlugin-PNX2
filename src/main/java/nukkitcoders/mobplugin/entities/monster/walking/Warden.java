package nukkitcoders.mobplugin.entities.monster.walking;

import cn.nukkit.entity.mob.EntityWarden;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

@Deprecated
public class Warden extends EntityWarden {

    public static final int NETWORK_ID = 131;

    public Warden(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

}
