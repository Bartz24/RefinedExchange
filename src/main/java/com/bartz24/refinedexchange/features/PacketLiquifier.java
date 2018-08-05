package com.bartz24.refinedexchange.features;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

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
}