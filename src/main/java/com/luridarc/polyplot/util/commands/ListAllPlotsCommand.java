package com.luridarc.polyplot.util.commands;

import java.util.List;

import com.google.common.collect.Lists;
import com.luridarc.polyplot.util.Reference;
import com.luridarc.polyplot.util.save_data.PlotsSaveData;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

public class ListAllPlotsCommand extends CommandBase {

    private final List<String> aliases = Lists.newArrayList(Reference.MOD_ID, "pp_ls");

    @Override
    public String getName() {
        return "pp_ls";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "pp_ls";
    }

    @Override
    public List<String> getAliases() {
        return aliases;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {        
        if(sender instanceof EntityPlayer) return;

        System.out.println(PlotsSaveData.getString((EntityPlayer) sender));
    }
    
}
