package nukkitcoders.mobplugin.entities.animal;

import cn.nukkit.Player;
import cn.nukkit.entity.data.ShortEntityData;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import co.aikar.timings.Timings;
import nukkitcoders.mobplugin.entities.SwimmingEntity;

public abstract class SwimmingAnimal extends SwimmingEntity implements Animal {

    public SwimmingAnimal(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        route = null;
    }

    @Override
    public double getSpeed() {
        return 0.8;
    }

    @Override
    protected void initEntity() {
        super.initEntity();
    }

    @Override
    public boolean entityBaseTick(int tickDiff) {
        boolean hasUpdate;
        Timings.entityBaseTickTimer.startTiming();

        hasUpdate = super.entityBaseTick(tickDiff);

        setDataProperty(new ShortEntityData(DATA_AIR, 300));

        Timings.entityBaseTickTimer.stopTiming();
        return hasUpdate;
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (closed) {
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

        Vector3 target = updateMove(tickDiff);
        if (target instanceof Player) {
            if (distanceSquared(target) <= 2) {
                pitch = 22;
                x = lastX;
                y = lastY;
                z = lastZ;
            }
        } else if (target != null && (Math.pow(x - target.x, 2) + Math.pow(z - target.z, 2)) <= 1) {
            moveTime = 0;
        }
        return true;
    }

    @Override
    public boolean isBaby() {
        return false;
    }
}
