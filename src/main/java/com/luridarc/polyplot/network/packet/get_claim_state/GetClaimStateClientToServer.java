package com.luridarc.polyplot.network.packet.get_claim_state;

import com.luridarc.polyplot.controllers.ClientActionController.ActionData;
import com.luridarc.polyplot.objects.items.ItemWand.WandAction;
import com.luridarc.polyplot.objects.items.ItemWand.WandType;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class GetClaimStateClientToServer implements IMessage 
{
    ActionData action_data;
    public boolean message_valid;

    public GetClaimStateClientToServer(ActionData _action_data) {
        this.action_data = _action_data;
        this.message_valid = true;
    }

    public GetClaimStateClientToServer() {
        this.message_valid = false;
    }

    public void fromBytes(ByteBuf buf) {
        try {
            this.action_data = new ActionData(
                WandType.values()[buf.readInt()],
                WandAction.values()[buf.readInt()], 

                // this can only run on a dedicated server?
                FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(ByteBufUtils.readUTF8String(buf)),

                ByteBufUtils.readItemStack(buf), 
                new BlockPos(
                    buf.readInt(),
                    buf.readInt(),
                    buf.readInt()
                ));
            this.message_valid = true;
        } catch (Exception e) {
            System.out.println("Caught exception: " + e);
        }
    }

    public void toBytes(ByteBuf buf) {
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
    } 
}
