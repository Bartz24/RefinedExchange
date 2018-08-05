package com.bartz24.refinedexchange.features;

import com.bartz24.refinedexchange.features.tile.TileLiquifier;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


public class PacketLiquifierHandler implements IMessageHandler<PacketLiquifier, IMessage> {

    @Override
    @SideOnly(Side.CLIENT)
    public IMessage onMessage(PacketLiquifier message,
                              MessageContext ctx) {

        Minecraft.getMinecraft().addScheduledTask(() -> processMessage(message));
        return null;
    }

    @SideOnly(Side.CLIENT)
    public void processMessage(PacketLiquifier message) {

        World world = Minecraft.getMinecraft().world;
        TileEntity tileEntity = world.getTileEntity(new
                BlockPos(message.x, message.y, message.z));

        if (tileEntity instanceof TileLiquifier) {

            TileLiquifier tileLiquifier = (TileLiquifier) tileEntity;
            tileLiquifier.getNode().setEmcStored(message.emcStored);
        }
    }
}