DeadHorses
==========

Allow players to tame and ride undead horses!

Permissions
-----------
    deadhorses.admin - Grants access to reload and user node
    deadhorses.user - Allows players to use the plugin

Commands
--------
    /deadhorses reload - Reload configuration from disk    
    /deadhorses summon - Summon dead horses for testing

Configuration
-------------
Each variant of dead horse has its own food list (from org.bukkit.Material)  
Foods can be defined with data values like: `'golden_apple:1': 100.0`    
Foods map to chance-to-tame odds, in percent (0.0-100.0)  
Default config.yml:

    skeleton_horse:
        spider_eye: 2.5
        fermented_spider_eye: 5.0
        poisonous_potato: 10.0
    undead_horse:
        raw_beef: 10.0
        pork: 5.0
        raw_chicken: 2.5
        raw_fish: 1.25
    