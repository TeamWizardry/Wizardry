package com.teamwizardry.wizardry.init;

import com.teamwizardry.wizardry.Wizardry;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.ArrayList;

/**
 * Created by Saad on 6/29/2016.
 */
public class ModSounds {

    public static ArrayList<SoundEvent> HALLOWED_SPIRIT_SOUNDS = new ArrayList<>();

    public static SoundEvent GLASS_BREAK;
    public static SoundEvent FIZZING_LOOP;
    public static SoundEvent FRYING_SIZZLE;
    public static SoundEvent BUBBLING;
    public static SoundEvent HARP1;
    public static SoundEvent HARP2;
    public static SoundEvent BELL;
    public static SoundEvent HALLOWED_SPIRIT_1;
    public static SoundEvent HALLOWED_SPIRIT_2;
    public static SoundEvent HALLOWED_SPIRIT_3;
    public static SoundEvent HALLOWED_SPIRIT_4;
    public static SoundEvent HALLOWED_SPIRIT_5;
    public static SoundEvent HALLOWED_SPIRIT_6;

    public static void init() {
        GLASS_BREAK = registerSound("glassbreak");
        FIZZING_LOOP = registerSound("fizzingloop");
        FRYING_SIZZLE = registerSound("firesizzleloop");
        HARP1 = registerSound("harp1");
        HARP2 = registerSound("harp2");
        BELL = registerSound("bell");
        BUBBLING = registerSound("bubbling");

        HALLOWED_SPIRIT_1 = registerSound("hallowed_spirit_shriek_1");
        HALLOWED_SPIRIT_2 = registerSound("hallowed_spirit_shriek_2");
        HALLOWED_SPIRIT_3 = registerSound("hallowed_spirit_shriek_3");
        HALLOWED_SPIRIT_4 = registerSound("hallowed_spirit_shriek_4");
        HALLOWED_SPIRIT_5 = registerSound("hallowed_spirit_shriek_5");
        HALLOWED_SPIRIT_6 = registerSound("hallowed_spirit_shriek_6");

        HALLOWED_SPIRIT_SOUNDS.add(HALLOWED_SPIRIT_1);
        HALLOWED_SPIRIT_SOUNDS.add(HALLOWED_SPIRIT_2);
        HALLOWED_SPIRIT_SOUNDS.add(HALLOWED_SPIRIT_3);
        HALLOWED_SPIRIT_SOUNDS.add(HALLOWED_SPIRIT_4);
        HALLOWED_SPIRIT_SOUNDS.add(HALLOWED_SPIRIT_5);
        HALLOWED_SPIRIT_SOUNDS.add(HALLOWED_SPIRIT_6);
    }

    private static SoundEvent registerSound(String soundName) {
        final ResourceLocation soundID = new ResourceLocation(Wizardry.MODID, soundName);
        return GameRegistry.register(new SoundEvent(soundID).setRegistryName(soundID));
    }
}
