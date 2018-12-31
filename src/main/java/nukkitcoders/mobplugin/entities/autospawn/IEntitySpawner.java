package nukkitcoders.mobplugin.entities.autospawn;

import cn.nukkit.Player;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;

import java.util.Collection;

/**
 * @author <a href="mailto:kniffman@googlemail.com">Michael Gertz</a>
 */
public interface IEntitySpawner {

    public void spawn(Collection<Player> onlinePlayers);

    public SpawnResult spawn(Player player, Position pos, Level level);

    public int getEntityNetworkId ();

    public String getEntityName ();
}
