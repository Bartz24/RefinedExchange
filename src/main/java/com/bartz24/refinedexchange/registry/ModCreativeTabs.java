package com.bartz24.refinedexchange.registry;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModCreativeTabs
{

	public static CreativeTabs tabMain = new CreativeTabs(
			"refinedExchange.tabMain")
	{
		@Override
		@SideOnly(Side.CLIENT)
		public Item getTabIconItem()
		{
			return Item.getItemFromBlock(ModBlocks.emcCrafter);
		}
	};
}
