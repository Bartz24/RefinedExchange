package com.bartz24.refinedexchange.proxy;

import com.bartz24.refinedexchange.RefinedExchange;
import com.bartz24.refinedexchange.features.PacketLiquifier;
import com.bartz24.refinedexchange.features.PacketLiquifierHandler;
import com.bartz24.refinedexchange.features.emc.ProjectEMappings;
import com.bartz24.refinedexchange.registry.ModGuiHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;

public class CommonProxy {

    public void preInit(FMLPreInitializationEvent e) {
        new ModGuiHandler();

    }

    public void init(FMLInitializationEvent e) {
        NetworkRegistry.INSTANCE.registerGuiHandler(RefinedExchange.instance, new ModGuiHandler());
        RefinedExchange.NETWORK_WRAPPER.registerMessage(PacketLiquifierHandler.class, PacketLiquifier.class, 0, Side.CLIENT);
    }

    public void postInit(FMLPostInitializationEvent e) {
        ProjectEMappings.postInit();
    }
}
