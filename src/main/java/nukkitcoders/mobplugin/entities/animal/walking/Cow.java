package nukkitcoders.mobplugin.entities.animal.walking;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.particle.ItemBreakParticle;
import cn.nukkit.nbt.tag.CompoundTag;
import nukkitcoders.mobplugin.entities.animal.WalkingAnimal;
import nukkitcoders.mobplugin.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class Cow extends WalkingAnimal {

    public static final int NETWORK_ID = 11;

    public Cow(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        if (isBaby()) {
            return 0.2f;
        }
        return 0.45f;
    }

    @Override
    public float getHeight() {
        if (isBaby()) {
            return 0.7f;
        }
        return 1.4f;
    }

    @Override
    public float getEyeHeight() {
        if (isBaby()) {
            return 0.65f;
        }
        return 1.2f;
    }

    @Override
    public boolean isBaby() {
        return getDataFlag(DATA_FLAGS, Entity.DATA_FLAG_BABY);
    }

    public void initEntity() {
        super.initEntity();
        setMaxHealth(10);
    }

    @Override
    public boolean onInteract(Player player, Item item) {
        if (item.equals(Item.get(Item.BUCKET,0),true)) {
            player.getInventory().removeItem(Item.get(Item.BUCKET,0,1));
            player.getInventory().addItem(Item.get(Item.BUCKET,1,1));
            level.addSound(this, Sound.MOB_COW_MILK);
            return true;
        }else if(item.equals(Item.get(Item.WHEAT,0)) && !isBaby()){
            player.getInventory().removeItem(Item.get(Item.WHEAT,0,1));
            level.addSound(this,Sound.RANDOM_EAT);
            level.addParticle(new ItemBreakParticle(add(0,getMountedYOffset(),0),Item.get(Item.WHEAT)));
            setInLove();
        }
        return false;
    }

    @Override
    public boolean targetOption(EntityCreature creature, double distance) {
        if (creature instanceof Player) {
            Player player = (Player) creature;
            return player.isAlive() && !player.closed && player.getInventory().getItemInHand().getId() == Item.WHEAT && distance <= 49;
        }
        return false;
    }

    public Item[] getDrops() {
        List<Item> drops = new ArrayList<>();
        if (lastDamageCause instanceof EntityDamageByEntityEvent) {

            int leatherDropCount = Utils.rand(0, 3);
            int beefDrop = Utils.rand(1, 4);

            // in any case, cow drops leather (0-2)
            for (int i = 0; i < leatherDropCount; i++) {
                drops.add(Item.get(Item.LEATHER, 0, 1));
            }
            // when cow is burning, it drops steak instead of raw beef (1-3)
            for (int i = 0; i < beefDrop; i++) {
                drops.add(Item.get(isOnFire() ? Item.STEAK : Item.RAW_BEEF, 0, 1));
            }
        }
        return drops.toArray(new Item[drops.size()]);
    }

    @Override
    public int getKillExperience() {
        return Utils.rand(1, 4);
    }

}
