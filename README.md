# Wizardry
[![Wizardry Curse Stats](http://cf.way2muchnoise.eu/full_278155_downloads.svg)](https://minecraft.curseforge.com/projects/wizardry-mod)
[![Build Status](https://travis-ci.org/TeamWizardry/Wizardry.svg?branch=master)](https://travis-ci.org/TeamWizardry/Wizardry)

Power is everything. It is what has driven you to learn the craft of wizardry; what has driven you to shun the limited teachings of Botanists, Thaumaturges and their ilk to create your own spells. You are only just now taking the first steps of this journey by studying this book you hold in your hands but, with dedication, you can become the master of these arts and break the chains on magic.

There are some things to remember, however. Any search for power comes with dangers, and any climb to the top has perils. A cape has the potential to give abilities beyond your wildest dreams, but guard it well; if another practitioner takes it, they can hijack your gains for themselves. Mana will feed your abilities and provide clean, abundant energy; take on more than your body can handle, however, and you will pay for your greed.

Finally, there is the Underworld. Dark creatures travel in that void, and the smallest misstep can cost you your life. The spirits there are slow and seem to pose little threat to a cautious adventurer, but once they have your scent you will find no shelter to hide from them, no wall to stop them. Unicorns may be beautiful – from a distance – but more than one wizard has perished on those sharp, spiral horns.

All that is still to come, however, for now it is time to take your first steps.

Good luck.

## For Packmakers:

### Mana Recipe Format
Mana recipes can be found, edited, created, and replaced in the config/wizardry/fluid_recipes folder, and can be placed inside any folder within that folder. Make sure to enable "customManaRecipes" in the main Wizardry config file if you wish to remove any default recipes.    
Only .json files will be read.

```
{
  "type": "item" or "block" // Item recipes drop in-world, while Block recipes place the given output block.
  "output":
  {
    "item": "string"    // Registry name of item or block resulting from recipe.
    "data": number      // Only optional if the item has only one variant.
    "nbt": { ... }      // Optional, only used by "item" type recipes. Specifies the exact NBT compound the output will be created with.
  },
  "input":
  {
    "item": "string"    // Registry name of item being loaded.
    "data": number      // Only optional if the item has only one variant.
    OR
    "type": "forge:ore_dict", "ore": "string" // Oredict value for the recipe's item, only used if "name" value is not given. Ignores "meta" value if used.
  },
  "extraInputs":        // Optional, use if recipe has more than one input item.
  [
    {
      "item": "string"  // All values here function the same as the "input" field
      "data": number    // Only optional if the item has only one variant.
      OR
      "type": "forge:ore_dict", "ore": "string"
    },
    ...
  ],
  "fluid": "fluid_name" // Optional, change the fluid the recipe crafts in. Defaults to "mana_fluid" if not present.
  "duration": number    // Optional, amount of time required for the recipe to run. Defaults to 200 ticks if not present.
  "required": number    // Optional, number of source blocks required to craft. Defaults to 1 if not present.
  "consume": boolean    // Optional, determines if the mana blocks required for the recipe will be consumed when processing finishes. Defaults to false if not present.
  "explode": boolean    // Optional, determines if any nearby entities will be pushed away when the recipe finishes. Defaults to false if not present.
  "bubbling": boolean   // Optional, determines if the items in the pool will make bubbling noises over the recipe's duration. Defaults to true if not present.
  "harp": boolean       // Optional, if true, a few notes will play on a harp when the recipe completes. Defaults to true if not present.
  "instant": boolean    // Optional, if true, then recipes with 0 duration that don't consume mana will run concurrently. Defaults to true if not present.
}
```

### Fire Recipe Format
Fire recipes can be found, edited, created, and replaced in the config/wizardry/fire_recipes folder, and can be placed inside any folder within that folder. Make sure to enable "customFireRecipes" in the main Wizardry config file if you wish to remove any default recipes.    
Only .json files will be read.

```
{
  "input":
  {
    "item": "string"  // Registry name of recipe's input
    "data": number    // Only optional if the item has only one variant.
    OR
    "type": "forge:ore_dict", "ore": "string" // Oredict value for the recipe's input item
  },
  "output":
  {
    "item": "string"  // Registry name of recipe's output
    "data": number    // Only optional if the item has only one variant.
    "count": number   // Optional, defaults to 1. Number of output items per input item
  },
  "duration": number  // Optional, defaults to 200. Duration in ticks of the recipe
}
```
