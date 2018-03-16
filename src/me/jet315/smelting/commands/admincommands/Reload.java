package me.jet315.smelting.commands.admincommands;

import me.jet315.smelting.Core;
import me.jet315.smelting.commands.CommandExecutor;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Reload extends CommandExecutor {

    /**
     * Base smelt command
     */

    public Reload() {
        setCommand("reload");
        setPermission("advancedsmelting.reload");
        setLength(1);
        setBoth();
        setUsage("/smelt reload");

    }


    @Override
    public void execute(CommandSender sender, String[] args) {
        //start time, stuff, test
        long startTime = System.currentTimeMillis();
        Core.getInstance().reloadPlugin();
        sender.sendMessage(ChatColor.GREEN +"Reload Complete! " + String.valueOf(System.currentTimeMillis()-startTime)+ "ms");

    }

}