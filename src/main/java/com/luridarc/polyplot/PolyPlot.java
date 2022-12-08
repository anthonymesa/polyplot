package com.luridarc.polyplot;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;

import com.luridarc.polyplot.proxy.IProxy;

import com.luridarc.polyplot.network.NetworkController;
import com.luridarc.polyplot.util.Reference;
import com.luridarc.polyplot.util.commands.ListAllPlotsCommand;
import com.luridarc.polyplot.util.commands.SetPillarConfigCommand;
import com.luridarc.polyplot.util.commands.SetWallConfigCommand;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

@Mod(
    modid = Reference.MOD_ID, 
    name = Reference.NAME, 
    version = Reference.VERSION,
    acceptableRemoteVersions = Reference.ACCEPTED_VERSIONS
)
public class PolyPlot
{
    // The instance is used so that other mods could reference this mod if they 
    // depended on its implementation.
    @Instance 
    public static PolyPlot instance;

    // At runtime, the proper proxy will be chosen that fits the running version,
    // whether client or server.
    @SidedProxy(
        clientSide = Reference.CLIENT_PROXY_CLASS, 
        serverSide = Reference.COMMON_PROXY_CLASS
    )
    public static IProxy proxy;

    public static SimpleNetworkWrapper network_wrapper = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MOD_ID);

    @EventHandler
    public static void preInit(FMLPreInitializationEvent event) {
        NetworkController.init();
    }

    
    @EventHandler
    public static void init(FMLInitializationEvent event) {
    }

    @EventHandler
    public static void serverInit(FMLServerStartingEvent event) {
        event.registerServerCommand(new ListAllPlotsCommand());
        event.registerServerCommand(new SetPillarConfigCommand());
        event.registerServerCommand(new SetWallConfigCommand());
    }

    @EventHandler
    public static void postInit(FMLPostInitializationEvent event) {
    }
}