# Nukkit Mob Plugin
Development: **[kniffo80](https://github.com/kniffo80)**
             **[matt404](https://github.com/matt404)**
             **[augesrob](https://github.com/augesrob)**
             **[PetteriM1](https://github.com/PetteriM1)**

MobPlugin is a plugin that implements the mob entities for MCPE including movement, aggression etc.

## Notice
This plug-in is in development. Therefore, It is possible to function abnormally.

[![Nukkit.io](https://img.shields.io/badge/Nukkit%20MobPlugin-Download-yellow.svg)](https://forums.nukkit.io/resources/mobplugin.155/)
[![Discord](https://discordapp.com/api/guilds/386601650963349504/widget.png)](https://discord.gg/rBew6kc)

test:
<iframe src="https://discordapp.com/widget?id=386601650963349504&theme=dark" width="350" height="500" allowtransparency="true" frameborder="0"></iframe>

# Credits
Credits go to Team-SW! They have a nice plugin already made.

# Plugin Example configuration
Place this plugin jar file to your Nukkit's home directory "${NUKKIT_HOME}/plugin".

#### Example:
  /usr/share/nukkit/plugins/MobPlugin.jar
  
  /usr/share/nukkit/plugins/MobPlugin/config.yml
  
## config.yml example

Make Sure to include all mobs. Previous builds had few missing.

The following configuration sets mobs AI enabled and the auto spawn task will be triggered all 300 ticks.
It's configured to spawn only wolfes:

```yaml
entities:
  mob-ai: true
  auto-spawn-tick: 300
  worlds-spawn-disabled: 

max-spawns:
  bat: 0
  blaze: 0
  cave-spider: 0
  chicken: 0
  cow: 0
  creeper: 0
  donkey: 0
  enderman: 0
  ghast: 0
  horse: 0
  iron-golem: 0
  mooshroom: 0
  mule: 0
  ocelot: 0
  pig: 0
  pig-zombie: 0
  rabbit: 0
  silverfish: 0
  sheep: 0
  skeleton: 0
  skeleton-horse: 0
  snow-golem: 0
  spider: 0
  squid: 0
  witch: 0
  wolf: 1
  zombie: 0
  zombie-horse: 0
  zombie-villager: 0
```
