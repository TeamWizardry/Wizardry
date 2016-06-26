package com.teamwizardry.wizardry.multiblock;

import net.minecraftforge.common.MinecraftForge;

import com.teamwizardry.wizardry.Config;

public enum Structures {
	INSTANCE;
	
	public static Structure craftingAltar;
	
	public static void reload() {
		craftingAltar = new Structure("crafting_altar");
	}
	
	private Structures() {}
}
