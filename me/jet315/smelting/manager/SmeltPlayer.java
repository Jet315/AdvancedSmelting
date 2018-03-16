package me.jet315.smelting.manager;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

/**
 * Created by Jet on 06/03/2018.
 */
public class SmeltPlayer {

    /**
     * This object stores various properties while a player is in a smelting process
     */

    /**
     * Stores the players original Player, location & whether this should be cancled
     */
    private Player p;

    private Location originalLocation;

    /**
     * Stores the raw items, that are needed to be smelted
     */
    private ArrayList<ItemStack> rawList;

    /**
     * Stores the total time, in milliseconds, needed to complete the operation
     */
    private int timeToComplete;

    /**
     * Stores the time that has progressed so far
     */
    private int timeCompleted;

    /**
     * Stores the total amount of coal needed
     */
    private int totalCoalNeeded;
    /**
     * Stores the total amount of money needed
     */
    private int totalMoneyNeeded;

    /**
     * Stores the total amount of coal left over
     */
    private double coalLeftOver = 0;

    /**
     * Stores whether a smelting process is going on
     */
    private boolean smelting;

    /**
     *  @param p
     * @param originalLocation
     * @param rawList
     * @param timeToComplete
     * @param totalCoalNeeded
     * @param totalMoneyNeeded
     */

    public SmeltPlayer(Player p, Location originalLocation, ArrayList<ItemStack> rawList, int timeToComplete, int totalCoalNeeded, int totalMoneyNeeded) {
        this.p = p;
        this.originalLocation = originalLocation;
        this.rawList = rawList;
        this.timeToComplete = timeToComplete;
        this.totalCoalNeeded = totalCoalNeeded;
        this.totalMoneyNeeded = totalMoneyNeeded;
    }




    public Location getOriginalLocation() {
        return originalLocation;
    }

    public ArrayList<ItemStack> getRawList() {
        return rawList;
    }

    public int getTimeToComplete() {
        return timeToComplete;
    }

    public int getTimeCompleted() {
        return timeCompleted;
    }

    public int getTotalCoalNeeded() {
        return totalCoalNeeded;
    }

    public int getTotalMoneyNeeded() {
        return totalMoneyNeeded;
    }

    public void setTimeCompleted(int timeCompleted) {
        this.timeCompleted = timeCompleted;
    }

    public double getCoalLeftOver() {
        return coalLeftOver;
    }

    public void setCoalLeftOver(double coalLeftOver) {
        this.coalLeftOver = coalLeftOver;
    }

    public boolean isSmelting() {
        return smelting;
    }

    public void setSmelting(boolean smelting) {
        this.smelting = smelting;
    }
}
