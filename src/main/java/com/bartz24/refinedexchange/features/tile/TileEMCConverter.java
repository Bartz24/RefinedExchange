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

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

public class TileEMCConverter extends TileNode implements ICraftingPatternContainer {

	private ItemHandlerBasic items;
	private ItemHandlerUpgrade upgrades;
	private ItemHandlerBasic output;
	private List<ICraftingPattern> patterns;
	private ICraftingPattern curPattern;
	private boolean convertUp;

	public TileEMCConverter() {
		items = new ItemHandlerBasic(1, this, new IItemValidator[] { new ItemValidatorBasic(ModItems.solidEMC) });
		upgrades = new ItemHandlerUpgrade(4, this, new int[] { 2 });
		output = new ItemHandlerBasic(1, this, new IItemValidator[0]);
		patterns = new ArrayList<>();
	}

	@Override
	public int getEnergyUsage() {
		return ConfigOptions.emcConverterEnergy + upgrades.getEnergyUsage();
	}

	@Override
	public void updateNode() {
		if (ticks % 100 == 0) {
			this.rebuildPatterns();
		}
		if (ticks % Math.max(ConfigOptions.emcConverterSpeed - upgrades.getUpgradeCount(ItemUpgrade.TYPE_SPEED) * 4,
				1) == 0) {
			getCraftingRecipe();
			if (curPattern == null) {
				if (items.getStackInSlot(0) != null)
					items.setStackInSlot(0,
							network.insertItem(items.getStackInSlot(0), items.getStackInSlot(0).stackSize, false));
			}
			craft();
		}
		if (ticks % 20 == 0)
			markDirty();
	}

	private void craft() {
		if (curPattern != null && output.getStackInSlot(0) == null) {
			if (checkCraft()) {
				output.setStackInSlot(0, curPattern.getOutputs().get(0).copy());
				if (items.getStackInSlot(0) != null) {
					items.getStackInSlot(0).stackSize -= curPattern.getInputs().get(0).stackSize;
					if (items.getStackInSlot(0).stackSize <= 0)
						items.setStackInSlot(0, null);
				}
				if (output.getStackInSlot(0) != null) {
					output.setStackInSlot(0,
							network.insertItem(output.getStackInSlot(0), output.getStackInSlot(0).stackSize, false));
				}
				curPattern = null;
			}
		}
	}

	private boolean checkCraft() {
		return (items.getStackInSlot(0) != null
				&& items.getStackInSlot(0).stackSize >= curPattern.getInputs().get(0).stackSize);
	}

	private void getCraftingRecipe() {
		List<ICraftingTask> tasks = network.getCraftingTasks();
		for (ICraftingTask task : tasks) {
			for (ICraftingStep step : task.getSteps()) {
				if (step.hasStartedProcessing() && step.getPattern().getContainer().getPosition() == pos) {
					curPattern = step.getPattern();
					return;
				}
			}
		}
		curPattern = null;
	}

	public void read(NBTTagCompound tag) {
		super.read(tag);
		items.deserializeNBT(tag.getCompoundTag("itemsInv"));
		upgrades.deserializeNBT(tag.getCompoundTag("upgradesInv"));
		output.deserializeNBT(tag.getCompoundTag("outputInv"));
		convertUp = tag.getBoolean("convertUp");
		this.rebuildPatterns();
	}

	public NBTTagCompound write(NBTTagCompound tag) {
		tag = super.write(tag);
		tag.setTag("itemsInv", items.serializeNBT());
		tag.setTag("upgradesInv", upgrades.serializeNBT());
		tag.setTag("outputInv", output.serializeNBT());
		tag.setBoolean("convertUp", convertUp);
		return tag;
	}

	public IItemHandler getUpgrades() {
		return upgrades;
	}

	public IItemHandler getItems() {
		return items;
	}

    public IItemHandler getDrops()
    {
        return new CombinedInvWrapper(items, output, upgrades);
    }

	public boolean getConvertUp() {
		return convertUp;
	}

	public void setConvertUp(boolean val) {
		convertUp = val;
		this.rebuildPatterns();
	}

	public List<ICraftingPattern> getPatterns() {
		return patterns;
	}

	public void rebuildPatterns() {
		if (world != null && !world.isRemote) {
			patterns.clear();

			addConvert(0);
			addConvert(1);
			addConvert(2);
			addConvert(3);

			if (network != null)
				network.rebuildPatterns();
		}
	}

	private void addConvert(int type) {
		ItemStack pattern = new ItemStack(RSItems.PATTERN);
		if (convertUp) {
			ItemPattern.setSlot(pattern, 0, new ItemStack(ModItems.solidEMC, 64, type));
			ItemPattern.addOutput(pattern, new ItemStack(ModItems.solidEMC, 1, type + 1));
		} else {

			ItemPattern.setSlot(pattern, 0, new ItemStack(ModItems.solidEMC, 1, type + 1));
			ItemPattern.addOutput(pattern, new ItemStack(ModItems.solidEMC, 64, type));
		}
		ItemPattern.isProcessing(pattern);
		patterns.add(((ICraftingPatternProvider) RSItems.PATTERN).create(world, pattern, this));
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
		return ConfigOptions.emcConverterSpeed + upgrades.getUpgradeCount(ItemUpgrade.TYPE_SPEED);
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
