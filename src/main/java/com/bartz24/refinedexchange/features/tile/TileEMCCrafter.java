package com.bartz24.refinedexchange.features.tile;

import com.bartz24.refinedexchange.features.network.NetworkNodeEMCCrafter;
import com.bartz24.refinedexchange.features.network.NetworkNodeLiquifier;
import com.raoulvdberge.refinedstorage.tile.TileNode;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEMCCrafter extends TileNode<NetworkNodeEMCCrafter> {

    public TileEMCCrafter() {

    }

    @Override
    @Nonnull
    public NetworkNodeEMCCrafter createNode(World world, BlockPos pos) {
        return new NetworkNodeEMCCrafter(world, pos);
    }

    @Override
    public String getNodeId() {
        return "emccrafter";
    }
}
