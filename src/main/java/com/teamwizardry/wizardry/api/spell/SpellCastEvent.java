package com.teamwizardry.wizardry.api.spell;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Created by LordSaad.
 */
public class SpellCastEvent extends Event {

	private World world;
	private Entity caster;
	private ItemStack spellHolder;
	private Vec3d pos;

	public SpellCastEvent(World world, Entity caster, ItemStack spellHolder, Vec3d pos) {
		this.world = world;
		this.caster = caster;
		this.spellHolder = spellHolder;
		this.pos = pos;
	}

	public Entity getCaster() {
		return caster;
	}

	public void setCaster(Entity caster) {
		this.caster = caster;
	}

	public ItemStack getSpellHolder() {
		return spellHolder;
	}

	public void setSpellHolder(ItemStack spellHolder) {
		this.spellHolder = spellHolder;
	}

	public Vec3d getPos() {
		return pos;
	}

	public void setPos(Vec3d pos) {
		this.pos = pos;
	}

	public World getWorld() {
		return world;
	}

	public void setWorld(World world) {
		this.world = world;
	}
}
