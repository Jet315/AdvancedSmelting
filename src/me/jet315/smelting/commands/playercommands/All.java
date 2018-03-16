package me.jet315.smelting.commands.playercommands;

import me.jet315.smelting.Core;
import me.jet315.smelting.commands.CommandExecutor;
import me.jet315.smelting.utils.SmeltingType;
import me.jet315.smelting.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class All extends CommandExecutor {

    /**
     * Base smelt command
     */

    public All() {
        setCommand("smelt");
        setPermission("advancedsmelting.smeltall");
        setLength(0);
        setPlayer();
        setUsage("/smelt all");

    }


    @Override
    public void execute(CommandSender sender, String[] args) {
        //Can cast as check is done before this method is called
        Player player = (Player) sender;


        //Smelt items in inventory
        ArrayList<ItemStack> itemsToSmelt = new ArrayList<>();
        for(ItemStack item : player.getInventory().getContents()){
            if(Utils.getSmeltedItemStack(item) != null){
                itemsToSmelt.add(item);
                player.getInventory().remove(item);

            }
        }
        //Smelt items
        Core.getInstance().getSmeltManager().smeltItems(player,itemsToSmelt, SmeltingType.ALL);

    }

}