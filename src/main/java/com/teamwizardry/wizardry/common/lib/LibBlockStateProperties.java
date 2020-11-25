package com.teamwizardry.wizardry.common.lib;

import com.teamwizardry.wizardry.common.block.BlockMagicWorktable;
import net.minecraft.state.EnumProperty;

/**
 * Project: Wizardry
 * Created by Carbon
 * Copyright (c) Carbon 2020
 */
public class LibBlockStateProperties {
	public static final EnumProperty<BlockMagicWorktable.WorktablePart> WORKTABLE_PART = EnumProperty.create("part", BlockMagicWorktable.WorktablePart.class);
}
