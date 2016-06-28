package com.teamwizardry.wizardry.api.gui;

import com.teamwizardry.wizardry.api.Constants;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
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

        CapabilityManager.INSTANCE.register(IWizardData.class, new Capability.IStorage<IWizardData>() {
            @Override
            public NBTBase writeNBT(Capability<IWizardData> capability, IWizardData instance, EnumFacing side) {
                return null;
            }

            @Override
            public void readNBT(Capability<IWizardData> capability, IWizardData instance, EnumFacing side, NBTBase nbt) {
            }
        }, () -> {
            throw new UnsupportedOperationException();
        });
    }

    public static IWizardData.BarData getEntityData(Entity entity) {
        return (IWizardData.BarData) entity.getCapability(Constants.Misc.BAR_HANDLER_CAPABILITY, null);
    }

    @SubscribeEvent
    public void attachCapability(AttachCapabilitiesEvent.Entity event) {
        if (event.getEntity() instanceof EntityPlayer) {
            event.addCapability(new ResourceLocation("barData"), new WizardryDataProvider());
        }
    }

    @SubscribeEvent
    public void playerUpdate(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntity() instanceof EntityPlayer) {
            if (tickCooldown >= 10) {
                tickCooldown = 0;

                EntityPlayer player = (EntityPlayer) event.getEntity();
                IWizardData.BarData provider = getEntityData(player);
                if (provider.manaAmount < provider.manaMax)
                    provider.manaAmount++;
                if (provider.burnoutAmount > 0)
                    provider.burnoutAmount--;

            } else tickCooldown++;
        }
    }
}
