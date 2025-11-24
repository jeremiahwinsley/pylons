package net.permutated.pylons.data.client;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.permutated.pylons.ModRegistry;
import net.permutated.pylons.Pylons;
import net.permutated.pylons.util.Constants;

import static net.permutated.pylons.util.TranslationKey.chat;
import static net.permutated.pylons.util.TranslationKey.config;
import static net.permutated.pylons.util.TranslationKey.gui;
import static net.permutated.pylons.util.TranslationKey.jei;
import static net.permutated.pylons.util.TranslationKey.tab;
import static net.permutated.pylons.util.TranslationKey.tooltip;

public class Languages {
    private Languages() {
        // nothing to do
    }

    public static class English extends LanguageProvider {

        public English(PackOutput packOutput) {
            super(packOutput, Pylons.MODID, "en_us");
        }

        @Override
        protected void addTranslations() {
            addBlock(ModRegistry.EXPULSION_PYLON, "Expulsion Pylon");
            addItem(ModRegistry.PLAYER_FILTER, "Player Filter");

            addBlock(ModRegistry.INFUSION_PYLON, "Infusion Pylon");
            addItem(ModRegistry.POTION_FILTER, "Potion Filter");

            addBlock(ModRegistry.HARVESTER_PYLON, "Harvester Pylon");

            addBlock(ModRegistry.INTERDICTION_PYLON, "Interdiction Pylon");
            addItem(ModRegistry.MOB_FILTER, "Mob Filter");
            addItem(ModRegistry.LIFELESS_FILTER, "Lifeless Filter");

            add(config("general"), "General");
            add(config("expulsion_pylon"), "Expulsion Pylon");
            add(config("infusion_pylon"), "Infusion Pylon");
            add(config("harvester_pylon"), "Harvester Pylon");
            add(config("teamSupportEnabled"), "Team Support Enabled");

            add(config("expulsionAllowedDimensions"), "Allowed Dimensions");
            add(config("expulsionWorldSpawnRadius"), "World Spawn Radius");
            add(config("expulsionPylonCanExplode"), "Can Explode");
            add(config("expulsionPylonMaxRadius"), "Max Radius");

            add(config("infusionMinimumDuration"), "Minimum Effect Duration");
            add(config("infusionRequiredDuration"), "Required Effect Duration");
            add(config("infusionAppliedDuration"), "Applied Effect Duration");
            add(config("infusionChunkloads"), "Acts As Chunkloader");
            add(config("infusionAllowedEffects"), "Allowed Effects");
            add(config("infusionDeniedEffects"), "Denied Effects");
            add(config("infusionMaximumPotency"), "Maximum Effect Potency");

            add(config("harvesterWorkDelay"), "Work Delay");
            add(config("harvesterRequiresTool"), "Requires Tool");
            add(config("harvesterCanBeAutomated"), "Can Be Automated");
            add(config("harvesterRequiresPower"), "Requires Power");
            add(config("harvesterPowerCost"), "Power Cost");
            add(config("harvesterPowerBuffer"), "Power Buffer");

            add(gui("owner"), "Owner: %s");
            add(gui("noOwner"), "Owner not found. Pylon disabled.");
            add(gui("wrongDimension"), "This dimension is disabled.");
            add(gui("insideWorldSpawn"), "Too close to world spawn.");
            add(gui("toolMissing"), "Hoe required for operation.");
            add(gui("inventoryMissing"), "Place inventory above pylon.");
            add(gui("energyMissing"), "Not enough power.");
            add(gui("inventoryFull"), "Inventory is full.");
            add(gui("working"), "Pylon is working.");
            add(gui("whitelist"), "Add players to whitelist:");
            add(gui("blockedMobs"), "Add mobs to prevent spawns:");
            add(gui("effects"), "Active potion effects:");
            add(gui("workArea"), "Work area (in chunks)");
            add(gui("workAreaBlocks"), "Work area (in blocks)");
            add(gui("toggleWork"), "Working status");
            add(gui("fluxBar"), "Redstone Flux:");
            add(gui("fluxData"), "%d/%d RF stored");
            add(tab(), "Pylons");

            add(chat("expelled"), "You have been expelled from %s's chunk!");

            add(tooltip("expulsion1"), "Expels other players from");
            add(tooltip("expulsion2"), "a configurable chunk range");
            add(tooltip("expulsion3"), "around the pylon.");

            add(tooltip("infusion1"), "Applies potion effects from");
            add(tooltip("infusion2"), "an activated potion filter");
            add(tooltip("infusion3"), "at any distance.");

            add(tooltip("harvester1"), "Harvests crops in a configurable");
            add(tooltip("harvester2"), "cube area around the pylon. Just place");
            add(tooltip("harvester3"), "inside or above the water block.");

            add(tooltip("interdiction1"), "Configurable mob spawn prevention");
            add(tooltip("interdiction2"), "in a radius around the pylon.");

            add(tooltip("no_player"), "Right-click on a player to select them.");
            add(tooltip("no_mob"), "Right-click on a mob to select it.");

            add(tooltip("no_effect1"), "Right-click with an active effect");
            add(tooltip("no_effect2"), "to apply it to the card.");
            add(tooltip("minimum_duration"), "Minimum effect duration: %s seconds");

            add(tooltip("effect_denied"), "Effect is disabled in the config.");

            add(tooltip("potency_capped"), "Capped at level %s");

            add(tooltip("insert1"), "Insert this filter into");
            add(tooltip("insert2"), "a pylon to use it!");

            add(tooltip("increase1"), "Right-click with the same effect");
            add(tooltip("increase2"), "active to increase progress.");

            add(tooltip("activated"), "Activated");
            add(tooltip("progress"), "Progress: %d/%d seconds");

            add(tooltip("player"), "Player: %s");

            add(tooltip("lifeless1"), "Disables natural spawns in range.");
            add(tooltip("lifeless2"), "Increases range to 25x25 chunks.");
            add(tooltip("lifeless3"), "Disables other mob filters.");

            add(tooltip("expulsion"), "Used in the Expulsion Pylon.");
            add(tooltip("infusion"), "Used in the Infusion Pylon.");
            add(tooltip("interdiction"), "Used in the Interdiction Pylon.");

            add(jei(Constants.HARVESTER_PYLON), """
Harvests crops in a cube area (from 3x3 to 9x9 blocks) around the pylon and outputs to an inventory above.
Place the pylon inside the water block of the farm, or level with the crops.

By default (configurable) this will require a hoe in the pylon, and use 1 durability per harvest,
unless the hoe is unbreakable. Unbreaking and other durability enchants are supported.

Optionally can be configured to require power instead of durability.

Can be toggled automatically with redstone.
                """);

            add(jei(Constants.INFUSION_PYLON), """
Allows you to apply effects to yourself from any distance with activated Potion Filters.
By default (configurable) this will load the chunk it's placed in, while the owner is online.

Activate potion filters by applying a potion effect to yourself,
and then right clicking with the filter in hand to extract it.

By default (configurable) the minimum duration that can be extracted is 60 seconds,
and the filter requires 1 hour of duration to activate.

Duplicate potion filters can be combined by placing one in each hand and right-clicking.

Can be toggled automatically with redstone.
                """);

            add(jei(Constants.EXPULSION_PYLON), """
This block allows you to expel other players from the selected chunk range (1x1 to 5x5 chunks).
You can whitelist players with a Player Filter.

OPs are automatically whitelisted.

If team support is enabled, team members will be automatically whitelisted.

Can be toggled automatically with redstone.
                """);

            add(jei(Constants.INTERDICTION_PYLON), """
Prevents natural and forced spawns of specified mobs within the selected chunk range (1x1 to 5x5 chunks).
Add Mob Filters to specify which mobs to block.

Using the lifeless filter will instead block all mobs, but only natural spawns, with a much larger range.

Can be toggled automatically with redstone.
                """);
        }
    }
}
