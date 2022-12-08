package com.luridarc.polyplot.controllers;

import com.luridarc.polyplot.PolyPlot;
import com.luridarc.polyplot.controllers.PlayerController.DeleteState;
import com.luridarc.polyplot.controllers.PlayerController.PlayerState;
import com.luridarc.polyplot.network.packet.get_claim_state.GetClaimStateClientToServer;
import com.luridarc.polyplot.objects.items.ItemWand.WandAction;
import com.luridarc.polyplot.objects.items.ItemWand.WandType;
import com.luridarc.polyplot.util.plot.Claim;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;

@SideOnly(Side.CLIENT)
public class ClientActionController {

    public static class ActionData {
        private WandAction result;
        public WandAction getResult() {
            return result;
        }
        public void setResult(WandAction result) {
            this.result = result;
        }

        private WandType wand_type;
        public WandType getWandType() {
            return wand_type;
        }
        public void setWandType(WandType wand_type) {
            this.wand_type = wand_type;
        }

        private EntityPlayer player;
        public EntityPlayer getPlayer() {
            return player;
        }
        public void setPlayer(EntityPlayer player) {
            this.player = player;
        }

        private ItemStack item_stack;
        public ItemStack getItem_stack() {
            return item_stack;
        }
        public void setItem_stack(ItemStack item_stack) {
            this.item_stack = item_stack;
        }

        private BlockPos pos;
        public BlockPos getPos() {
            return pos;
        }
        public void setPos(BlockPos pos) {
            this.pos = pos;
        }

        public ActionData(
                WandType _type,
                WandAction _result,
                EntityPlayer _player,
                ItemStack _item_stack,
                BlockPos _pos) {
            this.wand_type = _type;
            this.result = _result;
            this.player = _player;
            this.item_stack = _item_stack;
            this.pos = _pos;
        }
    }

    public static ActionResult<ItemStack> handleAction(ActionData action_data) {
        switch (action_data.getResult()) {
            case HIT_BLOCK:
                return handleHitBlock(action_data);
            case NO_HIT_BLOCK:
                handleNoHitBlock(action_data);
                return new ActionResult<ItemStack>(EnumActionResult.PASS, action_data.getItem_stack());
            default:
                return new ActionResult<ItemStack>(EnumActionResult.FAIL, action_data.getItem_stack());
        }
    }

    public static boolean blocksEqual(BlockPos block1, BlockPos block2) {
        return (block1.getX() == block2.getX()) && (block1.getZ() == block2.getZ());
    }

    /**
     * Handles the case that the player right-clicked the wand on a block.
     * 
     * Checks to see if the last block isn't null, that is, it checks to see if 
     * the user has slected the most recent point that they have already added
     * to the plot. If this is the case, nothing happens.
     * 
     * If the point selected is not equal to the last point, the player has
     * selected a valid point and next the claim state of the point needs to be checked.
     * 
     * From here the application flow is given up to the network handlers. In this
     * specific case, after the server evaluates the claim state of the hit, the 
     * server sends a packet back to the client that is handled in the 
     * GetClaimStateHandlerOnClient class' onMessage() function. This schedules a 
     * task to the client's main thread that picks up handling the case we are
     * dealing with here via calling ClientActionController.continueHandlingHitBlock().
     * 
     * @param action_data
     * @return
     */
    public static ActionResult<ItemStack> handleHitBlock(ActionData action_data) {

        BlockPos last_block = PlayerController.getLastBlock();

        if (last_block != null) {
            if (blocksEqual(action_data.getPos(), last_block)) {
                return new ActionResult<ItemStack>(EnumActionResult.PASS, action_data.getItem_stack());
            }
        }

        PolyPlot.network_wrapper.sendToServer(new GetClaimStateClientToServer(action_data));
        
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, action_data.getItem_stack());
    }

    public static void continueHandlingHitBlock(ActionData action_data, Claim claim) {
        switch (claim.state) {
            case OWNER:
                ClientPlotController.handleDeleteProcess(action_data, claim.plot_index.get().intValue());
                break;
            case CLAIMED:
                ClientPlotController.rejectPlot(action_data);
                break;
            case UNCLAIMED:
                handlePlayerState(action_data);
                break;
        }
    }

    public static void handleNoHitBlock(ActionData action_data) 
    {
        switch (PlayerController.getPlayerState()) {
            case APPEND:
                ClientPlotController.cancelPlot(action_data);
                PlayerController.setPlayerState(PlayerState.START);
                break;
            default:
                PlayerController.setDeleteState(DeleteState.LEVEL_0);
                break;
        }
    }

    public static void handlePlayerState(ActionData action_data) 
    {
        switch(PlayerController.getPlayerState()){
            case START:
                handleStart(action_data);
                break;
            case APPEND:
                handleAppend(action_data);
                break;
            default:
                break;
        }
    }

    public static void handleStart(ActionData action_data)
    {
        ClientPlotController.startPlot(action_data);
        PlayerController.setPlayerState(PlayerState.APPEND);
    }

    public static void handleAppend(ActionData action_data)
    {
        if(ClientPlotController.getCurrentPlot().isClosedBy(action_data.getPos())) 
        {
            ClientPlotController.finishPlot(action_data);
            PlayerController.setPlayerState(PlayerState.START);
        } else {
            ClientPlotController.appendPlot(action_data);
        }
    }
}
