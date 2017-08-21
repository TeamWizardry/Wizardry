package com.teamwizardry.wizardry.init;

import com.teamwizardry.wizardry.common.potion.*;
import net.minecraft.potion.Potion;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * Created by LordSaad.
 */
public class ModPotions {

	public static PotionNullGrav NULLIFY_GRAVITY;
	public static PotionNullMovement NULL_MOVEMENT;
	public static PotionSteroid STEROID;
	public static PotionPhase PHASE;
	public static PotionTimeSlow TIME_SLOW;
	public static PotionSlippery SLIPPERY;
	public static PotionLowGrav LOW_GRAVITY;
	public static PotionVanish VANISH;
	public static PotionCrash CRASH;

	@SubscribeEvent
	public static void register(RegistryEvent.Register<Potion> evt) {
		IForgeRegistry<Potion> r = evt.getRegistry();

		r.register(NULLIFY_GRAVITY = new PotionNullGrav());
		r.register(STEROID = new PotionSteroid());
		r.register(PHASE = new PotionPhase());
		r.register(TIME_SLOW = new PotionTimeSlow());
		r.register(NULL_MOVEMENT = new PotionNullMovement());
		r.register(SLIPPERY = new PotionSlippery());
		r.register(LOW_GRAVITY = new PotionLowGrav());
		r.register(CRASH = new PotionCrash());
		r.register(VANISH = new PotionVanish());
	}
}
