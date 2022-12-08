package com.luridarc.polyplot.network.packet.delete_plot;

import com.luridarc.polyplot.controllers.ClientPlotController;

import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class DeletePlotHandlerOnClient implements IMessageHandler<DeletePlotServerToClient, IMessage> {

    @Override
    public IMessage onMessage(DeletePlotServerToClient message, MessageContext ctx) {

        if(ctx.side != Side.CLIENT) {
            System.out.println("message on wrong side");
            return null;
        }

        if(!message.message_valid) {
            System.out.println("message invalid");
            return null;
        }

        Thread t = new Thread(new Runnable() {
            public void run() {
                if(message.success) {
                    ClientPlotController.deletePlot(message.plot_index);
                } else {
                    FMLClientHandler.instance().getClient().player.sendMessage(new TextComponentString("An issue occured when trying to delete the plot."));
                }
            }
        });

        FMLClientHandler.instance().getClient().addScheduledTask(t);

        return null;
    }
}
