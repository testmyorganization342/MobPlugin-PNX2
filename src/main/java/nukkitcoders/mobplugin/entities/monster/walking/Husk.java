package nukkitcoders.mobplugin.entities.monster.walking;

import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityAgeable;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.EntityEventPacket;

import nukkitcoders.mobplugin.entities.monster.WalkingMonster;
import nukkitcoders.mobplugin.route.WalkerRouteFinder;
import nukkitcoders.mobplugin.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class Husk extends WalkingMonster implements EntityAgeable {

    public static final int NETWORK_ID = 47;

    public Husk(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        route = new WalkerRouteFinder(this);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public String getName() {
        return "Husk";
    }

    @Override
    public float getWidth() {
        return 0.6f;
    }

    @Override
    public float getHeight() {
        return 1.95f;
    }

    @Override
    public double getSpeed() {
        return 1.1;
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        setDamage(new float[]{0, 3, 4, 6});
        setMaxHealth(20);
    }

    @Override
    public boolean isBaby() {
        return false;
    }

    public void setHealth(int health) {
        super.setHealth(health);

        if (isAlive()) {
            if (15 < getHealth()) {
                setDamage(new float[]{0, 2, 3, 4});
            } else if (10 < getHealth()) {
                setDamage(new float[]{0, 3, 4, 6});
            } else if (5 < getHealth()) {
                setDamage(new float[]{0, 3, 5, 7});
            } else {
                setDamage(new float[]{0, 4, 6, 9});
            }
        }
    }

    @Override
    public void attackEntity(Entity player) {
        if (attackDelay > 10 && player.distanceSquared(this) <= 1) {
            attackDelay = 0;
            player.attack(new EntityDamageByEntityEvent(this, player, DamageCause.ENTITY_ATTACK, getDamage()));
            EntityEventPacket pk = new EntityEventPacket();
            pk.eid = getId();
            pk.event = 4;
            Server.broadcastPacket(getViewers().values(), pk);
        }
    }

    @Override
    public Item[] getDrops() {
        List<Item> drops = new ArrayList<>();
        if (lastDamageCause instanceof EntityDamageByEntityEvent) {
            int rottenFlesh = Utils.rand(0, 3);
            for (int i = 0; i < rottenFlesh; i++) {
                drops.add(Item.get(Item.ROTTEN_FLESH, 0, 1));
            }
        }
        return drops.toArray(new Item[drops.size()]);
    }

    @Override
    public int getKillExperience() {
        return 5;
    }

}
