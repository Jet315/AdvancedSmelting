package me.jet315.smelting.listeners;

import me.jet315.smelting.Core;
import me.jet315.smelting.utils.SmeltingType;
import me.jet315.smelting.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class InventoryClick implements Listener{

    @EventHandler
    public void onClick(InventoryClickEvent e){
        //Check if moving to the Smelting inventory
        if(e.getClickedInventory() ==  null) return;
        if(Core.getInstance().getSmeltingGUI().isSmeltingInvetory(e.getInventory())){
            Player p = (Player) e.getWhoClicked();
            int getLastSlot = e.getInventory().getSize()-1;
            if(e.getCurrentItem() != null && e.getSlot() == getLastSlot){
                e.getInventory().setItem(getLastSlot,null);
                ArrayList<ItemStack> itemsToSmelt = new ArrayList<>();
                for(ItemStack itemStack : e.getInventory().getContents()){
                    if(itemStack == null) continue;
                    itemsToSmelt.add(new ItemStack(itemStack));
                }
                Core.getInstance().getSmeltManager().smeltItems(p,itemsToSmelt, SmeltingType.INVENTORY);
                Core.getInstance().getSmeltingGUI().getPlayersInGUI().remove(p);
                p.closeInventory();
                e.setCancelled(true);
                return;
            }

            updateInvetoryPrice(e.getInventory());

        }
    }

    public void updateInvetoryPrice(Inventory inventory){
        Bukkit.getScheduler().runTaskLater(Core.getInstance(), new Runnable() {
            @Override
            public void run() {
                int getLastSlot = inventory.getSize()-1;
                String[] costs = Utils.calcInveotryCosts(inventory.getContents());
                //Get the confirm item
                ItemStack confirmItem = inventory.getItem(getLastSlot);
                if(confirmItem == null) return;
                //Set the confirm items name, and lore
                ItemMeta confirmItemMeta = confirmItem.getItemMeta();
                String title = Core.getInstance().getProperties().getMessages().getItemConfirmName();
                confirmItemMeta.setDisplayName(title.replaceAll("%MONEY%",costs[0]).replaceAll("%COAL%",costs[1]).replaceAll("%EXP",costs[2]).replaceAll("%TIME%",costs[3]));
                //Lore
                ArrayList<String> lore = new ArrayList<>();
                for(String s : Core.getInstance().getProperties().getMessages().getItemConfirmLore()){
                    String formatted = s.replaceAll("%MONEY%",costs[0]).replaceAll("%COAL%",costs[1]).replaceAll("%EXP%",costs[2]).replaceAll("%TIME%",costs[3]);
                    lore.add(formatted);
                }
                confirmItemMeta.setLore(lore);
                confirmItem.setItemMeta(confirmItemMeta);
                inventory.setItem(getLastSlot,confirmItem);
            }
        },10L);
    }
}
