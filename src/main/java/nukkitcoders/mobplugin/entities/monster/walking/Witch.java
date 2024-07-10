package nukkitcoders.mobplugin.entities.monster.walking;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.entity.effect.EffectType;
import cn.nukkit.entity.effect.PotionType;
import cn.nukkit.entity.item.EntitySplashPotion;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.ProjectileLaunchEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Location;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import nukkitcoders.mobplugin.entities.monster.WalkingMonster;
import nukkitcoders.mobplugin.utils.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Witch extends WalkingMonster {

    public static final int NETWORK_ID = 45;

    public Witch(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public @NotNull String getIdentifier() {
        return WITCH;
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 0.6f;
    }

    @Override
    public float getHeight() {
        return 1.95f;
    }

    @Override
    protected void initEntity() {
        this.setMaxHealth(26);
        super.initEntity();
    }

    @Override
    public boolean targetOption(EntityCreature creature, double distance) {
        if (creature instanceof Player) {
            Player player = (Player) creature;
            return !player.closed && player.spawned && player.isAlive() && (player.isSurvival() || player.isAdventure()) && distance <= 100;
        }
        return creature.isAlive() && !creature.closed && distance <= 256;
    }

    @Override
    public boolean attack(EntityDamageEvent ev) {
        super.attack(ev);
        return true;
    }

    @Override
    public void attackEntity(Entity player) {
        if (this.attackDelay > 60 && Utils.rand(1, 3) == 2 && this.distanceSquared(player) <= 100) {
            this.attackDelay = 0;
            if (player.isAlive() && !player.closed) {
                double f = 1.5;

                Vector3 direction = player.getPosition().subtract(this.getPosition()).normalize();

                Location pos = new Location(
                        this.x + direction.x * 1.5,
                        this.y + this.getEyeHeight(),
                        this.z + direction.z * 1.5,
                        0,
                        0,
                        this.level
                );

                EntitySplashPotion thrownPotion = (EntitySplashPotion) Entity.createEntity("minecraft:splash_potion", pos, this);

                double distance = this.distanceSquared(player);
                if (!player.hasEffect(EffectType.SLOWNESS) && distance <= 64) {
                    thrownPotion.potionId = PotionType.SLOWNESS.id();
                } else if (player.getHealth() >= 8) {
                    thrownPotion.potionId = PotionType.POISON.id();
                } else if (!player.hasEffect(EffectType.WEAKNESS) && Utils.rand(0, 4) == 0 && distance <= 9) {
                    thrownPotion.potionId = PotionType.WEAKNESS.id();
                } else {
                    thrownPotion.potionId = PotionType.HARMING.id();
                }

                thrownPotion.setMotion(direction.multiply(f));

                ProjectileLaunchEvent launch = new ProjectileLaunchEvent(thrownPotion, this);
                this.server.getPluginManager().callEvent(launch);

                if (!launch.isCancelled()) {
                    thrownPotion.spawnToAll();
                    this.level.addSound(this, Sound.MOB_WITCH_THROW);
                } else {
                    thrownPotion.close();
                }
            }
        }
    }

    @Override
    public Item[] getDrops() {
        List<Item> drops = new ArrayList<>();

        if (Utils.rand(1, 4) == 1) {
            drops.add(Item.get(Item.STICK, 0, Utils.rand(0, 2)));
        }

        if (Utils.rand(1, 3) == 1) {
            switch (Utils.rand(1, 6)) {
                case 1:
                    drops.add(Item.get(Item.GLASS_BOTTLE, 0, Utils.rand(0, 2)));
                    break;
                case 2:
                    drops.add(Item.get(Item.GLOWSTONE_DUST, 0, Utils.rand(0, 2)));
                    break;
                case 3:
                    drops.add(Item.get(Item.GUNPOWDER, 0, Utils.rand(0, 2)));
                    break;
                case 4:
                    drops.add(Item.get(Item.REDSTONE, 0, Utils.rand(0, 2)));
                    break;
                case 5:
                    drops.add(Item.get(Item.SPIDER_EYE, 0, Utils.rand(0, 2)));
                    break;
                case 6:
                    drops.add(Item.get(Item.SUGAR, 0, Utils.rand(0, 2)));
                    break;
            }
        }

        return drops.toArray(new Item[0]);
    }

    @Override
    public int getKillExperience() {
        return 5;
    }

    @Override
    public int nearbyDistanceMultiplier() {
        return 8;
    }
}
