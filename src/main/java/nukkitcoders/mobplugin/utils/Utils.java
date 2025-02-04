package nukkitcoders.mobplugin.utils;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemSkull;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector3;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import nukkitcoders.mobplugin.entities.monster.walking.Skeleton;
import nukkitcoders.mobplugin.entities.monster.walking.WitherSkeleton;

import java.util.SplittableRandom;

/**
 * @author <a href="mailto:kniffman@googlemail.com">Michael Gertz (kniffo80)</a>
 */
public class Utils {

    public static final SplittableRandom random = new SplittableRandom();
    public static final int ACCORDING_X_OBTAIN_Y = 0;
    public static final int ACCORDING_Y_OBTAIN_X = 1;
    public static final IntSet monstersList = new IntOpenHashSet(new int[]{43, 40, 33, 110, 50, 38, 55, 104, 41, 49, 124, 47, 127, 124, 59, 54, 39, 34, 37, 35, 46, 105, 57, 45, 52, 48, 126, 32, 36, 44, 116});

    public static int rand(int min, int max) {
        if (min == max) {
            return max;
        }
        return random.nextInt(max + 1 - min) + min;
    }

    public static double rand(double min, double max) {
        if (min == max) {
            return max;
        }
        return min + random.nextDouble() * (max - min);
    }

    public static boolean rand() {
        return random.nextBoolean();
    }

    public static double calLinearFunction(Vector3 pos1, Vector3 pos2, double element, int type) {
        if (pos1.getFloorY() != pos2.getFloorY()) return Double.MAX_VALUE;
        if (pos1.getX() == pos2.getX()) {
            if (type == ACCORDING_Y_OBTAIN_X) return pos1.getX();
            else return Double.MAX_VALUE;
        } else if (pos1.getZ() == pos2.getZ()) {
            if (type == ACCORDING_X_OBTAIN_Y) return pos1.getZ();
            else return Double.MAX_VALUE;
        } else {
            if (type == ACCORDING_X_OBTAIN_Y) {
                return (element - pos1.getX()) * (pos1.getZ() - pos2.getZ()) / (pos1.getX() - pos2.getX()) + pos1.getZ();
            } else {
                return (element - pos1.getZ()) * (pos1.getX() - pos2.getX()) / (pos1.getZ() - pos2.getZ()) + pos1.getX();
            }
        }
    }

    public static boolean entityInsideWaterFast(Entity ent) {
        double y = ent.y + ent.getEyeHeight();
        String b = ent.level.getBlockIdAt(NukkitMath.floorDouble(ent.x), NukkitMath.floorDouble(y), NukkitMath.floorDouble(ent.z));
        return b == BlockID.FLOWING_WATER || b == BlockID.WATER;
    }

    public static boolean hasCollisionBlocks(Level level, AxisAlignedBB bb) {
        int minX = NukkitMath.floorDouble(bb.getMinX());
        int minY = NukkitMath.floorDouble(bb.getMinY());
        int minZ = NukkitMath.floorDouble(bb.getMinZ());
        int maxX = NukkitMath.ceilDouble(bb.getMaxX());
        int maxY = NukkitMath.ceilDouble(bb.getMaxY());
        int maxZ = NukkitMath.ceilDouble(bb.getMaxZ());

        for (int z = minZ; z <= maxZ; ++z) {
            for (int x = minX; x <= maxX; ++x) {
                for (int y = minY; y <= maxY; ++y) {
                    Block block = level.getBlock(x, y, z, false);
                    if (block != null && !block.getId().equals(Block.AIR) && block.collidesWithBB(bb)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static Item getMobHead(int mob) {
        switch (mob) {
            case Skeleton.NETWORK_ID:
                return Item.get("minecraft:skull", ItemSkull.SKELETON_SKULL, 1);
            case WitherSkeleton.NETWORK_ID:
                return Item.get("minecraft:skull", ItemSkull.WITHER_SKELETON_SKULL, 1);
            case 32:
                return Item.get("minecraft:skull", ItemSkull.ZOMBIE_HEAD, 1);
            case 33:
                return Item.get("minecraft:skull", ItemSkull.CREEPER_HEAD, 1);
            default:
                return null;
        }
    }
}
