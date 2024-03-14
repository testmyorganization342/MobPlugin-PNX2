package nukkitcoders.mobplugin.entities.projectile;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.projectile.EntityThrownTrident;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class DespawnableThrownTrident extends EntityThrownTrident {

    public DespawnableThrownTrident(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }


    public DespawnableThrownTrident(IChunk chunk, CompoundTag nbt, Entity shootingEntity) {
        super(chunk, nbt, shootingEntity);
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (!this.closed && this.age > 1200 && this.pickupMode < 1) {
            this.close();
            return false;
        }

        super.onUpdate(currentTick);
        return !this.closed;
    }
}
