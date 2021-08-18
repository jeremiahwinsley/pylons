package net.permutated.pylons;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.Pair;

@Mod.EventBusSubscriber
public class ConfigManager {
    private ConfigManager() {
        // nothing to do
    }

    public static final String CATEGORY_PROCESSING = "processing";

    public static final String SUBCATEGORY_AGGREGATOR = "aggregator";
    public static final String SUBCATEGORY_CENTRIFUGE = "centrifuge";
    public static final String SUBCATEGORY_ETCHER = "etcher";
    public static final String SUBCATEGORY_ENERGIZER = "energizer";

    public static final String CATEGORY_NETWORK = "network";
    public static final String CATEGORY_ASSEMBLER = "assembler";


    public static final CommonConfig COMMON;
    public static final ForgeConfigSpec COMMON_SPEC;

    static {
        final Pair<CommonConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(CommonConfig::new);
        COMMON_SPEC = specPair.getRight();
        COMMON = specPair.getLeft();
    }

    public static class CommonConfig {
        // CATEGORY_PROCESSING
        public final ForgeConfigSpec.IntValue aggregatorEnergyBuffer;
        public final ForgeConfigSpec.IntValue aggregatorEnergyCostBase;
        public final ForgeConfigSpec.IntValue aggregatorEnergyCostUpgrade;
        public final ForgeConfigSpec.IntValue aggregatorWorkTicksBase;
        public final ForgeConfigSpec.IntValue aggregatorWorkTicksUpgrade;
        public final ForgeConfigSpec.IntValue centrifugeEnergyBuffer;
        public final ForgeConfigSpec.IntValue centrifugeEnergyCostBase;
        public final ForgeConfigSpec.IntValue centrifugeEnergyCostUpgrade;
        public final ForgeConfigSpec.IntValue centrifugeWorkTicksBase;
        public final ForgeConfigSpec.IntValue centrifugeWorkTicksUpgrade;
        public final ForgeConfigSpec.IntValue etcherEnergyBuffer;
        public final ForgeConfigSpec.IntValue etcherEnergyCostBase;
        public final ForgeConfigSpec.IntValue etcherEnergyCostUpgrade;
        public final ForgeConfigSpec.IntValue etcherWorkTicksBase;
        public final ForgeConfigSpec.IntValue etcherWorkTicksUpgrade;
        public final ForgeConfigSpec.IntValue energizerEnergyBuffer;
        public final ForgeConfigSpec.IntValue energizerEnergyCostUpgrade;
        public final ForgeConfigSpec.IntValue energizerWorkTicksBase;
        public final ForgeConfigSpec.IntValue energizerWorkTicksUpgrade;

        // CATEGORY_NETWORK
        public final ForgeConfigSpec.DoubleValue fastCrafterIdlePower;
        public final ForgeConfigSpec.DoubleValue levelMaintainerIdlePower;
        public final ForgeConfigSpec.IntValue levelMaintainerSleepMin;
        public final ForgeConfigSpec.IntValue levelMaintainerSleepMax;

        // CATEGORY_ASSEMBLER
        public final ForgeConfigSpec.DoubleValue idlePower;
        public final ForgeConfigSpec.IntValue jobQueueSize;
        public final ForgeConfigSpec.IntValue workPerJob;
        public final ForgeConfigSpec.DoubleValue energyPerWorkBase;
        public final ForgeConfigSpec.DoubleValue energyPerWorkUpgrade;
        public final ForgeConfigSpec.IntValue workPerTickBase;
        public final ForgeConfigSpec.IntValue workPerTickUpgrade;

