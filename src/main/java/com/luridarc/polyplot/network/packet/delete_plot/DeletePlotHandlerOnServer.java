package com.luridarc.polyplot.network.packet.delete_plot;

import com.luridarc.polyplot.PolyPlot;
import com.luridarc.polyplot.controllers.ServerPlotController;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class DeletePlotHandlerOnServer implements IMessageHandler<DeletePlotClientToServer, IMessage>
{
    @Override
    public IMessage onMessage(final DeletePlotClientToServer message, final MessageContext ctx) {

        if(ctx.side != Side.SERVER) {
            System.out.println("message on wrong side");
            return null;
        }

        if(!message.message_valid) {
            System.out.println("message invalid");
            return null;
        }

        if(message.player == null) {
            System.out.println("player was null");
            return null;
        }

        // Try to delete the plot for the player and send a corresponding message back to the client
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    ServerPlotController.removePlot(message.player, message.plot_index);
                } catch (Exception e){
                    PolyPlot.network_wrapper.sendTo(new DeletePlotServerToClient(false, message.plot_index), (EntityPlayerMP) message.player);
                    return;
                }

                PolyPlot.network_wrapper.sendTo(new DeletePlotServerToClient(true, message.plot_index), (EntityPlayerMP) message.player);
                return;
            }
        });

        FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(t);

        return null;
    }
}
