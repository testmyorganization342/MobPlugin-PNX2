package nukkitcoders.mobplugin.entities;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector2;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import nukkitcoders.mobplugin.entities.animal.Animal;
import nukkitcoders.mobplugin.utils.Utils;

public abstract class FlyingEntity extends BaseEntity {

    public FlyingEntity(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    protected void checkTarget() {
        if (isKnockback()) {
            return;
        }

        Vector3 target = target;
        if (!(target instanceof EntityCreature) || !targetOption((EntityCreature) target, distanceSquared(target))) {
            double near = Integer.MAX_VALUE;

            for (Entity entity : getLevel().getEntities()) {
                if (entity == this || !(entity instanceof EntityCreature) || entity instanceof Animal) {
                    continue;
                }

                EntityCreature creature = (EntityCreature) entity;
                if (creature instanceof BaseEntity && ((BaseEntity) creature).isFriendly() == isFriendly()) {
                    continue;
                }

                double distance = distanceSquared(creature);
                if (distance > near || !targetOption(creature, distance)) {
                    continue;
                }
                near = distance;

                moveTime = 0;
                target = creature;
            }
        }

        if (target instanceof EntityCreature && ((EntityCreature) target).isAlive()) {
            return;
        }

        int x, y, z;
        int maxY = Math.max(getLevel().getHighestBlockAt((int) x, (int) z) + 15, 120);
        if (stayTime > 0) {
            if (Utils.rand(1, 100) > 5) {
                return;
            }

            x = Utils.rand(10, 30);
            z = Utils.rand(10, 30);
            if (y > maxY) {
                y = Utils.rand(-12, -4);
            } else {
                y = Utils.rand(-10, 10);
            }
            target = add(Utils.rand() ? x : -x, y, Utils.rand() ? z : -z);
        } else if (Utils.rand(1, 410) == 1) {
            x = Utils.rand(10, 30);
            z = Utils.rand(10, 30);
            if (y > maxY) {
                y = Utils.rand(-12, -4);
            } else {
                y = Utils.rand(-10, 10);
            }
            stayTime = Utils.rand(90, 400);
            target = add(Utils.rand() ? x : -x, y, Utils.rand() ? z : -z);
        } else if (moveTime <= 0 || !(target instanceof Vector3)) {
            x = Utils.rand(20, 100);
            z = Utils.rand(20, 100);
            if (y > maxY) {
                y = Utils.rand(-12, -4);
            } else {
                y = Utils.rand(-10, 10);
            }
            stayTime = 0;
            moveTime = Utils.rand(300, 1200);
            target = add(Utils.rand() ? x : -x, y, Utils.rand() ? z : -z);
        }
    }

    @Override
    public Vector3 updateMove(int tickDiff) {
        if (!isImmobile()) {
            if (!isMovement()) {
                return null;
            }
    
            if (isKnockback()) {
                move(motionX * tickDiff, motionY * tickDiff, motionZ * tickDiff);
                updateMovement();
                return null;
            }
    
            if (followTarget != null && !followTarget.closed && followTarget.isAlive()) {
                double x = followTarget.x - x;
                double y = followTarget.y - y;
                double z = followTarget.z - z;
    
                double diff = Math.abs(x) + Math.abs(z);
                if (stayTime > 0 || distance(followTarget) <= (getWidth() + 0.0d) / 2 + 0.05) {
                    motionX = 0;
                    motionZ = 0;
                } else {
                    motionX = getSpeed() * 0.15 * (x / diff);
                    motionZ = getSpeed() * 0.15 * (z / diff);
                    motionY = getSpeed() * 0.27 * (y / diff);
                }
                yaw = Math.toDegrees(-Math.atan2(x / diff, z / diff));
                pitch = y == 0 ? 0 : Math.toDegrees(-Math.atan2(y, Math.sqrt(x * x + z * z)));
            }
    
            Vector3 before = target;
            checkTarget();
            if (target instanceof EntityCreature || before != target) {
                double x = target.x - x;
                double y = target.y - y;
                double z = target.z - z;
    
                double diff = Math.abs(x) + Math.abs(z);
                if (stayTime > 0 || distance(target) <= (getWidth() + 0.0d) / 2 + 0.05) {
                    motionX = 0;
                    motionZ = 0;
                } else {
                    motionX = getSpeed() * 0.15 * (x / diff);
                    motionZ = getSpeed() * 0.15 * (z / diff);
                    motionY = getSpeed() * 0.27 * (y / diff);
                }
                yaw = Math.toDegrees(-Math.atan2(x / diff, z / diff));
                pitch = y == 0 ? 0 : Math.toDegrees(-Math.atan2(y, Math.sqrt(x * x + z * z)));
            }
    
            double dx = motionX * tickDiff;
            double dy = motionY * tickDiff;
            double dz = motionZ * tickDiff;
            Vector3 target = target;
            if (stayTime > 0) {
                stayTime -= tickDiff;
                move(0, dy, 0);
            } else {
                Vector2 be = new Vector2(x + dx, z + dz);
                move(dx, dy, dz);
                Vector2 af = new Vector2(x, z);
    
                if (be.x != af.x || be.y != af.y) {
                    moveTime -= 90 * tickDiff;
                }
            }
            updateMovement();
            return target;
        }
        return null;
    }
}
