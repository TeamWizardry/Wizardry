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
	public static SoundEvent EXPLOSION_BOOM;
	public static SoundEvent PROJECTILE_LAUNCH;
	public static SoundEvent BASS_BOOM;
	public static SoundEvent CHAINY_ZAP;
	public static SoundEvent CHORUS_GOOD;
	public static SoundEvent COLD_WIND;
	public static SoundEvent ELECTRIC_BLAST;
	public static SoundEvent ETHEREAL_PASS_BY;
	public static SoundEvent FAIRY;
	public static SoundEvent FIRE;
	public static SoundEvent FIREBALL;
	public static SoundEvent FLY;
	public static SoundEvent FROST_FORM;
	public static SoundEvent HEAL;
	public static SoundEvent LIGHTNING;
	public static SoundEvent SLOW_MOTION_IN;
	public static SoundEvent SLOW_MOTION_OUT;
	public static SoundEvent SMOKE_BLAST;
	public static SoundEvent TELEPORT;
	public static SoundEvent THUNDERBLAST;
	public static SoundEvent WIND;

	public static void init() {
		GLASS_BREAK = registerSound("glassbreak");
		FIZZING_LOOP = registerSound("fizzingloop");
		FRYING_SIZZLE = registerSound("firesizzleloop");
		HARP1 = registerSound("harp1");
		HARP2 = registerSound("harp2");
		BELL = registerSound("bell");
		BUBBLING = registerSound("bubbling");
		HALLOWED_SPIRIT = registerSound("hallowed_spirit_shriek");
		EXPLOSION_BOOM = registerSound("expl_boom");
		PROJECTILE_LAUNCH = registerSound("proj_launch");
		BASS_BOOM = registerSound("bass_boom");
		CHAINY_ZAP = registerSound("chainy_zap");
		CHORUS_GOOD = registerSound("chorus_good");
		COLD_WIND = registerSound("cold_wind");
		ELECTRIC_BLAST = registerSound("electric_blast");
		ETHEREAL_PASS_BY = registerSound("ethereal_pass_by");
		FAIRY = registerSound("fairy_1");
		FIRE = registerSound("fire");
		FIREBALL = registerSound("fireball");
		FLY = registerSound("fly");
		FROST_FORM = registerSound("frost_form");
		HEAL = registerSound("heal");
		LIGHTNING = registerSound("lightning");
		SLOW_MOTION_IN = registerSound("slow_motion_in");
		SLOW_MOTION_OUT = registerSound("slow_motion_out");
		SMOKE_BLAST = registerSound("smoke_blast");
		TELEPORT = registerSound("teleport");
		THUNDERBLAST = registerSound("thunder_blast");
		WIND = registerSound("wind");
	}

	private static SoundEvent registerSound(String soundName) {
		final ResourceLocation soundID = new ResourceLocation(Wizardry.MODID, soundName);
		return GameRegistry.register(new SoundEvent(soundID).setRegistryName(soundID));
	}
}
