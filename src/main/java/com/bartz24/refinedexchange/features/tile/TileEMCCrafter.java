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
import com.raoulvdberge.refinedstorage.api.network.INetworkMaster;
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
	private List<ItemStack> curCrafts;
	private long emcNeeded;
	private boolean dirty;

	public TileEMCCrafter() {
		items = new ItemHandlerBasic(1, this, new IItemValidator[] { new ItemValidatorBasic(ModItems.solidEMC) });
		filter = new ItemHandlerBasic(9, this, new IItemValidator[0]) {

			protected void onContentsChanged(int slot) {
				super.onContentsChanged(slot);
				TileEMCCrafter.this.rebuildPatterns();
			}
		};
		output = new ItemHandlerBasic(1, this, new IItemValidator[0]);
		upgrades = new ItemHandlerUpgrade(4, this, new int[] { 2 });
		patterns = new ArrayList<>();
		curCrafts = new ArrayList<>();
	}

	@Override
	public int getEnergyUsage() {
		return ConfigOptions.emcCrafterEnergy + upgrades.getEnergyUsage();
	}

	@Override
	public void updateNode() {
		if (ticks % 20 == 0) {
			int patternAmt = 0;
			for (int i = 0; i < filter.getSlots(); i++) {
				if (filter.getStackInSlot(i) != null)
					patternAmt++;
			}
			if (patternAmt != patterns.size())
				rebuildPatterns();
			curCrafts.clear();
			getCraftingRecipes();
			updateEMC();
			for (int i = curCrafts.size() - 1; i >= 0; i--) {
				craft();
				if (output.getStackInSlot(0) != null) {
					output.setStackInSlot(0,
							network.insertItem(output.getStackInSlot(0), output.getStackInSlot(0).stackSize, false));
					dirty = true;
				}
			}
		}
		if (dirty) {
			markDirty();
			dirty = false;
		}

	}

	private void updateEMC() {

		if (curCrafts.size() == 0) {
			if (items.getStackInSlot(0) != null) {
				items.setStackInSlot(0,
						network.insertItem(items.getStackInSlot(0), items.getStackInSlot(0).stackSize, false));
				dirty = true;
			}
		} else {
			int needed = 0;
			for (ItemStack s : curCrafts) {
				needed += ProjectEAPI.getEMCProxy().getValue(s);
			}
			int amt = items.getStackInSlot(0) == null ? needed : (needed - items.getStackInSlot(0).stackSize);

			if (amt > 0) {
				ItemStack extract = network.extractItem(new ItemStack(ModItems.solidEMC, amt), amt, false);
				if (extract != null) {
					if (items.getStackInSlot(0) == null)
						items.setStackInSlot(0, extract);
					else
						items.getStackInSlot(0).stackSize += extract.stackSize;
					dirty = true;
				}
			}
		}
	}

	private void craft() {
		if (getCurCraft() != null && items.getStackInSlot(0) != null && output.getStackInSlot(0) == null) {
			if (items.getStackInSlot(0).stackSize >= ProjectEAPI.getEMCProxy().getValue(getCurCraft())) {
				output.setStackInSlot(0, getCurCraft());
				items.getStackInSlot(0).stackSize -= ProjectEAPI.getEMCProxy().getValue(getCurCraft());
				if (items.getStackInSlot(0).stackSize <= 0)
					items.setStackInSlot(0, null);

				curCrafts.remove(curCrafts.size() - 1);
				dirty = true;
			}
		}
	}

	private void getCraftingRecipes() {
		int maxAmount = upgrades.getUpgradeCount(ItemUpgrade.TYPE_SPEED) * 8 + 1;
		int amount = 0;
		for (ICraftingTask task : network.getCraftingTasks()) {
			for (ICraftingStep step : task.getSteps()) {
				if (step.hasStartedProcessing() && step.getPattern().getContainer().getPosition() == pos) {
					curCrafts.add(step.getPattern().getOutputs().get(0));
					amount++;
					if (amount >= maxAmount)
						return;
				}
			}
		}
	}

	public void read(NBTTagCompound tag) {
		super.read(tag);
		items.deserializeNBT(tag.getCompoundTag("itemsInv"));
		upgrades.deserializeNBT(tag.getCompoundTag("upgradesInv"));
		filter.deserializeNBT(tag.getCompoundTag("filterInv"));
		output.deserializeNBT(tag.getCompoundTag("outputInv"));
		this.rebuildPatterns();
	}

	public NBTTagCompound write(NBTTagCompound tag) {
		tag = super.write(tag);
		tag.setTag("itemsInv", items.serializeNBT());
		tag.setTag("upgradesInv", upgrades.serializeNBT());
		tag.setTag("filterInv", filter.serializeNBT());
		tag.setTag("outputInv", output.serializeNBT());
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
		return curCrafts.size() > 0 ? curCrafts.get(curCrafts.size() - 1) : null;
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
					ItemPattern.addOutput(pattern, new ItemStack(filter.getStackInSlot(i).getItem(), 1,
							filter.getStackInSlot(i).getMetadata()));
					ItemPattern.isProcessing(pattern);
					patterns.add(((ICraftingPatternProvider) RSItems.PATTERN).create(world, pattern, this));
				}
			}
			if (network != null)
				network.rebuildPatterns();
			dirty = true;
		}
	}

	private void setEMCInputs(ItemStack pattern, int emc) {
		for (int i = 0; i < Math.floor(((float) emc) / 64f); i++) {
			ItemPattern.setSlot(pattern, i, new ItemStack(ModItems.solidEMC, Math.min(64, emc)));
			emc -= Math.min(64, emc);
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
		return 1;
	}

	@Override
	public void onConnectionChange(INetworkMaster network, boolean state) {
		if (!state) {
			network.getCraftingTasks().stream()
					.filter(task -> task.getPattern().getContainer().getPosition().equals(pos))
					.forEach(network::cancelCraftingTask);
		}

		network.rebuildPatterns();
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
