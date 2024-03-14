package nukkitcoders.mobplugin.entities.animal.swimming;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.effect.Effect;
import cn.nukkit.entity.effect.EffectType;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import nukkitcoders.mobplugin.utils.Utils;
import org.jetbrains.annotations.NotNull;

public class Pufferfish extends Fish {

    public static final int NETWORK_ID = 108;

    private int puffed = 0;

    public Pufferfish(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public @NotNull String getIdentifier() {
        return PUFFERFISH;
    }

    @Override
    int getBucketMeta() {
        return 5;
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 0.35f;
    }

    @Override
    public float getHeight() {
        return 0.35f;
    }

    @Override
    public void initEntity() {
        super.initEntity();
        this.setMaxHealth(3);
    }

    @Override
    public Item[] getDrops() {
        return new Item[]{Item.get(Item.PUFFERFISH, 0, 1), Item.get(Item.BONE, 0, Utils.rand(0, 2))};
    }

    @Override
    public boolean attack(EntityDamageEvent ev) {
        super.attack(ev);
        
        if (ev.getCause() != DamageCause.ENTITY_ATTACK) return true;
        
        if (ev instanceof EntityDamageByEntityEvent) {            
            Entity damager = ((EntityDamageByEntityEvent) ev).getDamager();
            if (damager instanceof Player) {
                if (this.puffed > 0) return true;
                this.puffed = 200;
                damager.addEffect(Effect.get(EffectType.POISON).setDuration(200));
                this.setDataProperty(PUFFED_STATE, 2);
            }
        }

        return true;
    }

    @Override
    public boolean entityBaseTick(int tickDiff) {
        if (puffed == 0) {
            if (this.getDataProperty(PUFFED_STATE) == 2) {
                this.setDataProperty(PUFFED_STATE, 0);
            }
        }

        if (puffed > 0) {
            puffed--;
        }

        return super.entityBaseTick(tickDiff);
    }

    public boolean isPuffed() {
        return this.puffed > 0;
    }
}
