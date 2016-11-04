package com.teamwizardry.wizardry.common.item.pearl;

import com.teamwizardry.wizardry.api.item.Explodable;
import com.teamwizardry.wizardry.api.item.Infusable;
import com.teamwizardry.wizardry.common.item.ItemWizardry;

/**
 * Created by Saad on 6/10/2016.
 */
public class ItemQuartzPearl extends ItemWizardry implements Infusable, Explodable {

	public ItemQuartzPearl() {
		super("quartz_pearl");
		setMaxStackSize(1);
	}
}
