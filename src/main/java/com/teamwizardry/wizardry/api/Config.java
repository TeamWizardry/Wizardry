package com.teamwizardry.wizardry.api;

import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class Config {
    //World Gen
    public static int manaPoolRarity, particlePercentage;
    public static boolean developmentEnvironment = (Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");

    public static void initConfig(File configFile) {
        Configuration config = new Configuration(configFile);
        config.load();
        manaPoolRarity = config.get("World", "ManaPool", 75, "How rare the mana pool is in terms of 1 in X").getInt();
        particlePercentage = config.get("General", "particle-percentage", -1, "The lower the percentage, the less particles you'll see and they will die quicker. Set to -1 to ignore this setting and use the vanilla setting instead.").getInt();
        config.save();
    }

}
