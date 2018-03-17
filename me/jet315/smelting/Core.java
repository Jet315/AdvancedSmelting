package me.jet315.smelting;

import me.jet315.smelting.commands.CommandHandler;
import me.jet315.smelting.gui.SmeltingGUI;
import me.jet315.smelting.listeners.CloseInventory;
import me.jet315.smelting.listeners.InventoryClick;
import me.jet315.smelting.manager.SmeltManager;
import me.jet315.smelting.utils.ActionBar;
import me.jet315.smelting.utils.Properties;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Jet on 05/03/2018.
 */
public class Core extends JavaPlugin {

    /**
     * Plugins instance
     */
    private static Core instance;
    /**
     * Properties class
     */
    private Properties properties;

    /**
     * Smelt manager class
     */
    private SmeltManager smeltManager;

    /**
     * GUI Class
     */
    private SmeltingGUI smeltingGUI;

    /**
     * Vault dependency, needed for Vault, a third party API/Plugin
     */
    public static Economy economy = null;

    public void onEnable(){
        //Just cool knowing how long the plugin takes to enable, in my opinion :)
        long startTime = System.currentTimeMillis();
        System.out.println("\n[AdvancedSmelting] Initializing Plugin");

        instance = this;
        //Create objects
        properties = new Properties(this);

        //Need Minecrafts servers version for ActionBar
        ActionBar.nmsver = Bukkit.getServer().getClass().getPackage().getName();
        ActionBar.nmsver = ActionBar.nmsver.substring(ActionBar.nmsver.lastIndexOf(".") + 1);

        if (ActionBar.nmsver.equalsIgnoreCase("v1_8_R1") || ActionBar.nmsver.startsWith("v1_7_")) {
            ActionBar.useOld = true;
        }

        smeltManager = new SmeltManager(properties,properties.getRawItems());
        smeltingGUI = new SmeltingGUI(properties.getSizeOfInventory(),properties.getNameOfInventory());

        //Register events & commands
        registerEvents();
        registerCommands();

        //Setup economy
        setupEconomy();

        System.out.println("[AdvancedSmelting] Initializing Complete - Time took " + String.valueOf(System.currentTimeMillis()-startTime) +"Ms\n");

    }


    public void onDisable(){
        Bukkit.getScheduler().cancelTasks(this);

        properties = null;
        smeltManager = null;
        smeltingGUI = null;
    }

    public void reloadPlugin(){

        //Send user message, or close  smelt inventories?
/*        for(Player p : Bukkit.getOnlinePlayers()){

        }*/

        Bukkit.getScheduler().cancelTasks(this);
        this.reloadConfig();
        properties = null;
        smeltManager = null;
        smeltingGUI = null;

        properties = new Properties(this);
        smeltManager = new SmeltManager(properties,properties.getRawItems());
        smeltingGUI = new SmeltingGUI(properties.getSizeOfInventory(),properties.getNameOfInventory());

    }



    public void registerEvents(){
        Bukkit.getServer().getPluginManager().registerEvents(new CloseInventory(),this);
        Bukkit.getServer().getPluginManager().registerEvents(new InventoryClick(),this);
    }

    public void registerCommands(){
        getCommand("smelt").setExecutor(new CommandHandler());
    }


    public Properties getProperties() {
        return properties;
    }
    public static Core getInstance() {
        return instance;
    }

    public SmeltManager getSmeltManager() {
        return smeltManager;
    }

    private boolean setupEconomy()
    {
       try {
           RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
           if (economyProvider != null) {
               economy = economyProvider.getProvider();
           }

           return (economy != null);
       }catch(NoClassDefFoundError e){
        return false;
       }
    }

    public SmeltingGUI getSmeltingGUI() {
        return smeltingGUI;
    }
}
