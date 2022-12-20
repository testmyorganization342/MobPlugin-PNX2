package nukkitcoders.mobplugin.entities.monster.walking;

import cn.nukkit.entity.mob.EntityZombie;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

@Deprecated
public class Zombie extends EntityZombie {

    public static final int NETWORK_ID = 32;

    public Zombie(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

}
