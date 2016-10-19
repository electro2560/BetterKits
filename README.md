###BetterKits
BetterKits is a kit plugin compatible for Bukkit and Sponge.

####Major features
- Supports item NBT data (for modded items like bags!)
- Supports Cauldron servers
- Kit preview
- Per kit cooldown (and per kit cooldown bypass permission )
- Per kit permission (users with this permission can get the kit without limits)
- Command to give a limited amount of kits to players

####Main commands
- /kit (kitname) - get a kit
- /kits - kit list
- /kitcreate (kitname) - create a new kit for the target chest (just look at it directly)
- /kitpreview (kitname) - shows a preview of the kit
- /kitgive (player) (kitname) (amount) - gives the specified amount of the specified kit to the specified player

####Links
- [Latest release](https://github.com/KaiKikuchi/BetterKits/releases)
- [Issue tracker](https://github.com/KaiKikuchi/BetterKits/issues)
- [Source code](https://github.com/KaiKikuchi/BetterKits/)
- [Personal page](http://mc.kaikk.net)
- [Donate](http://mc.kaikk.net/#donate)

####Basic installation and configuration
- Copy the jar file under "/plugins/" (Bukkit) or "/mods/plugins/" (Sponge).
- Restart the server.
- Make a protected area in your server that will contain all the kit chests (e.g. a private dimension or another protected area). This area must be inaccessible to players.
- Place a chest and fill it with the content of the kit you wish to create.
- While looking at the chest within 4 blocks without any other block between you and the chest, use "/kitcreate (KitName)".
- You can make more kits by placing a new chest and repeating the previous instructions.
- WARNING: do not remove the chests you made for the kits! Those are used to read the kits items content! You also have to be sure they're are inaccesible to players, or they may steal from those chests!

#####Further details
Kits are stored into chests in the world so NBT data is preserved and kits can be modified easily.

Kits can be created by placing a chest, filling the chest with items, then running the /kitcreate (kitname) command while looking at the chest. Do not remove the chests. Be sure to protect the chests from players.

Players can get kits with the "/kit (kitname)" command. Kits can be added to players with the "/kitgive (playername) (kitname) (amount)", or allow them to get the kit without the amount limits with the permission node "betterkits.kit.(kitname)".

Cooldown can be assigned to kits so the player won't spam the kit command with the command "/kitedit (kitname) cooldown (seconds)". Cooldown can be bypassed with the permission node "betterkits.cooldownbypass.kit.(kitname)".

Players can preview the kits with the "/kitp (kitname)" command. The "betterkits.preview" permission node is necessary (default to true).

Kit chest content is cached after first kit use. After modifying the kit chest content by either using the "/kitedit (kitname) content" or by manually opening the chest, use /kitclearcache.

Commands can be also run when a player gets a kit. Check "/kitedit (kitname) <commandslist|addcommand|removecommand>". Commands are run by console. When adding a command, you can specify %name and %uuid and they'll be replaced with the player's name and uuid.

Kits can be deleted with the "/kitdelete (kitname)" command.

The config.yml/config.conf file contains a list of allowed chests. More chests can be added on the list by specifying the proper Material (Bukkit) or BlockType (Sponge) name.

The plugin can be reloaded with /kitreload.
