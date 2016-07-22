package com.teamwizardry.wizardry.api.spell;

import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.api.module.ModuleList;
import com.teamwizardry.wizardry.api.spell.event.SpellCastEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SpellHandler
{
	public static final SpellHandler INSTANCE = new SpellHandler();
	
	private SpellHandler()
	{
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@SubscribeEvent
	public void onModuleCast(SpellCastEvent event)
	{
		EntityPlayer player = event.player;
		Entity source = event.source;
		NBTTagCompound spell = event.spell;
		
		if (!event.isCanceled())
		{
			ModuleList.INSTANCE.modules.get(spell.getString(Module.CLASS)).construct().cast(player, source, spell);
		}
	}
}
