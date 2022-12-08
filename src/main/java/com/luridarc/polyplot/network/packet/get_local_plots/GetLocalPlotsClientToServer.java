package com.luridarc.polyplot.network.packet.get_local_plots;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class GetLocalPlotsClientToServer implements IMessage 
{
    BlockPos player_pos;
    public boolean message_valid;

    public GetLocalPlotsClientToServer(BlockPos pos) {
        this.player_pos = pos;
        this.message_valid = true;
    }

    public GetLocalPlotsClientToServer() {
        this.message_valid = false;
    }

    public void fromBytes(ByteBuf buf) {
        try {
            this.player_pos = new BlockPos(
                buf.readInt(),
                buf.readInt(),
                buf.readInt()
            );
            this.message_valid = true;
        } catch (Exception e) {
            System.out.println("Caught exception: " + e);
        }
    }

    public void toBytes(ByteBuf buf) {
        if(!this.message_valid) {
            return;
        }

        buf.writeInt(player_pos.getX());
        buf.writeInt(player_pos.getY());
        buf.writeInt(player_pos.getZ());
    } 
}
