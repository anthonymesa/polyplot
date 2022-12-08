package com.luridarc.polyplot.network;

import com.luridarc.polyplot.PolyPlot;
import com.luridarc.polyplot.network.packet.add_plot.AddPlotClientToServer;
import com.luridarc.polyplot.network.packet.add_plot.AddPlotHandlerOnClient;
import com.luridarc.polyplot.network.packet.add_plot.AddPlotHandlerOnServer;
import com.luridarc.polyplot.network.packet.add_plot.AddPlotServerToClient;
import com.luridarc.polyplot.network.packet.delete_plot.DeletePlotClientToServer;
import com.luridarc.polyplot.network.packet.delete_plot.DeletePlotHandlerOnClient;
import com.luridarc.polyplot.network.packet.delete_plot.DeletePlotHandlerOnServer;
import com.luridarc.polyplot.network.packet.delete_plot.DeletePlotServerToClient;
import com.luridarc.polyplot.network.packet.get_claim_state.GetClaimStateClientToServer;
import com.luridarc.polyplot.network.packet.get_claim_state.GetClaimStateHandlerOnClient;
import com.luridarc.polyplot.network.packet.get_claim_state.GetClaimStateHandlerOnServer;
import com.luridarc.polyplot.network.packet.get_claim_state.GetClaimStateServerToClient;
import com.luridarc.polyplot.network.packet.get_local_plots.GetLocalPlotsClientToServer;
import com.luridarc.polyplot.network.packet.get_local_plots.GetLocalPlotsHandlerOnClient;
import com.luridarc.polyplot.network.packet.get_local_plots.GetLocalPlotsHandlerOnServer;
import com.luridarc.polyplot.network.packet.get_local_plots.GetLocalPlotsServerToClient;
import com.luridarc.polyplot.network.packet.new_plots_available.NewPlotsAvailableHandlerOnClient;
import com.luridarc.polyplot.network.packet.new_plots_available.NewPlotsAvailableServerToClient;

import net.minecraftforge.fml.relauncher.Side;

public class NetworkController {
    public static void init() {
        PolyPlot.network_wrapper.registerMessage(DeletePlotHandlerOnClient.class, DeletePlotServerToClient.class, 1, Side.CLIENT);
        PolyPlot.network_wrapper.registerMessage(DeletePlotHandlerOnServer.class, DeletePlotClientToServer.class, 2, Side.SERVER);

        PolyPlot.network_wrapper.registerMessage(GetClaimStateHandlerOnClient.class, GetClaimStateServerToClient.class, 3, Side.CLIENT);
        PolyPlot.network_wrapper.registerMessage(GetClaimStateHandlerOnServer.class, GetClaimStateClientToServer.class, 4, Side.SERVER);

        PolyPlot.network_wrapper.registerMessage(AddPlotHandlerOnClient.class, AddPlotServerToClient.class, 5, Side.CLIENT);
        PolyPlot.network_wrapper.registerMessage(AddPlotHandlerOnServer.class, AddPlotClientToServer.class, 6, Side.SERVER);

        PolyPlot.network_wrapper.registerMessage(GetLocalPlotsHandlerOnClient.class, GetLocalPlotsServerToClient.class, 7, Side.CLIENT);
        PolyPlot.network_wrapper.registerMessage(GetLocalPlotsHandlerOnServer.class, GetLocalPlotsClientToServer.class, 8, Side.SERVER);
        
        PolyPlot.network_wrapper.registerMessage(NewPlotsAvailableHandlerOnClient.class, NewPlotsAvailableServerToClient.class, 9, Side.CLIENT);
    }
}
