package com.bartz24.refinedexchange.features.item;

import java.util.ArrayList;
import java.util.List;

import com.bartz24.refinedexchange.References;
import com.bartz24.refinedexchange.registry.ModCreativeTabs;
import com.bartz24.refinedexchange.registry.ModItems;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemSolidEMC extends Item {
	private static ArrayList<String> names = new ArrayList<String>();

	public static final String low = "low";
	public static final String medium = "medium";
	public static final String high = "high";
	public static final String extreme = "extreme";
	public static final String ultimate = "ultimate";

	public ItemSolidEMC() {
		super();

		setUnlocalizedName(References.ModID + ".solidEMC.");
		setRegistryName("solidEMC");
		setHasSubtypes(true);
		this.setCreativeTab(ModCreativeTabs.tabMain);

		itemList();
	}

	private void itemList() {
		names.add(0, low);
		names.add(1, medium);
		names.add(2, high);
		names.add(3, extreme);
		names.add(4, ultimate);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return super.getUnlocalizedName(stack) + names.get(stack.getItemDamage());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item id, CreativeTabs creativeTab, List<ItemStack> list) {
		for (int i = 0; i < names.size(); i++)
			list.add(new ItemStack(id, 1, i));
	}

	public static ItemStack getStack(String name) {
		return new ItemStack(ModItems.solidEMC, 1, names.indexOf(name));
	}

	public static ArrayList<String> getNames() {
		return names;
	}

	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
		tooltip.add("Worth " + TextFormatting.WHITE + (long) Math.pow((long) 64, (long) stack.getMetadata())
				+ TextFormatting.YELLOW + " EMC");
		if (stack.stackSize > 1)
			tooltip.add("Stack worth " + TextFormatting.WHITE
					+ (long) Math.pow((long) 64, (long) stack.getMetadata()) * stack.stackSize + TextFormatting.YELLOW
					+ " EMC");
	}
}
