package nukkitcoders.mobplugin.entities.animal.walking;

import cn.nukkit.entity.passive.EntityHorse;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author <a href="mailto:kniffman@googlemail.com">Michael Gertz</a>
 */
@Deprecated
public class Horse extends EntityHorse {

    public static final int NETWORK_ID = 23;

    public Horse(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

}