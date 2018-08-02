package nukkitcoders.mobplugin.entities.animal.walking;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.entity.data.ByteEntityData;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDye;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.particle.ItemBreakParticle;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.DyeColor;
import nukkitcoders.mobplugin.entities.animal.WalkingAnimal;
import nukkitcoders.mobplugin.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Sheep extends WalkingAnimal {

    public static final int NETWORK_ID = 13;

    public boolean sheared = false;
    public int color = 0;

    public Sheep(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        if (isBaby()) {
            return 0.45f;
        }
        return 0.9f;
    }

    @Override
    public float getHeight() {
        if (isBaby()) {
            return 0.65f;
        }
        return 1.3f;
    }

    @Override
    public float getEyeHeight() {
        if (isBaby()) {
            return 0.65f;
        }
        return 1.1f;
    }

    @Override
    public boolean isBaby() {
        return getDataFlag(DATA_FLAGS, Entity.DATA_FLAG_BABY);
    }

    @Override
    public void initEntity() {
        setMaxHealth(8);

        if (!namedTag.contains("Color")) {
            setColor(randomColor());
        } else {
            setColor(namedTag.getByte("Color"));
        }

        if (!namedTag.contains("Sheared")) {
            namedTag.putByte("Sheared", 0);
        } else {
            sheared = namedTag.getBoolean("Sheared");
        }

        setDataFlag(DATA_FLAGS, DATA_FLAG_SHEARED, sheared);
    }

    public void saveNBT() {
        super.saveNBT();
        namedTag.putByte("Color", color);
        namedTag.putBoolean("Sheared", sheared);
    }

    @Override
    public boolean onInteract(Player player, Item item) {
        if (item.getId() == Item.DYE) {
            setColor(((ItemDye) item).getDyeColor().getWoolData());;
            return true;
        }else if(item.equals(Item.get(Item.WHEAT,0,1)) && !isBaby()){
            player.getInventory().removeItem(Item.get(Item.WHEAT,0,1));
            level.addSound(this,Sound.RANDOM_EAT);
            level.addParticle(new ItemBreakParticle(add(0,getMountedYOffset(),0),Item.get(Item.WHEAT)));
            setInLove();
            return true;
        }else if(item.equals(Item.get(Item.SHEARS,0,1),false) && !isBaby() && !sheared){
            shear();
            level.addSound(this,Sound.MOB_SHEEP_SHEAR);
            player.getInventory().getItemInHand().setDamage(item.getDamage() + 1);
            return true;
        }
        return false;

    }

    public void shear() {
        sheared = true;
        setDataFlag(DATA_FLAGS, DATA_FLAG_SHEARED, true);
        level.dropItem(this, Item.get(Item.WOOL, getColor(), Utils.rand(0,4)));
    }

    @Override
    public boolean targetOption(EntityCreature creature, double distance) {
        if (creature instanceof Player) {
            Player player = (Player) creature;
            return player.spawned && player.isAlive() && !player.closed && player.getInventory().getItemInHand().getId() == Item.WHEAT && distance <= 49;
        }
        return false;
    }

    @Override
    public Item[] getDrops() {
        List<Item> drops = new ArrayList<>();
        if (lastDamageCause instanceof EntityDamageByEntityEvent) {
            drops.add(Item.get(Item.WOOL, namedTag.getByte("Color"), 1)); // each time drops 1 wool
            int muttonDrop = Utils.rand(1, 3); // drops 1-2 muttons / cooked muttons
            for (int i = 0; i < muttonDrop; i++) {
                drops.add(Item.get(isOnFire() ? Item.COOKED_MUTTON : Item.RAW_MUTTON, 0, 1));
            }
        }
        return drops.toArray(new Item[drops.size()]);
    }


    public void setColor(int color) {
        color = color;
        namedTag.putByte("Color",color);
        setDataProperty(new ByteEntityData(DATA_COLOUR, color));
    }

    public int getColor() {
        return namedTag.getByte("Color");
    }

    private int randomColor() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int rand = random.nextInt(0, 2500);

        if(rand < 125 && 0 <= rand)return DyeColor.BLACK.getDyeData();
        else if(rand < 250 && 125 <= rand)return DyeColor.GRAY.getDyeData();
        else if(rand < 375 && 250 <= rand)return DyeColor.LIGHT_GRAY.getDyeData();
        else if(rand < 500 && 375 <= rand)return DyeColor.GRAY.getDyeData();
        else if(rand < 541 && 500 <= rand)return DyeColor.PINK.getDyeData();
        else return DyeColor.WHITE.getDyeData();
    }

    @Override
    public int getKillExperience() {
        return Utils.rand(1, 4); // gain 1-3 experience
    }

}
