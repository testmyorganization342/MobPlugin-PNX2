package nukkitcoders.mobplugin.entities.monster.flying;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockFence;
import cn.nukkit.block.BlockFenceGate;
import cn.nukkit.block.BlockLiquid;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.ProjectileLaunchEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Location;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector2;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import nukkitcoders.mobplugin.MobPlugin;
import nukkitcoders.mobplugin.entities.BaseEntity;
import nukkitcoders.mobplugin.entities.animal.Animal;
import nukkitcoders.mobplugin.entities.monster.FlyingMonster;
import nukkitcoders.mobplugin.entities.projectile.EntityFireBall;
import nukkitcoders.mobplugin.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class Blaze extends FlyingMonster {

    public static final int NETWORK_ID = 43;

    public Blaze(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
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
    public float getGravity() {
        return 0.04f;
    }

    public void initEntity() {
        super.initEntity();

        fireProof = true;
        setDamage(new float[]{0, 0, 0, 0});
    }

    protected void checkTarget() {
        if (isKnockback()) {
            return;
        }

        if (followTarget != null && !followTarget.closed && followTarget.isAlive()) {
            return;
        }

        Vector3 target = target;
        if (!(target instanceof EntityCreature) || !targetOption((EntityCreature) target, distanceSquared(target))) {
            double near = Integer.MAX_VALUE;

            for (Entity entity : getLevel().getEntities()) {
                if (entity == this || !(entity instanceof EntityCreature) || entity instanceof Animal) {
                    continue;
                }

                EntityCreature creature = (EntityCreature) entity;
                if (creature instanceof BaseEntity && ((BaseEntity) creature).isFriendly() == isFriendly()) {
                    continue;
                }

                double distance = distanceSquared(creature);
                if (distance > near || !targetOption(creature, distance)) {
                    continue;
                }
                near = distance;

                moveTime = 0;
                target = creature;
            }
        }

        if (target instanceof EntityCreature && ((EntityCreature) target).isAlive()) {
            return;
        }

        int x, z;
        if (stayTime > 0) {
            if (Utils.rand(1, 100) > 5) {
                return;
            }
            x = Utils.rand(10, 30);
            z = Utils.rand(10, 30);
            target = add(Utils.rand() ? x : -x, Utils.rand(-20, 20) / 10, Utils.rand() ? z : -z);
        } else if (Utils.rand(1, 410) == 1) {
            x = Utils.rand(10, 30);
            z = Utils.rand(10, 30);
            stayTime = Utils.rand(90, 400);
            target = add(Utils.rand() ? x : -x, Utils.rand(-20, 20) / 10, Utils.rand() ? z : -z);
        } else if (moveTime <= 0 || target == null) {
            x = Utils.rand(20, 100);
            z = Utils.rand(20, 100);
            stayTime = 0;
            moveTime = Utils.rand(300, 1200);
            target = add(Utils.rand() ? x : -x, 0, Utils.rand() ? z : -z);
        }
    }

    protected boolean checkJump(double dx, double dz) {
        if (motionY == getGravity() * 2) {
            return level.getBlock(new Vector3(NukkitMath.floorDouble(x), (int) y, NukkitMath.floorDouble(z))) instanceof BlockLiquid;
        } else {
            if (level.getBlock(new Vector3(NukkitMath.floorDouble(x), (int) (y + 0.8), NukkitMath.floorDouble(z))) instanceof BlockLiquid) {
                motionY = getGravity() * 2;
                return true;
            }
        }

        if (!onGround || stayTime > 0) {
            return false;
        }

        Block that = getLevel().getBlock(new Vector3(NukkitMath.floorDouble(x + dx), (int) y, NukkitMath.floorDouble(z + dz)));
        if (getDirection() == null) {
            return false;
        }

        Block block = that.getSide(getDirection());
        if (!block.canPassThrough() && block.getSide(BlockFace.UP).canPassThrough() && that.getSide(BlockFace.UP, 2).canPassThrough()) {
            if (block instanceof BlockFence || block instanceof BlockFenceGate) {
                motionY = getGravity();
            } else if (motionY <= getGravity() * 4) {
                motionY = getGravity() * 4;
            } else {
                motionY += getGravity() * 0.25;
            }
            return true;
        }
        return false;
    }

    @Override
    public Vector3 updateMove(int tickDiff) {
        if (!isMovement()) {
            return null;
        }

        if (isKnockback()) {
            move(motionX * tickDiff, motionY * tickDiff, motionZ * tickDiff);
            updateMovement();
            return null;
        }

        if (followTarget != null && !followTarget.closed && followTarget.isAlive()) {
            double x = followTarget.x - x;
            double y = followTarget.y - y;
            double z = followTarget.z - z;

            double diff = Math.abs(x) + Math.abs(z);
            if (stayTime > 0 || distance(followTarget) <= (getWidth() + 0.0d) / 2 + 0.05) {
                motionX = 0;
                motionZ = 0;
            } else {
                motionX = getSpeed() * 0.15 * (x / diff);
                motionZ = getSpeed() * 0.15 * (z / diff);
            }
            yaw = Math.toDegrees(-Math.atan2(x / diff, z / diff));
            pitch = y == 0 ? 0 : Math.toDegrees(-Math.atan2(y, Math.sqrt(x * x + z * z)));
            return followTarget;
        }

        Vector3 before = target;
        checkTarget();
        if (target instanceof EntityCreature || before != target) {
            double x = target.x - x;
            double y = target.y - y;
            double z = target.z - z;

            double diff = Math.abs(x) + Math.abs(z);
            double distance = distance(target);
            if (distance <= (getWidth() + 0.0d) / 2 + 0.05) {
                motionX = 0;
                motionZ = 0;
            } else {
                if (target instanceof EntityCreature) {
                    motionX = 0;
                    motionZ = 0;
                    if (distance > y - getLevel().getHighestBlockAt((int) x, (int) z)) {
                        motionY = getGravity();
                    } else {
                        motionY = 0;
                    }
                } else {
                    motionX = getSpeed() * 0.15 * (x / diff);
                    motionZ = getSpeed() * 0.15 * (z / diff);
                }
            }
            yaw = Math.toDegrees(-Math.atan2(x / diff, z / diff));
            pitch = y == 0 ? 0 : Math.toDegrees(-Math.atan2(y, Math.sqrt(x * x + z * z)));
        }

        double dx = motionX * tickDiff;
        double dz = motionZ * tickDiff;
        boolean isJump = checkJump(dx, dz);
        if (stayTime > 0) {
            stayTime -= tickDiff;
            move(0, motionY * tickDiff, 0);
        } else {
            Vector2 be = new Vector2(x + dx, z + dz);
            move(dx, motionY * tickDiff, dz);
            Vector2 af = new Vector2(x, z);

            if ((be.x != af.x || be.y != af.y) && !isJump) {
                moveTime -= 90 * tickDiff;
            }
        }

        if (!isJump) {
            if (onGround) {
                motionY = 0;
            } else if (motionY > -getGravity() * 4) {
                motionY = -getGravity() * 4;
            } else {
                motionY -= getGravity() * tickDiff;
            }
        }
        updateMovement();
        return target;
    }

    public void attackEntity(Entity player) {
        if (attackDelay > 20 && Utils.rand(1, 32) < 4 && distance(player) <= 18) {
            attackDelay = 0;

            double f = 1.2;
            double yaw = yaw + Utils.rand(-150, 150) / 10;
            double pitch = pitch + Utils.rand(-75, 75) / 10;
            Location pos = new Location(x - Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * 0.5, y + getEyeHeight(),
                    z + Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * 0.5, yaw, pitch, level);
            Entity k = MobPlugin.create("FireBall", pos, this);
            if (!(k instanceof EntityFireBall)) {
                return;
            }

            EntityFireBall fireball = (EntityFireBall) k;
            fireball.setExplode(true);
            fireball.setMotion(new Vector3(-Math.sin(Math.toDegrees(yaw)) * Math.cos(Math.toDegrees(pitch)) * f * f, -Math.sin(Math.toDegrees(pitch)) * f * f,
                    Math.cos(Math.toDegrees(yaw)) * Math.cos(Math.toDegrees(pitch)) * f * f));

            ProjectileLaunchEvent launch = new ProjectileLaunchEvent(fireball);
            server.getPluginManager().callEvent(launch);
            if (launch.isCancelled()) {
                fireball.kill();
            } else {
                fireball.spawnToAll();
                level.addSound(this, Sound.MOB_BLAZE_SHOOT);
            }
        }
    }

    @Override
    public Item[] getDrops() {
        List<Item> drops = new ArrayList<>();
        if (lastDamageCause instanceof EntityDamageByEntityEvent) {
            int blazeRod = Utils.rand(0, 2); // drops 0-1 blaze rod
            int glowStoneDust = Utils.rand(0, 3); // drops 0-2 glowstone dust
            for (int i = 0; i < blazeRod; i++) {
                drops.add(Item.get(Item.BLAZE_ROD, 0, 1));
            }
            for (int i = 0; i < glowStoneDust; i++) {
                drops.add(Item.get(Item.GLOWSTONE_DUST, 0, 1));
            }
        }
        return drops.toArray(new Item[drops.size()]);
    }

    @Override
    public int getKillExperience() {
        return 10; // gain 10 experience
    }

}
