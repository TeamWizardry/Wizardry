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
    public static SoundEvent FRYING_SIZZLE;
    public static SoundEvent BUBBLING;
    public static SoundEvent HARP1;
    public static SoundEvent HARP2;
    public static SoundEvent BELL;
    public static SoundEvent HALLOWED_SPIRIT;

    public static void init() {
        GLASS_BREAK = registerSound("glassbreak");
        FIZZING_LOOP = registerSound("fizzingloop");
        FRYING_SIZZLE = registerSound("firesizzleloop");
        HARP1 = registerSound("harp1");
        HARP2 = registerSound("harp2");
        BELL = registerSound("bell");
        BUBBLING = registerSound("bubbling");
        HALLOWED_SPIRIT = registerSound("hallowed_spirit_shriek");
    }

    private static SoundEvent registerSound(String soundName) {
        final ResourceLocation soundID = new ResourceLocation(Wizardry.MODID, soundName);
        return GameRegistry.register(new SoundEvent(soundID).setRegistryName(soundID));
    }
}
