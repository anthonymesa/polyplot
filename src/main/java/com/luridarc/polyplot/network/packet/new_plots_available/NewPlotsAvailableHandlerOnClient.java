package com.luridarc.polyplot.network.packet.new_plots_available;

import com.luridarc.polyplot.PolyPlot;
import com.luridarc.polyplot.network.packet.get_local_plots.GetLocalPlotsClientToServer;

import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class NewPlotsAvailableHandlerOnClient implements IMessageHandler<NewPlotsAvailableServerToClient, IMessage>
{
    @Override
    public IMessage onMessage(final NewPlotsAvailableServerToClient message, final MessageContext ctx)
    {
        if(ctx.side != Side.CLIENT) {
            System.out.println("message on wrong side");
            return null;
        }

        // Try to delete the plot for the player and send a corresponding message back to the client
        Thread t = new Thread(new Runnable() {
            public void run() {
                PolyPlot.network_wrapper.sendToServer(new GetLocalPlotsClientToServer(FMLClientHandler.instance().getClient().player.getPosition()));
                return;
            }
        });

        FMLClientHandler.instance().getClient().addScheduledTask(t);

        return null;
    }
}
