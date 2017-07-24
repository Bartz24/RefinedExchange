package com.bartz24.refinedexchange.config;

import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ConfigOptions {
	public static Configuration config;

	public static int emcCrafterEnergy;

	public static int emcSolidifierEnergy;

	public static List<IConfigElement> getConfigElements() {
		List<IConfigElement> list = new ArrayList<IConfigElement>();

		list.addAll(new ConfigElement(config.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements());

		return list;
	}

	public static void setConfigSettings() {
		emcCrafterEnergy = config.get(Configuration.CATEGORY_GENERAL, "EMC Crafter Energy", 40).getInt(40);

		emcSolidifierEnergy = config.get(Configuration.CATEGORY_GENERAL, "EMC Solidifier Energy", 20).getInt(20);

		if (config.hasChanged())
			config.save();
	}

	public static void loadConfigThenSave(FMLPreInitializationEvent e) {
		config = new Configuration(e.getSuggestedConfigurationFile());

		config.load();
		setConfigSettings();
		config.save();
	}

	public static void reloadConfigs() {
		setConfigSettings();
		if (config.hasChanged())
			config.save();
	}
}
