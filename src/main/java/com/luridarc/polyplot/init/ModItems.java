package com.luridarc.polyplot.init;

import java.util.ArrayList;
import java.util.List;

import com.luridarc.polyplot.objects.items.ItemWand;
import com.luridarc.polyplot.objects.items.ItemWeirdWand;
import com.luridarc.polyplot.objects.items.ItemWand.WandType;

import net.minecraft.item.Item;

public class ModItems 
{
    public static final List<Item> ITEMS = new ArrayList<Item>();    

    public static final Item WAND_NON = new ItemWand("wand_non", WandType.NON);
    public static final Item WAND_POINT = new ItemWand("wand_point", WandType.POINT);
    public static final Item WAND_EDGE = new ItemWand("wand_edge", WandType.EDGE);
    public static final Item WAND_WEIRD = new ItemWeirdWand("wand_weird");
}
