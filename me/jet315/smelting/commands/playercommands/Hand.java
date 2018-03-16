package me.jet315.smelting.commands.playercommands;

import me.jet315.smelting.Core;
import me.jet315.smelting.commands.CommandExecutor;
import me.jet315.smelting.utils.SmeltingType;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class Hand extends CommandExecutor {

    /**
     * Base smelt command
     */

    public Hand() {
        setCommand("smelt");
        setPermission("advancedsmelting.smelthand");
        setLength(0);
        setPlayer();
        setUsage("/smelt hand");

    }


    @Override
    public void execute(CommandSender sender, String[] args) {
        //Can cast as check is done before this method is called
        Player player = (Player) sender;

        //Check if player is already smelting
        if(Core.getInstance().getSmeltManager().getActivelySmelting().containsKey(player)){
            player.sendMessage(Core.getInstance().getProperties().getMessages().getAlreadySmeltingMessage());
            return;
        }

        ArrayList<ItemStack> itemsToSmelt = new ArrayList<>();
        itemsToSmelt.add(player.getInventory().getItemInMainHand().clone());
        player.getInventory().getItemInMainHand().setAmount(0);
        Core.getInstance().getSmeltManager().smeltItems(player,itemsToSmelt, SmeltingType.HAND);

    }
}