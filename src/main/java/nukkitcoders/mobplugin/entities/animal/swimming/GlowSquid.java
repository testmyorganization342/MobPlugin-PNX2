package nukkitcoders.mobplugin.entities.animal.swimming;

import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class GlowSquid extends Squid {

    public static final int NETWORK_ID = 129;

    public GlowSquid(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }
}
