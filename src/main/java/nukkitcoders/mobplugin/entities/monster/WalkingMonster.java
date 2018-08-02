package nukkitcoders.mobplugin.entities.monster;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.BlockWater;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.ShortEntityData;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.potion.Effect;
import co.aikar.timings.Timings;
import nukkitcoders.mobplugin.entities.WalkingEntity;
import nukkitcoders.mobplugin.entities.monster.walking.Enderman;
import nukkitcoders.mobplugin.utils.Utils;

public abstract class WalkingMonster extends WalkingEntity implements Monster {

    protected float[]   minDamage;

    protected float[]   maxDamage;

    protected int   attackDelay = 0;

    protected boolean canAttack   = true;

    public WalkingMonster(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        //route = new WalkerRouteFinder(this);
    }

    @Override
    public void setFollowTarget(Entity target) {
        setFollowTarget(target, true);
    }

    public void setFollowTarget(Entity target, boolean attack) {
        super.setFollowTarget(target);
        canAttack = attack;
    }

    public float getDamage() {
        return getDamage(null);
    }

    public float getDamage(Integer difficulty) {
        return Utils.rand(getMinDamage(difficulty), getMaxDamage(difficulty));
    }

    public float getMinDamage() {
        return getMinDamage(null);
    }

    public float getMinDamage(Integer difficulty) {
        if (difficulty == null || difficulty > 3 || difficulty < 0) {
            difficulty = Server.getInstance().getDifficulty();
        }
        return minDamage[difficulty];
    }

    public float getMaxDamage() {
        return getMaxDamage(null);
    }

    public float getMaxDamage(Integer difficulty) {
        if (difficulty == null || difficulty > 3 || difficulty < 0) {
            difficulty = Server.getInstance().getDifficulty();
        }
        return maxDamage[difficulty];
    }

    public void setDamage(float damage) {
        setDamage(damage, Server.getInstance().getDifficulty());
    }

    public void setDamage(float damage, int difficulty) {
        if (difficulty >= 1 && difficulty <= 3) {
            minDamage[difficulty] = damage;
            maxDamage[difficulty] = damage;
        }
    }

    public void setDamage(float[] damage) {
        if (damage.length < 4) {
            return;
        }

        if (minDamage == null || minDamage.length < 4) {
            minDamage = new float[] { 0, 0, 0, 0 };
        }

        if (maxDamage == null || maxDamage.length < 4) {
            maxDamage = new float[] { 0, 0, 0, 0 };
        }

        for (int i = 0; i < 4; i++) {
            minDamage[i] = damage[i];
            maxDamage[i] = damage[i];
        }
    }

    public void setMinDamage(float[] damage) {
        if (damage.length < 4) {
            return;
        }

        for (int i = 0; i < 4; i++) {
            setMinDamage(Math.min(damage[i], getMaxDamage(i)), i);
        }
    }

    public void setMinDamage(float damage) {
        setMinDamage(damage, Server.getInstance().getDifficulty());
    }

    public void setMinDamage(float damage, int difficulty) {
        if (difficulty >= 1 && difficulty <= 3) {
            minDamage[difficulty] = Math.min(damage, getMaxDamage(difficulty));
        }
    }

    public void setMaxDamage(float[] damage) {
        if (damage.length < 4)
            return;

        for (int i = 0; i < 4; i++) {
            setMaxDamage(Math.max(damage[i], getMinDamage(i)), i);
        }
    }

    public void setMaxDamage(float damage) {
        setMinDamage(damage, Server.getInstance().getDifficulty());
    }

    public void setMaxDamage(float damage, int difficulty) {
        if (difficulty >= 1 && difficulty <= 3) {
            maxDamage[difficulty] = Math.max(damage, getMinDamage(difficulty));
        }
    }

    public boolean onUpdate(int currentTick) {
        if (closed) {
            return false;
        }

        if (server.getDifficulty() < 1) {
            close();
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
        if ((!isFriendly() || !(target instanceof Player)) && target instanceof Entity) {
            if (target != followTarget || canAttack) {
                attackEntity((Entity) target);
            }
        } else if (target != null && (Math.pow(x - target.x, 2) + Math.pow(z - target.z, 2)) <= 1) {
            moveTime = 0;
        }
        return true;
    }

    @Override
    public boolean entityBaseTick(int tickDiff) {

        boolean hasUpdate;

        Timings.entityBaseTickTimer.startTiming();

        hasUpdate = super.entityBaseTick(tickDiff);

        attackDelay += tickDiff;
        if (this instanceof Enderman) {
            if (level.getBlock(new Vector3(NukkitMath.floorDouble(x), (int) y, NukkitMath.floorDouble(z))) instanceof BlockWater) {
                attack(new EntityDamageEvent(this, EntityDamageEvent.DamageCause.DROWNING, 2));
                move(Utils.rand(-20, 20), Utils.rand(-20, 20), Utils.rand(-20, 20));
            }
        } else {
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
        }

        Timings.entityBaseTickTimer.stopTiming();
        return hasUpdate;
    }
}
