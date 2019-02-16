package nukkitcoders.mobplugin.entities.autospawn;

import cn.nukkit.Player;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;

import java.util.Collection;

/**
 * @author <a href="mailto:kniffman@googlemail.com">Michael Gertz</a>
 */
public interface IEntitySpawner {

    void spawn(Collection<Player> onlinePlayers);

    SpawnResult spawn(Player player, Position pos, Level level);

    int getEntityNetworkId ();
}
