# DeadHorses

Allow players to tame and ride undead horses! (Skeleton horses and Zombie horses)

Supports two different modes of taming:
vanilla-like and feeding.  Vanilla-like taming lets users mount the dead horse in an
attempt to tame it, following the same rules as regular horses.  Food-based taming
allows users to feed dead horses in an attempt to tame them.  Foods and their
respective chances of taming are defined in the configuration.

Additionally, this plugin supports the leashing of ndead horses and the aging of
dead horses via feeding.  The chance to age a baby dead horse is the same as the
chance to tame it (with the food item) as defined in the configuration.

*Note: This plugin does not affect natural mob spawning, and is best used with some
other plugin that allows dead horses to spawn in some way.  The `/deadhorses summon` command will spawn one of each horse, but is primarily meant for administrative testing and not for users.*

## Permissions
    deadhorses.admin - Grants access to reload and user node
    deadhorses.user - Allows players to use the plugin

## Commands
    /deadhorses reload - Reload configuration from disk    
    /deadhorses summon - Summon dead horses for testing

## Configuration

### General
Default configuration has all set to 'true'.  Settings default to 'false' if undefined.

    feeding-can-age: (Boolean, whether feeding can turn baby dead horses to adults)
    leashing: (Boolean, whether users can attach leashes to dead horses)
    taming:
      vanilla-like: (Boolean, whether users can attempt to tame dead horses by mounting)
      food-based: (Boolean, whether users can attempt to tame dead horses by feeding)    

### Food
Each variant of dead horse has its own food list (from org.bukkit.Material)  
Foods can be defined with data values like: `'golden_apple:1': 100.0`    
Foods map to chance-to-tame odds, in percent (0.0-100.0)  
Defaults:

    skeleton_horse:
        spider_eye: 2.5
        fermented_spider_eye: 5.0
        poisonous_potato: 10.0
    undead_horse:
        raw_beef: 10.0
        pork: 5.0
        raw_chicken: 2.5
        raw_fish: 1.25
    