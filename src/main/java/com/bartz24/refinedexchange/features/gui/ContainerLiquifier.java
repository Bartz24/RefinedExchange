package com.bartz24.refinedexchange.features.gui;

import com.bartz24.refinedexchange.features.tile.TileLiquifier;
import com.raoulvdberge.refinedstorage.container.ContainerBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerLiquifier extends ContainerBase {
    protected TileLiquifier tile;

    public ContainerLiquifier(TileLiquifier tile, EntityPlayer player) {
        super(tile, player);
        this.tile = tile;

        addSlotToContainer(new SlotItemHandler(tile.getNode().getInputs(), 0, 80, 20));

        for (int i = 0; i < 4; ++i) {
            addSlotToContainer(new SlotItemHandler(tile.getNode().getUpgrades(), i, 187, 6 + (i * 18)));
        }

        addPlayerInventory(8, 55);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack stack = ItemStack.EMPTY;

        Slot slot = getSlot(index);

        if (slot.getHasStack()) {
            stack = slot.getStack();

            if (index < 5) {
                if (!mergeItemStack(stack, 9 + 9 + 9 + 4, inventorySlots.size(), false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!mergeItemStack(stack, 1, 5, false)) {
                if (!mergeItemStack(stack, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (stack.getCount() == 0) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
        }

        return stack;
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }
}
