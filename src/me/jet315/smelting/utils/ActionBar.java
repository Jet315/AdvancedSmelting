package me.jet315.smelting.utils;

import me.jet315.smelting.Core;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ActionBar {

    /**
     * Code from https://github.com/ConnorLinfoot/ActionBarAPI/blob/master/src/main/java/com/connorlinfoot/actionbarapi/ActionBarAPI.java
     */

    public static String nmsver = "v1_12_";
    public static boolean useOld = false;

    public static void sendActionBarPercentage(Player player, int value) {
        if (!player.isOnline()) {
            return; // Player may have logged out
        }
        int numberOfGreenBars = (int) ((value+0.5)/10);
        int numberOfRedBars = 10-numberOfGreenBars;


        String message = Core.getInstance().getProperties().getMessages().getActionBarMessage().replaceAll("%GREENBARS%",getBars(numberOfGreenBars,ChatColor.GREEN) +ChatColor.RED).replaceAll("%REDBARS%",getBars(numberOfRedBars,ChatColor.RED)).replaceAll("%PERCENTAGE%",String.valueOf(value));
        if (nmsver.startsWith("v1_12_")) {
            sendActionBarPost112(player, message);
        } else {
            sendActionBarPre112(player, message);
        }
    }

    private static void sendActionBarPost112(Player player, String message) {
        if (!player.isOnline()) {
            return; // Player may have logged out
        }

        try {
            Class<?> craftPlayerClass = Class.forName("org.bukkit.craftbukkit." + nmsver + ".entity.CraftPlayer");
            Object craftPlayer = craftPlayerClass.cast(player);
            Object ppoc;
            Class<?> c4 = Class.forName("net.minecraft.server." + nmsver + ".PacketPlayOutChat");
            Class<?> c5 = Class.forName("net.minecraft.server." + nmsver + ".Packet");
            Class<?> c2 = Class.forName("net.minecraft.server." + nmsver + ".ChatComponentText");
            Class<?> c3 = Class.forName("net.minecraft.server." + nmsver + ".IChatBaseComponent");
            Class<?> chatMessageTypeClass = Class.forName("net.minecraft.server." + nmsver + ".ChatMessageType");
            Object[] chatMessageTypes = chatMessageTypeClass.getEnumConstants();
            Object chatMessageType = null;
            for (Object obj : chatMessageTypes) {
                if (obj.toString().equals("GAME_INFO")) {
                    chatMessageType = obj;
                }
            }
            Object o = c2.getConstructor(new Class<?>[]{String.class}).newInstance(message);
            ppoc = c4.getConstructor(new Class<?>[]{c3, chatMessageTypeClass}).newInstance(o, chatMessageType);
            Method m1 = craftPlayerClass.getDeclaredMethod("getHandle");
            Object h = m1.invoke(craftPlayer);
            Field f1 = h.getClass().getDeclaredField("playerConnection");
            Object pc = f1.get(h);
            Method m5 = pc.getClass().getDeclaredMethod("sendPacket", c5);
            m5.invoke(pc, ppoc);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void sendActionBarPre112(Player player, String message) {
        if (!player.isOnline()) {
            return; // Player may have logged out
        }

        try {
            Class<?> craftPlayerClass = Class.forName("org.bukkit.craftbukkit." + nmsver + ".entity.CraftPlayer");
            Object craftPlayer = craftPlayerClass.cast(player);
            Object ppoc;
            Class<?> c4 = Class.forName("net.minecraft.server." + nmsver + ".PacketPlayOutChat");
            Class<?> c5 = Class.forName("net.minecraft.server." + nmsver + ".Packet");
            if (useOld) {
                Class<?> c2 = Class.forName("net.minecraft.server." + nmsver + ".ChatSerializer");
                Class<?> c3 = Class.forName("net.minecraft.server." + nmsver + ".IChatBaseComponent");
                Method m3 = c2.getDeclaredMethod("a", String.class);
                Object cbc = c3.cast(m3.invoke(c2, "{\"text\": \"" + message + "\"}"));
                ppoc = c4.getConstructor(new Class<?>[]{c3, byte.class}).newInstance(cbc, (byte) 2);
            } else {
                Class<?> c2 = Class.forName("net.minecraft.server." + nmsver + ".ChatComponentText");
                Class<?> c3 = Class.forName("net.minecraft.server." + nmsver + ".IChatBaseComponent");
                Object o = c2.getConstructor(new Class<?>[]{String.class}).newInstance(message);
                ppoc = c4.getConstructor(new Class<?>[]{c3, byte.class}).newInstance(o, (byte) 2);
            }
            Method m1 = craftPlayerClass.getDeclaredMethod("getHandle");
            Object h = m1.invoke(craftPlayer);
            Field f1 = h.getClass().getDeclaredField("playerConnection");
            Object pc = f1.get(h);
            Method m5 = pc.getClass().getDeclaredMethod("sendPacket", c5);
            m5.invoke(pc, ppoc);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static String getBars(int numberOfBars, ChatColor color){
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < numberOfBars;i++){
            stringBuilder.append(color + "▉");
        }
        return stringBuilder.toString();
    }
}
