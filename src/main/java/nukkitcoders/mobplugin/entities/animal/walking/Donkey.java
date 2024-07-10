package nukkitcoders.mobplugin.entities.animal.walking;

import cn.nukkit.Player;
import cn.nukkit.block.BlockID;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.entity.data.EntityFlag;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import nukkitcoders.mobplugin.entities.HorseBase;
import nukkitcoders.mobplugin.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:kniffman@googlemail.com">Michael Gertz</a>
 */
public class Donkey extends HorseBase {

    public static final int NETWORK_ID = 24;

    private boolean chested;

    public Donkey(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        if (this.isBaby()) {
            return 0.6982f;
        }
        return 1.3965f;
    }

    @Override
    public float getHeight() {
        if (this.isBaby()) {
            return 0.8f;
        }
        return 1.6f;
    }

    @Override
    public void initEntity() {
        this.setMaxHealth(15);
        super.initEntity();

        if (this.namedTag.contains("ChestedHorse")) {
            this.setChested(this.namedTag.getBoolean("ChestedHorse"));
        }
    }

    @Override
    public boolean targetOption(EntityCreature creature, double distance) {
        boolean canTarget = super.targetOption(creature, distance);

        if (canTarget && (creature instanceof Player)) {
            Player player = (Player) creature;
            return player.spawned && player.isAlive() && !player.closed &&
                    this.isFeedItem(player.getInventory().getItemInHand()) && distance <= 49;
        }
        return false;
    }

    @Override
    public Item[] getDrops() {
        List<Item> drops = new ArrayList<>();

        if (!this.isBaby()) {
            for (int i = 0; i < Utils.rand(0, 2); i++) {
                drops.add(Item.get(Item.LEATHER, 0, 1));
            }
        }

        if (this.isChested()) {
            drops.add(Item.get(BlockID.CHEST, 0, 1));
        }

        if (this.isSaddled()) {
            drops.add(Item.get(Item.SADDLE, 0, 1));
        }

        return drops.toArray(new Item[0]);
    }

    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        if (!this.isBaby() && !this.isChested() && item.getId() == BlockID.CHEST) {
            if (!player.isCreative()) {
                player.getInventory().decreaseCount(player.getInventory().getHeldItemIndex());
            }
            this.setChested(true);
            return false;
        }

        return super.onInteract(player, item, clickedPos);
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.namedTag.putBoolean("ChestedHorse", this.isChested());
    }

    public boolean isChested() {
        return this.chested;
    }

    public void setChested(boolean chested) {
        this.chested = chested;
        this.setDataFlag(EntityFlag.CHESTED, chested);
    }
}
