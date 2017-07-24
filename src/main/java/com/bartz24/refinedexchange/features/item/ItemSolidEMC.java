package com.bartz24.refinedexchange.features.item;

import java.util.List;

import com.bartz24.refinedexchange.References;
import com.bartz24.refinedexchange.registry.ModCreativeTabs;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

public class ItemSolidEMC extends Item {

	public ItemSolidEMC() {
		super();

		setUnlocalizedName(References.ModID + ".solidEMC");
		setRegistryName("solidEMC");
		setHasSubtypes(true);
		this.setCreativeTab(ModCreativeTabs.tabMain);
	}

	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
		tooltip.add("Worth " + TextFormatting.WHITE + "1" + TextFormatting.YELLOW + " EMC");
		if (stack.stackSize > 1)
			tooltip.add("Stack worth " + TextFormatting.WHITE + stack.stackSize + TextFormatting.YELLOW + " EMC");
	}
}
