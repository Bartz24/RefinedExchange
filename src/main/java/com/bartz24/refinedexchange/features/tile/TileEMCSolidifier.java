package com.bartz24.refinedexchange.features.tile;

import com.bartz24.refinedexchange.config.ConfigOptions;
import com.bartz24.refinedexchange.registry.ModItems;
import com.raoulvdberge.refinedstorage.inventory.IItemValidator;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerBasic;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerUpgrade;
import com.raoulvdberge.refinedstorage.item.ItemUpgrade;
import com.raoulvdberge.refinedstorage.tile.TileNode;

import moze_intel.projecte.api.ProjectEAPI;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

public class TileEMCSolidifier extends TileNode {

	private ItemHandlerBasic items;
	private ItemHandlerBasic output;
	private ItemHandlerUpgrade upgrades;
	private long emcStored;

	public TileEMCSolidifier() {
		items = new ItemHandlerBasic(1, this, new IItemValidator[0]);
		output = new ItemHandlerBasic(1, this, new IItemValidator[0]);
		upgrades = new ItemHandlerUpgrade(4, this, new int[] { 2, 4 });
	}

	@Override
	public int getEnergyUsage() {
		return ConfigOptions.emcSolidifierEnergy + upgrades.getEnergyUsage();
	}

	@Override
	public void updateNode() {
		if (ticks % (ConfigOptions.emcSolidifierSpeed - upgrades.getUpgradeCount(ItemUpgrade.TYPE_SPEED) * 4) == 0) {
			updateEMC();
			if (output.getStackInSlot(0) != null) {
				output.setStackInSlot(0,
						network.insertItem(output.getStackInSlot(0), output.getStackInSlot(0).stackSize, false));
			}
			solidify();
		}
		if (ticks % 20 == 0)
			markDirty();

	}

	private void updateEMC() {

		for (int i = 4; i >= 0; i--) {
			if (emcStored >= Math.pow(64, i) && output.getStackInSlot(0) == null) {
				long num = Math.min(Math.floorDiv(emcStored, (int) Math.pow(64, i)),
						upgrades.getUpgradeCount(ItemUpgrade.TYPE_STACK) > 0 ? 64 : 1);
				output.setStackInSlot(0, new ItemStack(ModItems.solidEMC, (int) num, i));
				emcStored -= Math.pow(64, i) * num;
			}
		}
	}

	private void solidify() {
		if (items.getStackInSlot(0) != null && ProjectEAPI.getEMCProxy().getValue(items.getStackInSlot(0)) > 0) {
			int num = Math.min(items.getStackInSlot(0).stackSize,
					upgrades.getUpgradeCount(ItemUpgrade.TYPE_STACK) > 0 ? 64 : 1);
			emcStored += ProjectEAPI.getEMCProxy().getValue(items.getStackInSlot(0)) * num;
			items.getStackInSlot(0).stackSize -= num;
			if (items.getStackInSlot(0).stackSize <= 0)
				items.setStackInSlot(0, null);
		}
	}

	public void read(NBTTagCompound tag) {
		super.read(tag);
		items.deserializeNBT(tag.getCompoundTag("itemsInv"));
		upgrades.deserializeNBT(tag.getCompoundTag("upgradesInv"));
		output.deserializeNBT(tag.getCompoundTag("outputInv"));
		emcStored = tag.getLong("emcStored");
	}

	public NBTTagCompound write(NBTTagCompound tag) {
		tag = super.write(tag);
		tag.setTag("itemsInv", items.serializeNBT());
		tag.setTag("upgradesInv", upgrades.serializeNBT());
		tag.setTag("outputInv", output.serializeNBT());
		tag.setLong("emcStored", emcStored);
		return tag;
	}

	public IItemHandler getUpgrades() {
		return upgrades;
	}

	public IItemHandler getItems() {
		return items;
	}

	public IItemHandler getDrops() {
		return new CombinedInvWrapper(items, output, upgrades);
	}

	public long getEmcStored() {
		return emcStored;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return (T) items;
		}
		return super.getCapability(capability, facing);
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound nbtTag = new NBTTagCompound();
		this.writeToNBT(nbtTag);
		return new SPacketUpdateTileEntity(getPos(), 1, nbtTag);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
		super.onDataPacket(net, packet);
		this.readFromNBT(packet.getNbtCompound());
	}

	public void markDirty() {
		super.markDirty();
		world.notifyBlockUpdate(getPos(), world.getBlockState(getPos()), world.getBlockState(getPos()), 3);
	}
}
