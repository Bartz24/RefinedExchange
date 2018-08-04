package com.bartz24.refinedexchange.features.tile;

import com.bartz24.refinedexchange.features.network.NetworkNodeLiquifier;
import com.raoulvdberge.refinedstorage.tile.TileNode;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileLiquifier extends TileNode<NetworkNodeLiquifier> {

    public TileLiquifier() {

    }

    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(getNode().getInputs()) : super.getCapability(capability, facing);
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @Override
    @Nonnull
    public NetworkNodeLiquifier createNode(World world, BlockPos pos) {
        return new NetworkNodeLiquifier(world, pos);
    }

    @Override
    public String getNodeId() {
        return "liquifier";
    }
}
