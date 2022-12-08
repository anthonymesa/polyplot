package com.luridarc.polyplot.network.packet.add_plot;

import com.luridarc.polyplot.util.FinishedPlotError;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class AddPlotServerToClient implements IMessage 
{
    FinishedPlotError error;
    boolean success;
    public boolean message_valid;

    public AddPlotServerToClient(FinishedPlotError _error, boolean _success) {
        this.error = _error;
        this.success = _success;
        this.message_valid = true;
    }

    public AddPlotServerToClient() {
        this.message_valid = false;
    }

    // message structure
    public void fromBytes(ByteBuf buf) {
        try {
            this.success = buf.readBoolean();
            this.error = FinishedPlotError.values()[buf.readInt()];
            this.message_valid = true;
        } catch (Exception e) {
            System.out.println("Caught exception: " + e);
        }
    }

    public void toBytes(ByteBuf buf) {
        // put message content into byte array
        if(!this.message_valid) {
            return;
        }

        buf.writeBoolean(success);
        buf.writeInt(error.ordinal());
    } 
}
