package nukkitcoders.mobplugin.entities.monster.flying;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.entity.data.EntityFlag;
import cn.nukkit.event.entity.ProjectileLaunchEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Location;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.LevelEventPacket;
import nukkitcoders.mobplugin.entities.monster.FlyingMonster;
import nukkitcoders.mobplugin.entities.projectile.EntityBlazeFireBall;
import nukkitcoders.mobplugin.utils.FastMathLite;
import nukkitcoders.mobplugin.utils.Utils;
import org.jetbrains.annotations.NotNull;

public class Blaze extends FlyingMonster {

    public static final int NETWORK_ID = 43;

    private int fireball;

    public Blaze(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public @NotNull String getIdentifier() {
        return BLAZE;
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
        return 1.8f;
    }

    @Override
    public void initEntity() {
        this.setMaxHealth(20);
        super.initEntity();

        this.fireProof = true;
    }

    @Override
    public boolean targetOption(EntityCreature creature, double distance) {
        if (creature instanceof Player) {
            Player player = (Player) creature;
            return !player.closed && player.spawned && player.isAlive() && (player.isSurvival() || player.isAdventure()) && distance <= 2304; // 48 blocks
        }
        return false;
    }

    @Override
    public void attackEntity(Entity player) {
        if (this.attackDelay == 60 && this.fireball == 0) {
            this.setDataFlag(EntityFlag.CHARGED, true);
        }
        if (((this.fireball > 0 && this.fireball < 3 && this.attackDelay > 5) || (this.attackDelay > 120 && Utils.rand(1, 32) < 4)) && this.distanceSquared(player) <= 256) { // 16 blocks
            this.attackDelay = 0;
            this.fireball++;

            if (this.fireball == 3) {
                this.fireball = 0;
                this.setDataFlag(EntityFlag.CHARGED, false);
            }

            double f = 1.1;
            double yaw = this.yaw + Utils.rand(-4.0, 4.0);
            double pitch = this.pitch + Utils.rand(-4.0, 4.0);
            Location pos = new Location(this.x - Math.sin(FastMathLite.toRadians(yaw)) * Math.cos(FastMathLite.toRadians(pitch)) * 0.5, this.y + this.getEyeHeight(),
                    this.z + Math.cos(FastMathLite.toRadians(yaw)) * Math.cos(FastMathLite.toRadians(pitch)) * 0.5, yaw, pitch, this.level);

            if (this.getLevel().getBlockIdAt(pos.getFloorX(), pos.getFloorY(), pos.getFloorZ()) != Block.AIR) {
                return;
            }

            EntityBlazeFireBall fireball = (EntityBlazeFireBall) Entity.createEntity("BlazeFireBall", pos, this);

            fireball.setMotion(new Vector3(-Math.sin(FastMathLite.toRadians(yaw)) * Math.cos(FastMathLite.toRadians(pitch)) * f * f, -Math.sin(FastMathLite.toRadians(pitch)) * f * f,
                    Math.cos(FastMathLite.toRadians(yaw)) * Math.cos(FastMathLite.toRadians(pitch)) * f * f));

            ProjectileLaunchEvent launch = new ProjectileLaunchEvent(fireball, this);
            this.server.getPluginManager().callEvent(launch);
            if (launch.isCancelled()) {
                fireball.close();
            } else {
                fireball.spawnToAll();
                this.level.addLevelEvent(this, LevelEventPacket.EVENT_SOUND_BLAZE_FIREBALL);
            }
        }
    }

    @Override
    public Item[] getDrops() {
        return new Item[]{Item.get(Item.BLAZE_ROD, 0, Utils.rand(0, 1))};
    }

    @Override
    public int getKillExperience() {
        return 10;
    }

    @Override
    public int nearbyDistanceMultiplier() {
        return 1000; // don't follow
    }
}
