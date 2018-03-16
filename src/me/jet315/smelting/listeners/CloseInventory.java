package me.jet315.smelting.listeners;

import me.jet315.smelting.Core;
import me.jet315.smelting.utils.SmeltingType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class CloseInventory implements Listener{

    /**
     * Listens to when a player closes an inventory - Not currently in use
     */

    //Check if inventory is a smelting inventory (May need to do this on quit too)

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e){
        if(Core.getInstance().getSmeltingGUI().isSmeltingInvetory(e.getInventory())){
            if(Core.getInstance().getSmeltManager().getActivelySmelting().containsKey(e.getPlayer())) return;
            //Is a smelting inventory
            ArrayList<ItemStack> itemsToSmelt = new ArrayList<>();
            e.getInventory().setItem(e.getInventory().getSize()-1,null);
            for(ItemStack itemStack : e.getInventory().getContents()){
                if(itemStack == null) continue;
                e.getPlayer().getInventory().addItem(itemStack);
            }
            Core.getInstance().getSmeltingGUI().getPlayersInGUI().remove(e.getPlayer());
        }
    }
}
