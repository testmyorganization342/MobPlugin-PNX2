package nukkitcoders.mobplugin.entities.animal.walking;

import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import nukkitcoders.mobplugin.entities.animal.WalkingAnimal;
import nukkitcoders.mobplugin.utils.Utils;
import org.jetbrains.annotations.NotNull;

public class Panda extends WalkingAnimal {

    public static final int NETWORK_ID = 113;

    public Panda(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public @NotNull String getIdentifier() {
        return PANDA;
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getLength() {
        return 1.825f;
    }

    @Override
    public float getWidth() {
        return 1.125f;
    }

    @Override
    public float getHeight() {
        return 1.25f;
    }

    @Override
    public void initEntity() {
        this.setMaxHealth(20);
        super.initEntity();
    }

    @Override
    public int getKillExperience() {
        return this.isBaby() ? 0 : Utils.rand(1, 3);
    }
}