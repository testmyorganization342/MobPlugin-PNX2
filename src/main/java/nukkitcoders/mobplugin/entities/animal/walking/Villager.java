package nukkitcoders.mobplugin.entities.animal.walking;

import cn.nukkit.entity.Entity;
import static cn.nukkit.entity.passive.EntityVillager.PROFESSION_GENERIC;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import nukkitcoders.mobplugin.entities.animal.WalkingAnimal;

public class Villager extends WalkingAnimal {

    public static final int NETWORK_ID = 15;

    public Villager(FullChunk chunk, CompoundTag nbt) {
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

    @Override
    public float getWidth() {
        if (isBaby()) {
            return 0.3f;
        }
        return 0.6f;
    }

    @Override
    public float getHeight() {
        if (isBaby()) {
            return 0.975f;
        }
        return 1.95f;
    }

    @Override
    public double getSpeed() {
        return 1.1;
    }

    @Override
    public void initEntity() {
        super.initEntity();
        setMaxHealth(10);

        if (!namedTag.contains("Profession")) {
            setProfession(PROFESSION_GENERIC);
        }
    }

    public int getProfession() {
        return namedTag.getInt("Profession");
    }

    public void setProfession(int profession) {
        namedTag.putInt("Profession", profession);
    }

    @Override
    public boolean isBaby() {
        return getDataFlag(DATA_FLAGS, Entity.DATA_FLAG_BABY);
    }

    @Override
    public int getKillExperience() {
        return 0;
    }
}
