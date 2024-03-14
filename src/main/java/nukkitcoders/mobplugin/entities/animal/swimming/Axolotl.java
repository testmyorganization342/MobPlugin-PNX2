package nukkitcoders.mobplugin.entities.animal.swimming;

import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

public class Axolotl extends Fish {

    public static final int NETWORK_ID = 130;

    public Axolotl(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public @NotNull String getIdentifier() {
        return AXOLOTL;
    }

    @Override
    public void initEntity() {
        super.initEntity();

        this.setMaxHealth(14);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 0.75f;
    }

    @Override
    public float getHeight() {
        return 0.42f;
    }

    @Override
    int getBucketMeta() {
        return 12;
    }
}
