package com.luridarc.polyplot.util.save_data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import com.luridarc.polyplot.util.Reference;
import com.luridarc.polyplot.util.exceptions.MalformedPlotException;
import com.luridarc.polyplot.util.plot.DynamicByteBuffer;
import com.luridarc.polyplot.util.plot.Plot;
import com.luridarc.polyplot.util.plot.ProtoPlot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class PlotsSaveData extends WorldSavedData {

    public static final String NAME = Reference.MOD_ID + "_plots";

    private final Map<UUID, List<Plot>> DATA = new HashMap<UUID, List<Plot>>();

    public PlotsSaveData(String name) {
        super(name);
    }

    public PlotsSaveData() {
        this(NAME);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        loadMapFromBytes(nbt.getByteArray("plots_data"));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {

        DynamicByteBuffer dbuf = new DynamicByteBuffer(1);

        dbuf.putInt(getAllPlotsCount());
        for(Map.Entry<UUID, List<Plot>> entry : DATA.entrySet()) 
        {
            for(Plot each : entry.getValue()) {
                each.writeToDynamicBuffer(dbuf);
            }
        }

        compound.setByteArray("plots_data", dbuf.array());
        return compound;
    }

    public static void add(Plot _plot) {

        EntityPlayer player = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(_plot.getOwnerUUID());
        
        WorldServer worldServer = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(((EntityPlayerMP) player).dimension);

        PlotsSaveData data = (PlotsSaveData) worldServer.getPerWorldStorage().getOrLoadData(PlotsSaveData.class, PlotsSaveData.NAME);

        if(data.DATA.containsKey(_plot.getOwnerUUID())) {
            data.DATA.get(_plot.getOwnerUUID()).add(_plot);
        } else {
            data.DATA.put(_plot.getOwnerUUID(), new ArrayList<Plot>());
            data.DATA.get(_plot.getOwnerUUID()).add(_plot);
        }

        data.markDirty();
    }

    public static void delete(EntityPlayer player, int index) throws Exception 
    {
        WorldServer worldServer = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(((EntityPlayerMP) player).dimension);

        PlotsSaveData data = (PlotsSaveData) worldServer.getPerWorldStorage().getOrLoadData(PlotsSaveData.class, PlotsSaveData.NAME);

        data.DATA.get(player.getUniqueID()).remove(index);

        data.markDirty();
    }

    public static PlotsSaveData get(EntityPlayer player){
        WorldServer worldServer = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(((EntityPlayerMP) player).dimension);

        MapStorage storage = worldServer.getPerWorldStorage();

        PlotsSaveData data = (PlotsSaveData) storage.getOrLoadData(PlotsSaveData.class, PlotsSaveData.NAME);

        if(data == null) {
            data = new PlotsSaveData();
            storage.setData(NAME, data);
        }

        return data;
    }

    public int getAllPlotsCount() {
        int count = 0;
        for(Map.Entry<UUID, List<Plot>> entry : DATA.entrySet()) 
        {
            for(Plot each : entry.getValue()) {
                count++;
            }
        }
        return count;
    }

    public void loadMapFromBytes(byte[] data_bytes) {

        DynamicByteBuffer dbuf = new DynamicByteBuffer(1);
        dbuf.put(data_bytes);
        dbuf.rewind();
        
        int total_plots = dbuf.getInt();
        for(int i = 0; i < total_plots; i++) {
            Plot curr_plot = null;
            try{
                curr_plot = new Plot(new ProtoPlot(dbuf));
            } catch (MalformedPlotException e) {
                System.out.println(e + ". Skipping malformed plot...");
            } 
            
            if(curr_plot == null) continue;

            add(curr_plot.getOwnerUUID(), curr_plot);  
        }
    }

    public void add(UUID player_id, Plot plot) 
    {
        if(DATA.containsKey(player_id)) {
            DATA.get(player_id).add(plot);
        } else {
            DATA.put(player_id, new ArrayList<Plot>());
            DATA.get(player_id).add(plot);
        }
    }

    //===========================================================

    public static List<Plot> getPlotList(EntityPlayer player) {
        return PlotsSaveData.get(player).DATA.get(player.getUniqueID());
    }

    public static int getTotalPlotsCount(EntityPlayer player) {
        int count = 0;
        for(Map.Entry<UUID, List<Plot>> entry : PlotsSaveData.get(player).DATA.entrySet()) 
        {
            for(Plot each : entry.getValue()) {
                count++;
            }
        }
        return count;
    }

    public static int getPlayerPlotsCount(EntityPlayer player) {
        return PlotsSaveData.get(player).DATA.get(player.getUniqueID()).size();
    }

    public static int getOwnerCount(EntityPlayer player) {
        int count = 0;
        for(Map.Entry<UUID, List<Plot>> entry : PlotsSaveData.get(player).DATA.entrySet()) 
        {
            count++;
        }
        return count;
    }

    public static String getString(EntityPlayer player) {
        StringBuilder sb = new StringBuilder();
        PlotsSaveData.get(player).DATA.forEach((uuid, plot) -> {
            sb.append("\n" + plot.toString());
        });
        return sb.toString();
    }

    public static class PlotInfo {
        UUID player_id;
        Plot plot;
        int idx;

        public UUID getPlayerId() {
            return player_id;
        }

        public Plot getPlot() {
            return plot;
        }

        public int getIdx() {
            return idx;
        }

        public PlotInfo(UUID player_id, Plot plot, int idx) {
            this.player_id = player_id;
            this.plot = plot;
            this.idx = idx;
        }
    }

    public static void forEach(Consumer<? super PlotInfo> action, EntityPlayer player) 
    {
        for(Map.Entry<UUID, List<Plot>> entry : PlotsSaveData.get(player).DATA.entrySet()) 
        {
            for(int i = 0; i < entry.getValue().size(); i++)
            {
                action.accept(new PlotInfo(entry.getKey(), entry.getValue().get(i), i));
            }
        }
    }

    public static void forEachPlayerPlot(Consumer<? super Plot> action, EntityPlayer player) {
        for(Plot each : PlotsSaveData.get(player).DATA.get(player.getUniqueID())) {
            action.accept(each);
        }
    }

    public static boolean hasPlotOwnedBy(EntityPlayer player) {
        return PlotsSaveData.get(player).DATA.containsKey(player.getUniqueID());
    }
}
