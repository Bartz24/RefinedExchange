package com.bartz24.refinedexchange.features.network;

import com.bartz24.refinedexchange.config.ConfigOptions;
import com.bartz24.refinedexchange.registry.ModBlocks;
import com.raoulvdberge.refinedstorage.RSItems;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPatternContainer;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPatternProvider;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.ICraftingTask;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.util.Action;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task.CraftingTask;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task.step.CraftingStep;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNode;
import com.raoulvdberge.refinedstorage.inventory.item.ItemHandlerBase;
import com.raoulvdberge.refinedstorage.inventory.item.ItemHandlerUpgrade;
import com.raoulvdberge.refinedstorage.inventory.listener.ListenerNetworkNode;
import com.raoulvdberge.refinedstorage.item.ItemPattern;
import com.raoulvdberge.refinedstorage.item.ItemUpgrade;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import moze_intel.projecte.api.ProjectEAPI;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

public class NetworkNodeEMCCrafter extends NetworkNode implements ICraftingPatternContainer {

    private ItemHandlerBase patterns = new ItemHandlerBase(9, new ListenerNetworkNode(this), new Predicate[]{(s) -> {
        return isValidPatternInSlot((ItemStack) s);
    }}) {
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            if (!NetworkNodeEMCCrafter.this.world.isRemote) {
                NetworkNodeEMCCrafter.this.rebuildPatterns();
            }

            if (NetworkNodeEMCCrafter.this.network != null) {
                NetworkNodeEMCCrafter.this.network.getCraftingManager().rebuild();
            }

        }

