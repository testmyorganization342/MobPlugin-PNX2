package nukkitcoders.mobplugin.entities.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntitySpawnable;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ShortTag;
import nukkitcoders.mobplugin.MobPlugin;
import nukkitcoders.mobplugin.utils.Utils;

import java.util.ArrayList;

public class BlockEntitySpawner extends BlockEntitySpawnable{

    private int entityId = -1;
    private int spawnRange;
    private int maxNearbyEntities;
    private int requiredPlayerRange;

    private int delay = 0;

    private int minSpawnDelay;
    private int maxSpawnDelay;

    public BlockEntitySpawner(FullChunk chunk, CompoundTag nbt){
        super(chunk, nbt);

        if(namedTag.contains("EntityId")){
            entityId = namedTag.getInt("EntityId");
        }

        if(!namedTag.contains("SpawnRange") || !(namedTag.get("SpawnRange") instanceof ShortTag)){
            namedTag.putShort("SpawnRange", 8);
        }

        if(!namedTag.contains("MinSpawnDelay") || !(namedTag.get("MinSpawnDelay") instanceof ShortTag)){
            namedTag.putShort("MinSpawnDelay", 200);
        }

        if(!namedTag.contains("MaxSpawnDelay") || !(namedTag.get("MaxSpawnDelay") instanceof ShortTag)){
            namedTag.putShort("MaxSpawnDelay", 5000);
        }

        if(!namedTag.contains("MaxNearbyEntities") || !(namedTag.get("MaxNearbyEntities") instanceof ShortTag)){
            namedTag.putShort("MaxNearbyEntities", 20);
        }

        if(!namedTag.contains("RequiredPlayerRange") || !(namedTag.get("RequiredPlayerRange") instanceof ShortTag)){
            namedTag.putShort("RequiredPlayerRange", 16);
        }

        spawnRange = namedTag.getShort("SpawnRange");
        minSpawnDelay = namedTag.getInt("MinSpawnDelay");
        maxSpawnDelay = namedTag.getInt("MaxSpawnDelay");
        maxNearbyEntities = namedTag.getShort("MaxNearbyEntities");
        requiredPlayerRange = namedTag.getShort("RequiredPlayerRange");

        scheduleUpdate();
    }

    @Override
    public boolean onUpdate(){
        if(closed){
            return false;
        }

        if(delay++ >= Utils.rand(minSpawnDelay, maxSpawnDelay)){
            delay = 0;

            ArrayList<Entity> list = new ArrayList<>();
            boolean isValid = false;
            for(Entity entity : level.getEntities()){
                if(entity.distance(this) <= requiredPlayerRange){
                    if(entity instanceof Player){
                        isValid = true;
                    }
                    list.add(entity);
                }
            }

            if(isValid && list.size() <= maxNearbyEntities){
                Position pos = new Position(
                    x + Utils.rand(-spawnRange, spawnRange),
                    y,
                    z + Utils.rand(-spawnRange, spawnRange),
                    level
                );
                Entity entity = MobPlugin.create(entityId, pos);
                if(entity != null){
                    entity.spawnToAll();
                }
            }
        }
        return true;
    }

    @Override
    public void saveNBT(){
        super.saveNBT();

        namedTag.putInt("EntityId", entityId);
        namedTag.putShort("SpawnRange", spawnRange);
        namedTag.putShort("MinSpawnDelay", minSpawnDelay);
        namedTag.putShort("MaxSpawnDelay", maxSpawnDelay);
        namedTag.putShort("MaxNearbyEntities", maxNearbyEntities);
        namedTag.putShort("RequiredPlayerRange", requiredPlayerRange);
    }

    @Override
    public CompoundTag getSpawnCompound(){
        return new CompoundTag()
            .putString("id", MOB_SPAWNER)
            .putInt("EntityId", entityId)
            .putInt("x", (int) x)
            .putInt("y", (int) y)
            .putInt("z", (int) z);
    }

    @Override
    public boolean isBlockEntityValid() {
        return getBlock().getId() == Item.MONSTER_SPAWNER;
    }

    public void setSpawnEntityType(int entityId){
        entityId = entityId;
        spawnToAll();
    }

    public void setMinSpawnDelay(int minDelay){
        if(minDelay > maxSpawnDelay){
            return;
        }

        minSpawnDelay = minDelay;
    }

    public void setMaxSpawnDelay(int maxDelay){
        if(minSpawnDelay > maxDelay){
            return;
        }

        maxSpawnDelay = maxDelay;
    }

    public void setSpawnDelay(int minDelay, int maxDelay){
        if(minDelay > maxDelay){
            return;
        }

        minSpawnDelay = minDelay;
        maxSpawnDelay = maxDelay;
    }

    public void setRequiredPlayerRange(int range){
        requiredPlayerRange = range;
    }

    public void setMaxNearbyEntities(int count){
        maxNearbyEntities = count;
    }

}
