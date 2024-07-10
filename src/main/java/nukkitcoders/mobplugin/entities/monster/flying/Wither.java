package nukkitcoders.mobplugin.entities.monster.flying;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.entity.Attribute;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.entity.EntitySmite;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityExplosionPrimeEvent;
import cn.nukkit.event.entity.ProjectileLaunchEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Explosion;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.Location;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.AddEntityPacket;
import cn.nukkit.network.protocol.BossEventPacket;
import cn.nukkit.network.protocol.DataPacket;
import nukkitcoders.mobplugin.MobPlugin;
import nukkitcoders.mobplugin.entities.Boss;
import nukkitcoders.mobplugin.entities.monster.FlyingMonster;
import nukkitcoders.mobplugin.entities.projectile.EntityBlueWitherSkull;
import nukkitcoders.mobplugin.entities.projectile.EntityWitherSkull;
import nukkitcoders.mobplugin.utils.FastMathLite;
import nukkitcoders.mobplugin.utils.Utils;
import org.jetbrains.annotations.NotNull;

public class Wither extends FlyingMonster implements Boss, EntitySmite {

    public static final int NETWORK_ID = 52;

    private boolean exploded;
    private boolean wasExplosion;

    public Wither(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public @NotNull String getIdentifier() {
        return WITHER;
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 0.9f;
    }

    @Override
    public float getHeight() {
        return 3.5f;
    }

    @Override
    public double getSpeed() {
        return 1.3;
    }

    @Override
    public void initEntity() {
        this.setMaxHealth(witherMaxHealth());
        super.initEntity();

        this.fireProof = true;
        this.setDamage(new float[]{0, 2, 4, 6});
        if (this.age == 0) {
            this.setDataProperty(WITHER_INVULNERABLE_TICKS, 200);
        }
        this.stayTime = 220;
    }

    @Override
    public boolean targetOption(EntityCreature creature, double distance) {
        if (creature instanceof Player) {
            Player player = (Player) creature;
            if (!player.isSurvival() && !player.isAdventure()) {
                return false;
            }
        }
        return creature.isAlive() && !creature.closed && distance <= 10000;
    }

    @Override
    public int getKillExperience() {
        return 50;
    }

    @Override
    public void attackEntity(Entity player) {
        if (this.age > 220 && this.attackDelay > 40 && this.distanceSquared(player) <= 4096) {
            this.attackDelay = 0;

            double f = 1;
            double yaw = this.yaw + Utils.rand(-4.0, 4.0);
            double pitch = this.pitch + Utils.rand(-4.0, 4.0);
            Location pos = new Location(this.x - Math.sin(FastMathLite.toRadians(yaw)) * Math.cos(FastMathLite.toRadians(pitch)) * 0.5, this.y + this.getEyeHeight(),
                    this.z + Math.cos(FastMathLite.toRadians(yaw)) * Math.cos(FastMathLite.toRadians(pitch)) * 0.5, yaw, pitch, this.level);

            if (this.getLevel().getBlockIdAt(pos.getFloorX(), pos.getFloorY(), pos.getFloorZ()) != Block.AIR) {
                return;
            }

            Entity k;
            ProjectileLaunchEvent launch;
            EntityWitherSkull skull;
            if (Utils.rand(0, 200) > 180 || Utils.rand(0, 200) < 20) {
                f = 0.8;
                k = Entity.createEntity("BlueWitherSkull", pos, this);
                skull = (EntityBlueWitherSkull) k;
                ((EntityBlueWitherSkull) skull).setExplode(true);
            } else {
                k = Entity.createEntity("WitherSkull", pos, this);
                skull = (EntityWitherSkull) k;
            }
            skull.setMotion(new Vector3(-Math.sin(FastMathLite.toRadians(yaw)) * Math.cos(FastMathLite.toRadians(pitch)) * f * f, -Math.sin(FastMathLite.toRadians(pitch)) * f * f,
                    Math.cos(FastMathLite.toRadians(yaw)) * Math.cos(FastMathLite.toRadians(pitch)) * f * f));
            launch = new ProjectileLaunchEvent(skull, this);

            this.server.getPluginManager().callEvent(launch);
            if (launch.isCancelled()) {
                skull.close();
            } else {
                skull.spawnToAll();
                this.level.addSound(this, Sound.MOB_WITHER_SHOOT);
            }
        }
    }

    @Override
    public Item[] getDrops() {
        return new Item[]{Item.get(Item.NETHER_STAR, 0, 1)};
    }

    @Override
    protected DataPacket createAddEntityPacket() {
        AddEntityPacket addEntity = new AddEntityPacket();
        addEntity.type = NETWORK_ID;
        addEntity.entityUniqueId = this.getId();
        addEntity.entityRuntimeId = this.getId();
        addEntity.yaw = (float) this.yaw;
        addEntity.headYaw = (float) this.yaw;
        addEntity.pitch = (float) this.pitch;
        addEntity.x = (float) this.x;
        addEntity.y = (float) this.y;
        addEntity.z = (float) this.z;
        addEntity.speedX = (float) this.motionX;
        addEntity.speedY = (float) this.motionY;
        addEntity.speedZ = (float) this.motionZ;
        addEntity.attributes = new Attribute[]{Attribute.getAttribute(Attribute.MAX_HEALTH).setMaxValue(witherMaxHealth()).setValue(witherMaxHealth())};
        return addEntity;
    }

    @Override
    public boolean entityBaseTick(int tickDiff) {
        if (getServer().getDifficulty() == 0) {
            this.close();
            return true;
        }

        if (!this.closed && this.age == 200) {
            this.explode();
            this.setDataProperty(WITHER_INVULNERABLE_TICKS, 0);
        }

        return super.entityBaseTick(tickDiff);
    }

    @Override
    public int nearbyDistanceMultiplier() {
        return 30;
    }

    @Override
    public void kill() {
        if (!this.isAlive()) {
            return;
        }

        if (!this.exploded && this.lastDamageCause != null && EntityDamageEvent.DamageCause.SUICIDE != this.lastDamageCause.getCause()) {
            this.exploded = true;
            this.explode();
        }
        if (this.lastDamageCause instanceof EntityDamageByEntityEvent) {
            Entity damager = ((EntityDamageByEntityEvent) this.lastDamageCause).getDamager();
            if (damager instanceof Player) {
                ((Player) damager).awardAchievement("killWither");
            }
        }
        super.kill();
    }

    @Override
    public boolean attack(EntityDamageEvent ev) {
        if (this.age <= 200 && ev.getCause() != EntityDamageEvent.DamageCause.SUICIDE) {
            return false;
        }

        boolean r = super.attack(ev);
        if (this.wasExplosion) {
            this.wasExplosion = false;
        } else if (r && ev instanceof EntityDamageByEntityEvent && ((EntityDamageByEntityEvent) ev).getDamager() instanceof Player && !this.closed && this.isAlive() && Utils.rand() && this.level.getGameRules().getBoolean(GameRule.MOB_GRIEFING)) {
            this.wasExplosion = true;
            this.level.addSound(this, Sound.MOB_WITHER_BREAK_BLOCK);
            int fx = this.getFloorX();
            int fy = this.getFloorY();
            int fz = this.getFloorZ();
            Vector3 pos = new Vector3(0, 0, 0);
            Item tool = Item.get(Item.DIAMOND_PICKAXE);
            for (int x = fx - 2; x <= fx + 2; x++) {
                for (int y = fy; y <= fy + 4; y++) {
                    for (int z = fz - 2; z <= fz + 2; z++) {
                        Block block = this.level.getBlock(x, y, z);
                        if (block.getToolTier() >= tool.getTier()) {
                            pos.setComponents(x, y, z);
                            this.level.setBlock(pos, Block.get(Block.AIR));
                            BlockEntity blockEntity = this.level.getBlockEntityIfLoaded(pos);
                            if (blockEntity != null) {
                                blockEntity.onBreak(false);
                                blockEntity.close();
                            }
                            if (this.level.getGameRules().getBoolean(GameRule.DO_TILE_DROPS) && Math.random() * 100 < 14) {
                                for (Item drop : block.getDrops(tool)) {
                                    this.level.dropItem(block, drop);
                                }
                            }
                        }
                    }
                }
            }
        }
        return r;
    }

    private int witherMaxHealth() {
        switch (this.getServer().getDifficulty()) {
            case 2:
                return 450;
            case 3:
                return 600;
            default:
                return 300;
        }
    }

    private void explode() {
        EntityExplosionPrimeEvent ev = new EntityExplosionPrimeEvent(this, 7);
        this.server.getPluginManager().callEvent(ev);

        if (!ev.isCancelled()) {
            Explosion explosion = new Explosion(this, (float) ev.getForce(), this);

            if (ev.isBlockBreaking() && this.level.getGameRules().getBoolean(GameRule.MOB_GRIEFING)) {
                explosion.explodeA();
            }

            explosion.explodeB();
        }
    }

    @Override
    public boolean canTarget(Entity entity) {
        return true;
    }

    @Override
    public void spawnTo(Player player) {
        super.spawnTo(player);
        if (!MobPlugin.getInstance().config.showBossBar) {
            return;
        }
        BossEventPacket pkBoss = new BossEventPacket();
        pkBoss.bossEid = this.id;
        pkBoss.type = BossEventPacket.TYPE_SHOW;
        pkBoss.title = this.getName();
        pkBoss.healthPercent = this.health / 100;
        player.dataPacket(pkBoss);
    }

    @Override
    public String getName() {
        String name = this.getNameTag();
        return !name.isEmpty() ? name : "Wither";
    }
}
