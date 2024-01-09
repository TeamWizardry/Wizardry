package com.teamwizardry.wizardry.api.sound;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreenOptionsSounds;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.EnumMap;
import java.util.Map;

public class SoundCategoryRegistry {
    private static final String SRG_SOUND_CATEGORIES = "field_187961_k";
    private static final String SRG_soundLevels = "field_186714_aM";

    public static SoundCategory register(String name) {
        final String constantName = name.toUpperCase().replace(" ", "");
        final String referenceName = constantName.toLowerCase();

        final Class<?>[] params = new Class<?>[] { String.class };
        final SoundCategory soundCategory = EnumHelper.addEnum(SoundCategory.class, constantName, params, referenceName);

        updateInternalCache(soundCategory);

        if (FMLLaunchHandler.side() == Side.CLIENT) {
            updateSoundLevels();
        }

        return soundCategory;
    }

    private static void updateInternalCache(SoundCategory soundCategory) {
        try {
            final Field field = getField(SoundCategory.class, "SOUND_CATEGORIES", SRG_SOUND_CATEGORIES);
            @SuppressWarnings("unchecked")
            final Map<String, SoundCategory> categories = (Map<String, SoundCategory>) field.get(null);
            categories.put(soundCategory.getName(), soundCategory);
        } catch (@Nonnull final Throwable ignore) {
        }
    }

    @SideOnly(Side.CLIENT)
    private static void updateSoundLevels() {
        try {
            final Field field = getField(GameSettings.class, "soundLevels", SRG_soundLevels);
            EnumMap<SoundCategory, Float> soundLevels = new EnumMap<>(SoundCategory.class);
            field.set(Minecraft.getMinecraft().gameSettings, soundLevels);
        } catch (@Nonnull final Throwable ignore) {
        }
    }

    @Nonnull
    private static Field getField(Class<?> classObject, String name, String srgName) throws NoSuchFieldException, SecurityException
    {
        Preconditions.checkNotNull((Class<?>) classObject);
        Preconditions.checkArgument(StringUtils.isNotEmpty(name), "Field name cannot be empty");

        final String nameToFind = FMLLaunchHandler.isDeobfuscatedEnvironment() ? name : MoreObjects.firstNonNull(srgName, name);

        final Field field = classObject.getDeclaredField(nameToFind);
        field.setAccessible(true);
        return field;
    }
}
