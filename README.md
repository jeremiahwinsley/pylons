# pylons
Pylons mod for NeoForge 1.21.1.

### Expulsion Pylon
Intended for use on servers, this block allows you to expel other players from the chunk it's placed in.
You can whitelist players with a Player Filter - OPs are always whitelisted.

### Infusion Pylon
Allows you to apply effects to yourself from any distance (may require chunkloading) with activated Potion Filters.
Potion Filters can be activated by applying an effect to yourself (from a potion, for example) and then right-clicking the Potion Filter.
The minimum effect duration that can be extracted is 60 seconds by default.
This will extract all remaining duration from the player and apply it to the Filter.
To fully activate it, you will need a total of 1 hour of duration, the equivalent of 7.5 vanilla extended potions.

If you have multiple cards containing the same effect, you can combine them.
Place one card in each hand, and right click.
The duration from the off hand card will be added to the card in your main hand.

### Harvest Pylon
Harvests crops in a configurable radius (from 3x3 to 9x9) around the pylon and outputs to an inventory above.
Place the pylon inside the water block of the farm.

By default, the pylon requires a hoe in the pylon inventory, and will consume 1 durability per crop harvest.
There is a config option to disable this. Unbreakable hoes, such as Mystical Agriculture's Supremium Hoe, can be used.

Most crops will be compatible out of the box, as long as they implement CropBlock.
Incompatible crops can be supported by adding a datapack recipe in the following format:

```json
{
  "neoforge:conditions": [
    {
      "type": "neoforge:mod_loaded",
      "modid": "minecraft"
    }
  ],
  "type": "pylons:harvesting",
  "block": "minecraft:sweet_berry_bush",
  "output": {
    "count": 2,
    "id": "minecraft:sweet_berries"
  }
}
```

### Interdiction Pylon
Prevents all spawns of specified mobs within the selected chunk range (1x1 to 5x5).
Right click on a mob with a Mob Filter, then add the filter to the pylon to block that mob.

Alternately, the Lifeless Filter can be used. This disables other mob filters for this pylon,
but increases the range to 25x25 chunks, and blocks all natural spawns.

Great for reducing lag from mob spawns around your base!
