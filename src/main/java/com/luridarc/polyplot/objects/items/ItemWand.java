package com.luridarc.polyplot.objects.items;

import java.util.List;

import javax.annotation.Nullable;

import com.luridarc.polyplot.controllers.ClientActionController;
import com.luridarc.polyplot.controllers.ClientActionController.ActionData;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemWand extends ItemBase {

    public enum WandType {
        NON, POINT, EDGE
    }

    public static enum WandAction {
        HIT_BLOCK, NO_HIT_BLOCK
    }

    public WandType type;

    public ItemWand(String name, WandType _type) {
        super(name);
        this.type = _type;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean hasEffect(ItemStack stack) {
        return true;
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        if(this.type == WandType.NON) {
            tooltip.add("Creates a protected plot of land");
        } else if (this.type == WandType.POINT) {
            tooltip.add("Creates a protected plot of land");
            tooltip.add("bounded by pillars");
        } else if (this.type == WandType.EDGE) {
            tooltip.add("Creates a protected plot of land");
            tooltip.add("bordered by walls and pillars");
        }
    }

    // When a player right clicks on a block that should be handled differently then when they right click onto nothing.
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack item_stack = playerIn.getHeldItem(handIn);

        if(!worldIn.isRemote) {
            return new ActionResult<ItemStack>(EnumActionResult.PASS, item_stack);
        }

        if((playerIn.dimension != 0) && (this.type != WandType.NON)) {
            playerIn.sendMessage(new TextComponentString("That doesn't seem to work here..."));
            return new ActionResult<ItemStack>(EnumActionResult.PASS, item_stack);
        }
        
        RayTraceResult mop = rayTrace(worldIn, playerIn, false);
        ActionData action_data = new ClientActionController.ActionData(this.type, null, playerIn, item_stack, null);

        if (hitFailed(mop)) {
            action_data.setResult(WandAction.NO_HIT_BLOCK);
            return ClientActionController.handleAction(action_data);
        } else {
            action_data.setResult(WandAction.HIT_BLOCK);
            action_data.setPos(mop.getBlockPos());
            return ClientActionController.handleAction(action_data);
        }
    }

    private boolean hitFailed(RayTraceResult mop) {
        return mop == null || mop.typeOfHit != RayTraceResult.Type.BLOCK;
    }
}
