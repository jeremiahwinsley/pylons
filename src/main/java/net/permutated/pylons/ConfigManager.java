package net.permutated.pylons;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber
public class ConfigManager {
    private ConfigManager() {
        // nothing to do
    }

    public static final String CATEGORY_EXPULSION = "expulsion_pylon";
    public static final String CATEGORY_INFUSION = "infusion_pylon";


    public static final ServerConfig SERVER;
    public static final ForgeConfigSpec SERVER_SPEC;

    static {
        final Pair<ServerConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ServerConfig::new);
        SERVER_SPEC = specPair.getRight();
        SERVER = specPair.getLeft();
    }

    public static class ServerConfig {
        // CATEGORY_EXPULSION
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> expulsionAllowedDimensions;
        public final ForgeConfigSpec.IntValue expulsionWorldSpawnRadius;

        // CATEGORY_INFUSION
        public final ForgeConfigSpec.IntValue infusionMinimumDuration;
        public final ForgeConfigSpec.IntValue infusionRequiredDuration;
        public final ForgeConfigSpec.IntValue infusionAppliedDuration;
        public final ForgeConfigSpec.BooleanValue infusionChunkloads;
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> infusionAllowedEffects;
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> infusionDeniedEffects;

        ServerConfig(ForgeConfigSpec.Builder builder) {
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
        }
    }
}
