package com.pickleface.starrynights.config;

import com.pickleface.starrynights.StarryNights;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = StarryNights.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.BooleanValue MOD_ENABLED = BUILDER
            .comment("Enables/disables this mod.")
            .define("modEnabled", true);

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    public static boolean modEnabled;

    @SubscribeEvent
    static void onLoad(final FMLCommonSetupEvent event)
    {
        modEnabled = MOD_ENABLED.get();
    }
}
