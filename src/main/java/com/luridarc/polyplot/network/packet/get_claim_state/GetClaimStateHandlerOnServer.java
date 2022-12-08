package com.luridarc.polyplot.network.packet.get_claim_state;

import com.luridarc.polyplot.PolyPlot;
import com.luridarc.polyplot.controllers.ServerPlotController;
import com.luridarc.polyplot.util.plot.Claim;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class GetClaimStateHandlerOnServer implements IMessageHandler<GetClaimStateClientToServer, IMessage>
{
    @Override
    public IMessage onMessage(final GetClaimStateClientToServer message, final MessageContext ctx)
    {
        if(ctx.side != Side.SERVER) {
            System.out.println("message on wrong side");
            return null;
        }

        if(!message.message_valid) {
            System.out.println("message invalid");
            return null;
        }

        if(message.action_data.getPlayer() == null) {
            System.out.println("player was null");
            return null;
        }

        // Try to delete the plot for the player and send a corresponding message back to the client
        Thread t = new Thread(new Runnable() {
            public void run() {
                Claim claim_state = ServerPlotController.getPlotClaimState(message.action_data.getPlayer(), message.action_data.getPos());
                PolyPlot.network_wrapper.sendTo(new GetClaimStateServerToClient(message.action_data, claim_state), (EntityPlayerMP) message.action_data.getPlayer());
                return;
            }
        });

        FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(t);


        return null;
    }
}