        CommonConfig(ForgeConfigSpec.Builder builder) {
            // CATEGORY_PROCESSING
            builder.push(CATEGORY_PROCESSING);
            builder.push(SUBCATEGORY_AGGREGATOR);
            aggregatorEnergyBuffer = builder
                .comment("The size of the fluix aggregator's energy buffer.")
                .defineInRange("aggregatorEnergyBuffer", 100000, 1, Integer.MAX_VALUE);

            aggregatorEnergyCostBase = builder
                .comment("The base energy cost for each fluix aggregation operation performed.")
                .defineInRange("aggregatorEnergyCostBase", 8100, 1, Integer.MAX_VALUE);

            aggregatorEnergyCostUpgrade = builder
                .comment("The additional energy cost for fluix aggregation incurred by each acceleration card.")
                .defineInRange("aggregatorEnergyCostUpgrade", 863, 0, Integer.MAX_VALUE);

            aggregatorWorkTicksBase = builder
                .comment("The base number of ticks needed to complete one fluix aggregation operation.")
                .defineInRange("aggregatorWorkTicksBase", 150, 1, Integer.MAX_VALUE);

            aggregatorWorkTicksUpgrade = builder
                .comment("The number of ticks by which each acceleration card hastens a fluix aggregation operation.")
                .defineInRange("aggregatorWorkTicksUpgrade", 18, 1, Integer.MAX_VALUE);
            builder.pop();

            builder.push(SUBCATEGORY_CENTRIFUGE);
            centrifugeEnergyBuffer = builder
                .comment("The size of the pulse centrifuge's energy buffer.")
                .defineInRange("centrifugeEnergyBuffer", 100000, 1, Integer.MAX_VALUE);

            centrifugeEnergyCostBase = builder
                .comment("The base energy cost for each centrifuging operation performed.")
                .defineInRange("centrifugeEnergyCostBase", 8100, 1, Integer.MAX_VALUE);

            centrifugeEnergyCostUpgrade = builder
                .comment("The additional energy cost for centrifuging incurred by each acceleration card.")
                .defineInRange("centrifugeEnergyCostUpgrade", 863, 0, Integer.MAX_VALUE);

            centrifugeWorkTicksBase = builder
                .comment("The base number of ticks needed to complete one centrifuging operation.")
                .defineInRange("centrifugeWorkTicksBase", 150, 1, Integer.MAX_VALUE);

            centrifugeWorkTicksUpgrade = builder
                .comment("The number of ticks by which each acceleration card hastens a centrifuging operation.")
                .defineInRange("centrifugeWorkTicksUpgrade", 18, 1, Integer.MAX_VALUE);
            builder.pop();

            builder.push(SUBCATEGORY_ETCHER);
            etcherEnergyBuffer = builder
                .comment("The size of the circuit etcher's energy buffer.")
                .defineInRange("etcherEnergyBuffer", 100000, 1, Integer.MAX_VALUE);

            etcherEnergyCostBase = builder
                .comment("The base energy cost for each circuit etching operation performed.")
                .defineInRange("etcherEnergyCostBase", 8100, 1, Integer.MAX_VALUE);

            etcherEnergyCostUpgrade = builder
                .comment("The additional energy cost for circuit etching incurred by each acceleration card.")
                .defineInRange("etcherEnergyCostUpgrade", 863, 0, Integer.MAX_VALUE);

            etcherWorkTicksBase = builder
                .comment("The base number of ticks needed to complete one circuit etching operation.")
                .defineInRange("etcherWorkTicksBase", 150, 1, Integer.MAX_VALUE);

            etcherWorkTicksUpgrade = builder
                .comment("The number of ticks by which each acceleration card hastens a circuit etching operation.")
                .defineInRange("etcherWorkTicksUpgrade", 18, 1, Integer.MAX_VALUE);
            builder.pop();

            builder.push(SUBCATEGORY_ENERGIZER);
            energizerEnergyBuffer = builder
                .comment("The size of the crystal energizer's energy buffer.")
                .defineInRange("energizerEnergyBuffer", 100000, 1, Integer.MAX_VALUE);

            energizerEnergyCostUpgrade = builder
                .comment("The additional energy cost for crystal energization incurred by each acceleration card.")
                .defineInRange("energizerEnergyCostUpgrade", 1625, 0, Integer.MAX_VALUE);

            energizerWorkTicksBase = builder
                .comment("The base number of ticks needed to complete one crystal energization operation.")
                .defineInRange("energizerWorkTicksBase", 150, 1, Integer.MAX_VALUE);

            energizerWorkTicksUpgrade = builder
                .comment("The number of ticks by which each acceleration card hastens a crystal energization operation.")
                .defineInRange("energizerWorkTicksUpgrade", 18, 1, Integer.MAX_VALUE);
            builder.pop();
            builder.pop();

            // CATEGORY_NETWORK
            builder.push(CATEGORY_NETWORK);
            fastCrafterIdlePower = builder
                .comment("The idle power consumption of the preemptive assembly unit.")
                .defineInRange("fastCrafterIdlePower", 6D, 0D, Double.MAX_VALUE);

            levelMaintainerIdlePower = builder
                .comment("The idle power consumption of the level maintainer.")
                .defineInRange("levelMaintainerIdlePower", 3D, 0D, Double.MAX_VALUE);

            levelMaintainerSleepMin = builder
                .comment(
                    "The minimum interval between work ticks for the level maintainer.",
                    "The level maintainer will gradually increase its work rate while running without obstruction.",
                    "Setting this too low may cause lag!")
                .defineInRange("levelMaintainerSleepMin", 12, 0, Integer.MAX_VALUE);

            levelMaintainerSleepMax = builder
                .comment(
                    "The maximum interval between work ticks for the level maintainer.",
                    "The level maintainer will gradually reduce its work rate when something prevents it from progressing.",
                    "Setting this too low may cause lag!")
                .defineInRange("levelMaintainerSleepMax", 200, 0, Integer.MAX_VALUE);

            builder.pop();

            // CATEGORY_ASSEMBLER
            builder.push(CATEGORY_ASSEMBLER);
            idlePower = builder
                .comment("The idle power consumption of the mass assembly chamber.")
                .defineInRange("idlePower", 3D, 0D, Double.MAX_VALUE);

            jobQueueSize = builder
                .comment(
                    "The size of the mass assembler's crafting job queue.",
                    "Some crafting job data may be lost if this is decreased!"
                )
                .defineInRange("jobQueueSize", 64, 1, Integer.MAX_VALUE);

            workPerJob = builder
                .comment("The amount of work needed to complete one crafting job.")
                .defineInRange("workPerJob", 16, 1, Integer.MAX_VALUE);

            energyPerWorkBase = builder
                .comment("The base amount of energy consumed to perform one unit of work.")
                .defineInRange("energyPerWorkBase", 16D, 0D, Double.MAX_VALUE);

            energyPerWorkUpgrade = builder
                .comment("The additional energy consumed per unit of work for each installed coprocessor.")
                .defineInRange("energyPerWorkUpgrade", 1D, 0D, Double.MAX_VALUE);

            workPerTickBase = builder
                .comment(
                    "The base amount of work performed per tick.",
                    "If set to zero, the mass assembler will not do any work without a coprocessor installed."
                )
                .defineInRange("workPerTickBase", 1, 0, Integer.MAX_VALUE);

            workPerTickUpgrade = builder
                .comment("The additional work performed per tick for each installed coprocessor.")
                .defineInRange("workPerTickUpgrade", 3, 1, Integer.MAX_VALUE);
        }
    }
}
