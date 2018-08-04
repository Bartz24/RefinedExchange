package com.bartz24.refinedexchange.features.network;

import com.bartz24.refinedexchange.RefinedExchange;
import com.bartz24.refinedexchange.config.ConfigOptions;
import com.bartz24.refinedexchange.features.PacketLiquifier;
import com.bartz24.refinedexchange.registry.ModBlocks;
import com.raoulvdberge.refinedstorage.api.util.Action;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNode;
import com.raoulvdberge.refinedstorage.inventory.item.ItemHandlerBase;
import com.raoulvdberge.refinedstorage.inventory.item.ItemHandlerUpgrade;
import com.raoulvdberge.refinedstorage.inventory.listener.ListenerNetworkNode;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import moze_intel.projecte.api.ProjectEAPI;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

import java.util.function.Predicate;

public class NetworkNodeLiquifier extends NetworkNode {

    private ItemHandlerBase inputs = new ItemHandlerBase(1, new ListenerNetworkNode(this), new Predicate[0]);
    private ItemHandlerUpgrade upgrades = new ItemHandlerUpgrade(4, new ListenerNetworkNode(this), new int[]{2, 4});

    private long emcStored;

    public NetworkNodeLiquifier(World world, BlockPos pos) {
        super(world, pos);
    }

    @Override
    public int getEnergyUsage() {
        int num = 20 + upgrades.getEnergyUsage() * 2;
        return (int) ((float) num * ConfigOptions.emcLiquifierEnergyMultiplier);
    }

    @Override
    public String getId() {
        return "liquifier";
    }

    public void update() {
        super.update();

        if (this.network != null && this.canUpdate() && this.ticks % this.upgrades.getSpeed() == 0) {

            boolean sendPacket = false;
            ItemStack stack = inputs.getStackInSlot(0);
            if (!stack.isEmpty() && ProjectEAPI.getEMCProxy().hasValue(stack)) {
                long emc = ProjectEAPI.getEMCProxy().getValue(stack);
                if (this.upgrades.hasUpgrade(4))
                    emc *= stack.getCount();
                emcStored += emc;
                stack.shrink(this.upgrades.hasUpgrade(4) ? stack.getCount() : 1);
                markDirty();
                sendPacket = true;
            }

            if (emcStored > 0) {
                int amountToInsert = 1000;
                if (this.upgrades.hasUpgrade(4))
                    amountToInsert *= 100;
                amountToInsert = (int) Math.max(Math.min(emcStored, amountToInsert), 0);
                if (amountToInsert > 0) {

                    FluidStack result = network.insertFluid(new FluidStack(ModBlocks.liquidEMC, amountToInsert), amountToInsert, Action.PERFORM);
                    if (result == null) {
                        emcStored -= amountToInsert;
                        markDirty();
                        sendPacket = true;
                    } else if (amountToInsert - result.amount > 0) {
                        emcStored -= (amountToInsert - result.amount);
                        markDirty();
                        sendPacket = true;
                    }
                }
            }
            if (sendPacket)
                sendPacket();
        }
    }

    public NBTTagCompound write(NBTTagCompound tag) {
        super.write(tag);
        StackUtils.writeItems(this.upgrades, 1, tag);
        StackUtils.writeItems(this.inputs, 0, tag);
        tag.setLong("emcStored", emcStored);
        return tag;
    }

    public void read(NBTTagCompound tag) {
        super.read(tag);
        StackUtils.readItems(this.upgrades, 1, tag);
        StackUtils.readItems(this.inputs, 0, tag);
        emcStored = tag.getLong("emcStored");
    }

    public ItemHandlerBase getInputs() {
        return inputs;
    }

    public ItemHandlerBase getUpgrades() {
        return upgrades;
    }

    public long getEmcStored() {
        return emcStored;
    }

    public void setEmcStored(long value) {
        emcStored = value;
    }

    @Override
    public IItemHandler getDrops() {
        return new CombinedInvWrapper(inputs, upgrades);
    }

    public void sendPacket() {

        if (!this.world.isRemote) {

            RefinedExchange.NETWORK_WRAPPER.sendToAll(new PacketLiquifier(emcStored, this.pos));
        }
    }

}
