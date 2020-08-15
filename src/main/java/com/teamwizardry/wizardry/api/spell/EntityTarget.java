package com.teamwizardry.wizardry.api.spell;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;

import java.util.function.Function;

public class EntityTarget implements ITargetComponent<Entity> {
	public static final Function<Entity, Boolean> ALWAYS = entity -> true;
	public static final Function<Entity, Boolean> NEVER = entity -> false;

	private final Function<Entity, Boolean> targetFunction;
	private final String name;
	private final Item item;

	private EntityTarget(String name, Item item, Function<Entity, Boolean> function) {
		this.name = name;
		this.item = item;
		this.targetFunction = function;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Item getItem() {
		return item;
	}

	public boolean apply(Entity entity) {
		return targetFunction.apply(entity);
	}
}
