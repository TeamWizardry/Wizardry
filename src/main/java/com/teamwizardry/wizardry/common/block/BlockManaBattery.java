package com.teamwizardry.wizardry.common.block;

import jdk.nashorn.internal.ir.Block;
import jdk.nashorn.internal.ir.Statement;
import net.minecraft.block.Blocks;

/**
 * Project: Wizardry
 * Created by Carbon
 * Copyright (c) Carbon 2020
 */
public class BlockManaBattery extends Block {
	public BlockManaBattery(long token, int finish, Statement... statements) {
		super(token, finish, statements);
	}
}
