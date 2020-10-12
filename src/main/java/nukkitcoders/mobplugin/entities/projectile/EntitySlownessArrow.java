package nukkitcoders.mobplugin.entities.projectile;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.projectile.EntityArrow;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.potion.Effect;

public class EntitySlownessArrow extends EntityArrow {

    public EntitySlownessArrow(FullChunk chunk, CompoundTag nbt, Entity shootingEntity) {
        super(chunk, nbt, shootingEntity);
    }

    @Override
    public void onCollideWithEntity(Entity entity) {
        super.onCollideWithEntity(entity);
        entity.addEffect(Effect.getEffect(Effect.SLOWNESS).setDuration(600));
    }
}
