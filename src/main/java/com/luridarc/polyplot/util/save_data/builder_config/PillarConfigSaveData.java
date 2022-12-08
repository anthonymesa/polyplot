package com.luridarc.polyplot.util.save_data.builder_config;

import com.luridarc.polyplot.util.Reference;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class PillarConfigSaveData extends WorldSavedData {

    public static final String NAME = Reference.MOD_ID + "_pillar_config";

    private final ThreeBlockConfig CONFIG = new ThreeBlockConfig(
            "minecraft:dirt", 0,
            "minecraft:dirt", 0,
            "minecraft:dirt", 0);

    public PillarConfigSaveData(String name) {
        super(name);
    }

    public PillarConfigSaveData() {
        this(NAME);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        CONFIG.setBlockLowID(nbt.getString("block_l_id"));
        CONFIG.setBlockLowData(nbt.getInteger("block_l_data"));
        CONFIG.setBlockMiddleId(nbt.getString("block_m_id"));
        CONFIG.setBlockMiddleData(nbt.getInteger("block_m_data"));
        CONFIG.setBlockHighId( nbt.getString("block_h_id"));
        CONFIG.setBlockHighData(nbt.getInteger("block_h_data"));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setString("block_l_id", CONFIG.getBlockLowID());
        compound.setInteger("block_l_data", CONFIG.getBlockLowData());
        compound.setString("block_m_id", CONFIG.getBlockMiddleId());
        compound.setInteger("block_m_data", CONFIG.getBlockMiddleData());
        compound.setString("block_h_id", CONFIG.getBlockHighId());
        compound.setInteger("block_h_data", CONFIG.getBlockHighData());
        return compound;
    }

    public static void setBlockStates(String[] config_string) throws IllegalArgumentException, NullPointerException {

        if(config_string.length != 7) {
            throw new IllegalArgumentException("Config string array must be of size (7)");
        }

        WorldServer worldServer;
        try {
            worldServer = getWorldServer(Integer.parseInt(config_string[0]));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Dimension id must be a parseable integer string.");
        } catch (IllegalArgumentException e) {
            throw e;
        }

        MapStorage storage = worldServer.getPerWorldStorage();
        PillarConfigSaveData data = getPillarConfigSaveData(storage);

        if(data == null) {
            throw new NullPointerException("Failed to retrieve PillarConfigSaveData for unknown reasons.");
        }

        data.CONFIG.setBlockLowID(config_string[1]);
        data.CONFIG.setBlockMiddleId(config_string[3]);
        data.CONFIG.setBlockHighId(config_string[5]);

        try {
            data.CONFIG.setBlockLowData(Integer.parseInt(config_string[2]));
            data.CONFIG.setBlockMiddleData(Integer.parseInt(config_string[4]));
            data.CONFIG.setBlockHighData(Integer.parseInt(config_string[6]));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Data values must be parseable integer strings.");
        }

        data.CONFIG.checkBlockStates();
        data.markDirty();
    }

    public static ConfigBlockStates getBlockStates(int dimension_id) {
        WorldServer worldServer = getWorldServer(dimension_id);
        MapStorage storage = worldServer.getPerWorldStorage();
        PillarConfigSaveData data = getPillarConfigSaveData(storage);

        if(data == null) {
            data = new PillarConfigSaveData();
            storage.setData(NAME, data);
        }

        return new ConfigBlockStates(
            data.CONFIG.getLowBlockState(),
            data.CONFIG.getMiddleBlockState(),
            data.CONFIG.getHighBlockState()
        );
    }

    private static PillarConfigSaveData getPillarConfigSaveData(MapStorage storage) {
        return (PillarConfigSaveData) storage.getOrLoadData(PillarConfigSaveData.class, PillarConfigSaveData.NAME);
    }

    private static WorldServer getWorldServer(int dimension_id) throws IllegalArgumentException {
        return FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(dimension_id);
    }
}
