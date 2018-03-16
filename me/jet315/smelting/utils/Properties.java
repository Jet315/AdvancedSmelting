package me.jet315.smelting.utils;

import me.jet315.smelting.Core;
import me.jet315.smelting.smelt.SmeltableItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Jet on 05/03/2018.
 */
public class Properties {

    /**
     * Stores plugin instance
     */
    private Core instance;


    /**
     * Action bar
     */
    private boolean isActionBarEnabled = true;

    /**
     * Stores whether the smelting should stop if the player moves
     */
    private boolean cancelSmeltingOnMove = true;

    /**
     * Stores whether an inventory should be used, and the name of it + size
     */
    private boolean inventoryEnabled = true;
    private String nameOfInventory = ChatColor.GRAY + "Smelting";
    private int sizeOfInventory = 27;
    private ItemStack confirmItem = new ItemStack(Material.STAINED_GLASS_PANE,0,(byte) 5);

    /**
     * Whether Individual Smelting Permissions should be used
     */
    private boolean individualSmeltingPermission = false;
    private boolean showInvalidItemPermissions = true;

    /**
     * Stores the valid raw items list
     */
    private ArrayList<SmeltableItem> rawItems = new ArrayList<>();

    /**
     * Stores messages
     */
    private Messages messages;

    public Properties(Core instance){
        this.instance = instance;

        messages = new Messages(instance);
        loadConfig();
    }

    private void loadConfig(){
        createConfig();
        FileConfiguration config = instance.getConfig();
        isActionBarEnabled = config.getBoolean("EnableActionBar");
        cancelSmeltingOnMove = config.getBoolean("cancelSmeltingOnMove");
        inventoryEnabled = config.getBoolean("EnableSmeltInventory");
        showInvalidItemPermissions = config.getBoolean("ShowInvalidItemPermissions");
        nameOfInventory = ChatColor.translateAlternateColorCodes('&',config.getString("NameOfSmeltInventory"));
        individualSmeltingPermission = config.getBoolean("IndividualSmeltingPermissions");
        sizeOfInventory = config.getInt("SizeOfSmeltInventory");

         String confirmItemArray[] = config.getString("ConfirmItemID").split(":");
         confirmItem = new ItemStack(Material.getMaterial(confirmItemArray[0]),1, Byte.valueOf(confirmItemArray[1]));

            for(String item : config.getConfigurationSection("Items").getKeys(false)){
                byte data = (byte) config.getInt("Items."+item+".Data");
                double expToGive = config.getDouble("Items."+item+".ExpToGive");
                int timeToSmelt = config.getInt("Items."+item+".TimeToSmelt");
                double costOfCoalToSmelt = config.getDouble("Items."+item+".CostOfCoalToSmelt");
                double costOfMoneyToSmelt = config.getDouble("Items."+"PORK"+".CostOfMoneyToSmelt");
                SmeltableItem smeltableItem = new SmeltableItem(item,data,expToGive,timeToSmelt,costOfCoalToSmelt,costOfMoneyToSmelt);
                if(smeltableItem.validate()){
                    this.rawItems.add(smeltableItem);
                }else{
                    System.out.println("AdvancedSmithing > Configuration error on the item " + item);
                }

            }


    }

    private void createConfig(){
        try {
            if (!instance.getDataFolder().exists()) {
                Core.getInstance().getDataFolder().mkdirs();
            }
            File file = new File(instance.getDataFolder(), "config.yml");
            if (!file.exists()) {
                instance.getLogger().info("Config.yml not found, creating!");
                instance.saveDefaultConfig();
            } else {
                instance.getLogger().info("Config.yml found, loading!");
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }



    public Messages getMessages() {
        return messages;
    }

    public boolean isActionBarEnabled() {
        return isActionBarEnabled;
    }

    public boolean isCancelSmeltingOnMove() {
        return cancelSmeltingOnMove;
    }

    public boolean isInventoryEnabled() {
        return inventoryEnabled;
    }

    public String getNameOfInventory() {
        return nameOfInventory;
    }

    public boolean isIndividualSmeltingPermission() {
        return individualSmeltingPermission;
    }

    public ArrayList<SmeltableItem> getRawItems() {
        return rawItems;
    }

    public int getSizeOfInventory() {
        return sizeOfInventory;
    }

    public boolean isShowInvalidItemPermissions() {
        return showInvalidItemPermissions;
    }

    public ItemStack getConfirmItem() {
        return new ItemStack(confirmItem);
    }
}
