package com.teamwizardry.wizardry.init;

import com.teamwizardry.wizardry.common.potion.*;
import net.minecraftforge.common.MinecraftForge;

/**
 * Created by Demoniaque.
 */
public class ModPotions {

	public static PotionNullGrav NULLIFY_GRAVITY;
	public static PotionNullMovement NULL_MOVEMENT;
	public static PotionSteroid STEROID;
	public static PotionPhase PHASE;
	public static PotionTimeSlow TIME_SLOW;
	public static PotionSlippery SLIPPERY;
	public static PotionLowGrav LOW_GRAVITY;
	public static PotionCrash CRASH;
	public static PotionZachCorruption ZACH_CORRUPTION;
	public static PotionSuffocate SUFFOCATE;
	public static PotionGrace GRACE;
	public static PotionBouncing BOUNCING;
	public static PotionTimeLock TIME_LOCK;

	public static void init() {
		NULLIFY_GRAVITY = new PotionNullGrav();
		STEROID = new PotionSteroid();
		PHASE = new PotionPhase();
		TIME_SLOW = new PotionTimeSlow();
		NULL_MOVEMENT = new PotionNullMovement();
		SLIPPERY = new PotionSlippery();
		LOW_GRAVITY = new PotionLowGrav();
		CRASH = new PotionCrash();
		ZACH_CORRUPTION = new PotionZachCorruption();
		SUFFOCATE = new PotionSuffocate();
		BOUNCING = new PotionBouncing();
		TIME_LOCK = new PotionTimeLock();
		MinecraftForge.EVENT_BUS.register(GRACE = new PotionGrace());
	}
}
