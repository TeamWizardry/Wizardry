package com.teamwizardry.wizardry.api.plugin;

import com.teamwizardry.wizardry.api.entity.fairy.fairytasks.FairyTask;
import com.teamwizardry.wizardry.common.entity.EntityFairy;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.function.BiPredicate;
import java.util.function.Supplier;

public interface PluginContext {

	void addFairyTask(ResourceLocation key, BiPredicate<ItemStack, EntityFairy> acceptTask, Supplier<FairyTask> factory);
}
