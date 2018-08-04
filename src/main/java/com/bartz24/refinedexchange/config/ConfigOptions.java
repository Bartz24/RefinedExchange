package com.bartz24.refinedexchange.config;

import com.bartz24.refinedexchange.References;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;


@Config(modid = References.ModID)
@Mod.EventBusSubscriber
public class ConfigOptions {

    @Config.Comment("Set the multiplier for the EMC Crafter")
    public static double emcCrafterEnergyMultiplier = 1f;

    @Config.Comment("Set the multiplier for the EMC Liquifier")
    public static double emcLiquifierEnergyMultiplier = 1f;

    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(References.ModID)) {
            ConfigManager.sync(References.ModID, Config.Type.INSTANCE);
        }
    }
}
