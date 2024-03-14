package nukkitcoders.mobplugin.entities.projectile;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.effect.Effect;
import cn.nukkit.entity.effect.EffectType;
import cn.nukkit.entity.projectile.EntityArrow;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class EntitySlownessArrow extends EntityArrow {

    public EntitySlownessArrow(IChunk chunk, CompoundTag nbt) {
        this(chunk, nbt, null);
    }

    public EntitySlownessArrow(IChunk chunk, CompoundTag nbt, Entity shootingEntity) {
        super(chunk, nbt, shootingEntity);
        this.setDataProperty(CUSTOM_DISPLAY, 19);
    }

    @Override
    public void onCollideWithEntity(Entity entity) {
        super.onCollideWithEntity(entity);
        entity.addEffect(Effect.get(EffectType.SLOWNESS).setDuration(600));
    }
}
