package com.teamwizardry.wizardry.api.spell;

import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.api.module.ModuleRegistry;
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
			ModuleRegistry.getInstance().modules.get(spell.getInteger(Module.CLASS)).construct(null).cast(player, source, spell);
		}
	}
}
