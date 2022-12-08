package com.luridarc.polyplot.util.plot;

import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.client.FMLClientHandler;

public class SearchGrid {

    public static final int GRID_SIZE = 100;

    // using a blockpos type here for convenience only
    public static BlockPos last_grid_cell;
    public static BlockPos current_grid_cell;

    private static boolean posEqual(BlockPos block1, BlockPos block2) {
        return (block1.getX() == block2.getX()) && (block1.getZ() == block2.getZ());
    }

    public static boolean playerChangedGrid() {
        try{
            BlockPos player_pos = FMLClientHandler.instance().getClient().player.getPosition();

            current_grid_cell = gridPosFromBlockPos(player_pos);
    
            if(last_grid_cell == null) {
                last_grid_cell = current_grid_cell;
                return false;
            } else if (!posEqual(last_grid_cell, current_grid_cell)) {
                last_grid_cell = current_grid_cell;
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    public static BlockPos gridPosFromBlockPos(BlockPos pos) {
        return new BlockPos(Math.floor(pos.getX() / (float) GRID_SIZE), Math.floor(pos.getY() / (float) GRID_SIZE), Math.floor(pos.getZ() / (float) GRID_SIZE));
    }

    public static Bounds posToBounds(BlockPos pos) {
        BlockPos temp_grid_cell = gridPosFromBlockPos(pos);
        return new Bounds(
            (int) (temp_grid_cell.getX() * (float) SearchGrid.GRID_SIZE),
            (int) ((temp_grid_cell.getX() * (float) SearchGrid.GRID_SIZE) + SearchGrid.GRID_SIZE),
            (int) ((temp_grid_cell.getZ() * (float) SearchGrid.GRID_SIZE) + SearchGrid.GRID_SIZE),
            (int) (temp_grid_cell.getZ() * (float) SearchGrid.GRID_SIZE)
        );
    }
}
