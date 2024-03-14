package nukkitcoders.mobplugin;

import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.entity.Entity;
import cn.nukkit.registry.BlockEntityRegistry;
import cn.nukkit.registry.EntityRegistry;
import cn.nukkit.registry.Registries;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import cn.nukkit.registry.EntityRegistry.EntityDefinition;
import cn.nukkit.registry.RegisterException;
import com.google.common.collect.BiMap;

import java.lang.reflect.Field;

public class PluginEntityRegistry {

    public void registerEntity(EntityDefinition definition, Class<? extends Entity> entity) {
        try {
            this.removeEntity(definition.id());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        try {
            Registries.ENTITY.register(definition, entity);
        } catch (RegisterException e) {
            e.printStackTrace();
        }
    }

    public void registerBlockEntity(String name, Class<? extends BlockEntity> blockEntity) {
        try {
            this.removeBlockEntity(name);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        try {
            Registries.BLOCKENTITY.register(name, blockEntity);
        } catch (RegisterException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public void removeEntity(String id) throws NoSuchFieldException, IllegalAccessException {
        EntityRegistry registry = Registries.ENTITY;

        Field field = EntityRegistry.class.getDeclaredField("CLASS");
        field.setAccessible(true);

        Object2ObjectOpenHashMap<String, Class<? extends Entity>> CLASS = (Object2ObjectOpenHashMap<String, Class<? extends Entity>>) field.get(registry);
        CLASS.remove(id);
    }

    @SuppressWarnings("unchecked")
    public void removeBlockEntity(String name) throws NoSuchFieldException, IllegalAccessException {
        BlockEntityRegistry registry = Registries.BLOCKENTITY;
        
        Field field = BlockEntityRegistry.class.getDeclaredField("knownBlockEntities");
        field.setAccessible(true);

        BiMap<String, Class<? extends BlockEntity>> CLASS = (BiMap<String, Class<? extends BlockEntity>>) field.get(registry);
        CLASS.remove(name);
    }
}
