package com.teamwizardry.wizardry.init;

import com.teamwizardry.wizardry.Wizardry;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * Created by Saad on 6/29/2016.
 */
@Mod.EventBusSubscriber
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

	@SubscribeEvent
	public static void register(RegistryEvent.Register<SoundEvent> evt) {
		IForgeRegistry<SoundEvent> r = evt.getRegistry();

		r.register(GLASS_BREAK = registerSound("glassbreak"));
		r.register(FIZZING_LOOP = registerSound("fizzingloop"));
		r.register(FRYING_SIZZLE = registerSound("firesizzleloop"));
		r.register(HARP1 = registerSound("harp1"));
		r.register(HARP2 = registerSound("harp2"));
		r.register(BELL = registerSound("bell"));
		r.register(BUBBLING = registerSound("bubbling"));
		r.register(HALLOWED_SPIRIT = registerSound("hallowed_spirit_shriek"));
		r.register(EXPLOSION_BOOM = registerSound("expl_boom"));
		r.register(PROJECTILE_LAUNCH = registerSound("proj_launch"));
		r.register(BASS_BOOM = registerSound("bass_boom"));
		r.register(CHAINY_ZAP = registerSound("chainy_zap"));
		r.register(CHORUS_GOOD = registerSound("chorus_good"));
		r.register(COLD_WIND = registerSound("cold_wind"));
		r.register(ELECTRIC_BLAST = registerSound("electric_blast"));
		r.register(ETHEREAL_PASS_BY = registerSound("ethereal_pass_by"));
		r.register(FAIRY = registerSound("fairy_1"));
		r.register(FIRE = registerSound("fire"));
		r.register(FIREBALL = registerSound("fireball"));
		r.register(FLY = registerSound("fly"));
		r.register(FROST_FORM = registerSound("frost_form"));
		r.register(HEAL = registerSound("heal"));
		r.register(LIGHTNING = registerSound("lightning"));
		r.register(SLOW_MOTION_IN = registerSound("slow_motion_in"));
		r.register(SLOW_MOTION_OUT = registerSound("slow_motion_out"));
		r.register(SMOKE_BLAST = registerSound("smoke_blast"));
		r.register(TELEPORT = registerSound("teleport"));
		r.register(THUNDERBLAST = registerSound("thunder_blast"));
		r.register(WIND = registerSound("wind"));
	}

	private static SoundEvent registerSound(String soundName) {
		final ResourceLocation soundID = new ResourceLocation(Wizardry.MODID, soundName);
		return new SoundEvent(soundID).setRegistryName(soundID);
	}
}
