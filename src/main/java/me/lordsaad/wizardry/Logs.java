package me.lordsaad.wizardry;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Logs {
    public static long prevTicks = -1;
    public static boolean debugMode = Config.developmentEnvironment;
    public static boolean doLogging = false;
    private static Logs instance = null;
    private Logger logger;

    private Logs() {
        logger = LogManager.getLogger("NodeNet");
        instance = this;
    }

    private static Logs getInstance() {
        if (instance == null) {
            instance = new Logs();
        }
        return instance;
    }

    public static void error(String message, Object... args) {
        getInstance().logger.log(Level.ERROR, String.format(message, args));
    }

    public static void error(Exception e, String message, Object... args) {
        getInstance().logger.log(Level.ERROR, String.format(message, args));
        e.printStackTrace();
    }

    public static void warn(String message, Object... args) {
        getInstance().logger.log(Level.WARN, String.format(message, args));
    }

    public static void log(String message, Object... args) {
        getInstance().logger.log(Level.INFO, String.format(message, args));
    }

    public static void debug(String message, Object... args) {
        if (debugMode) {
            getInstance().logger.log(Level.INFO, String.format(message, args));
        }
    }

    public static void log(World world, TileEntity te, String message, Object... args) {
        if (doLogging) {
            long ticks = world.getTotalWorldTime();
            if (ticks != prevTicks) {
                prevTicks = ticks;
                getInstance().logger.log(Level.INFO, "=== Time " + ticks + " ===");
            }
            String id = te.getPos().getX() + "," + te.getPos().getY() + "," + te.getPos().getZ() + ": ";
            getInstance().logger.log(Level.INFO, id + String.format(message, args));
        }
    }

    public static void message(EntityPlayer player, String message, Object... args) {
        player.addChatComponentMessage(new TextComponentString(String.format(message, args)));
    }

    public static void warn(EntityPlayer player, String message, Object... args) {
        player.addChatComponentMessage(new TextComponentString(String.format(message, args)).setStyle(new Style().setColor(TextFormatting.RED)));
    }
}