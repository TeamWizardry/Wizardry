package com.teamwizardry.wizardry.init;

import com.teamwizardry.wizardry.Wizardry;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * Created by Saad on 6/29/2016.
 */
public class ModSounds {

    public static SoundEvent GLASS_BREAK;
    public static SoundEvent FIZZING_LOOP;

    public static void init() {
        GLASS_BREAK = registerSound("glassbreak");
        FIZZING_LOOP = registerSound("fizzingloop");
    }

    private static SoundEvent registerSound(String soundName) {
        final ResourceLocation soundID = new ResourceLocation(Wizardry.MODID, soundName);
        return GameRegistry.register(new SoundEvent(soundID).setRegistryName(soundID));
    }
}
