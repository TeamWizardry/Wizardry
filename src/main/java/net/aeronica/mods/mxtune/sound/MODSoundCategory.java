package net.aeronica.mods.mxtune.sound;

import java.util.Map;

import com.google.common.collect.Maps;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/*
 * MODSoundCategory
 *
 * Add a new CONSTANT and reference name to net.minecraft.util.SoundCategory
 *
 * This allows the display of a volume control in the "Music & Sound Options" dialog.
 * Unfortunately the GuiScreenOptionsSounds dialog does not auto size
 * properly and move the Done button lower on the screen.
 *
 * To initialize the class create an instance during FMLPreInitializationEvent in
 * the file with the @Mod annotation or your common proxy class.
 *
 * Usage example: static final SoundCategory SC_MXTUNE = MODSoundCategory.add("MXTUNE");
 *
 * The language file key is "soundCategory.mxtune"
 * The game settings "options.txt" key is "soundCategory_mxtune"
 *
 * To use the MXTUNE enum constant in code it must be referenced by name because
 * SoundCategory.MXTUNE does not exist at compile time.
 *   e.g. SoundCategory.getByName("mxtune");
 *
 * @author Paul Boese aka Aeronica
 *
 */
public class MODSoundCategory
{

    private static final String SRG_soundLevels = "field_186714_aM";
    private static final String SRG_SOUND_CATEGORIES = "field_187961_k";
    private static Map<SoundCategory, Float> soundLevels;
    private static MODSoundCategory instance = new MODSoundCategory();

    private MODSoundCategory() {}

    public static MODSoundCategory getInstance() {return instance;}

    /**
     * The "name" should be your MODID or MODID_name if your mod adds more
     * than one SoundCategory.
     *
     * @param name
     * @return a unique SoundCategory
     * @throws Error fatal error if name is not unique
     */
    public static SoundCategory add(String name)
    {
        Map<String, SoundCategory> SOUND_CATEGORIES;

        String constantName;
        String referenceName;
        SoundCategory soundCategory;
        constantName = name.toUpperCase().replace(" ", "");
        referenceName = constantName.toLowerCase();
        soundCategory =  EnumHelper.addEnum(SoundCategory.class , constantName, new Class[]{String.class}, referenceName);
        SOUND_CATEGORIES = ObfuscationReflectionHelper.getPrivateValue(SoundCategory.class, SoundCategory.VOICE ,"SOUND_CATEGORIES", SRG_SOUND_CATEGORIES);
        if (SOUND_CATEGORIES.containsKey(referenceName))
            throw new Error("Clash in Sound Category name pools! Cannot insert " + constantName);
        SOUND_CATEGORIES.put(referenceName, soundCategory);
        if (FMLLaunchHandler.side() == Side.CLIENT) setSoundLevels();

        return soundCategory;
    }

    /** Game sound level options settings only exist on the client side */
    @SideOnly(Side.CLIENT)
    private static void setSoundLevels()
    {
        /** SoundCategory now contains 'name' sound category so build a new map */
        soundLevels = Maps.newEnumMap(SoundCategory.class);
        /** Replace the map in the GameSettings.class */
        ObfuscationReflectionHelper.setPrivateValue(GameSettings.class, Minecraft.getMinecraft().gameSettings, soundLevels, "soundLevels", SRG_soundLevels);
    }

}