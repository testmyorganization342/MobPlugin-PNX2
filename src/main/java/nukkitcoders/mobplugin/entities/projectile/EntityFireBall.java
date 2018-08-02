package nukkitcoders.mobplugin.entities.projectile;

import cn.nukkit.Player;
import cn.nukkit.block.BlockCobblestone;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.event.entity.ExplosionPrimeEvent;
import cn.nukkit.level.Explosion;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.particle.CriticalParticle;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.AddEntityPacket;
import nukkitcoders.mobplugin.utils.Utils;

public class EntityFireBall extends EntityProjectile {

    public static final int NETWORK_ID = 85;

    protected boolean critical = false;

    protected boolean canExplode = false;

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 0.5f;
    }

    @Override
    public float getHeight() {
        return 0.5f;
    }

    @Override
    public float getGravity() {
        return 0.05f;
    }

    @Override
    public float getDrag() {
        return 0.01f;
    }

    @Override
    protected double getDamage() {
        return 4;
    }

    public EntityFireBall(FullChunk chunk, CompoundTag nbt, Entity shootingEntity) {
        this(chunk, nbt, shootingEntity, false);
    }

    public EntityFireBall(FullChunk chunk, CompoundTag nbt, Entity shootingEntity, boolean critical) {
        super(chunk, nbt, shootingEntity);

        critical = critical;
    }

    public boolean isExplode() {
        return canExplode;
    }

    public void setExplode(boolean bool) {
        canExplode = bool;
    }

    public boolean onUpdate(int currentTick) {
        if (closed) {
            return false;
        }
        if (shootingEntity.getLevelBlock() instanceof BlockCobblestone) {
            return false;
        }

        boolean hasUpdate = super.onUpdate(currentTick);

        if (!hadCollision && critical) {
            level.addParticle(new CriticalParticle(
                    add(getWidth() / 2 + Utils.rand(-100, 100) / 500, getHeight() / 2 + Utils.rand(-100, 100) / 500, getWidth() / 2 + Utils.rand(-100, 100) / 500)));
        } else if (onGround) {
            critical = false;
        }

        if (age > 1200 || isCollided) {
            if (isCollided && canExplode) {
                ExplosionPrimeEvent ev = new ExplosionPrimeEvent(this, 2.8);
                server.getPluginManager().callEvent(ev);
                if (!ev.isCancelled()) {
                    Explosion explosion = new Explosion(this, (float) ev.getForce(), shootingEntity);
                    if (ev.isBlockBreaking()) {
                        explosion.explodeA();
                    }
                    explosion.explodeB();
                }
            }
            kill();
            hasUpdate = true;
        }

        return hasUpdate;
    }

    public void spawnTo(Player player) {
        AddEntityPacket pk = new AddEntityPacket();
        pk.type = NETWORK_ID;
        pk.entityRuntimeId = getId();
        pk.x = (float) x;
        pk.y = (float) y;
        pk.z = (float) z;
        pk.speedX = (float) motionX;
        pk.speedY = (float) motionY;
        pk.speedZ = (float) motionZ;
        pk.metadata = dataProperties;
        player.dataPacket(pk);

        super.spawnTo(player);
    }
}
