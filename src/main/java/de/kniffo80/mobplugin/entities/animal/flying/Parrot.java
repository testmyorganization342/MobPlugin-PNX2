package de.kniffo80.mobplugin.entities.animal.flying;

import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import de.kniffo80.mobplugin.entities.animal.FlyingAnimal;

public class Parrot extends FlyingAnimal {
    
    public static final int NETWORK_ID = 105;
    

    public Parrot(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }
    
    public String getName() {
        return "parrot";
    }

    @Override
    public float getWidth() {
        return 0.5f;
    }

    @Override
    public float getHeight() {
        return 0.9f;
    }

    @Override
    public void initEntity() {
        super.initEntity();
        this.setMaxHealth(6);
    }

    @Override
    public Item[] getDrops() {
        return new Item[0];
    }
    
    @Override
    public int getKillExperience() {
        return 4;
    }
    
}
