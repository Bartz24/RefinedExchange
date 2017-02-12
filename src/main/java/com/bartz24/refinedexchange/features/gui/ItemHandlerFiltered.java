package com.bartz24.refinedexchange.features.gui;

import com.raoulvdberge.refinedstorage.inventory.IItemValidator;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerBasic;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class ItemHandlerFiltered extends ItemHandlerBasic {

	public ItemHandlerFiltered(int size, TileEntity tile, IItemValidator[] validators) {
		super(size, tile, validators);
	}

	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		if (validators.length > slot) {
			IItemValidator aiitemvalidator[] = validators;
			IItemValidator validator = aiitemvalidator[slot];
			if (validator.isValid(stack))
				return super.insertItem(slot, stack, simulate);

			return stack;
		} else {
			return super.insertItem(slot, stack, simulate);
		}
	}

}
