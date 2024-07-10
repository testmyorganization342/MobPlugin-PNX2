package nukkitcoders.mobplugin.entities.monster.walking;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.BlockID;
import cn.nukkit.entity.Attribute;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.entity.passive.EntityWolf;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.UpdateAttributesPacket;
import nukkitcoders.mobplugin.entities.monster.WalkingMonster;
import nukkitcoders.mobplugin.utils.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class IronGolem extends WalkingMonster {

    public static final int NETWORK_ID = 20;

    public IronGolem(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        this.setFriendly(true);
    }

    @Override
    public @NotNull String getIdentifier() {
        return IRON_GOLEM;
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 1.4f;
    }

    @Override
    public float getHeight() {
        return 2.9f;
    }

    @Override
    public double getSpeed() {
        return 0.7;
    }

    @Override
    public void initEntity() {
        this.setMaxHealth(100);
        super.initEntity();
        this.noFallDamage = true;

        this.setDamage(new float[] { 0, 11, 21, 31 });
        this.setMinDamage(new float[] { 0, 4, 7, 11 });
    }

    public void attackEntity(Entity player) {
        if (this.attackDelay > 40 && this.distanceSquared(player) < 4) {
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
            player.attack(new EntityDamageByEntityEvent(this, player, EntityDamageEvent.DamageCause.ENTITY_ATTACK, damage));
            this.playAttack();
        }
    }

    public boolean targetOption(EntityCreature creature, double distance) {
        return (!(creature instanceof Player) || creature.getId() == this.isAngryTo) && !(creature instanceof EntityWolf) && creature.isAlive() && distance <= 100;
    }

    @Override
    public Item[] getDrops() {
        List<Item> drops = new ArrayList<>();
        drops.add(Item.get(Item.IRON_INGOT, 0, Utils.rand(3, 5)));
        int c2 = Utils.rand(0, 2);
        if (c2 > 0) drops.add(Item.get(Item.RED_FLOWER, 0, c2));
        return drops.toArray(new Item[0]);
    }

    @Override
    public int getKillExperience() {
        return 0;
    }

    @Override
    public String getName() {
        return this.hasCustomName() ? this.getNameTag() : "Iron Golem";
    }

    @Override
    public boolean canDespawn() {
        return false;
    }

    @Override
    public boolean attack(EntityDamageEvent ev) {
        if (super.attack(ev)) {
            if (ev instanceof EntityDamageByEntityEvent) {
                this.isAngryTo = ((EntityDamageByEntityEvent) ev).getDamager().getId();
            }
            return true;
        }

        return false;
    }

    @Override
    public boolean canTarget(Entity entity) {
        return entity.getId() == this.isAngryTo;
    }

    @Override
    public void spawnTo(Player player) {
        super.spawnTo(player);

        this.sendHealth();
    }

    @Override
    public void setHealth(float health) {
        super.setHealth(health);

        this.sendHealth();
    }

    private void sendHealth() {
        if (this.isAlive()) {
            UpdateAttributesPacket pk = new UpdateAttributesPacket();
            int max = this.getMaxHealth();
            pk.entries = new Attribute[]{Attribute.getAttribute(Attribute.MAX_HEALTH).setMaxValue(max).setValue(this.health < max ? this.health : max)};
            pk.entityId = this.id;
            Server.broadcastPacket(this.getViewers().values(), pk);
        }
    }
}
