package nukkitcoders.mobplugin.entities.monster.swimming;

import cn.nukkit.Player;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockSponge;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.entity.data.EntityFlag;
import cn.nukkit.entity.effect.Effect;
import cn.nukkit.entity.effect.EffectType;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.LevelEventPacket;
import nukkitcoders.mobplugin.entities.monster.SwimmingMonster;
import nukkitcoders.mobplugin.utils.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ElderGuardian extends SwimmingMonster {

    public static final int NETWORK_ID = 50;

    public ElderGuardian(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public @NotNull String getIdentifier() {
        return ELDER_GUARDIAN;
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 1.9975f;
    }

    @Override
    public float getHeight() {
        return 1.9975f;
    }

    @Override
    public void initEntity() {
        super.initEntity();

        this.setMaxHealth(80);
        this.setDataFlag(EntityFlag.ELDER, true);
    }

    @Override
    public boolean targetOption(EntityCreature creature, double distance) {
        return false;
    }

    @Override
    public void attackEntity(Entity player) {
    }

    @Override
    public Item[] getDrops() {
        List<Item> drops = new ArrayList<>();

        drops.add(Item.get(Item.PRISMARINE_SHARD, 0, Utils.rand(0, 2)));

        if (this.lastDamageCause instanceof EntityDamageByEntityEvent) {
            if (((EntityDamageByEntityEvent) this.lastDamageCause).getDamager() instanceof Player) {
                drops.add(Item.get(BlockID.SPONGE, 1));
            }
        }

        return drops.toArray(new Item[0]);
    }

    @Override
    public int getKillExperience() {
        return 10;
    }

    @Override
    public String getName() {
        return this.hasCustomName() ? this.getNameTag() : "Elder Guardian";
    }

    @Override
    public boolean entityBaseTick(int tickDiff) {
        boolean result = super.entityBaseTick(tickDiff);
        if (!this.closed && this.ticksLived % 1200 == 0 && this.isAlive()) {
            for (Player p : this.level.getPlayers().values()) {
                if (p.getGamemode() % 2 == 0 && p.distanceSquared(this) < 2500 && !p.hasEffect(EffectType.MINING_FATIGUE)) {
                    p.addEffect(Effect.get(EffectType.MINING_FATIGUE).setAmplifier(2).setDuration(6000));
                    LevelEventPacket pk = new LevelEventPacket();
                    pk.evid = LevelEventPacket.EVENT_GUARDIAN_CURSE;
                    pk.x = (float) this.x;
                    pk.y = (float) this.y;
                    pk.z = (float) this.z;
                    p.dataPacket(pk);
                }
            }
        }
        return result;
    }
}