        public int getSlotLimit(int slot) {
            return 1;
        }
    };
    private ItemHandlerUpgrade upgrades = new ItemHandlerUpgrade(4, new ListenerNetworkNode(this), new int[]{ItemUpgrade.TYPE_SPEED});
    private UUID uuid = null;

    private FluidTank tankToCraft = new FluidTank(ModBlocks.liquidEMC, 0, Integer.MAX_VALUE);

    public NetworkNodeEMCCrafter(World world, BlockPos pos) {
        super(world, pos);
    }

    public static boolean isValidPatternInSlot(ItemStack stack) {
        return ProjectEAPI.getEMCProxy().hasValue(stack) && ProjectEAPI.getEMCProxy().getValue(stack) < Integer.MAX_VALUE;
    }

    private List<ICraftingPattern> actualPatterns = new ArrayList();

    private void rebuildPatterns() {
        this.actualPatterns.clear();

        for (int i = 0; i < this.patterns.getSlots(); ++i) {
            ItemStack craftStack = this.patterns.getStackInSlot(i);
            if (!craftStack.isEmpty()) {

                ItemStack patternStack = new ItemStack(RSItems.PATTERN);

                ItemPattern.setFluidInputSlot(patternStack, 0, new FluidStack(ModBlocks.liquidEMC, (int) ProjectEAPI.getEMCProxy().getValue(craftStack)));
                ItemPattern.setOutputSlot(patternStack, 0, craftStack.copy());
                ItemPattern.setProcessing(patternStack, true);

                ICraftingPattern pattern = ((ICraftingPatternProvider) patternStack.getItem()).create(this.world, patternStack, this);

                if (pattern.isValid()) {
                    this.actualPatterns.add(pattern);
                }
            }
        }

    }

    @Override
    public int getEnergyUsage() {
        int num = 50;
        num += upgrades.getEnergyUsage() * 2;
        num += getEnergyFromPatterns();
        return (int)((float) num * ConfigOptions.emcCrafterEnergyMultiplier);
    }

    private int getEnergyFromPatterns() {
        int num = 0;
        for (int i = 0; i < actualPatterns.size(); i++) {
            if (ProjectEAPI.getEMCProxy().getValue(actualPatterns.get(i).getOutputs().get(0)) > 500000000L)
                num += 100000000;
            else
                num += (int) Math.max((Math.pow(ProjectEAPI.getEMCProxy().getValue(actualPatterns.get(i).getOutputs().get(0)) / 10f, 1.2) / 100f), 5);
        }
        return num;
    }

    @Override
    public String getId() {
        return "emccrafter";
    }

    @Override
    public void update() {
        super.update();

        if (ticks == 1) {
            rebuildPatterns();
        }

        if (this.network != null && this.canUpdate() && this.ticks % this.upgrades.getSpeed() == 0) {
            boolean hasTasks = false;
            for (ICraftingTask task : network.getCraftingManager().getTasks()) {
                if (task instanceof CraftingTask) {
                    CraftingTask craftingTask = (CraftingTask) task;
                    List<CraftingStep> steps = ObfuscationReflectionHelper.getPrivateValue(CraftingTask.class, craftingTask, "steps");
                    CraftingStep curStep = null;
                    for (int i = 0; i < steps.size(); i++) {
                        if (!steps.get(i).isCompleted() && steps.get(i).getPattern().getContainer().getPosition() == pos) {
                            curStep = steps.get(i);
                            break;
                        }
                    }

                    if (curStep != null) {
                        hasTasks = true;
                        ICraftingPattern pattern = curStep.getPattern();
                        if (tankToCraft.getFluidAmount() >= pattern.getFluidInputs().get(0).amount) {
                            if (network.insertItem(pattern.getOutputs().get(0).copy(), 1, Action.SIMULATE) == null) {
                                tankToCraft.drain(pattern.getFluidInputs().get(0).amount, true);
                                network.insertItemTracked(pattern.getOutputs().get(0).copy(), 1);
                                markDirty();
                                curStep.setCompleted();
                                break;
                            }
                        }
                    }
                }
            }
            if (!hasTasks && tankToCraft.getFluidAmount() > 0) {
                FluidStack result = network.insertFluid(tankToCraft.getFluid(), tankToCraft.getFluidAmount(), Action.PERFORM);
                if (result == null) {
                    tankToCraft.drain(tankToCraft.getFluidAmount(), true);
                    markDirty();
                } else if (tankToCraft.getFluidAmount() - result.amount > 0) {
                    tankToCraft.drain(tankToCraft.getFluidAmount() - result.amount, true);
                    markDirty();
                }
            }
        }
    }

    @Override
    protected void onConnectedStateChange(INetwork network, boolean state) {
        super.onConnectedStateChange(network, state);

        if (!state) {
            network.getCraftingManager().getTasks().stream()
                    .filter(task -> task.getPattern().getContainer().getPosition().equals(pos))
                    .forEach(task -> network.getCraftingManager().cancel(task.getId()));
        }

        network.getCraftingManager().rebuild();
    }


    public NBTTagCompound write(NBTTagCompound tag) {
        super.write(tag);
        StackUtils.writeItems(this.upgrades, 1, tag);
        StackUtils.writeItems(this.patterns, 0, tag);
        tankToCraft.writeToNBT(tag);
        return tag;
    }

    public void read(NBTTagCompound tag) {
        super.read(tag);
        StackUtils.readItems(this.upgrades, 1, tag);
        StackUtils.readItems(this.patterns, 0, tag);
        tankToCraft.readFromNBT(tag);
    }

    @Override
    public int getSpeedUpgradeCount() {
        return this.upgrades.getUpgradeCount(ItemUpgrade.TYPE_SPEED);
    }

    @Nullable
    @Override
    public IItemHandler getConnectedInventory() {
        return null;
    }

    @Nullable
    @Override
    public IFluidHandler getConnectedFluidInventory() {
        return tankToCraft;
    }

    @Nullable
    @Override
    public TileEntity getConnectedTile() {
        return world.getTileEntity(pos);
    }

    public List<ICraftingPattern> getPatterns() {
        return actualPatterns;
    }

    public ItemHandlerBase getCraftItems() {
        return patterns;
    }

    @Nullable
    @Override
    public IItemHandlerModifiable getPatternInventory() {
        return null;
    }

    @Override
    public String getName() {
        return "block.refinedstorage:emccrafter.name";
    }

    @Override
    public BlockPos getPosition() {
        return pos;
    }

    @Nullable
    @Override
    public ICraftingPatternContainer getRootContainer() {
        return this;
    }

    @Override
    public UUID getUuid() {
        if (uuid == null) {
            uuid = UUID.randomUUID();
            markDirty();
        }

        return uuid;
    }

    public IItemHandler getUpgrades() {
        return upgrades;
    }

    @Override
    public IItemHandler getDrops() {
        return new CombinedInvWrapper(upgrades);
    }
}
