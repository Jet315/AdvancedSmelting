package me.jet315.smelting.utils;

import me.jet315.smelting.Core;
import org.apache.logging.log4j.message.Message;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Jet on 06/03/2018.
 */
public class Messages{

    /**
     * Stores the main instance
     */
    private Core instance;

    /**
     * Configuration file
     */
    private YamlConfiguration messagesFile;

    /**
     * Messages
     */
    private String invalidItemMessage = "Invalid Item";
    private String noPermission = "No Permission";
    private String noItemPermission = "You do not have permission to smelt %ITEM%";
    private String notEnoughCoal = "Not Enough Coal";
    private String notEnoughMoney = "Not Enough Money";
    private String smeltStartMessage = "Smelting Started";
    private String smeltCompletedMessage = "Smelting finished";
    private String moveCancelMessage = "Smelting canceled! You moved!";
    private String unknownCommand = "Unknown Command!";
    private String consoleCommand = "Console Only!";
    private String playerCommand = "Players Only!";
    private String alreadySmeltingMessage = "You are already smelting!";
    private String actionBarMessage = "%PERCENTAGE%";
    private String noItemInGUIMessage = "No valid items!";
    //Item Confirm buttons
    private String itemConfirmName = "Confirm!";
    private ArrayList<String> itemConfirmLore = new ArrayList<>();

    public Messages(Core instance){
        this.instance = instance;
        createConfig();
        loadConfig();
    }

    private void loadConfig(){
        invalidItemMessage = ChatColor.translateAlternateColorCodes('&',messagesFile.getString("InvalidSmeltingItem"));
        noPermission = ChatColor.translateAlternateColorCodes('&',messagesFile.getString("NoPermission"));
        noItemPermission = ChatColor.translateAlternateColorCodes('&',messagesFile.getString("NoItemPermission"));
        notEnoughMoney = ChatColor.translateAlternateColorCodes('&',messagesFile.getString("NotEnoughMoney"));
        notEnoughCoal = ChatColor.translateAlternateColorCodes('&',messagesFile.getString("NotEnoughCoal"));
        smeltStartMessage = ChatColor.translateAlternateColorCodes('&',messagesFile.getString("SmeltStartMessage"));
        smeltCompletedMessage = ChatColor.translateAlternateColorCodes('&',messagesFile.getString("SmeltCompletedMessage"));
        moveCancelMessage = ChatColor.translateAlternateColorCodes('&',messagesFile.getString("MoveCancelMessage"));
        unknownCommand = ChatColor.translateAlternateColorCodes('&',messagesFile.getString("UnknownCommand"));
        consoleCommand = ChatColor.translateAlternateColorCodes('&',messagesFile.getString("ConsoleOnlyCommand"));
        playerCommand = ChatColor.translateAlternateColorCodes('&',messagesFile.getString("PlayerOnlyCommand"));
        alreadySmeltingMessage = ChatColor.translateAlternateColorCodes('&',messagesFile.getString("AlreadySmeltingMessage"));
        actionBarMessage = ChatColor.translateAlternateColorCodes('&',messagesFile.getString("ActionBarMessage"));
        itemConfirmName = ChatColor.translateAlternateColorCodes('&',messagesFile.getString("ConfirmItemName"));
        noItemInGUIMessage = ChatColor.translateAlternateColorCodes('&',messagesFile.getString("NoItemInGUIMessage"));
        for(Object s : messagesFile.getList("ConfirmLore")){
            itemConfirmLore.add(ChatColor.translateAlternateColorCodes('&',s.toString()));
        }
    }

    private void createConfig(){
        try {
            if (!instance.getDataFolder().exists()) {
                instance.getDataFolder().mkdirs();
            }
            File file = new File(instance.getDataFolder(), "messages.yml");
            if (!file.exists()) {
                instance.getLogger().info("messages.yml not found, creating!");
                instance.saveResource("messages.yml",false);
            } else {
                instance.getLogger().info("messages.yml found, loading!");
            }
            messagesFile = YamlConfiguration.loadConfiguration(file);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public String getInvalidItemMessage() {
        return invalidItemMessage;
    }

    public String getNoPermission() {
        return noPermission;
    }

    public String getNotEnoughCoal() {
        return notEnoughCoal;
    }

    public String getNotEnoughMoney() {
        return notEnoughMoney;
    }

    public String getSmeltStartMessage() {
        return smeltStartMessage;
    }

    public String getSmeltCompletedMessage() {
        return smeltCompletedMessage;
    }

    public String getMoveCancelMessage() {
        return moveCancelMessage;
    }

    public String getUnknownCommand() {
        return unknownCommand;
    }

    public String getConsoleCommand() {
        return consoleCommand;
    }

    public String getPlayerCommand() {
        return playerCommand;
    }

    public String getAlreadySmeltingMessage() {
        return alreadySmeltingMessage;
    }

    public String getNoItemPermission() {
        return noItemPermission;
    }

    public void setNoItemPermission(String noItemPermission) {
        this.noItemPermission = noItemPermission;
    }

    public String getActionBarMessage() {
        return actionBarMessage;
    }

    public String getItemConfirmName() {
        return itemConfirmName;
    }

    public ArrayList<String> getItemConfirmLore() {
        return itemConfirmLore;
    }

    public String getNoItemInGUIMessage() {
        return noItemInGUIMessage;
    }
}
