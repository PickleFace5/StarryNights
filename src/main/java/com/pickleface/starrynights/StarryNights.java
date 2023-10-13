package com.pickleface.starrynights;

import com.pickleface.starrynights.config.ClientConfig;
import com.pickleface.starrynights.config.CommonConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod(StarryNights.MODID)
public class StarryNights
{
    public static final String MODID = "starrynights";

    public StarryNights()
    {
        MinecraftForge.EVENT_BUS.register(this);

        ModLoadingContext modLoadingContext = ModLoadingContext.get();
        modLoadingContext.registerConfig(ModConfig.Type.COMMON, CommonConfig.SPEC);
        modLoadingContext.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
    }
}
