package com.bartz24.refinedexchange.features.gui;

import com.bartz24.refinedexchange.features.tile.TileEMCCrafter;
import com.raoulvdberge.refinedstorage.container.ContainerBase;
import com.raoulvdberge.refinedstorage.container.slot.filter.SlotFilter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerEMCCrafter extends ContainerBase {
    protected TileEMCCrafter tile;

    public ContainerEMCCrafter(TileEMCCrafter tile, EntityPlayer player) {
        super(tile, player);
        this.tile = tile;

        for (int i = 0; i < 9; ++i) {
            addSlotToContainer(new SlotFilter(tile.getNode().getCraftItems(), i, 8 + 18 * i, 20));
        }

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

            if (index > 9 && index < 9 + 4) {
                if (!mergeItemStack(stack, 9 + 4, inventorySlots.size(), false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!mergeItemStack(stack, 9, 9 + 4, false)) {
                return ItemStack.EMPTY;
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
