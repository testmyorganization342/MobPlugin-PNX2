package nukkitcoders.mobplugin.entities.animal;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.particle.HeartParticle;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import nukkitcoders.mobplugin.entities.BaseEntity;
import nukkitcoders.mobplugin.entities.WalkingEntity;
import nukkitcoders.mobplugin.utils.Utils;

public abstract class WalkingAnimal extends WalkingEntity implements Animal {

    protected int inLoveTicks = 0;
    private short inLoveCooldown = 0;

    private int panicTicks = 0;

    public WalkingAnimal(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        this.route = null;
    }

    @Override
    public boolean entityBaseTick(int tickDiff) {
        boolean hasUpdate = super.entityBaseTick(tickDiff);

        if (this.isInLove()) {
            this.inLoveTicks -= tickDiff;
            if (this.age % 20 == 0) {
                for (int i = 0; i < 3; i++) {
                    this.level.addParticle(new HeartParticle(this.add(Utils.rand(-1.0,1.0),this.getMountedYOffset()+ Utils.rand(-1.0,1.0),Utils.rand(-1.0,1.0))));
                }
                Entity[] collidingEntities = this.level.getCollidingEntities(this.boundingBox.grow(0.5d, 0.5d, 0.5d));
                for (Entity entity : collidingEntities) {
                    if (this.checkSpawnBaby(entity)) {
                        break;
                    }
                }
            }
        }else if (this.isInLoveCooldown()) {
            this.inLoveCooldown -= tickDiff;
        }

        return hasUpdate;
    }

    protected boolean checkSpawnBaby(Entity entity) {
        if (!(entity instanceof WalkingAnimal walkingAnimal) || entity.getNetworkId() != this.getNetworkId()) {
            return false;
        }
        if (!walkingAnimal.isInLove() || walkingAnimal.isBaby() || walkingAnimal.age <= 0) {
            return false;
        }

        this.setInLove(false);
        walkingAnimal.setInLove(false);

        this.setInLoveCooldown((short) 1200);
        walkingAnimal.setInLoveCooldown((short) 1200);

        this.stayTime = 60;
        walkingAnimal.stayTime = 60;

        int i = 0;
        for (Entity entity2 : this.chunk.getEntities().values()) {
            if (entity2.getNetworkId() == getNetworkId()) {
                i++;
                if (i > 10) {
                    return true;
                }
            }
        }

        BaseEntity newEntity = (BaseEntity) Entity.createEntity(getNetworkId(), this, new Object[0]);
        newEntity.setBaby(true);
        newEntity.spawnToAll();
        return true;
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (this.closed) {
            return false;
        }
        if (!this.isAlive()) {
            if (++this.deadTicks >= 23) {
                this.close();
                return false;
            }
            return true;
        }

        if (this.panicTicks > 0) {
            this.panicTicks--;
            if (panicTicks == 0) {
                doPanic(false);
            }
        }

        int tickDiff = currentTick - this.lastUpdate;
        this.lastUpdate = currentTick;
        this.entityBaseTick(tickDiff);

        Vector3 target = this.updateMove(tickDiff);
        if (target instanceof Player) {
            if (this.distanceSquared(target) <= 2) {
                this.x = this.lastX;
                this.y = this.lastY;
                this.z = this.lastZ;
            }
        }
        return true;
    }

    public void setInLove() {
        this.setInLove(true);
    }

    public void setInLove(boolean inLove) {
        if (inLove && !this.isBaby()) {
            this.inLoveTicks = 600;
            this.setDataFlag(DATA_FLAGS, DATA_FLAG_INLOVE, true);
        }else {
            this.inLoveTicks = 0;
            this.setDataFlag(DATA_FLAGS, DATA_FLAG_INLOVE, false);
        }
    }

    public boolean isInLove() {
        return inLoveTicks > 0;
    }

    public void setInLoveCooldown(short inLoveCooldown) {
        this.inLoveCooldown = inLoveCooldown;
    }

    public boolean isInLoveCooldown() {
        return this.inLoveCooldown > 0;
    }

    public boolean isBreedingItem(Item item) {
        return item != null && item.getId() == Item.WHEAT;
    }

    public void doPanic(boolean panic) {
        if (panic) {
            int time = Utils.rand(60, 100);
            this.panicTicks = time;
            this.stayTime = 0;
            this.moveTime = time;
            this.moveMultiplier = 1.8f;
        } else {
            this.moveMultiplier = 1.0f;
        }
    }

    @Override
    public boolean attack(EntityDamageEvent ev) {
        boolean result = super.attack(ev);

        if (result && !ev.isCancelled()) {
            this.doPanic(true);
        }

        return result;
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        if (this.isInLove()) {
            this.namedTag.putShort("inLoveTicks", this.inLoveTicks);
        }
        if (this.isInLoveCooldown()) {
            this.namedTag.putShort("inLoveCooldown", this.inLoveCooldown);
        }
    }

    @Override
    public boolean canTarget(Entity entity) {
        return (this.isInLove() || entity instanceof Player);
    }
}
