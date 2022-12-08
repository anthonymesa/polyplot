package com.luridarc.polyplot.util.handlers;

import com.luridarc.polyplot.init.ModItems;
import com.luridarc.polyplot.util.IHasModel;

import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

// This class is the event subscription handler for our mod. EventBusSubscriber is
// an interface defined within the Mod interface, so at runtime when the mod is
// recognised as a mod, the subscribe events listed within the EventBusSubscriber
// can be found and handled given that they are a SubscribeEvent function definition
// with a specific event parameter.
@EventBusSubscriber
public class RegistryEventHandler {

    // From what I can tell this register event is provided and this subscribe event
    // is called when forge mod loader is ready to register new items. Once receiving
    // the event, the items created in ModItems are converted to an array and added.
    @SubscribeEvent
    public static void onItemRegister(RegistryEvent.Register<Item> event) {
        // providing 'new Item[0]' just means that we are providing a new array of
        // 0 length and Item type to be used as the destination array for conversion.
        // This ensures that there are no issues with type conversion during runtime.
        event.getRegistry().registerAll(ModItems.ITEMS.toArray(new Item[0]));
    }

    // When forge decides it is time to load models, this subscribe event will be
    // called and all methods with this annotation and this parameter in their 
    // signature will be fired. This model registry event handler takes all of the 
    // items in our custom ModItems list and registers the model. This is strictly
    // enforced using the IHasModel interface, ensuring that it must be implemented
    // in the classes that implements it.
    @SubscribeEvent
    public static void onModelRegister(ModelRegistryEvent event){
        for(Item item : ModItems.ITEMS){
            if(item instanceof IHasModel) {
                ((IHasModel)item).registerModels();
            }
        }
    }
}
