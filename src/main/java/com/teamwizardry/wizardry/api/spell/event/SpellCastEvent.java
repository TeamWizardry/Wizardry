package com.teamwizardry.wizardry.api.spell.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Created by Saad on 6/21/2016.
 */
public class SpellCastEvent extends Event {

    public NBTTagCompound spell;
    public EntityPlayer player;

    public SpellCastEvent(NBTTagCompound spell, EntityPlayer player) {
        this.spell = spell;
        this.player = player;
    }
}
