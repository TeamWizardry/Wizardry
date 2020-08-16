package com.teamwizardry.wizardry.api.spell;

import java.util.List;
import java.util.function.Function;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;

public class EntityTarget implements ITargetComponent<Entity> {
	public static final Function<Entity, Boolean> ALWAYS = entity -> true;
	public static final Function<Entity, Boolean> NEVER = entity -> false;

	private final Function<Entity, Boolean> targetFunction;
	private final String name;
	private final List<Item> items;

	private EntityTarget(String name, List<Item> items, Function<Entity, Boolean> function) {
		this.name = name;
		this.items = items;
		this.targetFunction = function;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public List<Item> getItems() {
		return items;
	}

	public boolean apply(Entity entity) {
		return targetFunction.apply(entity);
	}
}
