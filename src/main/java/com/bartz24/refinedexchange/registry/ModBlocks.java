package com.bartz24.refinedexchange.registry;

import com.bartz24.refinedexchange.References;
import com.bartz24.refinedexchange.features.block.BlockEMCConverter;
import com.bartz24.refinedexchange.features.block.BlockEMCCrafter;
import com.bartz24.refinedexchange.features.block.BlockEMCSolidifier;
import com.bartz24.refinedexchange.features.tile.TileEMCConverter;
import com.bartz24.refinedexchange.features.tile.TileEMCCrafter;
import com.bartz24.refinedexchange.features.tile.TileEMCSolidifier;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModBlocks {
	public static Block emcCrafter;
	public static Block emcConverter;
	public static Block emcSolidifier;

	public static void init() {
		emcCrafter = registerBlock(new BlockEMCCrafter());
		GameRegistry.registerTileEntity(TileEMCCrafter.class, References.ModID + ":emcCrafterTile");
		emcConverter = registerBlock(new BlockEMCConverter());
		GameRegistry.registerTileEntity(TileEMCConverter.class, References.ModID + ":emcConverterTile");
		emcSolidifier = registerBlock(new BlockEMCSolidifier());
		GameRegistry.registerTileEntity(TileEMCSolidifier.class, References.ModID + ":emcSolidifierTile");

	}

	public static Block registerBlockOnly(Block block, String name) {
		GameRegistry.register(block, new ResourceLocation(References.ModID, name));

		return block;
	}

	public static Block registerBlock(Block block, String name) {
		GameRegistry.register(block, new ResourceLocation(References.ModID, name));
		GameRegistry.register(new ItemBlock(block).setRegistryName(new ResourceLocation(References.ModID, name)));

		return block;
	}

	public static Block registerBlockOnly(Block block) {
		GameRegistry.register(block);

		return block;
	}

	public static Block registerBlock(Block block) {
		GameRegistry.register(block);
		GameRegistry.register(new ItemBlock(block).setRegistryName(block.getRegistryName()));

		return block;
	}
}
