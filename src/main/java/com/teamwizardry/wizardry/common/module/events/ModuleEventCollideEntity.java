package com.teamwizardry.wizardry.common.module.events;

import com.teamwizardry.wizardry.api.spell.Module;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import com.teamwizardry.wizardry.api.spell.RegisterModule;
import com.teamwizardry.wizardry.api.spell.SpellData;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.ENTITY_HIT;

/**
 * Created by LordSaad.
 */
@RegisterModule
public class ModuleEventCollideEntity extends Module {

	@Nonnull
	@Override
	public ItemStack getRequiredStack() {
		return new ItemStack(Items.BEEF);
	}

	@Nonnull
	@Override
	public ModuleType getModuleType() {
		return ModuleType.EVENT;
	}

	@Nonnull
	@Override
	public String getID() {
		return "on_collide_entity";
	}

	@Nonnull
	@Override
	public String getReadableName() {
		return "On Collide Entity";
	}

	@Nonnull
	@Override
	public String getDescription() {
		return "Triggered when the spell collides with an entity";
	}

	@Override
	public boolean run(@Nonnull SpellData spell) {
		Entity entity = spell.getData(ENTITY_HIT);
		return entity != null && nextModule != null && nextModule.run(spell);
	}

	@Override
	public void runClient(@Nonnull SpellData spell) {

	}

	@Nonnull
	@Override
	public Module copy() {
		return cloneModule(new ModuleEventCollideEntity());
	}
}
