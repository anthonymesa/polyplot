package com.luridarc.polyplot.network.packet.delete_plot;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class DeletePlotClientToServer implements IMessage {
    public EntityPlayer player;
    public int plot_index;
    public boolean message_valid;

    public DeletePlotClientToServer(EntityPlayer _player, int _plot_index) {
        this.player = _player;
        this.plot_index = _plot_index;
        this.message_valid = true;
    }

    public DeletePlotClientToServer() {
        this.message_valid = false;
    }

    // message structure
    public void fromBytes(ByteBuf buf) {
        try {
            this.player = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(ByteBufUtils.readUTF8String(buf));
            this.plot_index = buf.readInt();
            this.message_valid = true;
        } catch (Exception e) {
            System.out.println("Caughte exception: " + e);
        }
    }

    public void toBytes(ByteBuf buf) {
        // put message content into byte array
        if(!this.message_valid) {
            return;
        }

        ByteBufUtils.writeUTF8String(buf, player.getName());
        buf.writeInt(plot_index);
    } 
}
