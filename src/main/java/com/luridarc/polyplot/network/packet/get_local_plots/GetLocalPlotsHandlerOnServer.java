package com.luridarc.polyplot.network.packet.get_local_plots;

import java.util.List;

import com.luridarc.polyplot.PolyPlot;
import com.luridarc.polyplot.controllers.ServerPlotController;
import com.luridarc.polyplot.util.plot.Plot;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class GetLocalPlotsHandlerOnServer implements IMessageHandler<GetLocalPlotsClientToServer, IMessage>
{
    @Override
    public IMessage onMessage(final GetLocalPlotsClientToServer message, final MessageContext ctx)
    {
        if(ctx.side != Side.SERVER) {
            System.out.println("message on wrong side");
            return null;
        }

        if(!message.message_valid) {
            System.out.println("message invalid");
            return null;
        }

        // Try to delete the plot for the player and send a corresponding message back to the client
        Thread t = new Thread(new Runnable() {
            public void run() {
                List<Plot> local_plot_list = ServerPlotController.getPlotsLocalToPos(ctx.getServerHandler().player, message.player_pos);
                PolyPlot.network_wrapper.sendTo(new GetLocalPlotsServerToClient(local_plot_list), ctx.getServerHandler().player);
                return;
            }
        });

        FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(t);


        return null;
    }
}

