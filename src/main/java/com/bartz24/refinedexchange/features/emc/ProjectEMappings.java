package com.bartz24.refinedexchange.features.emc;

import com.google.common.collect.ImmutableMap;
import com.raoulvdberge.refinedstorage.RSItems;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.proxy.IConversionProxy;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.List;

public class ProjectEMappings {
    public static void postInit() {
        IConversionProxy convProxy = ProjectEAPI.getConversionProxy();

        ProjectEAPI.getConversionProxy().addConversion(1, new ItemStack(RSItems.PROCESSOR, 1, 6), ImmutableMap.<Object, Integer>of("itemSilicon", 1));
        ProjectEAPI.getConversionProxy().addConversion(1, new ItemStack(RSItems.PROCESSOR, 1, 0), ImmutableMap.<Object, Integer>of("ingotIron", 1, "dustRedstone", 1, new ItemStack(RSItems.PROCESSOR, 1, 6), 1));
        ProjectEAPI.getConversionProxy().addConversion(1, new ItemStack(RSItems.PROCESSOR, 1, 1), ImmutableMap.<Object, Integer>of("ingotGold", 1, "dustRedstone", 1, new ItemStack(RSItems.PROCESSOR, 1, 6), 1));
        ProjectEAPI.getConversionProxy().addConversion(1, new ItemStack(RSItems.PROCESSOR, 1, 2), ImmutableMap.<Object, Integer>of("gemDiamond", 1, "dustRedstone", 1, new ItemStack(RSItems.PROCESSOR, 1, 6), 1));

        ProjectEAPI.getEMCProxy().registerCustomEMC(new ItemStack(RSItems.STORAGE_DISK, 1, OreDictionary.WILDCARD_VALUE), 0);
        ProjectEAPI.getEMCProxy().registerCustomEMC(new ItemStack(RSItems.FLUID_STORAGE_DISK, 1, OreDictionary.WILDCARD_VALUE), 0);
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
