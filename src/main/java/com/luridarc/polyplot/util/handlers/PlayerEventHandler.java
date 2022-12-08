package com.luridarc.polyplot.util.handlers;

import com.luridarc.polyplot.PolyPlot;
import com.luridarc.polyplot.controllers.ClientPlotController;
import com.luridarc.polyplot.controllers.PlayerController;
import com.luridarc.polyplot.network.packet.get_local_plots.GetLocalPlotsClientToServer;
import com.luridarc.polyplot.util.plot.Plot;
import com.luridarc.polyplot.util.plot.SearchGrid;
import com.luridarc.polyplot.util.save_data.PlotsSaveData;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

// should catch explosion events too

@EventBusSubscriber
public class PlayerEventHandler {

    // This event takes place on the logical server. The logical server needs to check if the block hit exists within any of the plots in the PlotController and if a plot is hit and it doesn't belong to the player, then the event needs to be rejected.
    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        PlotsSaveData.forEach((data) -> {
            if(data.getPlayerId().compareTo(event.getPlayer().getUniqueID()) == 0) {
                if(data.getPlot().isHitBy(event.getPos())) return;
            } else {
                if(data.getPlot().isHitBy(event.getPos())) {
                    event.setCanceled(true);
                }
            }
        }, event.getPlayer());
    }

    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.PlaceEvent event) {
        PlotsSaveData.forEach((data) -> {
            if(data.getPlayerId().compareTo(event.getPlayer().getUniqueID()) == 0) {
                if(data.getPlot().isHitBy(event.getPos())) return;
            } else {
                if(data.getPlot().isHitBy(event.getPos())) {
                    event.setCanceled(true);
                }
            }
        }, event.getPlayer());
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void onSearchGridBoundsChange(TickEvent event) {
        if(FMLClientHandler.instance().getClient().player == null) return;

        if(SearchGrid.playerChangedGrid()) {
            PolyPlot.network_wrapper.sendToServer(new GetLocalPlotsClientToServer(FMLClientHandler.instance().getClient().player.getPosition()));
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void onPlotCheck(TickEvent event) {

        if(FMLClientHandler.instance().getClient().player == null) return;

        BlockPos player_position = FMLClientHandler.instance().getClient().player.getPosition();
        
        for(Plot each : ClientPlotController.getLocal_plots()) {
            if(FMLClientHandler.instance().getClient().player.getUniqueID().compareTo(each.getOwnerUUID()) == 0) continue;

            if(!each.getBounds().isHitBy(player_position)) {
                continue;
            }

            if(each.isHitBy(FMLClientHandler.instance().getClient().player.getPosition())) {
                if(PlayerController.current_plot_in == each) return;
                PlayerController.current_plot_in = each;
                FMLClientHandler.instance().getClient().player.sendMessage(new TextComponentString("You are entering " + each.getPlotName()));
                return;
            }
        }

        if(PlayerController.current_plot_in == null) return;

        FMLClientHandler.instance().getClient().player.sendMessage(new TextComponentString("You are leaving " + PlayerController.current_plot_in.getPlotName()));
        PlayerController.current_plot_in = null;
    }
}