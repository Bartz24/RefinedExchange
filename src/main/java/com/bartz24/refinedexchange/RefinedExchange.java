package com.bartz24.refinedexchange;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import org.apache.logging.log4j.Logger;

import com.bartz24.refinedexchange.proxy.CommonProxy;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = References.ModID, name = References.ModName, dependencies = "required-after:refinedstorage;required-after:projecte", useMetadata = true)
public class RefinedExchange {
	@SidedProxy(clientSide = "com.bartz24.refinedexchange.proxy.ClientProxy", serverSide = "com.bartz24.refinedexchange.proxy.ServerProxy")
	public static CommonProxy proxy;

	@Mod.Instance
	public static RefinedExchange instance;

	public static Logger logger;

	public static final SimpleNetworkWrapper NETWORK_WRAPPER = NetworkRegistry.INSTANCE.newSimpleChannel(References.ModID);

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();
		proxy.preInit(event);
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init(event);
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit(event);
	}
}
