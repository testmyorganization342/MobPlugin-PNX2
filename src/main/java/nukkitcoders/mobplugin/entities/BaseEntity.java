package nukkitcoders.mobplugin.entities;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityAgeable;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.entity.EntityRideable;
import cn.nukkit.entity.data.EntityData;
import cn.nukkit.event.Event;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityInteractEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.EntityEventPacket;
import cn.nukkit.network.protocol.MoveEntityAbsolutePacket;
import cn.nukkit.network.protocol.SetEntityMotionPacket;
import cn.nukkit.potion.Effect;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import nukkitcoders.mobplugin.MobPlugin;
import nukkitcoders.mobplugin.entities.monster.Monster;
import nukkitcoders.mobplugin.entities.monster.flying.EnderDragon;
import nukkitcoders.mobplugin.utils.FastMathLite;
import nukkitcoders.mobplugin.utils.Utils;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public abstract class BaseEntity extends EntityCreature implements EntityAgeable {

    public int stayTime = 0;
    protected int moveTime = 0;
    private int airTicks = 0;
    protected float moveMultiplier = 1.0f;
    protected Vector3 target = null;
    protected Entity followTarget = null;
    private boolean baby = false;
    private boolean movement = true;
    private boolean friendly = false;
    protected int attackDelay = 0;
    protected boolean noFallDamage;
    public Item[] armor;
    //private int inEndPortal;
    //private int inNetherPortal;

    private static final Int2ObjectMap<Float> ARMOR_POINTS = new Int2ObjectOpenHashMap<Float>() {
        {
            put(Item.LEATHER_CAP, new Float(1));
            put(Item.LEATHER_TUNIC, new Float(3));
            put(Item.LEATHER_PANTS, new Float(2));
            put(Item.LEATHER_BOOTS, new Float(1));
            put(Item.CHAIN_HELMET, new Float(2));
            put(Item.CHAIN_CHESTPLATE, new Float(5));
            put(Item.CHAIN_LEGGINGS, new Float(4));
            put(Item.CHAIN_BOOTS, new Float(1));
            put(Item.GOLD_HELMET, new Float(2));
            put(Item.GOLD_CHESTPLATE, new Float(5));
            put(Item.GOLD_LEGGINGS, new Float(3));
            put(Item.GOLD_BOOTS, new Float(1));
            put(Item.IRON_HELMET, new Float(2));
            put(Item.IRON_CHESTPLATE, new Float(6));
            put(Item.IRON_LEGGINGS, new Float(5));
            put(Item.IRON_BOOTS, new Float(2));
            put(Item.DIAMOND_HELMET, new Float(3));
            put(Item.DIAMOND_CHESTPLATE, new Float(8));
            put(Item.DIAMOND_LEGGINGS, new Float(6));
            put(Item.DIAMOND_BOOTS, new Float(3));
            put(Item.NETHERITE_HELMET, new Float(3));
            put(Item.NETHERITE_CHESTPLATE, new Float(8));
            put(Item.NETHERITE_LEGGINGS, new Float(6));
            put(Item.NETHERITE_BOOTS, new Float(3));
            put(Item.TURTLE_SHELL, new Float(2));
        }
    };

    public BaseEntity(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);

        this.setHealth(this.getMaxHealth());
        this.setAirTicks(300);
    }

    public abstract Vector3 updateMove(int tickDiff);

    public abstract int getKillExperience();

    public boolean isFriendly() {
        return this.friendly;
    }

    public boolean isMovement() {
        return this.movement;
    }

    public boolean isKnockback() {
        return this.attackTime > 0;
    }

    public void setFriendly(boolean bool) {
        this.friendly = bool;
    }

    public void setMovement(boolean value) {
        this.movement = value;
    }

    public double getSpeed() {
        if (this.isBaby()) {
            return 1.2;
        }
        return 1;
    }

    public Vector3 getTarget() {
        return this.target;
    }

    public void setTarget(Vector3 vec) {
        this.target = vec;
    }

    public Entity getFollowTarget() {
        if (this.followTarget != null) {
            return this.followTarget;
        } else if (this.target instanceof Entity) {
            return (Entity) this.target;
        } else {
            return null;
        }
    }

    public Vector3 getTargetVector() {
        if (this.followTarget != null) {
            return this.followTarget;
        } else if (this.target instanceof Entity) {
            return this.target;
        } else {
            return null;
        }
    }

    public void setFollowTarget(Entity target) {
        this.followTarget = target;
        this.moveTime = 0;
        this.stayTime = 0;
        this.target = null;
    }

    @Override
    public boolean isBaby() {
        return this.baby;
    }

    public void setBaby(boolean baby) {
        this.baby = baby;
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_BABY, baby);
        if (baby) {
            this.setScale(0.5f);
            this.age = Utils.rand(-2400, -1800);
        } else {
            this.setScale(1.0f);
        }
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        if (this.namedTag.contains("Movement")) {
            this.setMovement(this.namedTag.getBoolean("Movement"));
        }

        if (this.namedTag.contains("Age")) {
            this.age = this.namedTag.getShort("Age");
        }

        if (this.namedTag.getBoolean("Baby")) {
            this.baby = true;
            this.setDataFlag(DATA_FLAGS, DATA_FLAG_BABY, true);
        }
    }

    @Override
    public String getName() {
        return this.hasCustomName() ? this.getNameTag() : this.getClass().getSimpleName();
    }

    public void saveNBT() {
        super.saveNBT();

        this.namedTag.putBoolean("Baby", this.isBaby());
        this.namedTag.putBoolean("Movement", this.isMovement());
        this.namedTag.putShort("Age", this.age);
    }

    public boolean targetOption(EntityCreature creature, double distance) {
        if (this instanceof Monster) {
            if (creature instanceof Player) {
                Player player = (Player) creature;
                return !player.closed && player.spawned && player.isAlive() && (player.isSurvival() || player.isAdventure()) && distance <= 100;
            }
            return creature.isAlive() && !creature.closed && distance <= 100;
        }
        return false;
    }

    @Override
    public boolean entityBaseTick(int tickDiff) {
        super.entityBaseTick(tickDiff);

        if (this.canDespawn()) {
            if (MobPlugin.getInstance().config.killOnDespawn) {
                this.kill();
            } else {
                this.close();
            }
        }

        if (this instanceof Monster && this.attackDelay < 200) {
            this.attackDelay++;
        }

        return true;
    }

    @Override
    protected void checkBlockCollision() {
        //boolean netherPortal = false;
        //boolean endPortal = false;

        for (Block block : this.getCollisionBlocks()) {
            /*if (block.getId() == Block.NETHER_PORTAL) {
                netherPortal = true;
                continue;
            } else if (block.getId() == Block.END_PORTAL) {
                endPortal = true;
                continue;
            }*/

            block.onEntityCollide(this);
        }

        /*if (endPortal) {
            inEndPortal++;
        } else {
            inEndPortal = 0;
        }

        if (inEndPortal == 1) {
            EntityPortalEnterEvent ev = new EntityPortalEnterEvent(this, EntityPortalEnterEvent.PortalType.END);
            this.getServer().getPluginManager().callEvent(ev);
            if (!ev.isCancelled()) {
                //TODO
            }
        }

        if (netherPortal) {
            inNetherPortal++;
        } else {
            inNetherPortal = 0;
        }

        if (inNetherPortal == 80) {
            EntityPortalEnterEvent ev = new EntityPortalEnterEvent(this, EntityPortalEnterEvent.PortalType.NETHER);
            this.getServer().getPluginManager().callEvent(ev);
            if (!ev.isCancelled()) {
                //TODO
            }
        }*/
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if (this.isKnockback() && source instanceof EntityDamageByEntityEvent && ((EntityDamageByEntityEvent) source).getDamager() instanceof Player) {
            return false;
        }

        if (this.fireProof && (source.getCause() == EntityDamageEvent.DamageCause.FIRE || source.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK || source.getCause() == EntityDamageEvent.DamageCause.LAVA)) {
            return false;
        }

        if (source instanceof EntityDamageByEntityEvent) {
            ((EntityDamageByEntityEvent) source).setKnockBack(0.25f);
        }

        super.attack(source);

        this.target = null;
        this.stayTime = 0;
        return true;
    }

    @Override
    public boolean move(double dx, double dy, double dz) {
        if (dy < -10 || dy > 10) {
            return false;
        }

        if (dx == 0 && dz == 0 && dy == 0) {
            return false;
        }

        double movX = dx * moveMultiplier;
        double movY = dy;
        double movZ = dz * moveMultiplier;

        AxisAlignedBB[] list = this.level.getCollisionCubes(this, this.boundingBox.addCoord(dx, dy, dz), false);
        for (AxisAlignedBB bb : list) {
            dx = bb.calculateXOffset(this.boundingBox, dx);
        }
        this.boundingBox.offset(dx, 0, 0);

        for (AxisAlignedBB bb : list) {
            dz = bb.calculateZOffset(this.boundingBox, dz);
        }
        this.boundingBox.offset(0, 0, dz);

        for (AxisAlignedBB bb : list) {
            dy = bb.calculateYOffset(this.boundingBox, dy);
        }
        this.boundingBox.offset(0, dy, 0);

        this.setComponents(this.x + dx, this.y + dy, this.z + dz);
        this.checkChunks();

        this.checkGroundState(movX, movY, movZ, dx, dy, dz);
        this.updateFallState(this.onGround);

        return true;
    }

    protected float getMountedYOffset() {
        return getHeight() * 0.75F;
    }

    protected Item[] getRandomArmor() {
        Item[] slots = new Item[4];
        Item helmet = Item.get(0);
        Item chestplate = Item.get(0);
        Item leggings = Item.get(0);
        Item boots = Item.get(0);

        switch (Utils.rand(1, 5)) {
            case 1:
                if (Utils.rand(1, 100) < 39) {
                    if (Utils.rand(0, 1) == 0) {
                        helmet = Item.get(Item.LEATHER_CAP, 0, 1);
                        this.addHealth(1);
                    }
                }
                break;
            case 2:
                if (Utils.rand(1, 100) < 50) {
                    if (Utils.rand(0, 1) == 0) {
                        helmet = Item.get(Item.GOLD_HELMET, 0, 1);
                        this.addHealth(1);
                    }
                }
                break;
            case 3:
                if (Utils.rand(1, 100) < 14) {
                    if (Utils.rand(0, 1) == 0) {
                        helmet = Item.get(Item.CHAIN_HELMET, 0, 1);
                        this.addHealth(1);
                    }
                }
                break;
            case 4:
                if (Utils.rand(1, 100) < 3) {
                    if (Utils.rand(0, 1) == 0) {
                        helmet = Item.get(Item.IRON_HELMET, 0, 1);
                        this.addHealth(1);
                    }
                }
                break;
            case 5:
                if (Utils.rand(1, 100) == 100) {
                    if (Utils.rand(0, 1) == 0) {
                        helmet = Item.get(Item.DIAMOND_HELMET, 0, 1);
                        this.addHealth(2);
                    }
                }
                break;
        }

        slots[0] = helmet;

        if (Utils.rand(1, 4) != 1) {
            switch (Utils.rand(1, 5)) {
                case 1:
                    if (Utils.rand(1, 100) < 39) {
                        if (Utils.rand(0, 1) == 0) {
                            chestplate = Item.get(Item.LEATHER_TUNIC, 0, 1);
                            this.addHealth(1);
                        }
                    }
                    break;
                case 2:
                    if (Utils.rand(1, 100) < 50) {
                        if (Utils.rand(0, 1) == 0) {
                            chestplate = Item.get(Item.GOLD_CHESTPLATE, 0, 1);
                            this.addHealth(1);
                        }
                    }
                    break;
                case 3:
                    if (Utils.rand(1, 100) < 14) {
                        if (Utils.rand(0, 1) == 0) {
                            chestplate = Item.get(Item.CHAIN_CHESTPLATE, 0, 1);
                            this.addHealth(1);
                        }
                    }
                    break;
                case 4:
                    if (Utils.rand(1, 100) < 3) {
                        if (Utils.rand(0, 1) == 0) {
                            chestplate = Item.get(Item.IRON_CHESTPLATE, 0, 1);
                            this.addHealth(2);
                        }
                    }
                    break;
                case 5:
                    if (Utils.rand(1, 100) == 100) {
                        if (Utils.rand(0, 1) == 0) {
                            chestplate = Item.get(Item.DIAMOND_CHESTPLATE, 0, 1);
                            this.addHealth(3);
                        }
                    }
                    break;
            }
        }

        slots[1] = chestplate;

        if (Utils.rand(1, 2) == 2) {
            switch (Utils.rand(1, 5)) {
                case 1:
                    if (Utils.rand(1, 100) < 39) {
                        if (Utils.rand(0, 1) == 0) {
                            leggings = Item.get(Item.LEATHER_PANTS, 0, 1);
                            this.addHealth(1);
                        }
                    }
                    break;
                case 2:
                    if (Utils.rand(1, 100) < 50) {
                        if (Utils.rand(0, 1) == 0) {
                            leggings = Item.get(Item.GOLD_LEGGINGS, 0, 1);
                            this.addHealth(1);
                        }
                    }
                    break;
                case 3:
                    if (Utils.rand(1, 100) < 14) {
                        if (Utils.rand(0, 1) == 0) {
                            leggings = Item.get(Item.CHAIN_LEGGINGS, 0, 1);
                            this.addHealth(1);
                        }
                    }
                    break;
                case 4:
                    if (Utils.rand(1, 100) < 3) {
                        if (Utils.rand(0, 1) == 0) {
                            leggings = Item.get(Item.IRON_LEGGINGS, 0, 1);
                            this.addHealth(1);
                        }
                    }
                    break;
                case 5:
                    if (Utils.rand(1, 100) == 100) {
                        if (Utils.rand(0, 1) == 0) {
                            leggings = Item.get(Item.DIAMOND_LEGGINGS, 0, 1);
                            this.addHealth(2);
                        }
                    }
                    break;
            }
        }

        slots[2] = leggings;

        if (Utils.rand(1, 5) < 3) {
            switch (Utils.rand(1, 5)) {
                case 1:
                    if (Utils.rand(1, 100) < 39) {
                        if (Utils.rand(0, 1) == 0) {
                            boots = Item.get(Item.LEATHER_BOOTS, 0, 1);
                            this.addHealth(1);
                        }
                    }
                    break;
                case 2:
                    if (Utils.rand(1, 100) < 50) {
                        if (Utils.rand(0, 1) == 0) {
                            boots = Item.get(Item.GOLD_BOOTS, 0, 1);
                            this.addHealth(1);
                        }
                    }
                    break;
                case 3:
                    if (Utils.rand(1, 100) < 14) {
                        if (Utils.rand(0, 1) == 0) {
                            boots = Item.get(Item.CHAIN_BOOTS, 0, 1);
                            this.addHealth(1);
                        }
                    }
                    break;
                case 4:
                    if (Utils.rand(1, 100) < 3) {
                        if (Utils.rand(0, 1) == 0) {
                            boots = Item.get(Item.IRON_BOOTS, 0, 1);
                            this.addHealth(1);
                        }
                    }
                    break;
                case 5:
                    if (Utils.rand(1, 100) == 100) {
                        if (Utils.rand(0, 1) == 0) {
                            boots = Item.get(Item.DIAMOND_BOOTS, 0, 1);
                            this.addHealth(2);
                        }
                    }
                    break;
            }
        }

        slots[3] = boots;

        return slots;
    }

    private void addHealth(int health) {
        this.setMaxHealth(this.getMaxHealth() + health);
        this.setHealth(this.getHealth() + health);
    }

    public boolean canDespawn() {
        int despawnTicks = MobPlugin.getInstance().config.despawnTicks;
        return despawnTicks > 0 && this.age > despawnTicks && !this.hasCustomName() && !(this instanceof Boss);
    }

    public int nearbyDistanceMultiplier() {
        return 1;
    }

    @Override
    public int getAirTicks() {
        return this.airTicks;
    }

    @Override
    public void setAirTicks(int ticks) {
        this.airTicks = ticks;
    }

    @Override
    public void addMovement(double x, double y, double z, double yaw, double pitch, double headYaw) {
        MoveEntityAbsolutePacket pk = new MoveEntityAbsolutePacket();
        pk.eid = this.id;
        pk.x = (float) x;
        pk.y = (float) y;
        pk.z = (float) z;
        pk.yaw = (float) yaw;
        pk.headYaw = (float) headYaw;
        pk.pitch = (float) pitch;
        pk.onGround = this.onGround;
        for (Player p : this.hasSpawned.values()) {
            p.dataPacket(pk);
        }
    }

    @Override
    public void addMotion(double motionX, double motionY, double motionZ) {
        SetEntityMotionPacket pk = new SetEntityMotionPacket();
        pk.eid = this.id;
        pk.motionX = (float) motionX;
        pk.motionY = (float) motionY;
        pk.motionZ = (float) motionZ;
        for (Player p : this.hasSpawned.values()) {
            p.dataPacket(pk);
        }
    }

    @Override
    protected void checkGroundState(double movX, double movY, double movZ, double dx, double dy, double dz) {
        if (onGround && movX == 0 && movY == 0 && movZ == 0 && dx == 0 && dy == 0 && dz == 0) {
            return;
        }
        this.isCollidedVertically = movY != dy;
        this.isCollidedHorizontally = (movX != dx || movZ != dz);
        this.isCollided = (this.isCollidedHorizontally || this.isCollidedVertically);
        this.onGround = (movY != dy && movY < 0);
    }

    public static void setProjectileMotion(Entity projectile, double pitch, double yawR, double pitchR, double speed) {
        double verticalMultiplier = Math.cos(pitchR);
        double x = verticalMultiplier * Math.sin(-yawR);
        double z = verticalMultiplier * Math.cos(yawR);
        double y = Math.sin(-(FastMathLite.toRadians(pitch)));
        double magnitude = Math.sqrt(x * x + y * y + z * z);
        if (magnitude > 0) {
            x += (x * (speed - magnitude)) / magnitude;
            y += (y * (speed - magnitude)) / magnitude;
            z += (z * (speed - magnitude)) / magnitude;
        }
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        x += rand.nextGaussian() * 0.007499999832361937 * 6;
        y += rand.nextGaussian() * 0.007499999832361937 * 6;
        z += rand.nextGaussian() * 0.007499999832361937 * 6;
        projectile.setMotion(new Vector3(x, y, z));
    }

    @Override
    public void resetFallDistance() {
        this.highestPosition = this.y;
    }

    @Override
    public boolean setMotion(Vector3 motion) {
        this.motionX = motion.x;
        this.motionY = motion.y;
        this.motionZ = motion.z;
        if (!this.justCreated) {
            this.updateMovement();
        }
        return true;
    }

    public boolean canTarget(Entity entity) {
        return entity instanceof Player;
    }

    @Override
    protected boolean applyNameTag(Player player, Item item) {
        return !(this instanceof EnderDragon) && super.applyNameTag(player, item);
    }

    @Override
    public boolean setDataProperty(EntityData data, boolean send) {
        if (!Objects.equals(data, this.dataProperties.get(data.getId()))) {
            this.dataProperties.put(data);
            if (send && (data.getId() != DATA_HEALTH || this instanceof EntityRideable || this instanceof Boss)) {
                this.sendData(this.hasSpawned.values().toArray(new Player[0]), this.dataProperties);
            }
            return true;
        }
        return false;
    }

    protected float getArmorPoints(int item) {
        Float points = ARMOR_POINTS.get(item);
        if (points == null) return 0;
        return points;
    }

    protected void playAttack() {
        EntityEventPacket pk = new EntityEventPacket();
        pk.eid = this.getId();
        pk.event = EntityEventPacket.ARM_SWING;
        Server.broadcastPacket(this.getViewers().values(), pk);
    }

    @Override
    public void fall(float fallDistance) {
        if (fallDistance > 0.75) {
            if (!this.hasEffect(Effect.SLOW_FALLING)) {
                Block down = this.level.getBlock(this.down());
                if (!this.noFallDamage) {
                    float damage = (float) Math.floor(fallDistance - 3 - (this.hasEffect(Effect.JUMP) ? this.getEffect(Effect.JUMP).getAmplifier() + 1 : 0));
                    if (down.getId() == BlockID.HAY_BALE) {
                        damage -= (damage * 0.8f);
                    }
                    if (damage > 0) {
                        this.attack(new EntityDamageEvent(this, EntityDamageEvent.DamageCause.FALL, damage));
                    }
                }
                if (down.getId() == BlockID.FARMLAND) {
                    Event ev = new EntityInteractEvent(this, down);
                    this.server.getPluginManager().callEvent(ev);
                    if (ev.isCancelled()) {
                        return;
                    }
                    this.level.setBlock(down, Block.get(BlockID.DIRT), false, true);
                }
            }
        }
    }
}
