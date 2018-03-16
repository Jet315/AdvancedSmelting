package me.jet315.smelting.manager;

import me.jet315.smelting.Core;
import me.jet315.smelting.events.SmeltingStartEvent;
import me.jet315.smelting.smelt.SmeltableItem;
import me.jet315.smelting.utils.ActionBar;
import me.jet315.smelting.utils.Properties;
import me.jet315.smelting.utils.SmeltingType;
import me.jet315.smelting.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Coal;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Jet on 06/03/2018.
 */
public class SmeltManager {

    private HashMap<Player, SmeltPlayer> activelySmelting = new HashMap<>();
    private HashMap<Material, SmeltableItem> smeltableItems = new HashMap<>();

    /**
     * Action bar related varibles
     * var milisInRefreshRate relates to the amount of milliseconds in the refresh rate, this is needed as it is used to calculate the amount of items it can process per 'clock tick'
     */
    private int refreshRate;
    private boolean cancelOnMove;

    /**
     * The decimal format
     */
    private DecimalFormat df = new DecimalFormat("#.##");

    public SmeltManager(Properties properties, ArrayList<SmeltableItem> smeltableItems) {
        for (SmeltableItem item : smeltableItems) {
            this.smeltableItems.put(item.getRawItemStack().getType(), item);
        }
        refreshRate = 10;
        cancelOnMove = properties.isCancelSmeltingOnMove();

        startClock();
    }

    /**
     * Smelts items for a particular player
     *
     * @param p        Player
     * @param rawItems The raw items, as an Array of ItemStacks, to smelt
     */
    public void smeltItems(Player p, ArrayList<ItemStack> rawItems, SmeltingType type) {
        //1st step
        //Check items in inventory, add smeltable itemstacks to an array, check permissions
        //Calculate time needed to smelt all items, in milliseconds

        //2nd step
        //Perform checks to ensure player has enough money, coal, etc
        //Call the action bar updater in the SmeltPlayer class

        //3rd step
        //Take each itemstack in array,wait required time, smelt it, ckeck if player has moved, update value in SmeltPlayer with the completed, add to players inventory (take old item, do a check)

        //Call Event so other people can have a say
        SmeltingStartEvent smeltingStartEvent = new SmeltingStartEvent(p, rawItems, type);
        Core.getInstance().getServer().getPluginManager().callEvent(smeltingStartEvent);

        if (smeltingStartEvent.isCancelled()) return;

        rawItems = smeltingStartEvent.getItemList();

        ArrayList<ItemStack> validItems = new ArrayList<>();
        ArrayList<ItemStack> nullItems = new ArrayList<>();
        long totalTimeInMillisecondsToSmelt = 0;
        double totalCost = 0;
        double totalCoalNeeded = 0;

        for (ItemStack item : rawItems) {
            //Check if valid item
            if (!smeltableItems.containsKey(item.getType())) {
                p.sendMessage(Core.getInstance().getProperties().getMessages().getInvalidItemMessage().replaceAll("%ITEM%", item.getType().toString()));
                nullItems.add(item);

                continue;
            }
            //Check if has permission
            if (Core.getInstance().getProperties().isIndividualSmeltingPermission()) {
                if (!p.hasPermission("advancedsmelting." + item.getType().toString())) {
                    if (Core.getInstance().getProperties().isShowInvalidItemPermissions())
                        p.sendMessage(Core.getInstance().getProperties().getMessages().getNoItemPermission().replaceAll("%ITEM%", item.getType().toString()));

                    continue;
                }
            }
            SmeltableItem smeltableItem = smeltableItems.get(item.getType());
            totalTimeInMillisecondsToSmelt += (smeltableItem.getTimeToSmeltItem() * item.getAmount());
            totalCost += (smeltableItem.getCostOfMoneyToSmelt() * item.getAmount());
            totalCoalNeeded += (smeltableItem.getCostOfCoalToSmelt() * item.getAmount());

            validItems.add(item);
        }

        //Null items will be given back automatically, as they wont be added to the smelting list
        if (validItems.size() == 0){
            if(type == SmeltingType.INVENTORY) {
                p.sendMessage(Core.getInstance().getProperties().getMessages().getNoItemInGUIMessage());
            }
            if(type == SmeltingType.HAND){
                for(ItemStack item : nullItems){
                    p.getInventory().addItem(item);
                }
            }
            return;
        }
        if(nullItems.size() > 0){
            for(ItemStack item : nullItems){
                p.getInventory().addItem(item);
            }
        }

        if (totalCost > 0) {
            if (Core.economy == null) {
                System.out.println("ADVANEDCSMELTING > Vault is not installed, it must be to enable cost! ");
            } else if (!(Core.economy.getBalance(p) >= totalCost)) {
                p.sendMessage(Core.getInstance().getProperties().getMessages().getNotEnoughMoney().replaceAll("%DIFFERENCE%", String.valueOf(totalCost - Core.economy.getBalance(p))));
                //Need to give items back -  Items are given back by the Inventory Close Event if
                if(type != SmeltingType.INVENTORY) refundItems(p,validItems);
                return;
            }
        }
        int playersCoal = 0;
        if (totalCoalNeeded > 0) {
            for (ItemStack itemInInventory : p.getInventory()) {
                if (itemInInventory != null && itemInInventory.getType() == Material.COAL) {
                    playersCoal += itemInInventory.getAmount();
                }
            }
            if (playersCoal < totalCoalNeeded) {
                int coalDifference = (int) (totalCoalNeeded - playersCoal + 0.99);
                if (coalDifference == 0) coalDifference = 1;
                p.sendMessage(Core.getInstance().getProperties().getMessages().getNotEnoughCoal().replaceAll("%DIFFERENCE%", String.valueOf(coalDifference)));
                //Need to give items back - Items are given back by the Inventory Close Event if
                if(type != SmeltingType.INVENTORY) refundItems(p,validItems);

                return;
            }
        }
        p.sendMessage(Core.getInstance().getProperties().getMessages().getSmeltStartMessage());
        SmeltPlayer smeltPlayer = new SmeltPlayer(p, p.getLocation(), validItems, (int) totalTimeInMillisecondsToSmelt, (int) (totalCoalNeeded + 0.98), (int) (totalCost + 0.5));
        activelySmelting.put(p, smeltPlayer);

    }


