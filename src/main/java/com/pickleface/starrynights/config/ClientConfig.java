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

    private static final ForgeConfigSpec.ConfigValue<String> STAR_PATH = BUILDER
            .comment("This is the path to stars.txt. This gives the game all the info to render stars. Changing this without knowing what you're doing will result in no stars rendering.")
            .define("starPath", "/assets/star/stars.txt");

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    public static long starSeed;
    public static String starPath;

    @SubscribeEvent
    static void onLoad(final FMLClientSetupEvent event)
    {
        BUILDER.push("Stars");
        starSeed = RANDOM_SEED.get();
        starPath = STAR_PATH.get();
        BUILDER.pop();
    }
}
