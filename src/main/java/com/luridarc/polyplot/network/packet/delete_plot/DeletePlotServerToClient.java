package com.luridarc.polyplot.network.packet.delete_plot;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class DeletePlotServerToClient implements IMessage 
{
    public int plot_index;
    public boolean success;
    public boolean message_valid;

    public DeletePlotServerToClient(boolean _success, int _plot_index) {
        this.success = _success;
        this.plot_index = _plot_index;
        this.message_valid = true;
    }

    public DeletePlotServerToClient() {
        this.message_valid = false;
    }

    public void fromBytes(ByteBuf buf) {
        try {
            this.plot_index = buf.readInt();
            this.success = buf.readBoolean();
            this.message_valid = true;
        } catch (Exception e) {
            System.out.println("Caught exception: " + e);
        }
    }

    public void toBytes(ByteBuf buf) {
        if(!this.message_valid) {
            return;
        }

        buf.writeInt(plot_index);
        buf.writeBoolean(success);
    } 
}
