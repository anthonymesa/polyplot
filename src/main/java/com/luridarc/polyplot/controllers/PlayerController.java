package com.luridarc.polyplot.controllers;

import com.luridarc.polyplot.util.plot.Plot;

import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

// These four enum states are equivalent to:
//
// START_NONE -> the player has no current_plot and no saved plots.
// APPEND_NONE -> the player has a current plot but no saved plots.
// START_SOME -> the player has no current plot but one or more saved plots.
// APPEND_SOME -> the player has a current plot and one or more saved plots.

@SideOnly(Side.CLIENT)
public class PlayerController {

    public static Plot current_plot_in = null;
    public static Plot last_plot_in = null;

    public static enum PlayerState { START, APPEND }
    public static enum DeleteState { LEVEL_0, LEVEL_1, LEVEL_2 }

    private static PlayerState player_state = PlayerState.START;
    public static PlayerState getPlayerState() {
        return player_state;
    }
    
    public static void setPlayerState(PlayerState state) {
        player_state = state;
    }

    private static DeleteState delete_state = DeleteState.LEVEL_0;
    public static DeleteState getDeleteState() {
        return delete_state;
    }

    public static void setDeleteState(DeleteState delete_state) {
        PlayerController.delete_state = delete_state;
    }

    private static BlockPos last_block = null;
    public static BlockPos getLastBlock() {
        return last_block;
    }
    
    public static void setLastBlock(BlockPos pos) {
        last_block = pos;
    }

    public static void clearLastBlock() {
        last_block = null;
    }

    public static void setCurrentPlot(Plot each) {
        if((current_plot_in == null) && (last_plot_in == null)) {
            last_plot_in = each;
            current_plot_in = each;
        } else {
            last_plot_in = current_plot_in;
            current_plot_in = each;
        }
    }

    public static boolean playerStillInPlot() {
        if((current_plot_in == null) && (last_plot_in == null)) {
            return false;
        } else {
            return current_plot_in == last_plot_in;
        }
    }
}
