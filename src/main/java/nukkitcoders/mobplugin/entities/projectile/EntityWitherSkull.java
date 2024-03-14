package nukkitcoders.mobplugin.entities.projectile;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.effect.Effect;
import cn.nukkit.entity.effect.EffectType;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.particle.SmokeParticle;
import cn.nukkit.nbt.tag.CompoundTag;
import nukkitcoders.mobplugin.utils.Utils;
import org.jetbrains.annotations.NotNull;

public class EntityWitherSkull extends EntityProjectile {

    public static final int NETWORK_ID = 89;

    public EntityWitherSkull(IChunk chunk, CompoundTag nbt) {
        this(chunk, nbt, null);
    }

    @Override
    public @NotNull String getIdentifier() {
        return WITHER_SKULL;
    }

    public EntityWitherSkull(IChunk chunk, CompoundTag nbt, Entity shootingEntity) {
        super(chunk, nbt, shootingEntity);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 0.25f;
    }

    @Override
    public float getLength() {
        return 0.25f;
    }

    @Override
    public float getHeight() {
        return 0.25f;
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
    protected double getBaseDamage() {
        switch (server.getDifficulty()) {
            case 2: // normal
                return 8;
            case 3: // hard
                return 12;
            default:
                return 5;
        }
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (this.closed) {
            return false;
        }

        if (this.age > 1200 || this.isCollided || this.hadCollision) {
            this.close();
        } else if (this.age % 4 == 0) {
            this.level.addParticle(new SmokeParticle(this.add(this.getWidth() / 2 + Utils.rand(-100.0, 100.0) / 500, this.getHeight() / 2 + Utils.rand(-100.0, 100.0) / 500, this.getWidth() / 2 + Utils.rand(-100.0, 100.0) / 500)));
        }

        super.onUpdate(currentTick);
        return !this.closed;
    }

    @Override
    public void onCollideWithEntity(Entity entity) {
        super.onCollideWithEntity(entity);
        entity.addEffect(Effect.get(EffectType.WITHER).setDuration(200));
    }
}
