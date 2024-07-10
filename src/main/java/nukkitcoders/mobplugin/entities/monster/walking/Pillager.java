package nukkitcoders.mobplugin.entities.monster.walking;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.entity.data.EntityFlag;
import cn.nukkit.entity.projectile.EntityArrow;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.event.entity.EntityShootBowEvent;
import cn.nukkit.event.entity.ProjectileLaunchEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Location;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.network.protocol.MobEquipmentPacket;
import nukkitcoders.mobplugin.entities.monster.WalkingMonster;
import nukkitcoders.mobplugin.utils.FastMathLite;
import nukkitcoders.mobplugin.utils.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Pillager extends WalkingMonster {

    public static final int NETWORK_ID = 114;

    private boolean angryFlagSet;

    public Pillager(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public @NotNull String getIdentifier() {
        return PILLAGER;
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
    public void initEntity() {
        this.setMaxHealth(24);
        super.initEntity();
    }

    @Override
    public void attackEntity(Entity player) {
        if (this.attackDelay > 80 && Utils.rand(1, 32) < 4 && this.distanceSquared(player) <= 100) {
            this.attackDelay = 0;

            double f = 1.5;
            double yaw = this.yaw;
            double pitch = this.pitch;
            double yawR = FastMathLite.toRadians(yaw);
            double pitchR = FastMathLite.toRadians(pitch);
            Location pos = new Location(this.x - Math.sin(yawR) * Math.cos(pitchR) * 0.5, this.y + this.getHeight() - 0.18,
                    this.z + Math.cos(yawR) * Math.cos(pitchR) * 0.5, yaw, pitch, this.level);

            if (this.getLevel().getBlockIdAt(pos.getFloorX(), pos.getFloorY(), pos.getFloorZ()) == Block.AIR) {
                Entity k = Entity.createEntity("Arrow", pos, this);
                if (!(k instanceof EntityArrow)) {
                    return;
                }

                EntityArrow arrow = (EntityArrow) k;
                setProjectileMotion(arrow, pitch, yawR, pitchR, f);

                EntityShootBowEvent ev = new EntityShootBowEvent(this, Item.get(Item.ARROW, 0, 1), arrow, f);
                this.server.getPluginManager().callEvent(ev);

                EntityProjectile projectile = ev.getProjectile();
                if (ev.isCancelled()) {
                    projectile.close();
                } else {
                    ProjectileLaunchEvent launch = new ProjectileLaunchEvent(projectile, this);
                    this.server.getPluginManager().callEvent(launch);
                    if (launch.isCancelled()) {
                        projectile.close();
                    } else {
                        projectile.namedTag.putDouble("damage", 4);
                        projectile.spawnToAll();
                        ((EntityArrow) projectile).setPickupMode(EntityArrow.PICKUP_NONE);
                        this.level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_CROSSBOW_SHOOT);
                    }
                }
            }
        }
    }

    @Override
    public Item[] getDrops() {
        List<Item> drops = new ArrayList<>();

        drops.add(Item.get(Item.ARROW, 0, Utils.rand(0, 2)));

        if (Utils.rand(1, 12) == 1) {
            drops.add(Item.get(Item.CROSSBOW, Utils.rand(300, 380), Utils.rand(0, 1)));
        }

        return drops.toArray(new Item[0]);
    }

    @Override
    public int getKillExperience() {
        return 5;
    }

    @Override
    public void spawnTo(Player player) {
        super.spawnTo(player);

        MobEquipmentPacket pk = new MobEquipmentPacket();
        pk.eid = this.getId();
        pk.item = Item.get(Item.CROSSBOW, 0, 1);
        pk.hotbarSlot = 0;
        player.dataPacket(pk);
    }

    @Override
    public int nearbyDistanceMultiplier() {
        return 20;
    }

    @Override
    public boolean targetOption(EntityCreature creature, double distance) {
        boolean hasTarget = super.targetOption(creature, distance);
        if (hasTarget) {
            if (!this.angryFlagSet) {
                this.setDataFlag(EntityFlag.CHARGED, true);
                this.angryFlagSet = true;
            }
        } else {
            if (this.angryFlagSet) {
                this.setDataFlag(EntityFlag.CHARGED, false);
                this.angryFlagSet = false;
                this.stayTime = 100;
            }
        }
        return hasTarget;
    }
}
