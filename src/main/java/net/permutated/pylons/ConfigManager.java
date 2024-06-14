package net.permutated.pylons;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class ConfigManager {
    private ConfigManager() {
        // nothing to do
    }

    public static final String CATEGORY_GENERAL = "general";
    public static final String CATEGORY_EXPULSION = "expulsion_pylon";
    public static final String CATEGORY_INFUSION = "infusion_pylon";
    public static final String CATEGORY_HARVESTER = "harvester_pylon";


    public static final ServerConfig SERVER;
    public static final ModConfigSpec SERVER_SPEC;

    static {
        final Pair<ServerConfig, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(ServerConfig::new);
        SERVER_SPEC = specPair.getRight();
        SERVER = specPair.getLeft();
    }

    public static class ServerConfig {
        // CATEGORY_GENERAL
        public final ModConfigSpec.BooleanValue teamSupportEnabled;

        // CATEGORY_EXPULSION
        public final ModConfigSpec.ConfigValue<List<? extends String>> expulsionAllowedDimensions;
        public final ModConfigSpec.IntValue expulsionWorldSpawnRadius;
        public final ModConfigSpec.BooleanValue expulsionPylonCanExplode;
        public final ModConfigSpec.IntValue expulsionPylonMaxRadius;

        // CATEGORY_INFUSION
        public final ModConfigSpec.IntValue infusionMinimumDuration;
        public final ModConfigSpec.IntValue infusionRequiredDuration;
        public final ModConfigSpec.IntValue infusionAppliedDuration;
        public final ModConfigSpec.BooleanValue infusionChunkloads;
        public final ModConfigSpec.ConfigValue<List<? extends String>> infusionAllowedEffects;
        public final ModConfigSpec.ConfigValue<List<? extends String>> infusionDeniedEffects;

        // CATEGORY_HARVESTER
        public final ModConfigSpec.IntValue harvesterWorkDelay;
        public final ModConfigSpec.BooleanValue harvesterRequiresTool;
        public final ModConfigSpec.BooleanValue harvesterCanBeAutomated;


        ServerConfig(ModConfigSpec.Builder builder) {
            // CATEGORY_GENERAL
            builder.push(CATEGORY_GENERAL);

            teamSupportEnabled = builder
                .comment("Whether team support is enabled if a compatible mod (FTB Teams, Argonauts) is installed")
                .define("teamSupportEnabled", true);

            builder.pop();

            // CATEGORY_EXPULSION
            builder.push(CATEGORY_EXPULSION);

            expulsionAllowedDimensions = builder
                .comment("Which dimensions the Expulsion Pylon is allowed to operate in.")
                .defineListAllowEmpty(List.of("expulsionAllowedDimensions"), () -> List.of("minecraft:overworld"),
                    s -> s instanceof String string && string.matches("^\\w+:\\w+$"));

            expulsionWorldSpawnRadius = builder
                .comment("The radius around the world spawn where the pylon is not allowed to operate.",
                    "By default this uses the world spawn radius (/gamerule spawnRadius).",
                    "This config will only take effect if it is larger than the world spawn radius.")
                .defineInRange("expulsionWorldSpawnRadius", 1, 1, 512);

            expulsionPylonCanExplode = builder
                .comment("Whether the Expulsion Pylon can be destroyed with explosions.")
                .define("expulsionPylonCanExplode", false);

            expulsionPylonMaxRadius = builder
                .comment("Limit the max radius for expulsion pylons.",
                    "Does not include center chunk, so a radius of 2 equals a 5x5 chunk diameter.")
                .defineInRange("expulsionPylonMaxRadius", 2, 0, 2);

            builder.pop();

            // CATEGORY_INFUSION
            builder.push(CATEGORY_INFUSION);

            infusionMinimumDuration = builder
                .comment("The minimum effect duration (in seconds) that can be used for Potion Filters.",
                    "This defaults to 60 seconds to prevent unintended interactions",
                    "with other mods that add persistent potion effects at low durations.")
                .defineInRange("infusionMinimumDuration", 60, 1, 3600);

            infusionRequiredDuration = builder
                .comment("The total duration (in seconds) required before a Potion Filter can be used.",
                    "By default this is 3600 seconds/1 hour, which is equivalent to 7.5 vanilla extended potions.")
                .defineInRange("infusionRequiredDuration", 3600, 1, 28800);

            infusionAppliedDuration = builder
                .comment("The max duration of effects (in seconds) applied to the player.",
                    "The duration is refreshed up to this amount every 60 ticks.")
                .defineInRange("infusionAppliedDuration", 20, 5, 60);

            infusionChunkloads = builder
                .comment("Whether the Infusion Pylon chunkloads itself.",
                    "This is limited to one pylon per player, while the player is online.")
                .define("infusionChunkloads", true);

            infusionAllowedEffects = builder.comment("Effects that may be used in the Infusion Pylon.",
                    "List may include either effect IDs (like `minecraft:strength`) or an entire namespace (like `minecraft`).",
                    "If the list is empty, then all effects will be allowed except for those specifically denied.")
                .defineListAllowEmpty(List.of("infusionAllowedEffects"), ArrayList::new,
                    s -> s instanceof String string && string.matches("^\\w+(:\\w+)?$"));

            infusionDeniedEffects = builder.comment("Effects that may not be used in the Infusion Pylon.",
                    "This list will override the allowed effect list.")
                .defineListAllowEmpty(List.of("infusionDeniedEffects"), ArrayList::new,
                    s -> s instanceof String string && string.matches("^\\w+(:\\w+)?$"));

            builder.pop();
            builder.push(CATEGORY_HARVESTER);

            harvesterWorkDelay = builder
                .comment("Delay between harvest attempts (in ticks).")
                .defineInRange("harvesterWorkDelay", 60, 10, 120);

            harvesterRequiresTool = builder
                .comment("Whether the harvester requires a hoe to work.",
                    "If enabled, it will use 1 durability per harvest action")
                .define("harvesterRequiresTool", true);

            harvesterCanBeAutomated = builder
                .comment("Whether the harvester can have tools piped in to automate it.",
                    "By default, unbreakable tools are required for full automation.")
                .define("harvesterCanBeAutomated", false);

            builder.pop();
        }
    }
}
