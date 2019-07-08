package com.teamwizardry.wizardry.api.entity.fairy.fairytasks;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.common.entity.EntityFairy;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Supplier;

public final class FairyTaskRegistry {

	public static final ResourceLocation IDLE_TASK = new ResourceLocation(Wizardry.MODID, "idle");

	private static final HashMap<ResourceLocation, Entry<FairyTask>> factories = new HashMap<>();

	static {
		factories.put(IDLE_TASK, new Entry<>((stack, fairy) -> false, FairyTaskIdle::new));
	}

	private FairyTaskRegistry() {
	}

	public static void addTask(ResourceLocation resourceLocation, BiPredicate<ItemStack, EntityFairy> acceptTask, Supplier<FairyTask> factory) {
		factories.put(resourceLocation, new Entry<>(acceptTask, factory));
	}

	@Nonnull
	public static FairyTask createTaskFromResource(@Nonnull ResourceLocation resourceLocation) {
		Entry<FairyTask> entry = factories.get(resourceLocation);
		if (entry == null) return factories.get(IDLE_TASK).factory.get();

		return entry.factory.get();
	}

	@Nonnull
	public static ResourceLocation getAcceptableTask(ItemStack stack, EntityFairy fairy) {
		for (Map.Entry<ResourceLocation, Entry<FairyTask>> entry : factories.entrySet()) {
			if (entry.getValue().acceptTask.test(stack, fairy)) {
				return entry.getKey();
			}
		}

		return IDLE_TASK;
	}

	private static final class Entry<T extends FairyTask> {

		private final BiPredicate<ItemStack, EntityFairy> acceptTask;
		private final Supplier<T> factory;

		Entry(final BiPredicate<ItemStack, EntityFairy> acceptTask, final Supplier<T> factory) {
			this.acceptTask = acceptTask;
			this.factory = factory;
		}
	}

}
