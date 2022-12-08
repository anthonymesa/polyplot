package com.luridarc.polyplot.util.save_data.builder_config;

import net.minecraft.block.state.IBlockState;

public class ConfigBlockStates {
    public IBlockState bottom;
    public IBlockState middle;
    public IBlockState top;
    public ConfigBlockStates(IBlockState bottom, IBlockState middle, IBlockState top) {
        this.bottom = bottom;
        this.middle = middle;
        this.top = top;
    }
}
