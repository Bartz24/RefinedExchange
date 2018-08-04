package com.bartz24.refinedexchange.features;

import com.bartz24.refinedexchange.features.tile.TileLiquifier;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketLiquifier implements IMessage {

    protected long emcStored;
    protected int x;
    protected int y;
    protected int z;

    public PacketLiquifier() {
    }

    public PacketLiquifier(long emcStored, BlockPos pos) {

        this.emcStored = emcStored;
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
    }

    @Override
    public void fromBytes(ByteBuf buf) {

        this.emcStored = buf.readLong();
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {

        buf.writeLong(this.emcStored);
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.z);
    }

    public static class Handler implements IMessageHandler<PacketLiquifier, IMessage> {

        @Override
        public IMessage onMessage(PacketLiquifier message,
                                  MessageContext ctx) {

            Minecraft.getMinecraft().addScheduledTask(() -> processMessage(message));
            return null;
        }

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
}