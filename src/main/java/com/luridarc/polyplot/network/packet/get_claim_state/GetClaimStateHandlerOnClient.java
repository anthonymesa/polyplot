package com.luridarc.polyplot.network.packet.get_claim_state;

import com.luridarc.polyplot.controllers.ClientActionController;

import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class GetClaimStateHandlerOnClient implements IMessageHandler<GetClaimStateServerToClient, IMessage>
{
    @Override
    public IMessage onMessage(final GetClaimStateServerToClient message, final MessageContext ctx)
    {
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
                ClientActionController.continueHandlingHitBlock(message.action_data, message.claim);
            }
        });

        FMLClientHandler.instance().getClient().addScheduledTask(t);

        return null;
    }
}