package nukkitcoders.mobplugin.entities.monster.walking;

import cn.nukkit.entity.passive.EntityWolf;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author GoodLucky777
 */
@Deprecated
public class Wolf extends EntityWolf {

    public static final int NETWORK_ID = 14;

    public Wolf(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

}
