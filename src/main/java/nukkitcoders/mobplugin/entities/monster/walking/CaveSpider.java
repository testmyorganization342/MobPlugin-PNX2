package nukkitcoders.mobplugin.entities.monster.walking;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityArthropod;
import cn.nukkit.entity.effect.Effect;
import cn.nukkit.entity.effect.EffectType;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import nukkitcoders.mobplugin.utils.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CaveSpider extends Spider implements EntityArthropod {

    public static final int NETWORK_ID = 40;

    public CaveSpider(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public @NotNull String getIdentifier() {
        return CAVE_SPIDER;
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 0.7f;
    }

    @Override
    public float getHeight() {
        return 0.5f;
    }

    @Override
    public double getSpeed() {
        return 1.3;
    }

    @Override
    public void initEntity() {
        this.setMaxHealth(12);
        super.initEntity();

        this.setDamage(new float[] { 0, 2, 3, 3 });
    }

    @Override
    public void attackEntity(Entity player) {
        if (this.attackDelay > 23 && this.distanceSquared(player) < 1.32) {
            this.attackDelay = 0;
            HashMap<EntityDamageEvent.DamageModifier, Float> damage = new HashMap<>();
            damage.put(EntityDamageEvent.DamageModifier.BASE, this.getDamage());

            if (player instanceof Player) {
                float points = 0;
                for (Item i : ((Player) player).getInventory().getArmorContents()) {
                    points += this.getArmorPoints(i.getId());
                }

                damage.put(EntityDamageEvent.DamageModifier.ARMOR,
                        (float) (damage.getOrDefault(EntityDamageEvent.DamageModifier.ARMOR, 0f) - Math.floor(damage.getOrDefault(EntityDamageEvent.DamageModifier.BASE, 1f) * points * 0.04)));
            }
            EntityDamageByEntityEvent ev = new EntityDamageByEntityEvent(this, player, EntityDamageEvent.DamageCause.ENTITY_ATTACK, damage);
            if (player.attack(ev) && !ev.isCancelled() && this.server.getDifficulty() > 0) {
                player.addEffect(Effect.get(EffectType.POISON).setDuration(this.server.getDifficulty() > 1 ? 300 : 140));
            }
        }
    }

    @Override
    public Item[] getDrops() {
        List<Item> drops = new ArrayList<>();

        drops.add(Item.get(Item.STRING, 0, Utils.rand(0, 2)));
        drops.add(Item.get(Item.SPIDER_EYE, 0, Utils.rand(0, 2) == 0 ? 1 : 0));

        return drops.toArray(new Item[0]);
    }

    @Override
    public int getKillExperience() {
        return 5;
    }

    @Override
    public String getName() {
        return this.hasCustomName() ? this.getNameTag() : "Cave Spider";
    }
}
