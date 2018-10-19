package com.teamwizardry.wizardry.common.module.events;

import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.module.IModuleEvent;
import com.teamwizardry.wizardry.api.spell.module.ModuleEvent;
import com.teamwizardry.wizardry.api.spell.module.RegisterModule;
import net.minecraft.entity.Entity;
import javax.annotation.Nonnull;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.BLOCK_HIT;

/**
 * Created by Demoniaque.
 */
@RegisterModule
public class ModuleEventCollideEntity implements IModuleEvent {

	@Nonnull
	@Override
	public String getClassID() {
		return "event_collide_entity";
	}

	@Override
	public boolean run(ModuleEvent instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		Entity entity = spell.getVictim();
		spell.removeData(BLOCK_HIT);
		return entity != null;
	}
}
