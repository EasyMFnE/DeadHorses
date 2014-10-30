<center>![DeadHorses](http://www.easymfne.net/images/deadhorses.png)</center>

<center>[Source](https://github.com/EasyMFnE/DeadHorses) |
[Change Log](https://github.com/EasyMFnE/DeadHorses/blob/master/CHANGES.log) |
[Feature Request](https://github.com/EasyMFnE/DeadHorses/issues) |
[Bug Report](https://github.com/EasyMFnE/DeadHorses/issues) |
[Donate](https://www.paypal.com/cgi-bin/webscr?hosted_button_id=457RX2KYUDY5G&item_name=DeadHorses&cmd=_s-xclick)</center>

<center>**Latest Release:** v1.2-alpha for Bukkit 1.7+</center>

## About ##

DeadHorses is designed to allow players to interact with dead horses (skeleton horse and undead/zombie horse). This plugin enables leashing, feeding, taming, and riding of dead horses.  All actions are passed though Bukkit's event system, letting protection plugins such as Worldguard function like they should.

## Features ##

Players using the DeadHorses plugin can:

* Attach leashes to dead horses (tamed and wild)
* Feed dead horses (see below)
* Tame dead horses (see below)
* Mount & ride dead horses
* Equip dead horses with barding (armor)

**Note:** This plugin does not affect natural mob spawning, and is best used with another plugin that allows dead horses to spawn.  The horses can be summoned via command (see `Commands`), but is primarily meant for administrative testing and not for users.

### Taming ###

The plugin supports two different modes of taming:

* **Vanilla-like** taming lets users mount the dead horse in an attempt to tame it, following the same rules as regular horses.
* **Food-based** taming allows users to feed dead horses in an attempt to tame them.  Foods and their respective chances of taming are defined in the configuration.

### Feeding ###

In addition to taming dead horses, feeding can cause baby dead horses to mature into adults. The chance to age a baby dead horse is the same as the chance to tame it (with the food item) as defined in the configuration.

### Barding ###

Dead horses can be equipped with barding like normal horses.  When equipped, the damage suffered is reduced by the appropriate amount.  The barding does not render, because of a limitation within Minecraft itself.

## Installation ##

1. Download DeadHorses jar file.
2. Move/copy to your server's `plugins` folder.
3. Restart your server.
4. [**Optional**] Grant specific user permissions (see below).

## Permissions ##

DeadHorses has two permission nodes:

* `deadhorses.admin` - Grants access to all commands, in addition to user permission. (Default: `op`)
* `deadhorses.user` - Allows players to use the plugin's main features. (Default: `true`)

## Commands ##

DeadHorses has only one command, `/deadhorses` (Alias `/dh`)

* `/deadhorses reload` - Reload configuration from disk.
* `/deadhorses summon` - Summon one of each dead horse for testing.

## Configuration ## 

At startup, the plugin will create a default configuration file if none exists.  This file is saved as `config.yml` and is located in `<server_folder>/plugins/DeadHorses`.

### General ###
Default configuration has all set to 'true'.  Settings default to 'false' if undefined.

* `armor.undead_horse:` & `armor.skeleton_horse:` (Boolean, Whether armor able to be equipped)
* `feeding-can-age:` (Boolean, whether feeding can turn baby dead horses to adults)
* `leashing:` (Boolean, whether users can attach leashes to dead horses)
* `taming.vanilla-like:` (Boolean, whether users can attempt to tame dead horses by mounting)
* `taming.food-based:` (Boolean, whether users can attempt to tame dead horses by feeding)    

### Food ###
Each variant of dead horse has its own food list (from [org.bukkit.Material](http://jd.bukkit.org/dev/apidocs/org/bukkit/Material.html))  
Foods can be defined with damage values like: `'golden_apple:1': 100.0`    
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

## Bugs/Requests ##

This plugin is continually tested to ensure that it is performing correctly, but sometimes bugs can sneak in.  If you have found a bug with the plugin, or if you have a feature request, please [create an issue on Github](https://github.com/EasyMFnE/DeadHorses/issues).

## Donations ##

Donating is a great way to thank the developer if you find the plugin useful for your server, and encourages work on more 100% free and open-source plugins.  If you would like to donate (any amount), there is an easily accessible link in the top right corner of this page.  Thank you!

## Privacy ##

This plugin utilizes Hidendra's **Plugin-Metrics** system.  You may opt out of this service by editing your configuration located in `plugins/Plugin Metrics`.  The following anonymous data is collected and sent to [mcstats.org](http://mcstats.org):

* A unique identifier
* The server's version of Java
* Whether the server is in online or offline mode
* The plugin's version
* The server's version
* The OS version, name, and architecture
* The number of CPU cores
* The number of online players
* The Metrics version

## License ##

This plugin is released as a free and open-source project under the [GNU General Public License version 3 (GPLv3)](http://www.gnu.org/copyleft/gpl.html).  To learn more about what this means, click that link or [read about it on Wikipedia](http://en.wikipedia.org/wiki/GNU_General_Public_License).
