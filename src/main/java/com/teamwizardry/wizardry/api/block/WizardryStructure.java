package com.teamwizardry.wizardry.api.block;

import com.teamwizardry.librarianlib.features.structure.Structure;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.structure.template.Template;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * The whole purpose of this class is to make getTemplateBlocks public
 */
public class WizardryStructure extends Structure {

	public WizardryStructure(@Nonnull ResourceLocation loc) {
		super(loc);
	}

	/**
	 * My cheap attempt at making a method public without any hacks.
	 */
	public List<Template.BlockInfo> sudoGetTemplateBlocks() {
		return getTemplateBlocks();
	}
}
