package com.luridarc.polyplot.util.handlers;

import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public class WorldEventListener {

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {

    }

    @SubscribeEvent
    public void onWorldSave(WorldEvent.Save event) {

    }
}
