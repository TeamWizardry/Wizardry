package com.teamwizardry.wizardry.init.plugin;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.plugin.PluginContext;
import com.teamwizardry.wizardry.api.plugin.WizardryPlugin;
import com.teamwizardry.wizardry.common.core.fairytasks.FairyTaskGrabItems;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;

public class InternalWizardryPlugin implements WizardryPlugin {

	@Override
	public void onInit(PluginContext context) {
		context.addFairyTask(new ResourceLocation(Wizardry.MODID, "grab_items"), (stack, fairy) -> stack.getItem().equals(Items.APPLE), FairyTaskGrabItems::new);
	}
}
