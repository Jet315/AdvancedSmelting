package me.jet315.smelting.events;

import me.jet315.smelting.utils.SmeltingType;
import me.jet315.smelting.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class SmeltingStartEvent extends Event implements Cancellable {

    /**
     * Called when starts the Smelting Process
     */

    private static final HandlerList handlers = new HandlerList();

    private Player player;

    private ArrayList<ItemStack> itemList = new ArrayList<>();

    private SmeltingType smeltingType;

    private boolean isCancelled = false;

    public SmeltingStartEvent(Player player, ArrayList<ItemStack> itemList, SmeltingType smeltingType) {
        this.player = player;
        this.itemList = itemList;
        this.smeltingType = smeltingType;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    /**
     *
     * @param b If this is canceled, the plugin will refund items IF the SmeltingType is INVENTORY, else it will not
     */
    @Override
    public void setCancelled(boolean b) {
        this.isCancelled = b;
    }

    /**
     *
     * @return Gets the items that are going to be smelted, This may return invalid items
     */
    public ArrayList<ItemStack> getItemList() {
        return itemList;
    }

    /**
     *
     * @param itemList Sets the items that will be smelted
     */
    public void setItemList(ArrayList<ItemStack> itemList) {
        this.itemList = itemList;
    }

    /**
     *
     * @return Gets how the Smelting Process was started
     * ALL = All items in inventory
     * Hand = Item in Hand
     * Inventory = Items in a predefined inventory
     */
    public SmeltingType getSmeltingType() {
        return smeltingType;
    }


    /**
     *
     * @param item The Itemstack that is being checked to see if valid
     * @return True if the itemstack is smeltable, false otherwise
     */
    public boolean isValidItemStack(ItemStack item){
        return Utils.getSmeltedItemStack(item) != null;
    }

}
