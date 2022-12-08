package com.luridarc.polyplot.network.packet.get_local_plots;

import java.util.ArrayList;
import java.util.List;

import com.luridarc.polyplot.util.plot.Plot;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class GetLocalPlotsServerToClient implements IMessage 
{
    List<Plot> local_plot_list;
    public boolean message_valid;

    public GetLocalPlotsServerToClient(List<Plot> local_plots) {
        this.local_plot_list = local_plots;
        this.message_valid = true;
    }

    public GetLocalPlotsServerToClient() {
        this.message_valid = false;
    }

    public void fromBytes(ByteBuf buf) {
        try {
            this.local_plot_list = new ArrayList<Plot>();

            int plots_list_size = buf.readInt();
            
            for(int i = 0; i < plots_list_size; i++) {
                this.local_plot_list.add(Plot.fromBuffer(buf));
            }

            this.message_valid = true;
        } catch (Exception e) {
            System.out.println("Encountered an error reading plot from bytes: " + e);
        }
    }

    public void toBytes(ByteBuf buf) {
        if(!this.message_valid) {
            return;
        }

        buf.writeInt(local_plot_list.size());

        for(Plot each : local_plot_list) {
            try {
                each.toBuffer(buf);
            } catch (Exception e) {
                System.out.println(String.format("Encountered an error converting %s to bytes.", each.getPlotName()));
                System.out.println(String.format("The server should fail reading this %s object from bytes.", this.getClass().getName()));
                System.out.println(e.toString());
                return;
            }
        }
    } 
}
