package net.permutated.pylons;

import com.google.common.collect.ImmutableList;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

@Mod.EventBusSubscriber
public class ConfigManager {
    private ConfigManager() {
        // nothing to do
    }

    public static final String CATEGORY_EXPULSION = "expulsion_pylon";
    public static final String CATEGORY_INFUSION = "infusion_pylon";


    public static final CommonConfig COMMON;
    public static final ForgeConfigSpec COMMON_SPEC;

    static {
        final Pair<CommonConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(CommonConfig::new);
        COMMON_SPEC = specPair.getRight();
        COMMON = specPair.getLeft();
    }

    public static class CommonConfig {
        // CATEGORY_EXPULSION
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> expulsionAllowedDimensions;

        // CATEGORY_INFUSION
        public final ForgeConfigSpec.IntValue infusionMinimumDuration;
        public final ForgeConfigSpec.IntValue infusionRequiredDuration;

        CommonConfig(ForgeConfigSpec.Builder builder) {
            // CATEGORY_EXPULSION
            builder.push(CATEGORY_EXPULSION);

            expulsionAllowedDimensions = builder
                .comment("Which dimensions the Expulsion Pylon is allowed to operate in.")
                .defineList("expulsionAllowedDimensions", ImmutableList.of("minecraft:overworld"),
                    s -> s instanceof String && ((String) s).matches("^\\w+:\\w+$"));

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

            builder.pop();
        }
    }
}
