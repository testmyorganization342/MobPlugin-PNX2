package nukkitcoders.mobplugin.entities;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityMotionEvent;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.AddEntityPacket;
import cn.nukkit.potion.Effect;
import co.aikar.timings.Timings;
import nukkitcoders.mobplugin.MobPlugin;
import nukkitcoders.mobplugin.entities.monster.Monster;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class BaseEntity extends EntityCreature {

    protected int stayTime = 0;

    protected int moveTime = 0;

    public double moveMultifier = 1.0d;

    protected Vector3 target = null;

    protected Entity followTarget = null;

    public boolean inWater = false;

    public boolean inLava = false;

    public boolean onClimbable = false;

    protected boolean fireProof = false;

    private boolean movement = true;

    private boolean friendly = false;

    private boolean wallcheck = true;

    protected List<Block> blocksAround = new ArrayList<>();

    protected List<Block> collisionBlocks = new ArrayList<>();
    
    private boolean despawnEntities;
    
    private int despawnTicks;

    private int maxJumpHeight = 1; // default: 1 block jump height - this should be 2 for horses e.g.
    protected boolean isJumping;
    public float jumpMovementFactor = 0.02F;

    public BaseEntity(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        
        despawnEntities = MobPlugin.getInstance().getConfig().getBoolean("entities.despawn-entities", true);
        despawnTicks = MobPlugin.getInstance().getConfig().getInt("entities.despawn-ticks", 12000);
    }

    public abstract Vector3 updateMove(int tickDiff);

    public abstract int getKillExperience();

    public boolean isFriendly() {
        return friendly;
    }

    public boolean isMovement() {
        return movement;
    }

    public boolean isKnockback() {
        return attackTime > 0;
    }

    public boolean isWallCheck() {
        return wallcheck;
    }

    public void setFriendly(boolean bool) {
        friendly = bool;
    }

    public void setMovement(boolean value) {
        movement = value;
    }

    public void setWallCheck(boolean value) {
        wallcheck = value;
    }

    public double getSpeed() {
        return 1;
    }

    public int getMaxJumpHeight() {
        return maxJumpHeight;
    }

    public int getAge() {
        return age;
    }

    public Vector3 getTarget(){
        return target;
    }

    public void setTarget(Vector3 vec){
        target = vec;
    }

    public Entity getFollowTarget() {
        return followTarget != null ? followTarget : (target instanceof Entity ? (Entity) target : null);
    }

    public void setFollowTarget(Entity target) {
        followTarget = target;

        moveTime = 0;
        stayTime = 0;
        target = null;
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        if (namedTag.contains("Movement")) {
            setMovement(namedTag.getBoolean("Movement"));
        }

        if (namedTag.contains("WallCheck")) {
            setWallCheck(namedTag.getBoolean("WallCheck"));
        }

        if (namedTag.contains("Age")) {
            age = namedTag.getShort("Age");
        }

        //setDataProperty(new ByteEntityData(DATA_FLAG_NO_AI, (byte) 1));
    }

    @Override
    public String getName(){
        return getClass().getSimpleName();
    }

    public void saveNBT() {
        super.saveNBT();
        namedTag.putBoolean("Movement", isMovement());
        namedTag.putBoolean("WallCheck", isWallCheck());
        namedTag.putShort("Age", age);
    }

    @Override
    public void spawnTo(Player player) {
        if (!hasSpawned.containsKey(player.getLoaderId()) && player.usedChunks.containsKey(Level.chunkHash(chunk.getX(), chunk.getZ()))) {
            AddEntityPacket pk = new AddEntityPacket();
            pk.entityRuntimeId = getId();
            pk.entityUniqueId = getId();
            pk.type = getNetworkId();
            pk.x = (float) x;
            pk.y = (float) y;
            pk.z = (float) z;
            pk.speedX = pk.speedY = pk.speedZ = 0;
            pk.yaw = (float) yaw;
            pk.pitch = (float) pitch;
            pk.metadata = dataProperties;

            player.dataPacket(pk);

            hasSpawned.put(player.getLoaderId(), player);
        }
    }

    @Override
    protected void updateMovement() {
        if (lastX != x || lastY != y || lastZ != z || lastYaw != yaw || lastPitch != pitch) {
            lastX = x;
            lastY = y;
            lastZ = z;
            lastYaw = yaw;
            lastPitch = pitch;

            addMovement(x, y, z, yaw, pitch, yaw);
        }
    }

    public boolean targetOption(EntityCreature creature, double distance) {
        if (this instanceof Monster) {
            if (creature instanceof Player) {
                Player player = (Player) creature;
                return (!player.closed) && player.spawned && player.isAlive() && player.isSurvival() && distance <= 80;
            }
            return creature.isAlive() && (!creature.closed) && distance <= 81;
        }
        return false;
    }

    /*@Override
    public List<Block> getBlocksAround() {
        if (blocksAround == null) {
            int minX = NukkitMath.floorDouble(boundingBox.getMinX());
            int minY = NukkitMath.floorDouble(boundingBox.getMinY());
            int minZ = NukkitMath.floorDouble(boundingBox.getMinZ());
            int maxX = NukkitMath.ceilDouble(boundingBox.getMaxX());
            int maxY = NukkitMath.ceilDouble(boundingBox.getMaxY());
            int maxZ = NukkitMath.ceilDouble(boundingBox.getMaxZ());

            blocksAround = new ArrayList<>();

            for (int z = minZ; z <= maxZ; ++z) {
                for (int x = minX; x <= maxX; ++x) {
                    for (int y = minY; y <= maxY; ++y) {
                        Block block = level.getBlock(temporalVector.setComponents(x, y, z));
                        blocksAround.add(block);
                    }
                }
            }
        }

        return blocksAround;
    }*/

    @Override
    protected void checkBlockCollision() {
        Vector3 vector = new Vector3(0.0D, 0.0D, 0.0D);
        Iterator<Block> d = getBlocksAround().iterator();

        inWater = false;
        inLava = false;
        onClimbable = false;

        while (d.hasNext()) {
            Block block = d.next();

            if (block.hasEntityCollision()) {
                block.onEntityCollide(this);
                block.addVelocityToEntity(this, vector);
            }

            if (block.getId() == Block.WATER || block.getId() == Block.STILL_WATER) {
                inWater = true;
            } else if (block.getId() == Block.LAVA || block.getId() == Block.STILL_LAVA) {
                inLava = true;
            } else if (block.getId() == Block.LADDER || block.getId() == Block.VINE) {
                onClimbable = true;
            }
        }

        if (vector.lengthSquared() > 0.0D) {
            vector = vector.normalize();
            double d1 = 0.014D;
            motionX += vector.x * d1;
            motionY += vector.y * d1;
            motionZ += vector.z * d1;
        }
    }

    /*@Override
    public boolean entityBaseTick(int tickDiff) {

        Timings.entityMoveTimer.startTiming();

        if (despawnEntities && age > despawnTicks) {
            close();
            return true;
        }

        boolean hasUpdate = false;

        blocksAround = null;
        justCreated = false;

        if (!effects.isEmpty()) {
            for (Effect effect : effects.values()) {
                if (effect.canTick()) {
                    effect.applyEffect(this);
                }
                effect.setDuration(effect.getDuration() - tickDiff);

                if (effect.getDuration() <= 0) {
                    removeEffect(effect.getId());
                }
            }
        }

        checkBlockCollision();

        if (isInsideOfSolid()) {
            hasUpdate = true;
            attack(new EntityDamageEvent(this, EntityDamageEvent.DamageCause.SUFFOCATION, 1));
        }

        if (y <= -16 && isAlive()) {
            hasUpdate = true;
            attack(new EntityDamageEvent(this, EntityDamageEvent.DamageCause.VOID, 10));
        }


        if (fireTicks > 0) {
            if (fireProof) {
                fireTicks -= 4 * tickDiff;
            } else {
                if (!hasEffect(Effect.FIRE_RESISTANCE) && (fireTicks % 20) == 0 || tickDiff > 20) {
                    EntityDamageEvent ev = new EntityDamageEvent(this, EntityDamageEvent.DamageCause.FIRE_TICK, 1);
                    attack(ev);
                }
                fireTicks -= tickDiff;
            }

            if (fireTicks <= 0) {
                extinguish();
            } else {
                setDataFlag(DATA_FLAGS, DATA_FLAG_ONFIRE, true);
                hasUpdate = true;
            }
        }

        if (moveTime > 0) {
            moveTime -= tickDiff;
        }

        if (attackTime > 0) {
            attackTime -= tickDiff;
        }

        if (noDamageTicks > 0) {
            noDamageTicks -= tickDiff;
            if (noDamageTicks < 0) {
                noDamageTicks = 0;
            }
        }

        age += tickDiff;
        ticksLived += tickDiff;

        Timings.entityMoveTimer.stopTiming();

        return hasUpdate;
    }*/

    @Override
    public boolean isInsideOfSolid() {
        Block block = level.getBlock(temporalVector.setComponents(NukkitMath.floorDouble(x), NukkitMath.floorDouble(y + getHeight() - 0.18f), NukkitMath.floorDouble(z)));
        AxisAlignedBB bb = block.getBoundingBox();
        return bb != null && block.isSolid() && !block.isTransparent() && bb.intersectsWith(getBoundingBox());
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if (isKnockback() && source instanceof EntityDamageByEntityEvent && ((EntityDamageByEntityEvent) source).getDamager() instanceof Player) {
            return false;
        }

        super.attack(source);

        target = null;
        //attackTime = 7;
        return true;
    }

    public List<Block> getCollisionBlocks() {
        return collisionBlocks;
    }

    public int getMaxFallHeight() {
        if (!(target instanceof Entity)) {
            return 3;
        } else {
            int i = (int) (getHealth() - getMaxHealth() * 0.33F);
            i = i - (3 - getServer().getDifficulty()) * 4;

            if (i < 0) {
                i = 0;
            }

            return i + 3;
        }
    }

    @Override
    public boolean setMotion(Vector3 motion) {
        if (!justCreated) {
            EntityMotionEvent ev = new EntityMotionEvent(this, motion);
            server.getPluginManager().callEvent(ev);
            if (ev.isCancelled()) {
                return false;
            }
        }

        motionX = motion.x;
        motionY = motion.y;
        motionZ = motion.z;

        return true;
    }

    @Override
    public boolean move(double dx, double dy, double dz) {
        Timings.entityMoveTimer.startTiming();

        double movX = dx * moveMultifier;
        double movY = dy;
        double movZ = dz * moveMultifier;

        AxisAlignedBB[] list = level.getCollisionCubes(this, level.getTickRate() > 1 ? boundingBox.getOffsetBoundingBox(dx, dy, dz) : boundingBox.addCoord(dx, dy, dz));
        if (isWallCheck()) {
            for (AxisAlignedBB bb : list) {
                dx = bb.calculateXOffset(boundingBox, dx);
            }
            boundingBox.offset(dx, 0, 0);

            for (AxisAlignedBB bb : list) {
                dz = bb.calculateZOffset(boundingBox, dz);
            }
            boundingBox.offset(0, 0, dz);
        }
        for (AxisAlignedBB bb : list) {
            dy = bb.calculateYOffset(boundingBox, dy);
        }
        boundingBox.offset(0, dy, 0);

        setComponents(x + dx, y + dy, z + dz);
        checkChunks();

        checkGroundState(movX, movY, movZ, dx, dy, dz);
        updateFallState(onGround);

        Timings.entityMoveTimer.stopTiming();

        return true;
    }
}
