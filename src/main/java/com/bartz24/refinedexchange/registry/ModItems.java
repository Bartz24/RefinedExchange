package com.bartz24.refinedexchange.registry;

import com.bartz24.refinedexchange.References;
import com.bartz24.refinedexchange.RefinedExchange;
import com.bartz24.refinedexchange.features.item.ItemSolidEMC;

import moze_intel.projecte.api.ProjectEAPI;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModItems
{
	public static Item solidEMC;

	public static void init()
	{

		solidEMC = registerItem(new ItemSolidEMC());
		
	}

	private static Item registerItem(Item item, String name)
	{
		GameRegistry.register(item, new ResourceLocation(References.ModID, name));

		return item;
	}

	private static Item registerItem(Item item)
	{
		if (item.getRegistryName() == null)
		{
			RefinedExchange.logger.error(
					"Item {} doesn't have a registry name. Item will not be registered.",
					item.getClass().getCanonicalName());
			return item;
		}
		GameRegistry.register(item);

		return item;
	}
}
