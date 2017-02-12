package com.bartz24.refinedexchange.features.tile;

import java.util.ArrayList;
import java.util.List;

import com.bartz24.refinedexchange.config.ConfigOptions;
import com.bartz24.refinedexchange.registry.ModItems;
import com.raoulvdberge.refinedstorage.RSItems;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPatternContainer;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPatternProvider;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.ICraftingStep;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.ICraftingTask;
import com.raoulvdberge.refinedstorage.inventory.IItemValidator;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerBasic;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerUpgrade;
import com.raoulvdberge.refinedstorage.inventory.ItemValidatorBasic;
import com.raoulvdberge.refinedstorage.item.ItemPattern;
import com.raoulvdberge.refinedstorage.item.ItemUpgrade;
import com.raoulvdberge.refinedstorage.tile.TileNode;

import moze_intel.projecte.api.ProjectEAPI;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

public class TileEMCCrafter extends TileNode implements ICraftingPatternContainer {

	private ItemHandlerBasic items;
	private ItemHandlerBasic filter;
	private ItemHandlerBasic output;
	private ItemHandlerUpgrade upgrades;
	private List<ICraftingPattern> patterns;
	private ItemStack curItem;
	private long emcStored;

	public TileEMCCrafter() {
		items = new ItemHandlerBasic(5, this, new IItemValidator[] { new ItemValidatorBasic(ModItems.solidEMC) });
		filter = new ItemHandlerBasic(9, this, new IItemValidator[0]);
		output = new ItemHandlerBasic(1, this, new IItemValidator[0]);
		upgrades = new ItemHandlerUpgrade(4, this, new int[] { 2 });
		patterns = new ArrayList<>();
	}

	@Override
	public int getEnergyUsage() {
		return ConfigOptions.emcCrafterEnergy + upgrades.getEnergyUsage();
	}

	@Override
	public void updateNode() {
		if (ticks % 100 == 0) {
			this.rebuildPatterns();
		}
		if (ticks % Math.max(ConfigOptions.emcCrafterSpeed - upgrades.getUpgradeCount(ItemUpgrade.TYPE_SPEED) * 4,
				1) == 0) {
			getCraftingRecipe();
			updateEMC();
			craft();
			if (output.getStackInSlot(0) != null) {
				output.setStackInSlot(0,
						network.insertItem(output.getStackInSlot(0), output.getStackInSlot(0).stackSize, false));
			}
		}
		if (ticks % 20 == 0)
			markDirty();

	}

	private void updateEMC() {

		if (curItem == null) {
			for (int i = 4; i >= 0; i--) {
				if (emcStored >= Math.pow(64, i)) {
					long num = Math.floorDiv(emcStored, (int) Math.pow(64, i));
					items.setStackInSlot(i, new ItemStack(ModItems.solidEMC, (int) num, i));
					emcStored -= Math.pow(64, i) * num;
				}
			}
			for (int i = 0; i < items.getSlots(); i++)
				if (items.getStackInSlot(i) != null)
					items.setStackInSlot(i,
							network.insertItem(items.getStackInSlot(i), items.getStackInSlot(i).stackSize, false));
		} else {
			for (int i = 0; i < items.getSlots(); i++) {
				if (items.getStackInSlot(i) != null) {
					emcStored += items.getStackInSlot(i).stackSize
							* Math.pow(64, items.getStackInSlot(i).getMetadata());
					items.setStackInSlot(i, null);
				}
			}
		}
	}

	private void craft() {
		if (curItem != null && output.getStackInSlot(0) == null) {
			if (emcStored >= ProjectEAPI.getEMCProxy().getValue(curItem)) {
				output.setStackInSlot(0, curItem);
				emcStored -= ProjectEAPI.getEMCProxy().getValue(curItem);
				curItem = null;
			}
		}
	}

	private void getCraftingRecipe() {
		List<ICraftingTask> tasks = network.getCraftingTasks();

		for (ICraftingTask task : tasks) {
			for (ICraftingStep step : task.getSteps()) {
				if (step.hasStartedProcessing() && step.getPattern().getContainer().getPosition() == pos) {
					curItem = step.getPattern().getOutputs().get(0);
					return;
				}
			}
		}
		curItem = null;
	}

	public void read(NBTTagCompound tag) {
		super.read(tag);
		items.deserializeNBT(tag.getCompoundTag("itemsInv"));
		upgrades.deserializeNBT(tag.getCompoundTag("upgradesInv"));
		filter.deserializeNBT(tag.getCompoundTag("filterInv"));
		output.deserializeNBT(tag.getCompoundTag("outputInv"));
		curItem = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("curItem"));
		emcStored = tag.getLong("emcStored");
		this.rebuildPatterns();
	}

	public NBTTagCompound write(NBTTagCompound tag) {
		tag = super.write(tag);
		tag.setTag("itemsInv", items.serializeNBT());
		tag.setTag("upgradesInv", upgrades.serializeNBT());
		tag.setTag("filterInv", filter.serializeNBT());
		tag.setTag("outputInv", output.serializeNBT());
		tag.setLong("emcStored", emcStored);
		if (curItem != null)
			curItem.writeToNBT(tag.getCompoundTag("curItem"));
		return tag;
	}

	public IItemHandler getUpgrades() {
		return upgrades;
	}

	public IItemHandler getItems() {
		return items;
	}

	public IItemHandler getFilter() {
		return filter;
	}

	public IItemHandler getDrops() {
		return new CombinedInvWrapper(items, output, upgrades);
	}

	public ItemStack getCurCraft() {
		return curItem;
	}

	public long getEMCStored() {
		return emcStored;
	}

	public List<ICraftingPattern> getPatterns() {
		return patterns;
	}

	public void rebuildPatterns() {
		if (world != null && !world.isRemote) {
			patterns.clear();
			for (int i = 0; i < filter.getSlots(); i++) {
				if (filter.getStackInSlot(i) != null
						&& ProjectEAPI.getEMCProxy().getValue(filter.getStackInSlot(i)) > 0) {
					ItemStack pattern = new ItemStack(RSItems.PATTERN);
					setEMCInputs(pattern, ProjectEAPI.getEMCProxy().getValue(filter.getStackInSlot(i)));
					ItemPattern.addOutput(pattern, new ItemStack(filter.getStackInSlot(i).getItem(), 1,
							filter.getStackInSlot(i).getMetadata()));
					ItemPattern.isProcessing(pattern);
					patterns.add(((ICraftingPatternProvider) RSItems.PATTERN).create(world, pattern, this));
				}
			}
			if (network != null)
				network.rebuildPatterns();
		}
	}

	private void setEMCInputs(ItemStack pattern, int emc) {
		for (int i = 4; i >= 0; i--) {
			if (emc >= Math.pow(64, i)) {
				int num = Math.floorDiv(emc, (int) Math.pow(64, i));
				ItemPattern.setSlot(pattern, i, new ItemStack(ModItems.solidEMC, num, i));
				emc -= Math.pow(64, i) * num;
			}
		}
	}

	@Override
	public IItemHandler getFacingInventory() {
		return items;
	}

	@Override
	public TileEntity getFacingTile() {
		return this;
	}

	@Override
	public int getSpeedUpdateCount() {
		return ConfigOptions.emcCrafterSpeed + upgrades.getUpgradeCount(ItemUpgrade.TYPE_SPEED);
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
