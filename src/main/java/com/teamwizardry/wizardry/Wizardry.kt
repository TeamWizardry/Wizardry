package com.teamwizardry.wizardry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.teamwizardry.wizardry.common.init.ModBlocks;
import com.teamwizardry.wizardry.common.init.ModFluids;
import com.teamwizardry.wizardry.common.init.ModItems;
import com.teamwizardry.wizardry.common.init.ModPatterns;
import com.teamwizardry.wizardry.common.init.ModSounds;
import com.teamwizardry.wizardry.common.init.ModTags;
import com.teamwizardry.wizardry.proxy.IProxy;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

public class Wizardry implements ModInitializer {

    public static final String MODID = "wizardry";
    public static final Logger LOGGER = LogManager.getLogger(Wizardry.class);
    
    public static IProxy PROXY;
    public static Wizardry INSTANCE;
    
    @Override
    public void onInitialize() {
        INSTANCE = this;
        
        ModTags.init();
        
        ModFluids.init();
        ModItems.init();
        ModBlocks.init();
        
        ModSounds.init();
        
        ModPatterns.init();
    }
    
    public final Logger makeLogger(Class<?> cls) { return LogManager.getLogger(cls); }

    public static final Identifier getId(String path) { return new Identifier(Wizardry.MODID, path); }
}
