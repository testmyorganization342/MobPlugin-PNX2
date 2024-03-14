package nukkitcoders.mobplugin.entities.animal.walking;

import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import nukkitcoders.mobplugin.entities.animal.WalkingAnimal;
import nukkitcoders.mobplugin.utils.Utils;
import org.jetbrains.annotations.NotNull;

public class Camel extends WalkingAnimal {

    public static final int NETWORK_ID = 138;

    public Camel(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public void initEntity() {
        this.setMaxHealth(32);
        super.initEntity();
    }

    @Override
    public float getWidth() {
        if (this.isBaby()) {
            return 0.85f;
        }
        return 1.77f;
    }

    @Override
    public @NotNull String getIdentifier() {
        return CAMEL;
    }

    @Override
    public float getHeight() {
        if (this.isBaby()) {
            return 1.1875f;
        }
        return 2.375f;
    }

    @Override
    public int getKillExperience() {
        return Utils.rand(1, 3);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }
}