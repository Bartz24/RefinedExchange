package com.bartz24.refinedexchange.proxy;

import com.bartz24.refinedexchange.RefinedExchange;
import com.bartz24.refinedexchange.config.ConfigOptions;
import com.bartz24.refinedexchange.features.emc.ProjectEMappings;
import com.bartz24.refinedexchange.registry.ModBlocks;
import com.bartz24.refinedexchange.registry.ModCrafting;
import com.bartz24.refinedexchange.registry.ModGuiHandler;
import com.bartz24.refinedexchange.registry.ModItems;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class CommonProxy
{
	//EventHandler events = new EventHandler();

	public void preInit(FMLPreInitializationEvent e)
	{
		ConfigOptions.loadConfigThenSave(e);
		ModBlocks.init();
		ModItems.init();
		new ModGuiHandler();

	}

	public void init(FMLInitializationEvent e)
	{
		NetworkRegistry.INSTANCE.registerGuiHandler(RefinedExchange.instance, new ModGuiHandler());
		ModCrafting.initOreDict();
	}

	public void postInit(FMLPostInitializationEvent e)
	{
		ModCrafting.init();
		ProjectEMappings.postInit();
	}
}
