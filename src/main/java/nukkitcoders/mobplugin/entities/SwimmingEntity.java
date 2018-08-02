package nukkitcoders.mobplugin.entities;

import cn.nukkit.block.BlockLiquid;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector2;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import nukkitcoders.mobplugin.MobPlugin;
import nukkitcoders.mobplugin.RouteFinderThreadPool;
import nukkitcoders.mobplugin.entities.animal.Animal;
import nukkitcoders.mobplugin.route.RouteFinder;
import nukkitcoders.mobplugin.runnable.RouteFinderSearchTask;
import nukkitcoders.mobplugin.utils.Utils;

public abstract class SwimmingEntity extends BaseEntity {

    protected RouteFinder route = null;

    public SwimmingEntity(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    protected void checkTarget() {
        if (isKnockback()) {
            return;
        }


        if (followTarget != null && !followTarget.closed && followTarget.isAlive() && targetOption((EntityCreature) followTarget,distanceSquared(followTarget)) && target!=null){
            return;
        }

        followTarget = null;

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

            stayTime = 0;
            moveTime = 0;
            followTarget = creature;
            if(route!=null)target = creature;

        }

        if (followTarget instanceof EntityCreature && !((EntityCreature) followTarget).closed && followTarget.isAlive() && targetOption((EntityCreature) followTarget, distanceSquared(followTarget)) && target != null) {
            return;
        }

        int x, z;
        if (stayTime > 0) {
            if (Utils.rand(1, 100) > 5) {
                return;
            }
            x = Utils.rand(10, 30);
            z = Utils.rand(10, 30);
            target = add(Utils.rand() ? x : -x, Utils.rand(-20, 20) / 10, Utils.rand() ? z : -z);
        } else if (Utils.rand(1, 410) == 1) {
            x = Utils.rand(10, 30);
            z = Utils.rand(10, 30);
            stayTime = Utils.rand(90, 400);
            target = add(Utils.rand() ? x : -x, Utils.rand(-20, 20) / 10, Utils.rand() ? z : -z);
        } else if (moveTime <= 0 || target == null) {
            x = Utils.rand(20, 100);
            z = Utils.rand(20, 100);
            stayTime = 0;
            moveTime = Utils.rand(300, 1200);
            target = add(Utils.rand() ? x : -x, 0, Utils.rand() ? z : -z);
        }
    }

    protected boolean checkJump(double dx, double dz) {
        if (isInsideOfWater()) {
            motionY = Utils.rand(-0.15, 0.15);
        } else {
            motionY -= getGravity();
        }
        return true;
    }

    public Vector3 updateMove(int tickDiff) {
        if (!isImmobile()) {
            if (!isMovement()) {
                return null;
            }
            if(age % 10 == 0 && route!=null && !route.isSearching()) {
                RouteFinderThreadPool.executeRouteFinderThread(new RouteFinderSearchTask(route));
                if(route.hasNext()) {
                    target = route.next();
                }
            }

            if (isKnockback()) {
                move(motionX * tickDiff, motionY, motionZ * tickDiff);
                motionY -= getGravity() * tickDiff;
                updateMovement();
                return null;
            }

            if (followTarget != null && !followTarget.closed && followTarget.isAlive() && target!=null) {

                double x = target.x - x;
                double y = target.y - y;
                double z = target.z - z;

                double diff = Math.abs(x) + Math.abs(z);
                if (stayTime > 0 || distance(followTarget) <= (getWidth() + 0.0d) / 2 + 0.05) {
                    motionX = 0;
                    motionZ = 0;
                } else {
                    motionX = getSpeed() * 0.1 * (x / diff);
                    motionZ = getSpeed() * 0.1 * (z / diff);
                }
                yaw = Math.toDegrees(-Math.atan2(x / diff, z / diff));
                pitch = y == 0 ? 0 : Math.toDegrees(-Math.atan2(y, Math.sqrt(x * x + z * z)));
            }

            Vector3 before = target;
            checkTarget();
            if (target instanceof Vector3 || before != target) {
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
                }
                yaw = Math.toDegrees(-Math.atan2(x / diff, z / diff));
                pitch = y == 0 ? 0 : Math.toDegrees(-Math.atan2(y, Math.sqrt(x * x + z * z)));
            }

            double dx = motionX * tickDiff;
            double dz = motionZ * tickDiff;
            boolean isJump = checkJump(dx, dz);
            if (stayTime > 0) {
                stayTime -= tickDiff;
                move(0, motionY * tickDiff, 0);
            } else {
                Vector2 be = new Vector2(x + dx, z + dz);
                move(dx, motionY * tickDiff, dz);
                Vector2 af = new Vector2(x, z);

                if ((be.x != af.x || be.y != af.y) && !isJump) {
                    moveTime -= 90 * tickDiff;
                }
            }

            if (!isJump) {
                if (onGround) {
                    motionY = 0;
                } else if (motionY > -getGravity() * 4) {
                    if (!(level.getBlock(new Vector3(NukkitMath.floorDouble(x), (int) (y + 0.8), NukkitMath.floorDouble(z))) instanceof BlockLiquid)) {
                        motionY -= getGravity() * 1;
                    }
                } else {
                    motionY -= getGravity() * tickDiff;
                }
            }
            updateMovement();
            if(route != null){
                if(route.hasCurrentNode() && route.hasArrivedNode(this)) {
                    if (route.hasNext()) {
                        target = route.next();
                    }
                }
            }
            return followTarget !=null ? followTarget : target ;
        }
        return null;
    }

    public RouteFinder getRoute(){
        return route;
    }

    public void setRoute(RouteFinder route){
        route = route;
    }
}
