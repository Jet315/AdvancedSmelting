package me.jet315.smelting.commands.playercommands;

import me.jet315.smelting.Core;
import me.jet315.smelting.commands.CommandExecutor;
import me.jet315.smelting.utils.SmeltingType;
import me.jet315.smelting.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Set;

public class Smelt extends CommandExecutor {

    /**
     * Base smelt command
     */

    public Smelt() {
        setCommand("smelt");
        setPermission("advancedsmelting.smelt");
        setLength(0);
        setPlayer();
        setUsage("/smelt");

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

        if(Core.getInstance().getProperties().isInventoryEnabled()){
            //Load inventory

            //Todo get the itemstack in the last possition, replace values, set item
            ItemStack item = Core.getInstance().getProperties().getConfirmItem();
            ItemMeta meta = item.getItemMeta();
            String title = Core.getInstance().getProperties().getMessages().getItemConfirmName();
            meta.setDisplayName(title.replaceAll("%MONEY%","0").replaceAll("%COAL%","0").replaceAll("%EXP","0").replaceAll("%TIME%","0"));
            //Lore
            ArrayList<String> lore = new ArrayList<>();
            for(String s : Core.getInstance().getProperties().getMessages().getItemConfirmLore()){
                String formatted = s.replaceAll("%MONEY%","0").replaceAll("%COAL%","0").replaceAll("%EXP%","0").replaceAll("%TIME%","0");
                lore.add(formatted);
            }
            meta.setLore(lore);
            item.setItemMeta(meta);
            //Open inventory
            Inventory inventory = Core.getInstance().getSmeltingGUI().getSmeltingInventory();
            inventory.setItem(Core.getInstance().getProperties().getSizeOfInventory()-1,item);
            player.openInventory(inventory);
            //Add to inventory viewer list
            Core.getInstance().getSmeltingGUI().getPlayersInGUI().add(player);

        }else{
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

}