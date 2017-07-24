package com.bartz24.refinedexchange.registry;

import com.raoulvdberge.refinedstorage.RSBlocks;
import com.raoulvdberge.refinedstorage.RSItems;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModCrafting {
	public static void init() {
		Item covDust = Item.REGISTRY.getObject(new ResourceLocation("projecte", "item.pe_covalence_dust"));
		Item matter = Item.REGISTRY.getObject(new ResourceLocation("projecte", "item.pe_matter"));
		Block condenser = Block.REGISTRY.getObject(new ResourceLocation("projecte", "condenser_mk1"));
		GameRegistry.addRecipe(new ItemStack(ModBlocks.emcCrafter),
				new Object[] { "XYX", "ZAZ", "XYX", 'X', new ItemStack(RSItems.QUARTZ_ENRICHED_IRON), 'Y',
						new ItemStack(matter), 'Z', new ItemStack(RSItems.CORE), 'A',
						new ItemStack(RSBlocks.CRAFTER) });
		GameRegistry.addRecipe(new ItemStack(ModBlocks.emcSolidifier),
				new Object[] { "XYX", "ZAZ", "XBX", 'X', new ItemStack(RSItems.QUARTZ_ENRICHED_IRON), 'Y',
						new ItemStack(condenser), 'Z', new ItemStack(RSItems.CORE, 1), 'A',
						new ItemStack(RSBlocks.MACHINE_CASING), 'B', new ItemStack(RSItems.PROCESSOR, 1, 5) });

	}

	public static void initOreDict() {

	}
}
