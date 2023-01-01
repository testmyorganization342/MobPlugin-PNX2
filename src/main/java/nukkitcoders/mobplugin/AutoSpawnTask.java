package nukkitcoders.mobplugin;

import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.entity.mob.EntityCreeper;
import cn.nukkit.entity.mob.EntityZombie;
import cn.nukkit.entity.passive.*;
import cn.nukkit.event.entity.CreatureSpawnEvent;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Config;
import nukkitcoders.mobplugin.entities.animal.flying.Bat;
import nukkitcoders.mobplugin.entities.animal.flying.Parrot;
import nukkitcoders.mobplugin.entities.animal.jumping.Rabbit;
import nukkitcoders.mobplugin.entities.animal.swimming.Dolphin;
import nukkitcoders.mobplugin.entities.animal.swimming.Pufferfish;
import nukkitcoders.mobplugin.entities.animal.swimming.Squid;
import nukkitcoders.mobplugin.entities.animal.swimming.Turtle;
import nukkitcoders.mobplugin.entities.animal.walking.*;
import nukkitcoders.mobplugin.entities.autospawn.IEntitySpawner;
import nukkitcoders.mobplugin.entities.monster.flying.Blaze;
import nukkitcoders.mobplugin.entities.monster.flying.Ghast;
import nukkitcoders.mobplugin.entities.monster.jumping.MagmaCube;
import nukkitcoders.mobplugin.entities.monster.jumping.Slime;
import nukkitcoders.mobplugin.entities.monster.walking.*;
import nukkitcoders.mobplugin.entities.spawners.*;
import nukkitcoders.mobplugin.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AutoSpawnTask implements Runnable {

    private final Map<Integer, Integer> maxSpawns = new HashMap<>();

    private final List<IEntitySpawner> animalSpawners = new ArrayList<>();
    private final List<IEntitySpawner> monsterSpawners = new ArrayList<>();

    private final Config pluginConfig;

    private final MobPlugin plugin;

    private boolean mobsNext;

    public AutoSpawnTask(MobPlugin plugin, Config pluginConfig) {
        this.pluginConfig = pluginConfig;
        this.plugin = plugin;

        prepareMaxSpawns();
        prepareSpawnerClasses();
    }

    @Override
    public void run() {
        if (!plugin.getServer().getOnlinePlayers().isEmpty()) {
            if (mobsNext) {
                mobsNext = false;
                for (IEntitySpawner spawner : monsterSpawners) {
                    spawner.spawn();
                }
            } else {
                mobsNext = true;
                for (IEntitySpawner spawner : animalSpawners) {
                    spawner.spawn();
                }
            }
        }
    }

    private void prepareSpawnerClasses() {
        animalSpawners.add(new BatSpawner(this));
        monsterSpawners.add(new BlazeSpawner(this));
        animalSpawners.add(new ChickenSpawner(this));
        animalSpawners.add(new CodSpawner(this));
        animalSpawners.add(new CowSpawner(this));
        monsterSpawners.add(new CreeperSpawner(this));
        animalSpawners.add(new DolphinSpawner(this));
        animalSpawners.add(new DonkeySpawner(this));
        monsterSpawners.add(new EndermanSpawner(this));
        monsterSpawners.add(new GhastSpawner(this));
        animalSpawners.add(new HorseSpawner(this));
        monsterSpawners.add(new HuskSpawner(this));
        monsterSpawners.add(new MagmaCubeSpawner(this));
        animalSpawners.add(new MooshroomSpawner(this));
        animalSpawners.add(new OcelotSpawner(this));
        animalSpawners.add(new ParrotSpawner(this));
        animalSpawners.add(new PigSpawner(this));
        animalSpawners.add(new PolarBearSpawner(this));
        animalSpawners.add(new PufferfishSpawner(this));
        animalSpawners.add(new RabbitSpawner(this));
        animalSpawners.add(new SalmonSpawner(this));
        animalSpawners.add(new SheepSpawner(this));
        monsterSpawners.add(new SkeletonSpawner(this));
        monsterSpawners.add(new SlimeSpawner(this));
        monsterSpawners.add(new SpiderSpawner(this));
        monsterSpawners.add(new StraySpawner(this));
        animalSpawners.add(new SquidSpawner(this));
        animalSpawners.add(new TropicalFishSpawner(this));
        animalSpawners.add(new TurtleSpawner(this));
        monsterSpawners.add(new WitchSpawner(this));
        monsterSpawners.add(new WitherSkeletonSpawner(this));
        animalSpawners.add(new WolfSpawner(this));
        monsterSpawners.add(new ZombieSpawner(this));
        monsterSpawners.add(new ZombiePigmanSpawner(this));
        animalSpawners.add(new FoxSpawner(this));
        animalSpawners.add(new PandaSpawner(this));
        monsterSpawners.add(new DrownedSpawner(this));
        monsterSpawners.add(new PiglinSpawner(this));
        monsterSpawners.add(new HoglinSpawner(this));
        animalSpawners.add(new LlamaSpawner(this));
        animalSpawners.add(new StriderSpawner(this));
    }

    private void prepareMaxSpawns() {
        maxSpawns.put(Bat.NETWORK_ID, this.pluginConfig.getInt("autospawn.bat"));
        maxSpawns.put(Blaze.NETWORK_ID, this.pluginConfig.getInt("autospawn.blaze"));
        maxSpawns.put(EntityChicken.NETWORK_ID, this.pluginConfig.getInt("autospawn.chicken"));
        maxSpawns.put(EntityCod.NETWORK_ID, this.pluginConfig.getInt("autospawn.cod"));
        maxSpawns.put(EntityCow.NETWORK_ID, this.pluginConfig.getInt("autospawn.cow"));
        maxSpawns.put(EntityCreeper.NETWORK_ID, this.pluginConfig.getInt("autospawn.creeper"));
        maxSpawns.put(Dolphin.NETWORK_ID, this.pluginConfig.getInt("autospawn.dolphin"));
        maxSpawns.put(Donkey.NETWORK_ID, this.pluginConfig.getInt("autospawn.donkey"));
        maxSpawns.put(Enderman.NETWORK_ID, this.pluginConfig.getInt("autospawn.enderman"));
        maxSpawns.put(Ghast.NETWORK_ID, this.pluginConfig.getInt("autospawn.ghast"));
        maxSpawns.put(Horse.NETWORK_ID, this.pluginConfig.getInt("autospawn.horse"));
        maxSpawns.put(Husk.NETWORK_ID, this.pluginConfig.getInt("autospawn.husk"));
        maxSpawns.put(MagmaCube.NETWORK_ID, this.pluginConfig.getInt("autospawn.magmacube"));
        maxSpawns.put(EntityMooshroom.NETWORK_ID, this.pluginConfig.getInt("autospawn.mooshroom"));
        maxSpawns.put(Ocelot.NETWORK_ID, this.pluginConfig.getInt("autospawn.ocelot"));
        maxSpawns.put(Parrot.NETWORK_ID, this.pluginConfig.getInt("autospawn.parrot"));
        maxSpawns.put(EntityPig.NETWORK_ID, this.pluginConfig.getInt("autospawn.pig"));
        maxSpawns.put(PolarBear.NETWORK_ID, this.pluginConfig.getInt("autospawn.polarbear"));
        maxSpawns.put(Pufferfish.NETWORK_ID, this.pluginConfig.getInt("autospawn.pufferfish"));
        maxSpawns.put(Rabbit.NETWORK_ID, this.pluginConfig.getInt("autospawn.rabbit"));
        maxSpawns.put(EntitySalmon.NETWORK_ID, this.pluginConfig.getInt("autospawn.salmon"));
        maxSpawns.put(EntitySheep.NETWORK_ID, this.pluginConfig.getInt("autospawn.sheep"));
        maxSpawns.put(Skeleton.NETWORK_ID, this.pluginConfig.getInt("autospawn.skeleton"));
        maxSpawns.put(Slime.NETWORK_ID, this.pluginConfig.getInt("autospawn.slime"));
        maxSpawns.put(Spider.NETWORK_ID, this.pluginConfig.getInt("autospawn.spider"));
        maxSpawns.put(Squid.NETWORK_ID, this.pluginConfig.getInt("autospawn.squid"));
        maxSpawns.put(Stray.NETWORK_ID, this.pluginConfig.getInt("autospawn.stray"));
        maxSpawns.put(EntityTropicalFish.NETWORK_ID, this.pluginConfig.getInt("autospawn.tropicalfish"));
        maxSpawns.put(Turtle.NETWORK_ID, this.pluginConfig.getInt("autospawn.turtle"));
        maxSpawns.put(Witch.NETWORK_ID, this.pluginConfig.getInt("autospawn.witch"));
        maxSpawns.put(WitherSkeleton.NETWORK_ID, this.pluginConfig.getInt("autospawn.witherskeleton"));
        maxSpawns.put(EntityWolf.NETWORK_ID, this.pluginConfig.getInt("autospawn.wolf"));
        maxSpawns.put(EntityZombie.NETWORK_ID, this.pluginConfig.getInt("autospawn.zombie"));
        maxSpawns.put(ZombiePigman.NETWORK_ID, this.pluginConfig.getInt("autospawn.zombiepigman"));
        maxSpawns.put(Fox.NETWORK_ID, this.pluginConfig.getInt("autospawn.fox"));
        maxSpawns.put(Panda.NETWORK_ID, this.pluginConfig.getInt("autospawn.panda"));
        maxSpawns.put(Drowned.NETWORK_ID, this.pluginConfig.getInt("autospawn.drowned"));
        maxSpawns.put(Piglin.NETWORK_ID, this.pluginConfig.getInt("autospawn.piglin"));
        maxSpawns.put(Hoglin.NETWORK_ID, this.pluginConfig.getInt("autospawn.hoglin"));
        maxSpawns.put(Llama.NETWORK_ID, this.pluginConfig.getInt("autospawn.llama"));
        maxSpawns.put(Strider.NETWORK_ID, this.pluginConfig.getInt("autospawn.strider"));
    }

    public boolean entitySpawnAllowed(Level level, int networkId, Vector3 pos) {
        if (!spawningAllowedByDimension(networkId, level.getDimension())) {
            return false;
        }

        int count = 0;
        int max = networkId == Enderman.NETWORK_ID && level.getDimension() == Level.DIMENSION_THE_END ? plugin.config.endEndermanSpawnRate : maxSpawns.getOrDefault(networkId, 0);

        if (max < 1) {
            return false;
        }

        for (Entity entity : level.getEntities()) {
            if (entity.isAlive() && entity.getNetworkId() == networkId && new Vector3(pos.x, entity.y, pos.z).distanceSquared(entity) < 10000) {
                count++;
                if (count > max) {
                    return false;
                }
            }
        }

        return count < max;
    }

    public EntityCreature createEntity(Object type, Position pos) {
        if (Entity.createEntity((String) type, pos) instanceof EntityCreature entity) {
            if (!entity.isInsideOfSolid()) {
                CreatureSpawnEvent ev = new CreatureSpawnEvent(entity.getNetworkId(), pos, entity.namedTag, CreatureSpawnEvent.SpawnReason.NATURAL);
                Server.getInstance().getPluginManager().callEvent(ev);
                if (!ev.isCancelled()) {
                    entity.spawnToAll();
                } else {
                    entity.close();
                    entity = null;
                }
            } else {
                entity.close();
                entity = null;
            }
            return entity;
        }
        return null;
    }

    public int getRandomSafeXZCoord(int degree, int safeDegree, int correctionDegree) {
        int addX = Utils.rand((degree >> 1) * -1, degree >> 1);
        if (addX >= 0) {
            if (degree < safeDegree) {
                addX = safeDegree;
                addX += Utils.rand((correctionDegree >> 1) * -1, correctionDegree >> 1);
            }
        } else {
            if (degree > safeDegree) {
                addX = -safeDegree;
                addX += Utils.rand((correctionDegree >> 1) * -1, correctionDegree >> 1);
            }
        }

        return addX;
    }

    public int getSafeYCoord(Level level, Position pos) {
        int x = (int) pos.x;
        int y = (int) pos.y;
        int z = (int) pos.z;

        if (level.getBlockIdAt(x, y, z) == Block.AIR) {
            while (true) {
                y--;
                if (y > 255) {
                    y = 256;
                    break;
                }
                if (y < 1) {
                    y = 0;
                    break;
                }
                if (level.getBlockIdAt(x, y, z) != Block.AIR) {
                    int checkNeedDegree = 3;
                    int checkY = y;
                    while (true) {
                        checkY++;
                        checkNeedDegree--;
                        if (checkY > 255 || level.getBlockIdAt(x, checkY, z) != Block.AIR) {
                            break;
                        }

                        if (checkNeedDegree <= 0) {
                            return y;
                        }
                    }
                }
            }
        } else {
            while (true) {
                y++;
                if (y > 255) {
                    y = 256;
                    break;
                }

                if (y < 1) {
                    y = 0;
                    break;
                }

                if (level.getBlockIdAt(x, y, z) != Block.AIR) {
                    int checkNeedDegree = 3;
                    int checkY = y;
                    while (true) {
                        checkY--;
                        checkNeedDegree--;
                        if (checkY < 1 || level.getBlockIdAt(x, checkY, z) != Block.AIR) {
                            break;
                        }

                        if (checkNeedDegree <= 0) {
                            return y;
                        }
                    }
                }
            }
        }
        return y;
    }

    private static boolean spawningAllowedByDimension(int id, int dimension) {
        switch (id) {
            case Enderman.NETWORK_ID:
                return true;
            case Blaze.NETWORK_ID:
            case Ghast.NETWORK_ID:
            case MagmaCube.NETWORK_ID:
            case Piglin.NETWORK_ID:
            case WitherSkeleton.NETWORK_ID:
            case ZombiePigman.NETWORK_ID:
            case Hoglin.NETWORK_ID:
            case Strider.NETWORK_ID:
                return Level.DIMENSION_NETHER == dimension;
            default:
                return Level.DIMENSION_OVERWORLD == dimension;
        }
    }
}
