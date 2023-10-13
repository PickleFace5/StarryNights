package com.pickleface.starrynights.config;

import com.pickleface.starrynights.StarryNights;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = StarryNights.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.LongValue RANDOM_SEED = BUILDER
            .comment("The random seed used for creating stars. This is only used for turning stars from a \"coordinate to a square\" and has no effect on the star itself.")
            .defineInRange("starSeed", 10842L, Long.MIN_VALUE, Long.MAX_VALUE);

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    public static long starSeed;

    @SubscribeEvent
    static void onLoad(final FMLClientSetupEvent event)
    {
        BUILDER.push("Stars");
        starSeed = RANDOM_SEED.get();
        BUILDER.pop();
    }
}
