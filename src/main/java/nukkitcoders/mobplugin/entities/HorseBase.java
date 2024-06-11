package nukkitcoders.mobplugin.entities;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.entity.EntityRideable;
import cn.nukkit.entity.data.EntityFlag;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.particle.ItemBreakParticle;
import cn.nukkit.math.Vector3;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.network.protocol.types.EntityLink;
import nukkitcoders.mobplugin.entities.animal.WalkingAnimal;
import nukkitcoders.mobplugin.entities.animal.walking.Donkey;
import nukkitcoders.mobplugin.utils.FastMathLite;
import nukkitcoders.mobplugin.utils.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

/**
 * @author PetteriM1
 */
public class HorseBase extends WalkingAnimal implements EntityRideable {

    private boolean saddled;

    public HorseBase(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public @NotNull String getIdentifier() {
        return HORSE;
    }

    @Override
    public int getNetworkId() {
        return -1;
    }

    @Override
    public int getKillExperience() {
        return this.isBaby() ? 0 : Utils.rand(1, 3);
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        if (this.namedTag.contains("Saddle")) {
            this.setSaddled(this.namedTag.getBoolean("Saddle"));
        }
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.namedTag.putBoolean("Saddle", this.isSaddled());
    }

    @Override
    public boolean mountEntity(Entity entity) {
        Objects.requireNonNull(entity, "The target of the mounting entity can't be null");

        if (entity.riding != null) {
            dismountEntity(entity);
            entity.resetFallDistance();
            this.motionX = 0;
            this.motionZ = 0;
            this.stayTime = 20;
        } else {
            if (isPassenger(entity)) {
                return false;
            }

            broadcastLinkPacket(entity, EntityLink.Type.RIDER);

            entity.riding = this;
            entity.setDataFlag(EntityFlag.RIDING, true);

            entity.setDataProperty(SEAT_OFFSET, new Vector3f(0, this instanceof Donkey ? 2.1f : 2.3f, 0));
            passengers.add(entity);
        }

        return true;
    }

    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        if (this.isFeedItem(item) && !this.isInLoveCooldown()) {
            this.level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_EAT);
            this.level.addParticle(new ItemBreakParticle(this.add(0,this.getMountedYOffset(), 0), Item.get(item.getId(), 0, 1)));
            this.setInLove();
            return true;
        } else if (this.canBeSaddled() && !this.isSaddled() && item.getId().equals(Item.SADDLE)) {
            player.getInventory().decreaseCount(player.getInventory().getHeldItemIndex());
            this.level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_SADDLE);
            this.setSaddled(true);
        } else if (this.passengers.isEmpty() && !this.isBaby() && !player.isSneaking() && (!this.canBeSaddled() || this.isSaddled())) {
            if (player.riding == null) {
                this.mountEntity(player);
            }
        }

        return super.onInteract(player, item, clickedPos);
    }

    @Override
    public boolean onUpdate(int currentTick) {
        Iterator<Entity> linkedIterator = this.passengers.iterator();

        while (linkedIterator.hasNext()) {
            cn.nukkit.entity.Entity linked = linkedIterator.next();

            if (!linked.isAlive()) {
                if (linked.riding == this) {
                    linked.riding = null;
                }

                linkedIterator.remove();
            }
        }

        return super.onUpdate(currentTick);
    }

    public void onPlayerInput(Player player, double strafe, double forward) {
        this.stayTime = 0;
        this.moveTime = 10;
        this.route = null;
        this.target = null;

        if (forward < 0) forward = forward / 2;

        strafe *= 0.4;

        double f = strafe * strafe + forward * forward;
        double friction = 0.6;

        this.yaw = player.yaw;

        if (f >= 1.0E-4) {
            f = Math.sqrt(f);

            if (f < 1) {
                f = 1;
            }

            f = friction / f;
            strafe = strafe * f;
            forward = forward * f;
            double f1 = FastMathLite.sin(this.yaw * 0.017453292);
            double f2 = FastMathLite.cos(this.yaw * 0.017453292);
            this.motionX = (strafe * f2 - forward * f1);
            this.motionZ = (forward * f2 + strafe * f1);
        } else {
            this.motionX = 0;
            this.motionZ = 0;
        }
    }

    @Override
    public boolean canDespawn() {
        if (this.isSaddled()) {
            return false;
        }

        return super.canDespawn();
    }

    @Override
    public void updatePassengers() {
        if (this.passengers.isEmpty()) {
            return;
        }

        for (Entity passenger : new ArrayList<>(this.passengers)) {
            if (!passenger.isAlive() || (this.getNetworkId() == 26 && Utils.entityInsideWaterFast(this))) {
                dismountEntity(passenger);
                passenger.resetFallDistance();
                continue;
            }

            updatePassengerPosition(passenger);
        }
    }

    @Override
    public boolean targetOption(EntityCreature creature, double distance) {
        return this.passengers.isEmpty();
    }

    public boolean canBeSaddled() {
        return !this.isBaby();
    }

    public boolean isSaddled() {
        return this.saddled;
    }

    public void setSaddled(boolean saddled) {
        if (this.canBeSaddled()) {
            this.saddled = saddled;
            this.setDataFlag(EntityFlag.SADDLED, saddled);
        }
    }

    public boolean isFeedItem(Item item) {
        return item.getId().equals(Item.WHEAT_SEEDS) ||
                item.getId().equals(Item.APPLE) ||
                item.getId().equals(Item.GOLDEN_APPLE) ||
                item.getId().equals(Item.SUGAR) ||
                item.getId().equals(Item.BREAD) ||
                item.getId().equals(Item.GOLDEN_CARROT);
    }

    @Override
    protected void checkTarget() {
        if (this.passengers.isEmpty()) {
            super.checkTarget();
        }
    }
}
