package com.teamwizardry.wizardry.api.save;

import com.teamwizardry.wizardry.api.spell.event.SpellCastEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created by Saad on 6/19/2016.
 */
public class WizardHandler {

    public static final WizardHandler INSTANCE = new WizardHandler();
    private int tickCooldown = 0;

    private WizardHandler() {
        MinecraftForge.EVENT_BUS.register(this);

    }

    public static BarData getEntityData(EntityPlayer entity) {
        BarData ret = new BarData();
        ret.burnoutAmount = WizardryDataHandler.getBurnoutAmount(entity);
        ret.burnoutMax = WizardryDataHandler.getBurnoutMax(entity);
        ret.manaAmount = WizardryDataHandler.getMana(entity);
        ret.manaMax = WizardryDataHandler.getManaMax(entity);

        return ret;
    }

    @SubscribeEvent
    public void playerUpdate(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntity() instanceof EntityPlayer) {
            if (tickCooldown >= 5) {
                tickCooldown = 0;

                EntityPlayer player = (EntityPlayer) event.getEntity();
                BarData provider = getEntityData(player);
                if (provider.manaAmount < provider.manaMax)
                    WizardryDataHandler.setMana(player, provider.manaAmount + 1);
                if (provider.burnoutAmount > 0)
                    WizardryDataHandler.setBurnoutAmount(player, provider.burnoutAmount - 1);

            } else tickCooldown++;
        }
    }

    @SubscribeEvent
    public void spellCast(SpellCastEvent event) {
        // TODO
    }
}
