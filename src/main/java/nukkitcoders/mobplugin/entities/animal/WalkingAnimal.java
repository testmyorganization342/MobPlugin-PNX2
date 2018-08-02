package nukkitcoders.mobplugin.entities.animal;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.ShortEntityData;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.particle.HeartParticle;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.potion.Effect;
import co.aikar.timings.Timings;
import nukkitcoders.mobplugin.entities.WalkingEntity;
import nukkitcoders.mobplugin.utils.Utils;

public abstract class WalkingAnimal extends WalkingEntity implements Animal {

    protected int inLoveTicks = 0;
    protected int spawnBabyDelay = 0; //TODO: spawn baby animal

    public WalkingAnimal(FullChunk chunk, CompoundTag nbt) {
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

        if(isInLove()) {
            inLoveTicks -= tickDiff;
            if (age % 20 == 0) {
                for (int i = 0; i < 3; i++) {
                    level.addParticle(new HeartParticle(add(Utils.rand(-1.0,1.0),getMountedYOffset()+ Utils.rand(-1.0,1.0),Utils.rand(-1.0,1.0))));
                }
                /*EntityEventPacket pk = new EntityEventPacket();
                pk.eid = getId();
                pk.event = 21;
                getLevel().addChunkPacket(getChunkX() >> 4,getChunkZ() >> 4,pk);*/
            }
        }

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

    public boolean onInteract(Entity entity, Item item) {
        //TODO: mating

        return false;
    }

    public void setInLove() {
        inLoveTicks = 600;
        setDataFlag(DATA_FLAGS, DATA_FLAG_INLOVE);
    }

    public boolean isInLove(){
        return inLoveTicks > 0;
    }

    public boolean isBreedingItem(Item item) {
        return item != null && item.getId() == Item.WHEAT;
    }
}
