package com.teamwizardry.wizardry.init.plugin;

import com.teamwizardry.wizardry.api.entity.fairy.fairytasks.FairyTask;
import com.teamwizardry.wizardry.api.entity.fairy.fairytasks.FairyTaskRegistry;
import com.teamwizardry.wizardry.api.plugin.PluginContext;
import com.teamwizardry.wizardry.common.entity.EntityFairy;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.function.BiPredicate;
import java.util.function.Supplier;

public class PluginLoaderContext implements PluginContext {

	@Override
	public void addFairyTask(ResourceLocation key, BiPredicate<ItemStack, EntityFairy> acceptTask, Supplier<FairyTask> factory) {
		FairyTaskRegistry.addTask(key, acceptTask, factory);
	}
}
