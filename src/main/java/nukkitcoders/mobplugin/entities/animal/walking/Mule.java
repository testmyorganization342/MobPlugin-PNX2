package nukkitcoders.mobplugin.entities.animal.walking;

import cn.nukkit.Player;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import nukkitcoders.mobplugin.entities.HorseBase;
import nukkitcoders.mobplugin.utils.Utils;

/**
 * @author <a href="mailto:kniffman@googlemail.com">Michael Gertz</a>
 */
public class Mule extends HorseBase {

    public static final int NETWORK_ID = 25;

    public Mule(FullChunk chunk, CompoundTag nbt) {
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
        super.initEntity();
        this.setMaxHealth(15);
    }

    @Override
    public boolean targetOption(EntityCreature creature, double distance) {
        boolean canTarget = super.targetOption(creature, distance);

        if (canTarget && (creature instanceof Player)) {
            Player player = (Player) creature;
            return player.spawned && player.isAlive() && !player.closed &&
                    this.isFeedItem(player.getInventory().getItemInHand()) && distance <= 40;
        }
        return false;
    }

    @Override
    public Item[] getDrops() {
        if (!this.isBaby()) {
            int c = Utils.rand(0, 2);
            if (c > 0) return new Item[]{Item.get(Item.LEATHER, 0, c)};
        }

        return new Item[0];
    }
}
