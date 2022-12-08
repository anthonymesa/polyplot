package com.luridarc.polyplot.util.save_data.builder_config;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class ThreeBlockConfig {
    private String block_l_id;
    private int block_l_data;
    private String block_m_id;
    private int block_m_data;
    private String block_h_id;
    private int block_h_data;

    public String getBlockLowID() {
        return block_l_id;
    }

    public void setBlockLowID(String block_l_id) {
        this.block_l_id = block_l_id;
    }

    public int getBlockLowData() {
        return block_l_data;
    }

    public void setBlockLowData(int block_l_data) {
        this.block_l_data = block_l_data;
    }

    public String getBlockMiddleId() {
        return block_m_id;
    }

    public void setBlockMiddleId(String block_m_id) {
        this.block_m_id = block_m_id;
    }

    public int getBlockMiddleData() {
        return block_m_data;
    }

    public void setBlockMiddleData(int block_m_data) {
        this.block_m_data = block_m_data;
    }

    public String getBlockHighId() {
        return block_h_id;
    }

    public void setBlockHighId(String block_h_id) {
        this.block_h_id = block_h_id;
    }

    public int getBlockHighData() {
        return block_h_data;
    }

    public void setBlockHighData(int block_h_data) {
        this.block_h_data = block_h_data;
    }

    public ThreeBlockConfig(
        String block_l_id, 
        int block_l_data, 
        String block_m_id, 
        int block_m_data, 
        String block_h_id,
        int block_h_data
    ) {
        this.block_l_id = block_l_id;
        this.block_l_data = block_l_data;
        this.block_m_id = block_m_id;
        this.block_m_data = block_m_data;
        this.block_h_id = block_h_id;
        this.block_h_data = block_h_data;

        checkBlockStates();
    }

    public void checkBlockStates() {
        checkBlockState(block_l_id, block_l_data);
        checkBlockState(block_m_id, block_m_data);
        checkBlockState(block_h_id, block_h_data);
    }

    private void checkBlockState(String block_id, int block_data) {
        Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(block_id));

        if((block == null) || (block == Blocks.AIR)) {
            System.out.println(String.format("Block with id %s and data %d could not be resolved. Setting to 'minecraft:dirt 0'.", block_id, block_data));
        }
    }

    public IBlockState getLowBlockState() {
        return getBlockState(block_l_id, block_l_data);
    }

    public IBlockState getMiddleBlockState() {
        return getBlockState(block_m_id, block_m_data);
    }
    
    public IBlockState getHighBlockState() {
        return getBlockState(block_h_id, block_h_data);
    }
    
    private static IBlockState getBlockState(String id, int data) {
        Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(id));

        if(block == null) return Blocks.DIRT.getDefaultState();
        if(block == Blocks.AIR) return Blocks.DIRT.getDefaultState();

        return block.getStateFromMeta(data);
    }

}