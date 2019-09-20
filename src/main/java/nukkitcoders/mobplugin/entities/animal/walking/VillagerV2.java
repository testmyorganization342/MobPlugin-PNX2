package nukkitcoders.mobplugin.entities.animal.walking;

import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class VillagerV2 extends Villager {

    public static final int NETWORK_ID = 115;

    public VillagerV2(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public String getName() {
        return "Villager";
    }
}
