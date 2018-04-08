package me.jet315.smelting.smelt;

import me.jet315.smelting.utils.Utils;
import org.bukkit.Material;
import org.bukkit.block.Furnace;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Jet on 05/03/2018.
 */
public class SmeltableItem {

    /**
     * This class is the blue print for each of the smeltable items.
     */

    //Stores the raw item name of the object
    private String itemName;
    private short durability;

    //Stores the item raw Itemstack of the object (IE. MATERIAL.COAL, etc)
    private ItemStack rawItemStack;

    //Stores the smelted Itemstack of the object
    private ItemStack smeltedItemStack;

    //The exp to give when the item is smelted
    private double expToGive;

    //Stores the time (In milliseconds) used to smelt an object
    private int timeToSmeltItem;

    //Stores the cost of coal needed to smelt an object
    private double costOfCoalToSmelt;

    //Stores the cost ($$$) needed to smelt an object
    private double costOfMoneyToSmelt;

    /**
     *
     * @param itemName The item name of the SMELTABLE item
     * @param timeToSmeltItem The time in milliseconds the item takes to smelt
     * @param durability The durability of the item (Used to determind what types of fish, etc)
     * @param costOfCoalToSmelt The cost in coal needed to smelt this item
     * @param costOfMoneyToSmelt The cost in money needed to smelt this item
     */
    public SmeltableItem(String itemName,short durability,double expToGive, int timeToSmeltItem, double costOfCoalToSmelt, double costOfMoneyToSmelt){
        this.itemName = itemName;
        this.durability = durability;
        this.expToGive = expToGive;
        this.timeToSmeltItem = timeToSmeltItem;
        this.costOfCoalToSmelt = costOfCoalToSmelt;
        this.costOfMoneyToSmelt = costOfMoneyToSmelt;
        populateMaterials();

    }
    private void populateMaterials(){
        //Set the raw material
        Material rawMaterial = Material.getMaterial(itemName);
        if(rawMaterial == null){
            return;
        }
        rawItemStack = new ItemStack(rawMaterial,1,durability);

        //Set the smelted material - can return null, although will be checked in the validate method
        smeltedItemStack = Utils.getSmeltedItemStack(rawItemStack);
    }
    /**
     * @return True if the data entered in the constructor is correct, false otherwise
     *
     * As data entered is from the configuration file (By a user), adeding this check prevents non smeltable items from being added
     */
    public boolean validate(){
        if(rawItemStack == null || smeltedItemStack == null){
            return false;
        }
        return true;
    }

    /**
     * Getters
     */

    public String getItemName() {
        return itemName;
    }


    public int getTimeToSmeltItem() {
        return timeToSmeltItem;
    }

    public double getCostOfCoalToSmelt() {
        return costOfCoalToSmelt;
    }

    public double getCostOfMoneyToSmelt() {
        return costOfMoneyToSmelt;
    }


    public ItemStack getRawItemStack() {
        return rawItemStack;
    }

    public ItemStack getSmeltedItemStack() {
        return smeltedItemStack;
    }

    public double getExpToGive() {
        return expToGive;
    }
}
