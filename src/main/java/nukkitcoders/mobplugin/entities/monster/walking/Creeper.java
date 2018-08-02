package nukkitcoders.mobplugin.entities.monster.walking;

import cn.nukkit.block.BlockLiquid;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.entity.EntityExplosive;
import cn.nukkit.entity.data.IntEntityData;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.ExplosionPrimeEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Explosion;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.particle.HugeExplodeSeedParticle;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector2;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import nukkitcoders.mobplugin.RouteFinderThreadPool;
import nukkitcoders.mobplugin.entities.monster.WalkingMonster;
import nukkitcoders.mobplugin.route.WalkerRouteFinder;
import nukkitcoders.mobplugin.runnable.RouteFinderSearchTask;
import nukkitcoders.mobplugin.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class Creeper extends WalkingMonster implements EntityExplosive {

    public static final int NETWORK_ID = 33;

    private int bombTime = 0;

    private boolean exploded = false;

    public Creeper(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        route = new WalkerRouteFinder(this);
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
        return 1.7f;
    }

    @Override
    public double getSpeed() {
        return 0.9;
    }

    @Override
    public void initEntity() {
        super.initEntity();

        setMaxHealth(20);
    }

    public int getBombTime() {
        return bombTime;
    }

    @Override
    public void explode() {
        ExplosionPrimeEvent ev = new ExplosionPrimeEvent(this, 2.8);
        server.getPluginManager().callEvent(ev);

        if (!ev.isCancelled()) {
            Explosion explosion = new Explosion(this, (float) ev.getForce(), this);
            if (ev.isBlockBreaking()) {
                explosion.explodeA();
            }
            explosion.explodeB();
            level.addParticle(new HugeExplodeSeedParticle(this));
            exploded = true;
        }
        close();
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (server.getDifficulty() < 1) {
            close();
            return false;
        }

        if (!isAlive()) {
            if (++deadTicks >= 23) {
                close();
                return false;
            }
            return true;
        }

        int tickDiff = currentTick - lastUpdate;
        lastUpdate = currentTick;
        entityBaseTick(tickDiff);

        if (!isMovement()) {
            return true;
        }
        if(age % 10 == 0 && route!=null && !route.isSearching()) {
            RouteFinderThreadPool.executeRouteFinderThread(new RouteFinderSearchTask(route));
            if(route.hasNext()) {
                target = route.next();
            }
        }

        if (isKnockback()) {
            move(motionX * tickDiff, motionY, motionZ * tickDiff);
            motionY -= getGravity() * tickDiff;
            updateMovement();
            return true;
        }

        Vector3 before = target;
        checkTarget();

        if (followTarget != null && !followTarget.closed && followTarget.isAlive() && target!=null) {
            double x = target.x - x;
            double y = target.y - y;
            double z = target.z - z;

            double diff = Math.abs(x) + Math.abs(z);
            double distance = followTarget.distance(this);
            if (distance <= 4.5) {
                if (followTarget instanceof EntityCreature) {
                    if (bombTime >= 0) {
                        level.addSound(this, Sound.RANDOM_FUSE);
                        setDataProperty(new IntEntityData(Entity.DATA_FUSE_LENGTH,bombTime));
                        setDataFlag(DATA_FLAGS, DATA_FLAG_IGNITED, true);
                    }
                    bombTime += tickDiff;
                    if (bombTime >= 64) {
                        explode();
                        return false;
                    }
                } else if (Math.pow(x - target.x, 2) + Math.pow(z - target.z, 2) <= 1) {
                    moveTime = 0;
                }
            } else {
                bombTime -= tickDiff;
                if (bombTime < 0) {
                    bombTime = 0;
                    setDataFlag(DATA_FLAGS, DATA_FLAG_IGNITED, false);
                }

                motionX = getSpeed() * 0.15 * (x / diff);
                motionZ = getSpeed() * 0.15 * (z / diff);
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
                if (!(level.getBlock(new Vector3(NukkitMath.floorDouble(x), (int) (y + 0.8), NukkitMath.floorDouble(z))) instanceof BlockLiquid)) {
                    motionY -= getGravity() * 1;
                }
            } else {
                motionY -= getGravity() * tickDiff;
            }
        }
        updateMovement();
        if(route != null){
            if(route.hasCurrentNode() && route.hasArrivedNode(this)) {
                target = null;
                if (route.hasNext()) {
                    target = route.next();
                }
            }
        }
        return true;
    }

    @Override
    public Vector3 updateMove(int tickDiff) {
        return null;
    }

    public void attackEntity(Entity player) {
        // creepers don't attack, they only explode
    }

    @Override
    public Item[] getDrops() {
        List<Item> drops = new ArrayList<>();
        if (lastDamageCause instanceof EntityDamageByEntityEvent) {
            int gunPowder = Utils.rand(0, 3); // drops 0-2 gunpowder
            for (int i = 0; i < gunPowder; i++) {
                drops.add(Item.get(Item.GUNPOWDER, 0, 1));
            }
        }
        return drops.toArray(new Item[drops.size()]);
    }

    @Override
    public int getKillExperience() {
        return 5; // gain 5 experience
    }

    public int getMaxFallHeight() {
        return followTarget == null ? 3 : 3 + (int) (getHealth() - 1.0F); //TODO: change this to attack target only
    }

}
