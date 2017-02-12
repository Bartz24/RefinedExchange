package com.bartz24.refinedexchange.features.gui;

import com.bartz24.refinedexchange.features.tile.TileEMCSolidifier;
import com.raoulvdberge.refinedstorage.container.ContainerBase;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerEMCSolidifier extends ContainerBase {

	public ContainerEMCSolidifier(TileEMCSolidifier condenser, EntityPlayer player) {
		super(condenser, player);
		for (int i = 0; i < 4; i++)
			addSlotToContainer(new SlotItemHandler(condenser.getUpgrades(), i, 187, 6 + i * 18));

		addSlotToContainer(new SlotItemHandler(condenser.getItems(), 0, 80, 20));

		addPlayerInventory(8, 55);
	}

	public ItemStack transferStackInSlot(EntityPlayer player, int index)
    {
        ItemStack stack = null;
        Slot slot = getSlot(index);
        if(slot != null && slot.getHasStack())
        {
            stack = slot.getStack();
            if(index < 5)
            {
                if(!mergeItemStack(stack, 5, inventorySlots.size(), false))
                    return null;
            } else
            if(!mergeItemStack(stack, 0, 5, false))
                return null;
            if(stack.stackSize == 0)
                slot.putStack(null);
            else
                slot.onSlotChanged();
        }
        return stack;
    }
}

/*
 * DECOMPILATION REPORT
 * 
 * Decompiled from:
 * C:\Users\Kids\.gradle\caches\modules-2\files-2.1\refinedstorage\
 * refinedstorage\1.2.14-793\880e2cbdea25e62c38a7c3e9ec2af77bf7b9ee53\
 * refinedstorage-1.2.14-793.jar Total time: 239 ms Jad reported
 * messages/errors: Exit status: 0 Caught exceptions:
 */