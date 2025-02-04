package nukkitcoders.mobplugin.entities.projectile;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

public class EntityBlazeFireBall extends EntityProjectile {

    public static final int NETWORK_ID = 94;

    public EntityBlazeFireBall(IChunk chunk, CompoundTag nbt) {
        this(chunk, nbt, null);
    }

    @Override
    public @NotNull String getIdentifier() {
        return SMALL_FIREBALL;
    }

    public EntityBlazeFireBall(IChunk chunk, CompoundTag nbt, Entity shootingEntity) {
        super(chunk, nbt, shootingEntity);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 0.31f;
    }

    @Override
    public float getHeight() {
        return 0.31f;
    }

    @Override
    public float getGravity() {
        return 0.001f;
    }

    @Override
    public float getDrag() {
        return 0.01f;
    }

    @Override
    public double getBaseDamage() {
        return 5;
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (this.closed) {
            return false;
        }

        if (this.age > 1200 || this.isCollided || this.hadCollision) {
            this.close();
            return false;
        } else {
            this.fireTicks = 2;
        }

        super.onUpdate(currentTick);
        return !this.closed;
    }
}
