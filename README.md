InventoryDropChance allow setting custom drop chances for items in player inventories upon death

### Commands
- `/mnd (makenodrop) <add_lore> <chance>`: Add an individual chance to save item on death, chances added by this command always have top priority over config chances
  - `<add_lore>`: Adds lore to the item, by default does not add lore
  - `<chance>`: Chance (in percent) to save the item on death, by default 100
- `/scrolls <add_lore> <chance>`: Makes item in hand a scroll, scrolls don't have chance to be saved on death by default. Scroll item allows to apply specified chance to any item by dragging and dropping scroll onto another item in inventory (does not work in creative mode)
  - `<add_lore>`: Adds lore to the item, by default does not add lore
  - `<chance>`: Chance (in percent) to save the item on death, by default 0
- `/idc (inventorydropchance) reload`: Reloads the config files

### Permissions
- `inventorydropchance.inventorydropchance` - Allows use of `/idc` command
- `inventorydropchance.reload` - Allows use of `/idc reload` command
- `inventorydropchance.makenodrop` - Allows use of `/mnd` command
- `inventorydropchance.scrolls` - Allows use of `/scrolls` command
- `inventorydropchance.X` - Allows user to have X% chance to save items on death, where X is a number from 0 to 100 (e.g. `inventorydropchance.75` for 75% chance) (Players with OP permission will always have 100% chance to save items on death)

### Integrations
- `ItemsAdder`: Supports ItemsAdder custom items, you can specify drop chances for ItemsAdder items in the config using their namespace IDs
- `WorldGuard`: Adds 2 new flags:
  - `idc-region-drop-chance`: Sets a custom drop chance for all items for players within the region
  - `idc-disabled`: Fully disables InventoryDropChance functionality in region
- `AxGraves`: Instead of dropping items on death, dropped items will be stored in AxGraves grave

### Configuration
`lang.yml` - Language file, allows customizing all messages sent by the plugin. (Does not support MiniMessage formatting yet, adding support requires dropping 1.16.5 support).
Also, you can enable title and chat messages on player death with amount of saved and dropped items

`config.yml` - Main configuration file, allows specifying ignored worlds, skip for Curse of Vanishing (any item Curse of Vanishing will always be removed ignoring chance),
if chance is applied to an item stack or each item in the stack individually, and enable/disable optional features (like permission chance or scrolls) and integrations with other plugins

`global.yml` - Allows specifying global drop chances for items by based on various criteria:
- Default chance for all items
- By the world name
- By item material
- By the custom model data value
- By the ItemsAdder namespace ID

By default, chances for item are checked in `FIRST_SUCCESS` mode, meaning that the first chance that success the item will be applied. You can change this behavior to `FIRST_APPLY` mode in the config, which will apply the first chance that is found for the item, regardless of success.
Also order of checking chances can be changed in the config as well

---

**Download from:**
- [SpigotMC](https://www.spigotmc.org/resources/inventorydropchance.110836/)
- [Modrinth](https://modrinth.com/plugin/inventorydropchance)

**Support:**
- [Discord](https://discord.gg/AFrDuzEre6)