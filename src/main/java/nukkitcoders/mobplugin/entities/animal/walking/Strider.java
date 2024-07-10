package nukkitcoders.mobplugin.entities.animal.walking;

import cn.nukkit.Player;
import cn.nukkit.block.BlockID;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.entity.EntityRideable;
import cn.nukkit.entity.data.EntityFlag;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.network.protocol.types.EntityLink;
import nukkitcoders.mobplugin.entities.animal.WalkingAnimal;
import nukkitcoders.mobplugin.utils.FastMathLite;
import nukkitcoders.mobplugin.utils.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class Strider extends WalkingAnimal implements EntityRideable {

    public final static int NETWORK_ID = 125;

    private boolean saddled;

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    public Strider(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public @NotNull String getIdentifier() {
        return STRIDER;
    }

    @Override
    public int getKillExperience() {
        return this.isBaby() ? 0 : Utils.rand(1, 3);
    }

    @Override
    protected void initEntity() {
        this.setMaxHealth(20);
        super.initEntity();
        this.fireProof = true;

        if (this.namedTag.contains("Saddle")) {
            this.setSaddled(this.namedTag.getBoolean("Saddle"));
        }
    }

    @Override
    public float getWidth() {
        return 0.9f;
    }

    @Override
    public float getHeight() {
        return 1.7f;
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
            if (entity instanceof Player && ((Player) entity).isSleeping()) {
                return false;
            }

            if (isPassenger(entity)) {
                return false;
            }

            broadcastLinkPacket(entity, EntityLink.Type.RIDER);

            entity.riding = this;
            entity.setDataFlag(EntityFlag.RIDING, true);
            entity.setDataProperty(SEAT_OFFSET, new Vector3f(0, 2.8f, 0));
            entity.setDataProperty(SEAT_ROTATION_OFFSET_DEGREES, 181);
            passengers.add(entity);
        }

        return true;
    }

    @Override
    public boolean onUpdate(int currentTick) {
        Iterator<Entity> linkedIterator = this.passengers.iterator();

        while (linkedIterator.hasNext()) {
            Entity linked = linkedIterator.next();

            if (!linked.isAlive()) {
                if (linked.riding == this) {
                    linked.riding = null;
                }

                linkedIterator.remove();
            }
        }

        return super.onUpdate(currentTick);
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        this.namedTag.putBoolean("Saddle", this.isSaddled());
    }

    public boolean isSaddled() {
        return this.saddled;
    }

    public void setSaddled(boolean saddled) {
        this.saddled = saddled;
        this.setDataFlag(EntityFlag.SADDLED, saddled);
    }

    public void onPlayerInput(Player player, double strafe, double forward) {
        if (player.getInventory().getItemInHand().getId().equals(Item.WARPED_FUNGUS_ON_A_STICK)) {
            this.stayTime = 0;
            this.moveTime = 10;
            this.route = null;
            this.target = null;
            this.yaw = player.yaw;

            strafe *= 0.4;

            double f = strafe * strafe + forward * forward;
            double friction = 0.3;

            if (f >= 1.0E-4) {
                f = Math.sqrt(f);

                if (f < 1) {
                    f = 1;
                }

                f = friction / f;
                strafe *= f;
                forward *= f;
                double f1 = FastMathLite.sin(this.yaw * 0.017453292);
                double f2 = FastMathLite.cos(this.yaw * 0.017453292);
                this.motionX = (strafe * f2 - forward * f1);
                this.motionZ = (forward * f2 + strafe * f1);
            } else {
                this.motionX = 0;
                this.motionZ = 0;
            }
        }
    }

    @Override
    protected void checkTarget() {
        if (this.passengers.isEmpty() || !(this.getPassengers().get(0) instanceof Player) || ((Player) this.getPassengers().get(0)).getInventory().getItemInHand().getId() != Item.WARPED_FUNGUS_ON_A_STICK) {
            super.checkTarget();
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
            if (!passenger.isAlive() || Utils.entityInsideWaterFast(this)) {
                dismountEntity(passenger);
                passenger.resetFallDistance();
                continue;
            }

            updatePassengerPosition(passenger);
        }
    }

    @Override
    public Item[] getDrops() {
        List<Item> drops = new ArrayList<>();

        if (!this.isBaby()) {
            for (int i = 0; i < Utils.rand(2, 5); i++) {
                drops.add(Item.get(Item.STRING, 0, 1));
            }
        }

        if (this.isSaddled()) {
            drops.add(Item.get(Item.SADDLE, 0, 1));
        }

        return drops.toArray(new Item[0]);
    }

    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        if (item.getId() == Item.SADDLE && !this.isSaddled() && !this.isBaby()) {
            player.getInventory().decreaseCount(player.getInventory().getHeldItemIndex());
            this.level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_SADDLE);
            this.setSaddled(true);
        } else if (this.isSaddled() && this.passengers.isEmpty() && !this.isBaby() && !player.isSneaking()) {
            if (player.riding == null) {
                this.mountEntity(player);
            }
        }
        return super.onInteract(player, item, clickedPos);
    }

    @Override
    public boolean targetOption(EntityCreature creature, double distance) {
        if (creature instanceof Player) {
            Player player = (Player) creature;
            return player.spawned && player.isAlive() && !player.closed && distance <= 49
                    && player.getInventory().getItemInHand().getId() == Item.WARPED_FUNGUS_ON_A_STICK;
        }
        return false;
    }

    @Override
    protected boolean canSwimIn(String block) {
        return block.equals(BlockID.WATER) || block.equals(BlockID.FLOWING_WATER) || block.equals(BlockID.LAVA) || block.equals(BlockID.FLOWING_LAVA);
    }
}
