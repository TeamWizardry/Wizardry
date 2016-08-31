package com.teamwizardry.wizardry.api.spell;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import com.teamwizardry.wizardry.api.capability.WizardryCapabilityProvider;
import com.teamwizardry.wizardry.api.spell.event.SpellEvent.SpellCastEvent;
import com.teamwizardry.wizardry.api.trackerobject.SpellStack;

public class SpellHandler {
    public static final SpellHandler INSTANCE = new SpellHandler();

    private SpellHandler() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onModuleCast(SpellCastEvent event) {
        SpellStack stack = event.stack;
        EntityPlayer player = event.player;
        
       WizardryCapabilityProvider.get(player).incrementBloodLevel(stack.getAffinity(), player); 
    }
}