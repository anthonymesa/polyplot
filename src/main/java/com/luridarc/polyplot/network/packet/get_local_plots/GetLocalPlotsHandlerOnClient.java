package com.luridarc.polyplot.network.packet.get_local_plots;

import com.luridarc.polyplot.controllers.ClientPlotController;

import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class GetLocalPlotsHandlerOnClient implements IMessageHandler<GetLocalPlotsServerToClient, IMessage>
{
    @Override
    public IMessage onMessage(final GetLocalPlotsServerToClient message, final MessageContext ctx)
    {
        if(ctx.side != Side.CLIENT) {
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
                ClientPlotController.setLocalPlots(message.local_plot_list);
                return;
            }
        });

        FMLClientHandler.instance().getClient().addScheduledTask(t);

        return null;
    }
}

