package com.luridarc.polyplot.network.packet.add_plot;

import com.luridarc.polyplot.controllers.ServerPlotController;
import com.luridarc.polyplot.util.plot.Plot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class AddPlotHandlerOnServer implements IMessageHandler<AddPlotClientToServer, IMessage>
{
    @Override
    public IMessage onMessage(final AddPlotClientToServer message, final MessageContext ctx)
    {
        if(ctx.side != Side.SERVER) {
            System.out.println("message on wrong side");
            return null;
        }

        if(!message.success) {
            System.out.println("message invalid");
            return null;
        }

        Thread t = new Thread(new Runnable() {
            public void run() {

                if(message.proto.getPlotName().matches("")) {
                    EntityPlayer player = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(message.proto.getOwnerUUID());
                    
                    int list_size;
                    if(ServerPlotController.existPlotsOwnedBy(player)) {
                        list_size = ServerPlotController.getPlayerPlotsCount(player) + 1;
                    } else {
                        list_size = 1;
                    }

                    message.proto.setPlotName(String.format("%s's plot #%d", player.getName(), list_size));
                }

                ServerPlotController.addToPlayerPlots(new Plot(message.proto));
            }
        });

        FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(t);

        return null;
    }
}
