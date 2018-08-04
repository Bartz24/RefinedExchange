package com.bartz24.refinedexchange.features.block;

import com.bartz24.refinedexchange.References;
import com.bartz24.refinedexchange.features.tile.TileLiquifier;
import com.bartz24.refinedexchange.registry.ModGuiHandler;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNode;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNodeProxy;
import com.raoulvdberge.refinedstorage.api.network.security.Permission;
import com.raoulvdberge.refinedstorage.block.BlockNode;
import com.raoulvdberge.refinedstorage.block.info.BlockInfoBuilder;
import com.raoulvdberge.refinedstorage.capability.CapabilityNetworkNodeProxy;
import com.raoulvdberge.refinedstorage.render.IModelRegistration;
import com.raoulvdberge.refinedstorage.util.WorldUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockLiquifier extends BlockNode {
    public BlockLiquifier() {
        super(BlockInfoBuilder.forId("liquifier").tileEntity(TileLiquifier::new).create());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerModels(IModelRegistration modelRegistration) {
        modelRegistration.setModel(this, 0, new ModelResourceLocation(info.getId(), "inventory"));
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        return openNetworkGui(ModGuiHandler.EMCLiquifier, player, world, pos, side, Permission.MODIFY, Permission.INSERT, Permission.EXTRACT);
    }

    @Override
    public boolean hasConnectedState() {
        return true;
    }

    protected boolean openNetworkGui(int guiId, EntityPlayer player, World world, BlockPos pos, EnumFacing facing, Permission... permissions) {
        if (world.isRemote) {
            return true;
        }

        TileEntity tile = world.getTileEntity(pos);

        if (tile != null && tile.hasCapability(CapabilityNetworkNodeProxy.NETWORK_NODE_PROXY_CAPABILITY, facing)) {
            INetworkNodeProxy nodeProxy = CapabilityNetworkNodeProxy.NETWORK_NODE_PROXY_CAPABILITY.cast(tile.getCapability(CapabilityNetworkNodeProxy.NETWORK_NODE_PROXY_CAPABILITY, facing));
            INetworkNode node = nodeProxy.getNode();

            if (node.getNetwork() != null) {
                for (Permission permission : permissions) {
                    if (!node.getNetwork().getSecurityManager().hasPermission(permission, player)) {
                        WorldUtils.sendNoPermissionMessage(player);

                        return false;
                    }

                }
            }
        }

        player.openGui(References.ModID, guiId, world, pos.getX(), pos.getY(), pos.getZ());


        return true;
    }
}
