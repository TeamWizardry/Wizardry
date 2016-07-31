package com.teamwizardry.wizardry.api.save;

import com.teamwizardry.wizardry.Wizardry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

//copy pasta from wiresegal's class in github.com/eladkay/quaritum
//AnimusHelper.Network
public class WizardryDataHandler {
    public enum EnumBloodType {
        NULL,
        AQUA,
        PYRO,
        AERO,
        TERRA;
    }
    private static final String KEY_WIZARDRY_NETWORK = Wizardry.MODID + "-Network";
    private static final String KEY_HAS_BLOOD = Wizardry.MODID + "-Blood";
    private static final String KEY_BLOODTYPE = Wizardry.MODID + "-BloodType";
    private static final String KEY_BURNOUT_MAX = Wizardry.MODID + "-BurnoutMax";
    private static final String TAG_LAST_KNOWN_USERNAME = "lastUsername";

    //blood
    public static void setHasBlood(EntityPlayer player, boolean blood) {
        setHasBlood(player.getUniqueID(), blood);
    }

    public static void setHasBlood(UUID uuid, boolean blood) {
        getPersistentCompound(uuid).setBoolean(KEY_HAS_BLOOD, blood);
        getSaveData().markDirty();

    }

    public static boolean getHasBlood(EntityPlayer uuid) {
        return getBooleanSafe(getPersistentCompound(uuid.getUniqueID()), KEY_HAS_BLOOD, true);
    }

    public static boolean getHasBlood(UUID uuid) {
        return getBooleanSafe(getPersistentCompound(uuid), KEY_HAS_BLOOD, false);
    }

    //bloodtype - mine is A+
    public static void setBloodType(EntityPlayer player, EnumBloodType blood) {
        setBloodType(player.getUniqueID(), blood);
    }

    public static void setBloodType(UUID uuid, EnumBloodType blood) {
        getPersistentCompound(uuid).setInteger(KEY_BLOODTYPE, blood.ordinal());
        getSaveData().markDirty();

    }

    public static EnumBloodType getBloodType(EntityPlayer uuid) {
        return EnumBloodType.values()[getIntegerSafe(getPersistentCompound(uuid.getUniqueID()), KEY_BLOODTYPE, 0)];
    }

    public static EnumBloodType getBloodType(UUID uuid) {
        return EnumBloodType.values()[getIntegerSafe(getPersistentCompound(uuid), KEY_BLOODTYPE, 0)];
    }

    //int burnoutMax = 100,
    public static void setBurnoutMax(EntityPlayer player, int burnoutMax) {
        setBurnoutMax(player.getUniqueID(), burnoutMax);
    }

    public static void setBurnoutMax(UUID uuid, int burnoutMax) {
        getPersistentCompound(uuid).setInteger(KEY_BLOODTYPE, burnoutMax);
        getSaveData().markDirty();

    }

    public static int getBurnoutMax(EntityPlayer uuid) {
        return getIntegerSafe(getPersistentCompound(uuid.getUniqueID()), KEY_BURNOUT_MAX, 100);
    }

    public static int getBurnoutMax(UUID uuid) {
        return getIntegerSafe(getPersistentCompound(uuid), KEY_BURNOUT_MAX, 100);
    }
    //manaMax = 100, burnoutAmount = burnoutMax, manaAmount = 0;

    public static void updatePlayerName(EntityPlayer player) {
        NBTTagCompound compound = getPersistentCompound(player.getUniqueID());
        if (!player.getDisplayNameString().equals(getStringSafe(compound, TAG_LAST_KNOWN_USERNAME, null))) {
            compound.setString(TAG_LAST_KNOWN_USERNAME, player.getDisplayNameString());
            getSaveData().markDirty();
        }
    }

    public static String getLastKnownUsername(UUID uuid) {
        return getStringSafe(getPersistentCompound(uuid), TAG_LAST_KNOWN_USERNAME, null);
    }


    private static boolean getBooleanSafe(NBTTagCompound compound, String tag, boolean fallback) {
        if (!compound.hasKey(tag)) return fallback;
        return compound.getBoolean(tag);
    }

    private static int getIntegerSafe(NBTTagCompound compound, String tag, int fallback) {
        if (!compound.hasKey(tag, 3)) return fallback;
        return compound.getInteger(tag);
    }

    private static String getStringSafe(NBTTagCompound compound, String tag, String fallback) {
        if (!compound.hasKey(tag, 8)) return fallback;
        return compound.getString(tag);
    }

    @Nonnull
    private static NBTTagCompound getPersistentCompound(UUID uuid) {
        if (uuid == null) return new NBTTagCompound();

        WizardrySaveData saveData = getSaveData();

        if (!saveData.animusData.containsKey(uuid))
            saveData.animusData.put(uuid, new NBTTagCompound());

        return saveData.animusData.get(uuid);
    }

    @Nonnull
    private static WizardrySaveData getSaveData() {
        World world = DimensionManager.getWorld(0);
        if (world == null || world.getMapStorage() == null)
            return new WizardrySaveData();

        WizardrySaveData saveData = (WizardrySaveData) world.getMapStorage().getOrLoadData(WizardrySaveData.class, KEY_WIZARDRY_NETWORK);

        if (saveData == null) {
            saveData = new WizardrySaveData();
            world.getMapStorage().setData(KEY_WIZARDRY_NETWORK, saveData);
        }

        return saveData;
    }

    public static class WizardrySaveData extends WorldSavedData {

        private Map<UUID, NBTTagCompound> animusData = new HashMap<>();

        public WizardrySaveData(String id) {
            super(id);
        }

        public WizardrySaveData() {
            super(KEY_WIZARDRY_NETWORK);
        }

        @Override
        @Nonnull
        public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
            for (UUID key : animusData.keySet())
                compound.setTag(key.toString(), animusData.get(key));
            return compound;
        }

        @Override
        public void readFromNBT(@Nonnull NBTTagCompound compound) {
            for (String key : compound.getKeySet()) {
                animusData.put(UUID.fromString(key), compound.getCompoundTag(key));
            }
        }
    }

    public static class EventHandler {
        public EventHandler() {
            MinecraftForge.EVENT_BUS.register(this);
        }

        @SubscribeEvent
        public void onEntityTick(LivingEvent.LivingUpdateEvent e) {
            if (e.getEntityLiving() instanceof EntityPlayer) {
                WizardryDataHandler.updatePlayerName((EntityPlayer) e.getEntityLiving());
            }
        }
    }
}
