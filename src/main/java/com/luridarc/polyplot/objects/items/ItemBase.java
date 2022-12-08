package com.luridarc.polyplot.objects.items;

import com.luridarc.polyplot.util.IHasModel;
import com.luridarc.polyplot.PolyPlot;
import com.luridarc.polyplot.init.ModItems;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ItemBase extends Item implements IHasModel {

    // On construction, this basic item is created and added to the ITEMS list
    // in ModItems.
    public ItemBase(String name){
        setUnlocalizedName(name);
        setRegistryName(name);
        setCreativeTab(CreativeTabs.TOOLS);

        ModItems.ITEMS.add(this);
    }

    @Override
    public void registerModels() {
        // By hiding the client and server proxies behind Main.proxy, and allowing
        // the runtime to choose which to use, we can make a single agnostic call
        // to the proxy for registering the itemRenderer. From what I can tell, this
        // is registering this item as an element available in the 'inventory'.
        PolyPlot.proxy.registerItemRenderer(this, 0, "inventory");        
    }
    
}
