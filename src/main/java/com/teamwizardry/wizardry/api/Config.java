package com.teamwizardry.wizardry.api;

import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class Config {
    //World Gen
    public static int manaPoolRarity, particlePercentage;
    public static boolean developmentEnvironment = (Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");

    public static void initConfig() {
        Configuration config = new Configuration(new File("config/Wizardry/Config.cfg"));
        config.load();
        manaPoolRarity = config.get("World", "ManaPool", 75, "How rare the mana pool is in terms of 1 in X").getInt();
        particlePercentage = config.get("General", "reduce-particles", 1, "Any particle effects will be divided by this number. Higher numbers will reduce the particles").getInt();
        config.save();
    }
}
