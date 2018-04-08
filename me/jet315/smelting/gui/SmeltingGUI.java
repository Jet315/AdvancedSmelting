package me.jet315.smelting.gui;

import me.jet315.smelting.Core;
import me.jet315.smelting.commands.playercommands.Smelt;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;

public class SmeltingGUI {

    /**
     * Stores players who are in the GUI
     */
    private ArrayList<Player> playersInGUI = new ArrayList<>();

    /**
     * Smelting Name and Inventory
     */
    private int inventorySize = 27;
    private String inventoryName = "Smelting Inventory";

    /**
     *
     * @param inventorySize The size of the inventory
     * @param inventoryName The name of the inventory
     */
    public SmeltingGUI(int inventorySize, String inventoryName){
        this.inventorySize = inventorySize;
        this.inventoryName = inventoryName;
    }

    /**
     * @return Returns a Smelting Inventory
     */
    public Inventory getSmeltingInventory(){
        return Bukkit.createInventory(null,inventorySize, inventoryName);
    }

    public boolean isSmeltingInventory(Inventory inventory){
        return inventory.getName().equals(inventoryName) && inventory.getSize() == inventorySize;
    }

    /**
     * @return Returns the players who are currently looking in the GUI
     */
    public ArrayList<Player> getPlayersInGUI() {
        return playersInGUI;
    }
}
