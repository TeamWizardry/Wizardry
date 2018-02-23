package com.teamwizardry.wizardry.api.book.hierarchy.entry.criterion.game;

import com.teamwizardry.wizardry.api.book.hierarchy.entry.criterion.ICriterion;
import net.minecraft.entity.player.EntityPlayer;

/**
 * @author WireSegal
 * Created at 9:56 PM on 2/21/18.
 */
public class CriterionCode implements ICriterion {

	@Override
	public boolean isUnlocked(EntityPlayer player, boolean grantedInCode) {
		return grantedInCode;
	}
}
