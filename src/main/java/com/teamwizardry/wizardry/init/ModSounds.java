package com.teamwizardry.wizardry.init;

import com.teamwizardry.librarianlib.features.base.ModSoundEvent;

/**
 * Created by Demoniaque on 6/29/2016.
 */
public class ModSounds {
	public static ModSoundEvent GLASS_BREAK;
	public static ModSoundEvent FIZZING_LOOP;
	public static ModSoundEvent FRYING_SIZZLE;
	public static ModSoundEvent BUBBLING;
	public static ModSoundEvent HARP1;
	public static ModSoundEvent HARP2;
	public static ModSoundEvent BELL;
	public static ModSoundEvent HALLOWED_SPIRIT;
	public static ModSoundEvent EXPLOSION_BOOM;
	public static ModSoundEvent PROJECTILE_LAUNCH;
	public static ModSoundEvent BASS_BOOM;
	public static ModSoundEvent CHAINY_ZAP;
	public static ModSoundEvent CHORUS_GOOD;
	public static ModSoundEvent COLD_WIND;
	public static ModSoundEvent ELECTRIC_BLAST;
	public static ModSoundEvent ETHEREAL_PASS_BY;
	public static ModSoundEvent FAIRY;
	public static ModSoundEvent FIRE;
	public static ModSoundEvent FIREBALL;
	public static ModSoundEvent FLY;
	public static ModSoundEvent FROST_FORM;
	public static ModSoundEvent HEAL;
	public static ModSoundEvent LIGHTNING;
	public static ModSoundEvent SLOW_MOTION_IN;
	public static ModSoundEvent SLOW_MOTION_OUT;
	public static ModSoundEvent SMOKE_BLAST;
	public static ModSoundEvent TELEPORT;
	public static ModSoundEvent THUNDERBLAST;
	public static ModSoundEvent WIND;
	public static ModSoundEvent ZAP;
	public static ModSoundEvent ELECTRIC_WHITE_NOISE;

	public static void init() {
		GLASS_BREAK = new ModSoundEvent("glassbreak");
		FIZZING_LOOP = new ModSoundEvent("fizzingloop");
		FRYING_SIZZLE = new ModSoundEvent("firesizzleloop");
		HARP1 = new ModSoundEvent("harp1");
		HARP2 = new ModSoundEvent("harp2");
		BELL = new ModSoundEvent("bell");
		BUBBLING = new ModSoundEvent("bubbling");
		HALLOWED_SPIRIT = new ModSoundEvent("hallowed_spirit_shriek");
		EXPLOSION_BOOM = new ModSoundEvent("expl_boom");
		PROJECTILE_LAUNCH = new ModSoundEvent("proj_launch");
		BASS_BOOM = new ModSoundEvent("bass_boom");
		CHAINY_ZAP = new ModSoundEvent("chainy_zap");
		CHORUS_GOOD = new ModSoundEvent("chorus_good");
		COLD_WIND = new ModSoundEvent("cold_wind");
		ELECTRIC_BLAST = new ModSoundEvent("electric_blast");
		ETHEREAL_PASS_BY = new ModSoundEvent("ethereal_pass_by");
		FAIRY = new ModSoundEvent("fairy_1");
		FIRE = new ModSoundEvent("fire");
		FIREBALL = new ModSoundEvent("fireball");
		FLY = new ModSoundEvent("fly");
		FROST_FORM = new ModSoundEvent("frost_form");
		HEAL = new ModSoundEvent("heal");
		LIGHTNING = new ModSoundEvent("lightning");
		SLOW_MOTION_IN = new ModSoundEvent("slow_motion_in");
		SLOW_MOTION_OUT = new ModSoundEvent("slow_motion_out");
		SMOKE_BLAST = new ModSoundEvent("smoke_blast");
		TELEPORT = new ModSoundEvent("teleport");
		THUNDERBLAST = new ModSoundEvent("thunder_blast");
		WIND = new ModSoundEvent("wind");
		ZAP = new ModSoundEvent("zap");
		ELECTRIC_WHITE_NOISE = new ModSoundEvent("electric_white_noise");
	}

}
