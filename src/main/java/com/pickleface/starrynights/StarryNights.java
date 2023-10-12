package com.pickleface.starrynights;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

@Mod(StarryNights.MODID)
public class StarryNights
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "starrynights";

    public StarryNights()
    {
        MinecraftForge.EVENT_BUS.register(this);
    }
}
