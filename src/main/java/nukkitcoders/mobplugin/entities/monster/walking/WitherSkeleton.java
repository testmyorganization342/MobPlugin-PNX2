package nukkitcoders.mobplugin.entities.monster.walking;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntitySmite;
import cn.nukkit.entity.effect.Effect;
import cn.nukkit.entity.effect.EffectType;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemStoneSword;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.MobEquipmentPacket;
import nukkitcoders.mobplugin.entities.monster.WalkingMonster;
import nukkitcoders.mobplugin.utils.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WitherSkeleton extends WalkingMonster implements EntitySmite {

    public static final int NETWORK_ID = 48;

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    public WitherSkeleton(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public @NotNull String getIdentifier() {
        return WITHER_SKELETON;
    }

    @Override
    public String getName() {
        return this.hasCustomName() ? this.getNameTag() : "Wither Skeleton";
    }

    @Override
    protected void initEntity() {
        this.setMaxHealth(20);
        super.initEntity();

        this.fireProof = true;
        this.setDamage(new float[] { 0, 3, 4, 6 });
    }

    @Override
    public float getWidth() {
        return 0.7f;
    }

    @Override
    public float getHeight() {
        return 2.4f;
    }

    @Override
    public void attackEntity(Entity player) {
        if (this.attackDelay > 23 && player.distanceSquared(this) <= 1) {
            this.attackDelay = 0;
            HashMap<EntityDamageEvent.DamageModifier, Float> damage = new HashMap<>();
            damage.put(EntityDamageEvent.DamageModifier.BASE, this.getDamage());
            if (player instanceof Player) {
                float points = 0;
                for (Item i : ((Player) player).getInventory().getArmorContents()) {
                    points += this.getArmorPoints(i.getId());
                }
                damage.put(EntityDamageEvent.DamageModifier.ARMOR, (float) (damage.getOrDefault(EntityDamageEvent.DamageModifier.ARMOR, 0f) - Math.floor(damage.getOrDefault(EntityDamageEvent.DamageModifier.BASE, 1f) * points * 0.04)));
            }
            if (player.attack(new EntityDamageByEntityEvent(this, player, EntityDamageEvent.DamageCause.ENTITY_ATTACK, damage))) {
                this.playAttack();
                player.addEffect(Effect.get(EffectType.WITHER).setDuration(200));
            }
        }
    }

    @Override
    public void spawnTo(Player player) {
        super.spawnTo(player);

        MobEquipmentPacket pk = new MobEquipmentPacket();
        pk.eid = this.getId();
        pk.item = new ItemStoneSword();
        pk.hotbarSlot = 0;
        player.dataPacket(pk);
    }

    @Override
    public Item[] getDrops() {
        List<Item> drops = new ArrayList<>();
        
        drops.add(Item.get(Item.BONE, 0, Utils.rand(0, 2)));

        if (Utils.rand(1, 3) == 1) {
            drops.add(Item.get(Item.COAL, 0, 1));
        }

        if (Utils.rand(1, 40) == 1) {
            drops.add(Item.get("minecraft:skull", 1, 1));
        }

        if (Utils.rand(1, 200) <= 17) {
            drops.add(Item.get(Item.STONE_SWORD, Utils.rand(0, 131), 1));
        }

        return drops.toArray(new Item[0]);
    }

    @Override
    public int getKillExperience() {
        return 5;
    }
}
