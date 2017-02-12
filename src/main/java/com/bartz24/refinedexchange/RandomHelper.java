package com.bartz24.refinedexchange;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.oredict.OreDictionary;

public class RandomHelper {

	public static IBlockState getBlockStateFromStack(ItemStack stack)
	{
		int meta = stack.getMetadata();
		if (!(stack.getItem() instanceof ItemBlock))
			return null;

		Block block = ((ItemBlock) stack.getItem()).getBlock();

		return block.getStateFromMeta(meta);
	}

	public static boolean itemStacksEqualOD(ItemStack stack1, ItemStack stack2)
	{
		if (stack1.isItemEqual(stack2))
			return true;

		if (stack1 == null && stack2 != null && stack1.getMetadata() == OreDictionary.WILDCARD_VALUE
				|| stack2.getMetadata() == OreDictionary.WILDCARD_VALUE)
		{
			return stack1.getItem() == stack2.getItem();
		}
		return false;
	}
	
	public static boolean canStacksMerge(ItemStack stack1, ItemStack stack2) {
		if (stack1 == null || stack2 == null) {
			return false;
		}
		if (!stack1.isItemEqual(stack2)) {
			return false;
		}
		if (!ItemStack.areItemStackTagsEqual(stack1, stack2)) {
			return false;
		}
		return true;

	}

	public static int mergeStacks(ItemStack mergeSource, ItemStack mergeTarget, boolean doMerge) {
		if (!canStacksMerge(mergeSource, mergeTarget)) {
			return 0;
		}
		int mergeCount = Math.min(mergeTarget.getMaxStackSize() - mergeTarget.stackSize, mergeSource.stackSize);
		if (mergeCount < 1) {
			return 0;
		}
		if (doMerge) {
			mergeTarget.stackSize += mergeCount;
		}
		return mergeCount;
	}

	public static ItemStack fillInventory(IInventory inv, ItemStack stack) {
		if (inv != null) {
			for (int i = 0; i < inv.getSizeInventory(); i++) {
				if (stack == null)
					return null;
				ItemStack inside = inv.getStackInSlot(i);
				if (inside == null) {
					inv.setInventorySlotContents(i, stack);
					return null;
				} else if (RandomHelper.canStacksMerge(inside, stack)) {
					stack.stackSize -= RandomHelper.mergeStacks(stack, inside, true);
				}
			}
		}
		return stack;

	}

	public static ItemStack fillInventory(IItemHandler inv, ItemStack stack) {
		if (inv != null) {
			for (int i = 0; i < inv.getSlots(); i++) {
				if (stack == null)
					return null;
				ItemStack inside = inv.getStackInSlot(i);
				if (inside == null) {
					inv.insertItem(i, stack, false);
					return null;
				} else if (RandomHelper.canStacksMerge(inside, stack)) {
					stack.stackSize -= RandomHelper.mergeStacks(stack, inside, true);
				}
			}
		}
		return stack;

	}
}
