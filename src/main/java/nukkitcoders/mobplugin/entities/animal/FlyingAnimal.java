package nukkitcoders.mobplugin.entities.animal;

import cn.nukkit.Player;
import cn.nukkit.entity.EntityAgeable;
import cn.nukkit.entity.data.ShortEntityData;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.potion.Effect;
import co.aikar.timings.Timings;
import nukkitcoders.mobplugin.entities.FlyingEntity;

public abstract class FlyingAnimal extends FlyingEntity implements EntityAgeable {

    public FlyingAnimal(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        if (getDataFlag(DATA_FLAG_BABY, 0)) {
            setDataFlag(DATA_FLAG_BABY, DATA_TYPE_BYTE);
        }

    }

    @Override
    public boolean isBaby() {
        return getDataFlag(DATA_FLAG_BABY, 0);
    }

    @Override
    public boolean entityBaseTick(int tickDiff) {

        boolean hasUpdate = false;

        Timings.entityBaseTickTimer.startTiming();

        hasUpdate = super.entityBaseTick(tickDiff);

        if (!hasEffect(Effect.WATER_BREATHING) && isInsideOfWater()) {
            hasUpdate = true;
            int airTicks = getDataPropertyShort(DATA_AIR) - tickDiff;
            if (airTicks <= -20) {
                airTicks = 0;
                attack(new EntityDamageEvent(this, EntityDamageEvent.DamageCause.DROWNING, 2));
            }
            setDataProperty(new ShortEntityData(DATA_AIR, airTicks));
        } else {
            setDataProperty(new ShortEntityData(DATA_AIR, 300));
        }

        Timings.entityBaseTickTimer.stopTiming();

        return hasUpdate;
    }

    @Override
    public boolean onUpdate(int currentTick) {
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
        } else if (target != null && distanceSquared(target) <= 1) {
            moveTime = 0;
        }
        return true;
    }
}
