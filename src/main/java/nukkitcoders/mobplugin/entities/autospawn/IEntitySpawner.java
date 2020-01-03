package nukkitcoders.mobplugin.entities.autospawn;

import cn.nukkit.Player;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;

/**
 * @author <a href="mailto:kniffman@googlemail.com">Michael Gertz</a>
 */
public interface IEntitySpawner {

    void spawn();

    void spawn(Player player, Position pos, Level level);

    int getEntityNetworkId();
}
