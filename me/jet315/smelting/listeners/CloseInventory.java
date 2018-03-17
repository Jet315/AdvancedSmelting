package me.jet315.smelting.listeners;

import me.jet315.smelting.Core;
import me.jet315.smelting.utils.SmeltingType;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class CloseInventory implements Listener{

    /**
     * Listens to when a player closes an inventory - Not currently in use
     */

    //Check if inventory is a smelting inventory (May need to do this on quit too)

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e){
        Inventory inventory = e.getInventory();
        if(Core.getInstance().getSmeltingGUI().isSmeltingInvetory(inventory)){
            if(Core.getInstance().getSmeltManager().getActivelySmelting().containsKey(e.getPlayer())) return;
            inventory.setItem(inventory.getSize()-1,null);
            ItemStack[] contents = inventory.getContents();

            for(ItemStack itemStack : contents){
                if(itemStack == null) continue;
                ItemStack item = new ItemStack(itemStack);
                itemStack.setAmount(0);
                e.getPlayer().getInventory().addItem(item);
            }
            Core.getInstance().getSmeltingGUI().getPlayersInGUI().remove(e.getPlayer());
        }
    }
}
