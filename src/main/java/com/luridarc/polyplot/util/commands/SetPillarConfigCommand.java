package com.luridarc.polyplot.util.commands;

import java.util.List;

import com.google.common.collect.Lists;
import com.luridarc.polyplot.util.Reference;
import com.luridarc.polyplot.util.save_data.builder_config.PillarConfigSaveData;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

public class SetPillarConfigCommand extends CommandBase {

    private final List<String> aliases = Lists.newArrayList(Reference.MOD_ID, "pp_setPillarConfig");

    @Override
    public String getName() {
        return "pp_setPillarConfig";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "pp_setPillarConfig <block:id> <block:data> <block:id> <block:data> <block:id> <block:data>";
    }

    @Override
    public List<String> getAliases() {
        return aliases;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {        
        if(sender instanceof EntityPlayer) return;

        try {
            PillarConfigSaveData.setBlockStates(args);
            System.out.println("Pillar config set!");
        } catch (Exception e) {
            System.out.println("Command failed: " + e.toString());
        }
    }
}
    
