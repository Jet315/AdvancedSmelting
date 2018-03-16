package me.jet315.smelting.utils;

import me.jet315.smelting.Core;
import me.jet315.smelting.smelt.SmeltableItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by Jet on 06/03/2018.
 */
public class Utils {

    /**
     *
     * @param itemStack The raw item
     * @return The smelted itemStack, returns null if invalid item
     */
    public static ItemStack getSmeltedItemStack(ItemStack itemStack) {
        if(itemStack == null) return null;
        Material rawMaterial = itemStack.getType();
        switch (rawMaterial) {
            case PORK: {
                return new ItemStack(Material.GRILLED_PORK, itemStack.getAmount());
            }
            case RAW_BEEF: {
                return new ItemStack(Material.COOKED_BEEF, itemStack.getAmount());
            }
            case RAW_CHICKEN: {
                return new ItemStack(Material.COOKED_CHICKEN, itemStack.getAmount());
            }
            case RAW_FISH: {
                if (itemStack.getDurability() >= 2) {
                    return null;
                }
                return new ItemStack(Material.COOKED_FISH, itemStack.getAmount(), itemStack.getDurability());
            }
            case MUTTON: {
                return new ItemStack(Material.COOKED_MUTTON, itemStack.getAmount());
            }
            case RABBIT: {
                return new ItemStack(Material.COOKED_RABBIT, itemStack.getAmount());
            }
            case POTATO_ITEM: {
                return new ItemStack(Material.BAKED_POTATO, itemStack.getAmount());
            }

            case IRON_ORE: {
                return new ItemStack(Material.IRON_INGOT, itemStack.getAmount());
            }
            case GOLD_ORE: {
                return new ItemStack(Material.GOLD_INGOT, itemStack.getAmount());
            }
            case SAND: {
                return new ItemStack(Material.GLASS, itemStack.getAmount());
            }
            case COBBLESTONE: {
                return new ItemStack(Material.STONE, itemStack.getAmount());
            }
            case CLAY_BALL: {
                return new ItemStack(Material.CLAY_BRICK, itemStack.getAmount());
            }
            case NETHERRACK: {
                return new ItemStack(Material.NETHER_BRICK_ITEM, itemStack.getAmount());
            }
            case CLAY: {
                return new ItemStack(Material.HARD_CLAY, itemStack.getAmount());
            }
            case SMOOTH_BRICK: {
                return new ItemStack(Material.SMOOTH_BRICK, itemStack.getAmount(), (short) 2);
            }
            case DIAMOND_ORE: {
                return new ItemStack(Material.DIAMOND, itemStack.getAmount());
            }
            case LAPIS_ORE: {
                return new ItemStack(Material.INK_SACK, itemStack.getAmount(), (short) 4);
            }
            case REDSTONE_ORE: {
                return new ItemStack(Material.REDSTONE, itemStack.getAmount());
            }
            case COAL_ORE: {
                return new ItemStack(Material.COAL, itemStack.getAmount());
            }
            case EMERALD_ORE: {
                return new ItemStack(Material.EMERALD, itemStack.getAmount());
            }
            case QUARTZ_ORE: {
                return new ItemStack(Material.QUARTZ, itemStack.getAmount());
            }
            case LOG: {
                return new ItemStack(Material.COAL, itemStack.getAmount(), (short) 1);
            }
            case LOG_2: {
                return new ItemStack(Material.COAL, itemStack.getAmount(), (short) 1);
            }
            case CACTUS: {
                return new ItemStack(Material.INK_SACK, itemStack.getAmount(), (short) 2);
            }
            case SPONGE: {
                if (itemStack.getDurability() == 0) {
                    return null;
                }

                return new ItemStack(Material.SPONGE, itemStack.getAmount());
            }
            case CHORUS_FRUIT: {
                return new ItemStack(Material.CHORUS_FRUIT_POPPED, itemStack.getAmount());
            }
            default:
                return null;
        }
    }

    /**
     *
     * @param items Each item in the inventory
     * @return Four Doubles, Money[0], Coal[1], EXP[2],time[3] in the format #.##
     */
    public static String[] calcInveotryCosts(ItemStack[] items){
        DecimalFormat df = new DecimalFormat("#.##");
        DecimalFormat timeDF = new DecimalFormat("#.#");
        String[] costs = new String[4];
        ArrayList<ItemStack> validItemsInInventory = new ArrayList<>();
        for(ItemStack item : items){
            if(item != null){
                if(getSmeltedItemStack(item) != null){
                    validItemsInInventory.add(item);
                }
            }
        }
        double moneyCost = 0;
        double coalCost = 0;
        double expGained = 0;
        double time = 0;
        for(ItemStack validItem : validItemsInInventory){
            SmeltableItem smeltableItem = Core.getInstance().getSmeltManager().getSmeltableItems().get(validItem.getType());
            moneyCost += smeltableItem.getCostOfMoneyToSmelt() * validItem.getAmount();
            coalCost += smeltableItem.getCostOfCoalToSmelt() * validItem.getAmount();
            expGained += smeltableItem.getExpToGive() * validItem.getAmount();
            time += smeltableItem.getTimeToSmeltItem() * validItem.getAmount();
        }
        costs[0] = df.format(moneyCost);
        costs[1] = df.format((int)(coalCost+0.999));
        costs[2] = df.format(expGained);
        costs[3] = timeDF.format(time/1000);


       // smeltPlayer.setCoalLeftOver(Double.valueOf(df.format(1-(coal-(int)coal))));

        return costs;
    }
}
