package nukkitcoders.mobplugin.entities.animal;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.particle.HeartParticle;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import nukkitcoders.mobplugin.MobPlugin;
import nukkitcoders.mobplugin.entities.BaseEntity;
import nukkitcoders.mobplugin.entities.WalkingEntity;
import nukkitcoders.mobplugin.utils.Utils;

public abstract class WalkingAnimal extends WalkingEntity implements Animal {

    protected short inLoveTicks = 0;

    protected short inLoveCooldown = 0;

    private int panicTicks = 0;

    protected Player lastInteract;

    public WalkingAnimal(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        this.route = null;
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        if (this.namedTag.contains("InLoveTicks")) {
            this.inLoveTicks = (short) this.namedTag.getShort("InLoveTicks");
        }

        if (this.namedTag.contains("InLoveCooldown")) {
            this.inLoveCooldown = (short) this.namedTag.getShort("InLoveCooldown");
        }
    }

    @Override
    public boolean entityBaseTick(int tickDiff) {
        boolean hasUpdate = super.entityBaseTick(tickDiff);

        if (this.isBaby() && this.age > 0) {
            this.setBaby(false);
        }

        if (this.isInLove()) {
            this.inLoveTicks -= tickDiff;
            if (!this.isBaby() && this.age > 0 && this.age % 20 == 0) {
                for (int i = 0; i < 3; i++) {
                    this.level.addParticle(new HeartParticle(this.add(Utils.rand(-1.0, 1.0), this.getMountedYOffset() + Utils.rand(-1.0, 1.0), Utils.rand(-1.0, 1.0))));
                }
                if (MobPlugin.getInstance().config.allowBreeding) {
                    Entity[] colliding = level.getCollidingEntities(this.boundingBox.grow(0.5f, 0.5f, 0.5f));
                    for (Entity entity : colliding) {
                        if (entity != this && entity != null && this.tryBreedWih(entity)) {
                            break;
                        }
                    }
                }
            }
        } else if (this.isInLoveCooldown()) {
            this.inLoveCooldown -= tickDiff;
        }

        return hasUpdate;
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
                this.doPanic(false);
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
        if (inLove) {
            if (!this.isBaby()) {
                this.inLoveTicks = 600;
                //this.setDataFlag(DATA_FLAGS, DATA_FLAG_INLOVE, true);
            }
        } else {
            this.inLoveTicks = 0;
            //this.setDataFlag(DATA_FLAGS, DATA_FLAG_INLOVE, false);
        }
    }

    public boolean isInLove() {
        return inLoveTicks > 0;
    }

    public boolean isBreedingItem(Item item) {
        return item != null && item.getId() == Item.WHEAT;
    }

    public int getPanicTicks() {
        return this.panicTicks;
    }

    public void doPanic(boolean panic) {
        if (panic) {
            int time = Utils.rand(60, 100);
            this.panicTicks = time;
            this.stayTime = 0;
            this.moveTime = time;
            this.moveMultiplier = 1.8f;
        } else {
            this.panicTicks = 0;
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
        if (this.isInLove()) this.namedTag.putShort("InLoveTicks", this.inLoveTicks);
        if (this.isInLoveCooldown()) this.namedTag.putShort("InLoveCooldown", this.inLoveCooldown);
    }

    public boolean isInLoveCooldown() {
        return inLoveCooldown > 0;
    }

    protected boolean tryBreedWih(Entity entity) {
        if (entity instanceof WalkingAnimal && entity.getNetworkId() == this.getNetworkId()) {
            WalkingAnimal be = (WalkingAnimal) entity;
            if (be.isInLove() && !be.isBaby() && be.age > 0) {
                be.lastInteract = null;
                this.setInLove(false);
                be.setInLove(false);
                this.inLoveCooldown = 1200;
                be.inLoveCooldown = 1200;
                this.stayTime = 60;
                be.stayTime = 60;
                BaseEntity baby = (BaseEntity) Entity.createEntity(this.getNetworkId(), this);
                baby.setBaby(true);
                baby.spawnToAll();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean targetOption(EntityCreature creature, double distance) {
        if (this.isInLove()) {
            return creature instanceof WalkingAnimal && ((WalkingAnimal) creature).isInLove() && creature.isAlive() && !creature.closed && creature.getNetworkId() == this.getNetworkId() && distance <= 100;
        }
        return false;
    }
}
