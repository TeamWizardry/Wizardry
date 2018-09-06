package com.teamwizardry.wizardry.api.block;

import com.teamwizardry.librarianlib.features.structure.Structure;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.structure.template.Template;

import javax.annotation.Nonnull;
import java.util.List;

public class WizardryStructure extends Structure {

	public WizardryStructure(@Nonnull ResourceLocation loc) {
		super(loc);
	}

	public List<Template.BlockInfo> sudoGetTemplateBlocks() {
		return getTemplateBlocks();
	}
}
