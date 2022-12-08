package com.luridarc.polyplot.network.packet.add_plot;

import com.luridarc.polyplot.util.plot.ProtoPlot;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class AddPlotClientToServer implements IMessage {

    public ProtoPlot proto;
    public boolean success;

    public AddPlotClientToServer(ProtoPlot _proto) {
        this.proto = _proto;
        this.success = true;
    }

    public AddPlotClientToServer() {
        this.success = false;
    }

    // message structure
    public void fromBytes(ByteBuf buf) {
        try {
            this.proto = ProtoPlot.fromBuffer(buf);
            this.success = true;
        } catch (Exception e) {
            System.out.println("Caught exception: " + e);
        }
    }

    public void toBytes(ByteBuf buf) {
        // put message content into byte array
        if(!this.success) {
            return;
        }

        try {
            proto.toBuffer(buf);
        } catch (Exception e) {
            System.out.println("Caught exception: " + e);
            this.success = false;
        }
    } 
}