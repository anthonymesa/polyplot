package com.luridarc.polyplot.network.packet.add_plot;

import com.luridarc.polyplot.controllers.ClientPlotController;

import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class AddPlotHandlerOnClient implements IMessageHandler<AddPlotServerToClient, IMessage>
{
    @Override
    public IMessage onMessage(final AddPlotServerToClient message, final MessageContext ctx)
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
                ClientPlotController.continueFinishingPlot(message.error, message.success);
            }
        });

        FMLClientHandler.instance().getClient().addScheduledTask(t);

        return null;
    }
}
