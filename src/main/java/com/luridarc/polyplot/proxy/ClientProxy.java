package com.luridarc.polyplot.proxy;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;

public class ClientProxy implements IProxy {

    // This registers the renderer for the item to the relevant id (e.g. 
    // 'inventory'), meaning that the model loader is made known of the item 
    // and its resource location which should be in the resources location under
    // the same name as the item that was provided to the ItemBase constructor 
    // (e.g. 'wand_non'). This is done on the client side, because any of the 
    // resource loading only needs to happen really on the client side, the 
    // server side just needs to know the object exists.
    public void registerItemRenderer(Item item, int meta, String id){
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(item.getRegistryName(), id));
    }
}
