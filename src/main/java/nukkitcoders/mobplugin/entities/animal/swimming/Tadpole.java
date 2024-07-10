package nukkitcoders.mobplugin.entities.animal.swimming;

import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

public class Tadpole extends Fish {

    public static final int NETWORK_ID = 133;

    public Tadpole(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public @NotNull String getIdentifier() {
        return TADPOLE;
    }

    @Override
    public int getKillExperience() {
        return 0;
    }

    @Override
    int getBucketMeta() {
        return 13;
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getHeight() {
        return 0.8f;
    }

    @Override
    public float getWidth() {
        return 0.6f;
    }

    @Override
    protected void initEntity() {
        this.setMaxHealth(6);
        super.initEntity();
    }
}
