package com.luridarc.polyplot.controllers;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.poly2tri.geometry.polygon.PolygonPoint;

import com.dmac100.clipper.ClipType;
import com.dmac100.clipper.Clipper;
import com.dmac100.clipper.Polygon;
import com.dmac100.clipper.internal.PolyType;
import com.luridarc.polyplot.PolyPlot;
import com.luridarc.polyplot.network.packet.add_plot.AddPlotServerToClient;
import com.luridarc.polyplot.network.packet.new_plots_available.NewPlotsAvailableServerToClient;
import com.luridarc.polyplot.util.FinishedPlotError;
import com.luridarc.polyplot.util.MaxAllowed;
import com.luridarc.polyplot.util.plot.Bounds;
import com.luridarc.polyplot.util.plot.Builder;
import com.luridarc.polyplot.util.plot.Claim;
import com.luridarc.polyplot.util.plot.Plot;
import com.luridarc.polyplot.util.plot.SearchGrid;
import com.luridarc.polyplot.util.save_data.PlotsSaveData;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class ServerPlotController 
{
    public static int getPlotOwnersCount(EntityPlayer player) 
    {
        return PlotsSaveData.getOwnerCount(player);
    }

    // for each player, iterate across their plots and check if the point is 
    // in a plot they own or not.
    public static Claim getPlotClaimState(EntityPlayer player, BlockPos pos) 
    {
        AtomicReference<Claim> claim_state = new AtomicReference<Claim>();

        PlotsSaveData.forEach((data) -> {
            if(data.getPlot().isHitBy(pos)) 
            {
                if(player.getUniqueID().compareTo(data.getPlayerId()) == 0) 
                {
                    claim_state.set(new Claim(Claim.State.OWNER, Optional.of(data.getIdx())));
                } else {
                    claim_state.set(new Claim(Claim.State.CLAIMED, Optional.empty()));
                }
            }
        }, player);

        if(claim_state.get() == null) {
            return new Claim(Claim.State.UNCLAIMED, Optional.empty());
        } else {
            return claim_state.get();
        }
    }

    public static void addToPlayerPlots(Plot _plot)
    {
        EntityPlayer player = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(_plot.getOwnerUUID());

        FinishedPlotError error = CheckPlotForErrors(_plot);

        if(error != FinishedPlotError.NON ) {
            PolyPlot.network_wrapper.sendTo(new AddPlotServerToClient(error, false), (EntityPlayerMP) player);
            return;
        }
        
        PlotsSaveData.add(_plot);

        PolyPlot.network_wrapper.sendTo(new AddPlotServerToClient(FinishedPlotError.NON, true), (EntityPlayerMP) player);
        PolyPlot.network_wrapper.sendToAll(new NewPlotsAvailableServerToClient());

        Builder.build(_plot);
    }

    private static FinishedPlotError CheckPlotForErrors(Plot _plot) {
        if(PlayerHasMaxPlots(_plot)) return FinishedPlotError.MAX_PLOTS;
        if(PlotDimensionsOverLimit(_plot)) return FinishedPlotError.MAX_DIMENSIONS;
        if(PlotPointsOverLimit(_plot)) return FinishedPlotError.MAX_POINTS;
        if(PlotIntersectsOtherPlot(_plot)) return FinishedPlotError.INTERSECTS;
        return FinishedPlotError.NON;
    }

    private static boolean PlotIntersectsOtherPlot(Plot _plot) {

        BlockPos left_top = new BlockPos(_plot.getBounds().getLeft(), 0, _plot.getBounds().getTop());
        BlockPos left_bottom = new BlockPos(_plot.getBounds().getLeft(), 0, _plot.getBounds().getBottom());
        BlockPos right_top = new BlockPos(_plot.getBounds().getRight(), 0, _plot.getBounds().getTop());
        BlockPos right_bottom = new BlockPos(_plot.getBounds().getRight(), 0, _plot.getBounds().getBottom());

        LinkedHashSet<Plot> found_plots = new LinkedHashSet<Plot>();

        EntityPlayer player = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(_plot.getOwnerUUID());

        List<Plot> left_top_plots = getPlotsLocalToPos(player, left_top);
        List<Plot> left_bottom_plots = getPlotsLocalToPos(player, left_bottom);
        List<Plot> right_top_plots = getPlotsLocalToPos(player, right_top);
        List<Plot> right_bottom_plots = getPlotsLocalToPos(player, right_bottom);

        for(Plot each : left_top_plots) found_plots.add(each);
        for(Plot each : left_bottom_plots) found_plots.add(each);
        for(Plot each : right_top_plots) found_plots.add(each);
        for(Plot each : right_bottom_plots) found_plots.add(each);

        List<Polygon> subj = new ArrayList<Polygon>();
        subj.add(new Polygon());
        for(PolygonPoint each : _plot.getPoints()) {
            subj.get(0).add((int) each.getX(), (int) each.getY());
        }

        List<Polygon> clip = new ArrayList<Polygon>();
        for(int i = 0; i < found_plots.size(); i++) {
            clip.add(new Polygon());
            
            Plot curr_plot = (Plot) found_plots.toArray()[i];
            LinkedHashSet<PolygonPoint> curr_points = curr_plot.getPoints();

            for(int j = 0; j < curr_points.size(); j ++) {
                clip.get(i).add(
                    (int) ((PolygonPoint)curr_points.toArray()[j]).getX(),
                    (int) ((PolygonPoint)curr_points.toArray()[j]).getY()
                );
            }
        }

        List<Polygon> solution = new ArrayList<Polygon>();

        Clipper c = new Clipper();
        c.addPolygons(subj, PolyType.ptSubject);
        c.addPolygons(clip, PolyType.ptClip);
        c.execute(ClipType.INTERSECTION, solution);

        return solution.size() > 0;
    }

    private static boolean PlotPointsOverLimit(Plot _plot) {
        if(_plot.getPoints().size() > MaxAllowed.max_points) {
            return true;
        } else {
            return false;
        }
    }

    private static boolean PlotDimensionsOverLimit(Plot _plot) 
    {
        boolean vertical_bounds_over_limit = Math.abs((_plot.getBounds().getTop() - _plot.getBounds().getBottom())) > MaxAllowed.max_bounds;
        boolean horizontal_bounds_over_limit = Math.abs((_plot.getBounds().getRight() - _plot.getBounds().getLeft())) > MaxAllowed.max_bounds;

        if(vertical_bounds_over_limit || horizontal_bounds_over_limit) {
            return true;
        } else {
            return false;
        }
    }

    private static boolean PlayerHasMaxPlots(Plot _plot) 
    {
        EntityPlayer player = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(_plot.getOwnerUUID());
        if(PlotsSaveData.hasPlotOwnedBy(player)) {
            return (PlotsSaveData.getPlayerPlotsCount(player) >= MaxAllowed.max_plots) ? true : false;
        } else {
            return false;
        }
    }

    public static List<Plot> getPlotsLocalToPos(EntityPlayer player, BlockPos player_pos) {

        List<Plot> temp_plot_list = new ArrayList<Plot>();
        Bounds current_grid_bounds = SearchGrid.posToBounds(player_pos);

        PlotsSaveData.forEach((data) -> {
            if(current_grid_bounds.intersects(data.getPlot().getBounds())) {
                temp_plot_list.add(data.getPlot());
            }
        }, player);

        return temp_plot_list;
    }

    public static int getAllPlotsSize(EntityPlayer player) {
        return PlotsSaveData.getTotalPlotsCount(player);
    }

    public static boolean existPlotsOwnedBy(EntityPlayer player) {
        return PlotsSaveData.hasPlotOwnedBy(player);
    }

    public static int getPlayerPlotsCount(EntityPlayer player) {
        return PlotsSaveData.getPlayerPlotsCount(player);
    }

    public static void removePlot(EntityPlayer player, int plot_index) throws Exception {
        PlotsSaveData.delete(player, plot_index);
    }
}
