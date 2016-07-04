package com.teamwizardry.wizardry.api.spell.event;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Created by Saad on 6/21/2016.
 */
@Cancelable
public class SpellCastEvent extends Event {

    public NBTTagCompound spell;
    public EntityPlayer player;
    public Entity source;

    public SpellCastEvent(NBTTagCompound spell, Entity source, EntityPlayer player) {
        this.spell = spell;
        this.source = source;
        this.player = player;
    }
}
