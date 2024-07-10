package nukkitcoders.mobplugin.entities.animal.jumping;

import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import nukkitcoders.mobplugin.entities.animal.JumpingAnimal;
import nukkitcoders.mobplugin.utils.Utils;
import org.jetbrains.annotations.NotNull;

public class Frog extends JumpingAnimal {

    public static final int NETWORK_ID = 132;

    public Frog(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public @NotNull String getIdentifier() {
        return FROG;
    }

    @Override
    public int getKillExperience() {
        return Utils.rand(1, 3);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getHeight() {
        return 0.55f;
    }

    @Override
    public float getWidth() {
        return 0.5f;
    }

    @Override
    protected void initEntity() {
        this.setMaxHealth(10);
        super.initEntity();
    }
}
