/**
 * MobPluginItems.java
 * 
 * Created on 15:36:32
 */
package de.kniffo80.mobplugin.items;

import cn.nukkit.item.ItemEdible;

/**
 * @author <a href="mailto:kniffman@googlemail.com">Michael Gertz (kniffo80)</a>
 */
public abstract class MobPluginItems extends ItemEdible {

    public static final int INK_SAC       = 351;

    public MobPluginItems(int networkId, Integer meta, int count, String name) {
        super(networkId, meta, count, name);
    }

}
