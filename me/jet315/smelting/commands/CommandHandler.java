package me.jet315.smelting.commands;

import me.jet315.smelting.Core;
import me.jet315.smelting.commands.admincommands.Reload;
import me.jet315.smelting.commands.playercommands.All;
import me.jet315.smelting.commands.playercommands.Hand;
import me.jet315.smelting.commands.playercommands.Smelt;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jet on 28/01/2018.
 */
public class CommandHandler implements org.bukkit.command.CommandExecutor {

    private Map<String, CommandExecutor> commands = new HashMap<String, CommandExecutor>();

    public CommandHandler() {
        //Player commands
        commands.put("smelt", new Smelt());
        commands.put("reload", new Reload());
        commands.put("hand", new Hand());
        commands.put("all", new All());

        //Admin commands
    }
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {

        if (cmd.getName().equalsIgnoreCase("smelt")) {
            if(args.length == 0) {
                if(sender instanceof Player){
                    final CommandExecutor command = commands.get("smelt");
                        if (command.getPermission() != null && !sender.hasPermission(command.getPermission())) {
                            sender.sendMessage(Core.getInstance().getProperties().getMessages().getNoPermission());
                            return true;
                        }
                        command.execute(sender,args);
                        return true;

                }else{
                    sender.sendMessage(Core.getInstance().getProperties().getMessages().getPlayerCommand());
                    return true;
                }
            }else{
                //This code is not currently used, but may need it in the future
                String name = args[0].toLowerCase();
                if (commands.containsKey(name)) {
                    final CommandExecutor command = commands.get(name);

                    if (command.getPermission() != null && !sender.hasPermission(command.getPermission())) {
                        sender.sendMessage(Core.getInstance().getProperties().getMessages().getNoPermission());
                        return true;
                    }

                    if (!command.isBoth()) {
                        if (command.isConsole() && sender instanceof Player) {
                            sender.sendMessage(Core.getInstance().getProperties().getMessages().getConsoleCommand());
                            return true;
                        }
                        if (command.isPlayer() && sender instanceof ConsoleCommandSender) {
                            sender.sendMessage(Core.getInstance().getProperties().getMessages().getPlayerCommand());
                            return true;
                        }
                    }

                    if (command.getLength() > args.length) {
                        sender.sendMessage(ChatColor.RED + "Usage: " + command.getUsage());
                        return true;
                    }

                    command.execute(sender, args);
                    return true;
                }
            }
            sender.sendMessage(Core.getInstance().getProperties().getMessages().getUnknownCommand());
        }
        return true;
    }
}
