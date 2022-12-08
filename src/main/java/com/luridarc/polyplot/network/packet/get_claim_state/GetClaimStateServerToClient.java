package com.luridarc.polyplot.network.packet.get_claim_state;

import java.util.Optional;

import com.luridarc.polyplot.controllers.ClientActionController.ActionData;
import com.luridarc.polyplot.objects.items.ItemWand.WandAction;
import com.luridarc.polyplot.objects.items.ItemWand.WandType;
import com.luridarc.polyplot.util.plot.Claim;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class GetClaimStateServerToClient implements IMessage 
{
    ActionData action_data;
    Claim claim;
    public boolean message_valid;

    public GetClaimStateServerToClient(ActionData _action_data, Claim _claim_state) {
        this.action_data = _action_data;
        this.claim = _claim_state;
        this.message_valid = true;
    }

    public GetClaimStateServerToClient() {
        this.message_valid = false;
    }

    // message structure
    public void fromBytes(ByteBuf buf) {
        try {
            this.action_data = new ActionData(
                WandType.values()[buf.readInt()],
                WandAction.values()[buf.readInt()], 
                FMLClientHandler.instance().getWorldClient().getPlayerEntityByName(ByteBufUtils.readUTF8String(buf)),
                ByteBufUtils.readItemStack(buf), 
                new BlockPos(
                    buf.readInt(),
                    buf.readInt(),
                    buf.readInt()
                ));
            this.claim = new Claim(Claim.State.values()[buf.readInt()], Optional.of(buf.readInt()));
            this.message_valid = true;
        } catch (Exception e) {
            System.out.println("Caught exception: " + e);
        }
    }

    public void toBytes(ByteBuf buf) {
        // put message content into byte array
        if(!this.message_valid) {
            return;
        }

        buf.writeInt(action_data.getWandType().ordinal());
        buf.writeInt(action_data.getResult().ordinal());
        ByteBufUtils.writeUTF8String(buf, action_data.getPlayer().getName());
        ByteBufUtils.writeItemStack(buf, action_data.getItem_stack());
        buf.writeInt(action_data.getPos().getX());
        buf.writeInt(action_data.getPos().getY());
        buf.writeInt(action_data.getPos().getZ());
        buf.writeInt(claim.state.ordinal());

        if(claim.plot_index.isPresent()) {
            buf.writeInt(claim.plot_index.get());
        } else {
            buf.writeInt(-1);
        }


    } 
}
