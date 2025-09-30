InventoryDropChance is a plugin that provides the ability to customize the chance of items dropping from their inventory upon death. With this plugin, server administrators can fine-tune the drop chances based on player permissions, allowing for a dynamic and configurable gameplay experience.

By granting players specific permissions such as **inventorydropchance.x**, where 'x' represents a number between 0 and 100 (e.g., **inventorydropchance.50**), the plugin will ensure that each item in the player's inventory has an 'x' percent chance of being saved upon death.

Additionally, the plugin introduces a command called "/mnd" (or its alias "/makenodrop"), which provides the ability to mark specific items as undroppable. By executing this command on an item, it becomes immune to the effects of the inventory drop chance calculation. Or you can run command "/mnd [addLore] [chance]" to make items undroppable with custom drop chances for items individually.

Also, a scroll can be created using "/scrolls [addLore] [chance]" command, when executed, item in hand become a scroll that can apply chance to not drop an item that was dragged on.

**Commands and Permissions:**
- /idc or /inventorydropchance (Permission: inventorydropchance.inventorydropchance): Main command to configure the inventory drop chance.
- /mnd or /makenodrop (Permission: inventorydropchance.makenodrop): Make an item undroppable.
- /mnd [addLore] [chance] or /makenodrop [addLore] [chance] (Permission: inventorydropchance.makenodrop): Make items undroppable and set custom drop chances for items individually.
- /scrolls [addLore] [chance] (Permission:inventorydropchance.scrolls): Make item a scroll with custom drop chances that can be applied to the item.

**WorldGuard Flags:**
- `idc-disabled` - Disables InventoryDropChance functionality in region
- `idc-region-drop-chance` - Sets a custom drop chance for players in the region

**Download from:**
- [SpigotMC](https://www.spigotmc.org/resources/inventorydropchance.110836/)
- [Modrinth](https://modrinth.com/plugin/inventorydropchance)

**Support:**
- [Discord](https://discord.gg/AFrDuzEre6)