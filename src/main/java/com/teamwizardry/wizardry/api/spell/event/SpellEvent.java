package com.teamwizardry.wizardry.api.spell.event;

import com.teamwizardry.wizardry.api.trackerobject.SpellStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Created by Saad on 6/21/2016.
 */
@Cancelable
public class SpellEvent extends Event {
	public NBTTagCompound spell;
	public EntityPlayer player;
	public Entity source;

	public SpellEvent(NBTTagCompound spell, Entity source, EntityPlayer player) {
		this.spell = spell;
		this.source = source;
		this.player = player;
	}

	public static class SpellCastEvent extends SpellEvent {
		public SpellStack stack;

		private SpellCastEvent(SpellStack spell) {
			super(spell.spell, spell.caster, spell.player);
			stack = spell;
		}

		public SpellCastEvent(SpellStack spell, Entity source) {
			super(spell.spell, source, spell.player);
			stack = spell;
		}
	}

	public static class SpellRunEvent extends SpellEvent {
		public SpellRunEvent(NBTTagCompound spell, Entity source, EntityPlayer player) {
			super(spell, source, player);
		}
	}
}