    /**
     * Bit annoying how this is constantly running, although couldn't cancel a runnable when calling it each time
     */

    //Work out how many items I can smelt in one coal, put coal leftover into a varible to use next time
    //Pass these items into a async method, which then after the time specified, calls  back to a sync method with an array of items to give

    private void startClock(){
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Core.getInstance(), new Runnable() {
            @Override
            public void run() {
                //Iterate through the keyset
                for(Player p : activelySmelting.keySet()){

                    //Ensure player is online
                    if(!p.isOnline()){
                        activelySmelting.remove(p);
                        continue;
                    }

                    //Get the SmeltPlayer object and the items this person wishes to smelt
                    SmeltPlayer smeltPlayer = activelySmelting.get(p);

                    //Check if a smelting operation is already happening

                    if(smeltPlayer.isSmelting()) continue;

                    smeltPlayer.setSmelting(true);

                    ArrayList<ItemStack> itemsToSmelt =  smeltPlayer.getRawList();

                    //Work out how many items to give for one coal, any coal over one should be put into smeltPlayers.leftOverCoal as a positive
                    //Check if player has that one coal, else send message
                    //check if player has the required money, else send message

                    //If player has coal&money, remove the about-to-be smelted items from the smeltPlayer.getRawList

                    //If item list == 0

                    //As coal left over will be positive, if I make it negative it will mean there is > 1 coal remaining as '1' is the maximum I can have
                    double coal = -(smeltPlayer.getCoalLeftOver());
                    //The number of milliseconds needed to wait, to process items
                    int numberOfMillisecondsToWait = 0;
                    double moneyToCharge = 0;
                    double expToGive = 0;

                    //Stores the items that need to be smelted
                    ArrayList<ItemStack> validItemsToSmelt = new ArrayList<>();

                    //Loop through items
                    INNER:for (ItemStack item : itemsToSmelt) {

                        //Get the material
                        Material material = item.getType();
                        //Get the smeltable item
                        SmeltableItem smeltableItem = smeltableItems.get(material);
                        //This value stores the number of items processed
                        int numberOfItemsProcessed = 0;

                        //This smelts the amount of items, allowed in the milliseconds given
                        INNERWHILE:
                        while (item.getAmount()-numberOfItemsProcessed !=0 && coal < 2) {

                            coal += smeltableItem.getCostOfCoalToSmelt();

                            //reduce the time in milis, reduce item amount, add to number of items processed

                            numberOfMillisecondsToWait += smeltableItem.getTimeToSmeltItem();


                            //Adds to the total coal needed to perform the operation
                            moneyToCharge += smeltableItem.getCostOfMoneyToSmelt();
                            expToGive += smeltableItem.getExpToGive();

                            numberOfItemsProcessed++;
                        }

                        //Add to valid array
                        validItemsToSmelt.add(new ItemStack(material,numberOfItemsProcessed));

                        break;
                    }
                    coal = Double.valueOf(df.format(coal));
                    //Check if whole number
                    if(!(coal % 1 == 0)){

                        smeltPlayer.setCoalLeftOver(Double.valueOf(df.format(1-(coal-(int)coal))));
                    }else{
                        smeltPlayer.setCoalLeftOver(0);
                    }

                    delayItems(p,numberOfMillisecondsToWait,validItemsToSmelt,(int) (coal+0.99),moneyToCharge,expToGive);
                }
            }
        },0L, refreshRate); //Change refresh rate,probably no need to have config
    }

    //Takes the player, seconds to wait, and items that are needed to be smelted
    private void delayItems(Player p, int millisecondsDelay, ArrayList<ItemStack> itemsToSmelt, int coalToTake,double priceToCharge,double expToGive){
        Bukkit.getScheduler().runTaskLaterAsynchronously(Core.getInstance(), new Runnable() {
            @Override
            public void run() {
                giveItems(p,itemsToSmelt,coalToTake,priceToCharge,expToGive,millisecondsDelay);
                updateActionBar(p);
            }
        },millisecondsDelay/50);
    }

    //Gives the player a list of (smelted) items, and takes coal & money from the player
    private void giveItems(Player p, ArrayList<ItemStack> itemsToSmelt, int coalToTake,double priceToCharge,double expToGive,int numberOfMillisecondsToWait){
        Bukkit.getScheduler().runTask(Core.getInstance(), new Runnable() {
            @Override
            public void run() {

                //If player does not have the coal or money anymore, give itemsToSmelt back & raw list
                //update seconds completed
                if(!p.isOnline()){
                    activelySmelting.remove(p);
                    return;
                }
                SmeltPlayer smeltPlayer = activelySmelting.get(p);
                //Check if player has moved
                if (cancelOnMove) {
                    if (p.getLocation().distance(smeltPlayer.getOriginalLocation()) > 2) {
                        //Has moved, cancel task
                        activelySmelting.remove(p);
                        p.sendMessage(Core.getInstance().getProperties().getMessages().getMoveCancelMessage());
                        refundItems(p,smeltPlayer.getRawList());
                        return;

                    }
                }

                //Check player has coal
                //Coal checker
                int playersCoal = 0;
                for (ItemStack itemInInventory : p.getInventory()) {
                    if (itemInInventory != null && itemInInventory.getType() == Material.COAL) {
                        playersCoal += itemInInventory.getAmount();
                    }
                }
                if(playersCoal < coalToTake){
                    p.sendMessage(Core.getInstance().getProperties().getMessages().getNotEnoughCoal().replaceAll("%DIFFERENCE%", String.valueOf( coalToTake - playersCoal)));
                    refundItems(p,smeltPlayer.getRawList());
                    activelySmelting.remove(p);
                    return;
                }

                //Check player has the funds
                if (Core.economy != null) {
                    if (!(Core.economy.getBalance(p) >= (int) (priceToCharge))) {
                        //Ran out of money
                        //remove from list,message, continue
                        p.sendMessage(Core.getInstance().getProperties().getMessages().getNotEnoughMoney().replaceAll("%DIFFERNCE%", String.valueOf((int) Core.economy.getBalance(p) - priceToCharge)));
                        //give unsmelted items back
                        refundItems(p,smeltPlayer.getRawList());
                        activelySmelting.remove(p);
                        return;
                    }
                }
                //Update timer
                smeltPlayer.setTimeCompleted(smeltPlayer.getTimeCompleted()+ numberOfMillisecondsToWait);
                //Remove coal
                p.getInventory().removeItem(new ItemStack(Material.COAL,coalToTake));

                //Give EXP
                p.giveExp((int) (expToGive + 0.5));

                //remove money
                if (Core.economy != null) Core.economy.withdrawPlayer(p, priceToCharge);
                //Remove from main array, add the smelted items to the player

                for(ItemStack itemStack : itemsToSmelt){
                    //This bit of code removes the item that is about to be smelted, from the taw list
                    INNER:for(ItemStack item : smeltPlayer.getRawList()){
                        if(item.getType() == itemStack.getType()){
                            int amountInRawList = item.getAmount();
                            if(amountInRawList > itemStack.getAmount()){
                                item.setAmount(amountInRawList-itemStack.getAmount());
                                break INNER;
                            }else{
                                smeltPlayer.getRawList().remove(item);
                                break INNER;
                            }
                        }
                    }
                    p.getInventory().addItem(Utils.getSmeltedItemStack(itemStack));

                }

                if(smeltPlayer.getRawList().size() == 0){
                    activelySmelting.remove(p);
                    p.sendMessage(Core.getInstance().getProperties().getMessages().getSmeltCompletedMessage().replaceAll("%COAL%", String.valueOf(smeltPlayer.getTotalCoalNeeded())).replaceAll("%MONEY%", String.valueOf(smeltPlayer.getTotalMoneyNeeded())));

                    //This bit is buggy, as really the checks are not in place to ensure they have this coal
                }
                smeltPlayer.setSmelting(false);
            }
        });
    }
    //Updates the action bar for the particular player
    private void updateActionBar(Player p){
        Bukkit.getScheduler().runTask(Core.getInstance(), new Runnable() {
            @Override
            public void run() {
                SmeltPlayer smeltPlayer = activelySmelting.get(p);
                if(smeltPlayer == null){
                    ActionBar.sendActionBarPercentage(p,100);
                    return;
                }
                ActionBar.sendActionBarPercentage(p,(int) (((float) ((smeltPlayer.getTimeCompleted() * 100) / smeltPlayer.getTimeToComplete()))+0.5));
            }
        });
    }

    private void refundItems(Player p,ArrayList<ItemStack> remainingItems){
        Bukkit.getScheduler().runTask(Core.getInstance(), new Runnable() {
            @Override
            public void run() {
                for (ItemStack item : remainingItems) {
                    p.getInventory().addItem(item);

                }
            }
        });
    }


    /**
     * Old, messy, semi working code. Just left it here for reference - Not in use
     * @param instance
     */
    @Deprecated
    private void startClock(Core instance) {
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(instance, new Runnable() {
            public void run() {
                //Iterate through the keyset
                for (Player p : activelySmelting.keySet()) {
                    //TODO possibily do async method, calculate maths stuff, call a sync one?

                    //Check if player is online
                    if (!p.isOnline()) {
                        activelySmelting.remove(p);
                        continue;
                    }

                    //Get the smeltable player, so we can get his/her properties
                    SmeltPlayer smeltPlayer = activelySmelting.get(p);
                    ArrayList<ItemStack> rawItems = (ArrayList<ItemStack>) smeltPlayer.getRawList().clone();
                    //Check if player has moved
                    if (cancelOnMove) {
                        if (p.getLocation().distance(smeltPlayer.getOriginalLocation()) > 2) {
                            //Has moved, cancel task
                            activelySmelting.remove(p);
                            p.sendMessage(Core.getInstance().getProperties().getMessages().getMoveCancelMessage());

                            continue;
                        }
                    }

                   // int timeInMilis = milisInRefreshRate + smeltPlayer.getMilisLeftOver();
                    //System.out.println("Start refresh rate =  " + timeInMilis);
                    double coalNeeded = -(smeltPlayer.getCoalLeftOver());
                    double moneyNeeded = 0;
                    double expToGive = 0;

                    //Create two new arrays
                    ArrayList<ItemStack> itemsToGive = new ArrayList<>();
                    ArrayList<ItemStack> itemsToTake = new ArrayList<>();
                    System.out.println("1");
                    //Loop through the smeltable items in the players inventory
                    INNER:
                    for (ItemStack item : rawItems) {
                        //Get the smeltable item
                        System.out.println("2");
                        SmeltableItem smeltableItem = smeltableItems.get(item.getType());
                        //This value stores the number of items processed
                        int numberOfItemsProcessed = 0;
                        //This smelts the amount of items, allowed in the milliseconds given

                        while (/*timeInMilis > 0 &&*/ item.getAmount() != 0) {
                            System.out.println("in loop 3");
                            //reduce the time in milis, reduce item amount, add to number of items processed
                            item.setAmount(item.getAmount() - 1);

                           // timeInMilis -= smeltableItem.getTimeToSmeltItem();

                            //add to coal neeeded
                            coalNeeded += smeltableItem.getCostOfCoalToSmelt();
                            //Adds to the total coal needed to perform the operation
                            moneyNeeded += smeltableItem.getCostOfMoneyToSmelt();
                            expToGive += smeltableItem.getExpToGive();

                            numberOfItemsProcessed++;
                        }
                        System.out.println("4");
                        //Give smelted item, remove the normal item (add them both to lists
                        if (numberOfItemsProcessed != 0) {
                            itemsToGive.add(new ItemStack(smeltableItem.getSmeltedItemStack().getType(), numberOfItemsProcessed));
                            itemsToTake.add(new ItemStack(item.getType(), numberOfItemsProcessed));
                        }
                        if (item.getAmount() == 0) {
                            smeltPlayer.getRawList().remove(item);
                        }
                        System.out.println("5");
                      /*  if (timeInMilis <= 0) {
                            System.out.println("6");
                            break INNER;
                        }*/
                        System.out.println("7");
                    }

                    //System.out.println("timeInMilis issss " + timeInMilis);

                    moneyNeeded += 0.5; //Rounds to nearest whole number, when casted
                    System.out.println("3");
                    //Check money/coal, if all good remove/add items.
                    //Coal checker
                    int playersCoal = 0;
                    for (ItemStack itemInInventory : p.getInventory()) {
                        if (itemInInventory != null && itemInInventory.getType() == Material.COAL) {
                            playersCoal += itemInInventory.getAmount();
                        }
                    }

                    //This will set the decimal of coal left
                    double decimal = Double.valueOf(df.format(coalNeeded - (int) coalNeeded));
                    if(decimal > 0){
                        smeltPlayer.setCoalLeftOver(1-decimal);
                    }else {
                        smeltPlayer.setCoalLeftOver(-decimal);
                    }
                    coalNeeded = Double.valueOf(df.format(coalNeeded));

                    System.out.println("coal decimal = " + decimal);
                    System.out.println("coal needed = " + coalNeeded);

                    if (playersCoal >= (int) coalNeeded) { //Java rounds down, therefor by adding 0.999 it is to the nearest 1
                        //Check if have money
                        if (Core.economy != null) {
                            if (!(Core.economy.getBalance(p) >= (int) (moneyNeeded))) {
                                //Ran out of money
                                //remove from list,message, continue
                                activelySmelting.remove(p);
                                p.sendMessage(Core.getInstance().getProperties().getMessages().getNotEnoughMoney().replaceAll("%DIFFERNCE%", String.valueOf((int) Core.economy.getBalance(p) - moneyNeeded)));
                                //give unsmelted items back
                                for (ItemStack item : (ArrayList<ItemStack>) smeltPlayer.getRawList().clone()) {
                                    p.getInventory().addItem(item);
                                }
                                for (ItemStack item : itemsToTake) {
                                    p.getInventory().addItem(item);
                                }

                                continue;

                            }
                        }
                        System.out.println("4");
                        //remove coal,raw items &money, give items, update progress bar, check if done
                        p.getInventory().removeItem(new ItemStack(Material.COAL,(int) (coalNeeded)));


                            p.giveExp((int) (expToGive + 0.5));
                        if (Core.economy != null) Core.economy.withdrawPlayer(p, moneyNeeded);
             /*           smeltPlayer.setMilisLeftOver(timeInMilis);
                        System.out.println(timeInMilis);*/
                        //smeltPlayer.setTimeCompleted(smeltPlayer.getTimeCompleted() + (milisInRefreshRate - timeInMilis));
                        for (ItemStack itemToTake : itemsToTake) {
                            p.getInventory().remove(itemToTake);
                        }

                        for (ItemStack itemToGive : itemsToGive) {
                            p.getInventory().addItem(itemToGive);
                        }
                        System.out.println("5");
                        float actionBarPercentage = (float) ((smeltPlayer.getTimeCompleted() * 100) / smeltPlayer.getTimeToComplete());
                        ActionBar.sendActionBarPercentage(p, (int) (actionBarPercentage + 0.5));
                        //Check if player is finished
                        if (smeltPlayer.getRawList().size() <= 0) {
                            activelySmelting.remove(p);
                            p.sendMessage(Core.getInstance().getProperties().getMessages().getSmeltCompletedMessage().replaceAll("%COAL%", String.valueOf((int) (smeltPlayer.getTotalCoalNeeded() + 0.999999))).replaceAll("%MONEY%", String.valueOf(smeltPlayer.getTotalMoneyNeeded())));

                        }
                    } else {
                        //Ran out of coal
                        //remove from list,message, continue
                        activelySmelting.remove(p);
                        p.sendMessage(Core.getInstance().getProperties().getMessages().getNotEnoughCoal().replaceAll("%DIFFERENCE%", String.valueOf((int) (coalNeeded + 0.999999) - playersCoal)));
                        //give unsmelted items back
                        for (ItemStack item : (ArrayList<ItemStack>) smeltPlayer.getRawList().clone()) {
                            p.getInventory().addItem(item);
                        }
                        for (ItemStack item : itemsToTake) {
                            p.getInventory().addItem(item);
                        }


                    }
                }
            }
        }, 0L, refreshRate);
    }

    /**
     *
     * @return Returns the users who are actively smelting
     */
    public HashMap<Player, SmeltPlayer> getActivelySmelting() {
        return activelySmelting;
    }

    /**
     *
     * @return The smeltable item list
     */
    public HashMap<Material, SmeltableItem> getSmeltableItems() {
        return smeltableItems;
    }
}
