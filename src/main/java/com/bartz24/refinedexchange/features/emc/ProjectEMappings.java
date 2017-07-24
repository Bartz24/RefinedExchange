package com.bartz24.refinedexchange.features.emc;

import java.util.HashMap;
import java.util.List;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSItems;
import com.raoulvdberge.refinedstorage.api.solderer.ISoldererRecipe;
import com.raoulvdberge.refinedstorage.apiimpl.API;

import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.proxy.IConversionProxy;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class ProjectEMappings {
	public static void postInit() {
		IConversionProxy convProxy = ProjectEAPI.getConversionProxy();
		for (ISoldererRecipe r : API.instance().getSoldererRegistry().getRecipes()) {
			HashMap<Object, Integer> map = new HashMap<Object, Integer>();
			for (int i = 0; i < 3; i++) {
				ItemStack in = r.getRow(i);
				if (in != null) {
					if (map.containsKey(in))
						map.put(in, map.get(in) + in.stackSize);
					else
						map.put(in, in.stackSize);
				}
			}
			if (map.size() > 0 && r.getResult() != null) {
				convProxy.addConversion(r.getResult().stackSize, ProjectEMappings.getStackOreDict(r.getResult()), map);
			}
		}
		ProjectEAPI.getEMCProxy().registerCustomEMC(new ItemStack(RSItems.STORAGE_DISK, 1, OreDictionary.WILDCARD_VALUE), 0);
		ProjectEAPI.getEMCProxy().registerCustomEMC(new ItemStack(RSItems.FLUID_STORAGE_DISK, 1, OreDictionary.WILDCARD_VALUE), 0);
		ProjectEAPI.getEMCProxy().registerCustomEMC(new ItemStack(RSItems.UPGRADE, 1, 7), 0);
	}

	public static Object getStackOreDict(ItemStack stack) {
		int[] ids = OreDictionary.getOreIDs(stack);

		if (ids != null && ids.length > 0) {
			if (OreDictionary.getOreName(ids[0]).contains("ore") || OreDictionary.getOreName(ids[0]).contains("ingot")
					|| OreDictionary.getOreName(ids[0]).contains("dust"))
				return OreDictionary.getOreName(ids[0]);
		}
		return stack;
	}

	public static ItemStack getStackFromOre(Object obj) {
		if (obj instanceof ItemStack)
			return (ItemStack) obj;
		else if (obj instanceof String) {
			String name = (String) obj;
			List<ItemStack> stacks = OreDictionary.getOres(name);

			if (stacks.size() > 0) {
				return stacks.get(0);
			}
		}
		return null;
	}
}
