package com.teamwizardry.wizardry.common.module.events;

import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.annotation.RegisterModule;
import com.teamwizardry.wizardry.api.spell.module.IModuleEvent;
import com.teamwizardry.wizardry.api.spell.module.ModuleInstanceEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.ENTITY_HIT;

/**
 * Created by Demoniaque.
 */
@RegisterModule(ID="event_collide_block")
public class ModuleEventCollideBlock implements IModuleEvent {

	@Override
	public boolean run(@NotNull World world, ModuleInstanceEvent module, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		BlockPos pos = spell.getTargetPos();
		spell.removeData(ENTITY_HIT);
		return pos != null;
	}

}
