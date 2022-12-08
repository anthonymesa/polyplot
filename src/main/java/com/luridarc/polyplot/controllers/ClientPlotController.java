package com.luridarc.polyplot.controllers;

import java.util.ArrayList;
import java.util.List;

import com.luridarc.polyplot.PolyPlot;
import com.luridarc.polyplot.controllers.ClientActionController.ActionData;
import com.luridarc.polyplot.controllers.PlayerController.DeleteState;
import com.luridarc.polyplot.controllers.PlayerController.PlayerState;
import com.luridarc.polyplot.network.packet.add_plot.AddPlotClientToServer;
import com.luridarc.polyplot.network.packet.delete_plot.DeletePlotClientToServer;
import com.luridarc.polyplot.util.FinishedPlotError;
import com.luridarc.polyplot.util.MaxAllowed;
import com.luridarc.polyplot.util.plot.Plot;
import com.luridarc.polyplot.util.plot.ProtoPlot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientPlotController
{
    private static ProtoPlot current_plot = null;
    private static List<Plot> local_plots = new ArrayList<Plot>();

    public static ProtoPlot getCurrentPlot() {
        return current_plot;
    }

    public static List<Plot> getLocal_plots() {
        return local_plots;
    }

    public static void setLocalPlots(List<Plot> local_plots) {
        ClientPlotController.local_plots = local_plots;
    }

    public static void startPlot(ActionData action_data) 
    {
        BlockPos pos = action_data.getPos();

        current_plot = new ProtoPlot();
        current_plot.appendPoint(pos);
        current_plot.setOwnerUUID(FMLClientHandler.instance().getClient().player.getUniqueID());
        PlayerController.setLastBlock(pos);

        action_data.getPlayer().sendMessage(new TextComponentString("Starting Plot..."));
    }

    public static void appendPlot(ActionData action_data) 
    {
        BlockPos pos = action_data.getPos();

        current_plot.appendPoint(pos);
        PlayerController.setLastBlock(pos);

        action_data.getPlayer().sendMessage(new TextComponentString("Appending Plot..."));
    }

    public static void finishPlot(ActionData action_data) 
    {
        current_plot.setOwnerUUID(action_data.getPlayer().getUniqueID());
        current_plot.setPlotName(action_data.getItem_stack().getDisplayName());
        current_plot.setWand_type(action_data.getWandType());

        if((current_plot.getPlotName().matches("Banagon's Wand")) ||
           (current_plot.getPlotName().matches("Banagon's Wand of Spires")) ||
           (current_plot.getPlotName().matches("Banagon's Wand of Barriers")) ||
           (current_plot.getPlotName().matches("Banagon's Wierd Wand"))) {
                current_plot.setPlotName("");
           }

        PolyPlot.network_wrapper.sendToServer(new AddPlotClientToServer(current_plot));
    }

    public static void continueFinishingPlot(FinishedPlotError _error, boolean _success) {

        EntityPlayer player = FMLClientHandler.instance().getClient().player;

        if(_success) {
            doSuccessParticles();
            // player.sendMessage(new TextComponentString("Banagon's wand hums happily."));
            player.sendMessage(new TextComponentString("Successfully created a new plot!"));
        } else {
            switch(_error){
                case INTERSECTS:
                    // player.sendMessage(new TextComponentString("Banagon's wand vibrates maliciously, but nothing happens..."));
                    player.sendMessage(new TextComponentString("Plot intersects another player's plot."));
                    break;
                case MAX_DIMENSIONS:
                    // player.sendMessage(new TextComponentString("Banagon's wand pulses cautiously, but nothing happens..."));
                    player.sendMessage(new TextComponentString(String.format("Plot dimensions can not be greater than %d.", MaxAllowed.max_bounds)));
                    break;
                case MAX_PLOTS:
                    // player.sendMessage(new TextComponentString("Banagon's wand jerks greedily, but nothing happens..."));
                    player.sendMessage(new TextComponentString(String.format("You can not have more than %d plots.", MaxAllowed.max_plots)));
                    break;
                case MAX_POINTS:
                    // player.sendMessage(new TextComponentString("Banagon's wand seethes angrily, but nothing happens..."));
                    player.sendMessage(new TextComponentString(String.format("Your plot can not have more than $d points.", MaxAllowed.max_points)));
                    break;
                case NON:
                    player.sendMessage(new TextComponentString("Banagon's wand ignores you."));
                    break;
                default:
                    player.sendMessage(new TextComponentString("Banagon's wand ignores you."));
                    break;
            }
            player.sendMessage(new TextComponentString("Failed to create new plot."));
            doFailParticles();
        }

        current_plot = null;
        PlayerController.clearLastBlock();
        PlayerController.setPlayerState(PlayerState.START);
    }

    private static void doFailParticle(EnumParticleTypes type) {
        FMLClientHandler.instance().getClient().world.spawnParticle(type, true, 
        FMLClientHandler.instance().getClient().player.getPosition().getX() + Math.random(),
        FMLClientHandler.instance().getClient().player.getPosition().getY() + Math.random(),
        FMLClientHandler.instance().getClient().player.getPosition().getZ() + Math.random(),
        1, 1, 1, null);
    }

    private static void doFailParticles() {
        int particle_count = 30;
        EnumParticleTypes type = EnumParticleTypes.SPELL_WITCH;

        for(int i = 0; i < particle_count; i++)
        {
            doFailParticle(type);
        }
    }

    private static void doSuccessParticle(EnumParticleTypes type) {

        double distance_from_player = 0.5;
        Vec3d particle_root = FMLClientHandler.instance().getClient().player.getPositionVector().add(FMLClientHandler.instance().getClient().player.getLookVec().scale(distance_from_player));
        particle_root.add(new Vec3d(0, 2, 0));
        FMLClientHandler.instance().getClient().world.spawnParticle(type, true, 
        particle_root.x,
        particle_root.y,
        particle_root.z,
        .1, .1, .1, null);
    }

    private static void doSuccessParticles() {
        int particle_count = 30;
        EnumParticleTypes type = EnumParticleTypes.SPELL;

        for(int i = 0; i < particle_count; i++)
        {
            doSuccessParticle(type);
        }
    }

    public static void cancelPlot(ActionData action_data) {
        current_plot = null;
        PlayerController.clearLastBlock();

        action_data.getPlayer().sendMessage(new TextComponentString("Canceled Plot."));
    }

    public static void rejectPlot(ActionData action_data) {
        action_data.getPlayer().sendMessage(new TextComponentString("That belongs to someone else..."));
    }

    public static void handleDeleteProcess(ActionData action_data, int plot_index)
    {
        EntityPlayer player = action_data.getPlayer();

        switch(PlayerController.getDeleteState()) {
            case LEVEL_0:
                player.sendMessage(new TextComponentString("Right-click plot 2 more times to delete."));
                PlayerController.setDeleteState(DeleteState.LEVEL_1);
                break;
            case LEVEL_1:
                player.sendMessage(new TextComponentString("Right-click plot 1 more time to delete."));
                PlayerController.setDeleteState(DeleteState.LEVEL_2);
                break;
            case LEVEL_2:
                PolyPlot.network_wrapper.sendToServer(new DeletePlotClientToServer(player, plot_index));
                break;
            default:
        }
    }

    public static void deletePlot(int plot_index) {
        FMLClientHandler.instance().getClient().player.sendMessage(new TextComponentString("Deleted the plot."));
        PlayerController.setLastBlock(null);
        PlayerController.setDeleteState(DeleteState.LEVEL_0);
        PlayerController.setPlayerState(PlayerState.START);
    }
}
