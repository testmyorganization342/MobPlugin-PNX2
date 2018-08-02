package nukkitcoders.mobplugin.entities.monster.walking;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockLiquid;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector2;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import nukkitcoders.mobplugin.entities.monster.WalkingMonster;
import nukkitcoders.mobplugin.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Spider extends WalkingMonster {

    public static final int NETWORK_ID = 35;
    
    private boolean angry = false;

    public Spider(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 1.4f;
    }

    @Override
    public float getHeight() {
        return 0.9f;
    }

    @Override
    public float getEyeHeight() {
        return 1;
    }

    @Override
    public double getSpeed() {
        return 1.13;
    }

    public void initEntity() {
        super.initEntity();

        setMaxHealth(16);
        setDamage(new float[] { 0, 2, 2, 3 });
        //setDataFlag(DATA_FLAGS,DATA_FLAG_CAN_CLIMB);
    }

    @Override
    public boolean onUpdate(int currentTick) {
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

        if (!isMovement()) {
            return true;
        }

        if (isKnockback()) {
            move(motionX * tickDiff, motionY, motionZ * tickDiff);
            motionY -= getGravity() * tickDiff;
            updateMovement();
            return true;
        }

        Vector3 before = target;
        boolean isJump = false;
        checkTarget();
        if (target instanceof EntityCreature || before != target) {
            double x = target.x - x;
            double y = target.y - y;
            double z = target.z - z;

            Vector3 target = target;
            double diff = Math.abs(x) + Math.abs(z);
            double distance = Math.sqrt(Math.pow(x - target.x, 2) + Math.pow(z - target.z, 2));
            if (distance <= 2) {
                if (target instanceof EntityCreature) {
                    if (distance <= getWidth() && y - target.y > 1 && y - target.y < 3) {
                        motionY = -getGravity() * 4;
                        if (attackDelay < 20) {
                            motionX = getSpeed() * 0.23 * (x / diff);
                            motionZ = getSpeed() * 0.23 * (z / diff);
                        } else {
                            motionX = 0;
                            motionZ = 0;
                            attackEntity((Entity) target);
                        }
                    } else {
                        if (!isFriendly() && attackDelay >= 12) {
                            y = 0;
                            isJump = true;
                            motionY = 0.08;
                        } else {
                            isJump = checkJump(motionX * tickDiff, motionZ * tickDiff);
                        }
                        motionX = getSpeed() * 0.15 * (x / diff);
                        motionZ = getSpeed() * 0.15 * (z / diff);
                    }
                } else if (distanceSquared(target) <= 1.2) {
                    moveTime = 0;
                }
            } else {
                motionX = getSpeed() * 0.15 * (x / diff);
                motionZ = getSpeed() * 0.15 * (z / diff);

                isJump = checkJump(motionX * tickDiff, motionZ * tickDiff);
            }
            yaw = Math.toDegrees(-Math.atan2(x / diff, z / diff));
            pitch = y == 0 ? 0 : Math.toDegrees(-Math.atan2(y, Math.sqrt(x * x + z * z)));
        }

        double dx = motionX * tickDiff;
        double dz = motionZ * tickDiff;
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
            } else if (motionY > -getGravity() * 4
                       && !(level.getBlock(new Vector3(NukkitMath.floorDouble(x), (int) (y + 0.8), NukkitMath.floorDouble(z))) instanceof BlockLiquid)) {
                motionY -= getGravity() * 1;
            } else {
                motionY -= getGravity() * tickDiff;
            }
        }
        updateMovement();
        return true;
    }

    @Override
    protected boolean checkJump(double dx, double dz) {
        if (motionY == getGravity() * 2) {
            return level.getBlock(new Vector3(NukkitMath.floorDouble(x), (int) y, NukkitMath.floorDouble(z))) instanceof BlockLiquid;
        } else {
            if (level.getBlock(new Vector3(NukkitMath.floorDouble(x), (int) (y + 0.8), NukkitMath.floorDouble(z))) instanceof BlockLiquid) {
                motionY = getGravity() * 2;
                return true;
            }
        }

        Block block = getLevel().getBlock(new Vector3(NukkitMath.floorDouble(x + dx), (int) y, NukkitMath.floorDouble(z + dz)));
        Block directionBlock = block.getSide(getDirection());
        if (!directionBlock.canPassThrough()) {
            motionY = getGravity() * 3;
            return true;
        }
        return false;
    }

    @Override
    public Vector3 updateMove(int tickDiff) {
        return null;
    }

    @Override
    public void attackEntity(Entity player) {
        int time = player.getLevel().getTime() % Level.TIME_FULL;
        if (!isFriendly() || !(player instanceof Player)) {
            if ((time > 13184 && time < 22800) || angry) {
            attackDelay = 0;
            HashMap<EntityDamageEvent.DamageModifier, Float> damage = new HashMap<>();
            damage.put(EntityDamageEvent.DamageModifier.BASE, getDamage());

            if (player instanceof Player) {
                @SuppressWarnings("serial")
                HashMap<Integer, Float> armorValues = new HashMap<Integer, Float>() {

                    {
                        put(Item.LEATHER_CAP, 1f);
                        put(Item.LEATHER_TUNIC, 3f);
                        put(Item.LEATHER_PANTS, 2f);
                        put(Item.LEATHER_BOOTS, 1f);
                        put(Item.CHAIN_HELMET, 1f);
                        put(Item.CHAIN_CHESTPLATE, 5f);
                        put(Item.CHAIN_LEGGINGS, 4f);
                        put(Item.CHAIN_BOOTS, 1f);
                        put(Item.GOLD_HELMET, 1f);
                        put(Item.GOLD_CHESTPLATE, 5f);
                        put(Item.GOLD_LEGGINGS, 3f);
                        put(Item.GOLD_BOOTS, 1f);
                        put(Item.IRON_HELMET, 2f);
                        put(Item.IRON_CHESTPLATE, 6f);
                        put(Item.IRON_LEGGINGS, 5f);
                        put(Item.IRON_BOOTS, 2f);
                        put(Item.DIAMOND_HELMET, 3f);
                        put(Item.DIAMOND_CHESTPLATE, 8f);
                        put(Item.DIAMOND_LEGGINGS, 6f);
                        put(Item.DIAMOND_BOOTS, 3f);
                    }
                };

                float points = 0;
                for (Item i : ((Player) player).getInventory().getArmorContents()) {
                    points += armorValues.getOrDefault(i.getId(), 0f);
                }
                damage.put(EntityDamageEvent.DamageModifier.ARMOR,
                        (float) (damage.getOrDefault(EntityDamageEvent.DamageModifier.ARMOR, 0f) - Math.floor(damage.getOrDefault(EntityDamageEvent.DamageModifier.BASE, 1f) * points * 0.04)));
            }
            player.attack(new EntityDamageByEntityEvent(this, player, EntityDamageEvent.DamageCause.ENTITY_ATTACK, damage));
            }
        }
    }
    
    @Override
    public boolean attack(EntityDamageEvent ev) {
        super.attack(ev);

        if (!ev.isCancelled()) {
            angry = true;
        }
        return true;
    }

    @Override
    public Item[] getDrops() {
        List<Item> drops = new ArrayList<>();
        if (lastDamageCause instanceof EntityDamageByEntityEvent) {
            int strings = Utils.rand(0, 3); // drops 0-2 strings
            int spiderEye = Utils.rand(0, 3) == 0 ? 1 : 0; // with a 1/3 chance it drops a spider eye
            for (int i = 0; i < strings; i++) {
                drops.add(Item.get(Item.STRING, 0, 1));
            }
            for (int i = 0; i < spiderEye; i++) {
                drops.add(Item.get(Item.SPIDER_EYE, 0, 1));
            }
        }
        return drops.toArray(new Item[drops.size()]);
    }

    @Override
    public int getKillExperience() {
        return 5; // gain 5 experience
    }

}
