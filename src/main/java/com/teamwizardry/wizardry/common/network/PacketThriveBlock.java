package com.teamwizardry.wizardry.common.network;

import com.teamwizardry.librarianlib.core.LibrarianLib;
import com.teamwizardry.librarianlib.features.network.PacketBase;
import com.teamwizardry.librarianlib.features.saving.Save;
import net.minecraft.init.Items;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.jetbrains.annotations.NotNull;

/**
 * Project: Wizardry
 * Created by Carbon
 * Copyright (c) Carbon 2020
 */
public class PacketThriveBlock extends PacketBase {
	@Save
	public BlockPos pos;

	public PacketThriveBlock() {
	}

	public PacketThriveBlock(BlockPos lower) {
		pos = lower;
	}

	@Override
	public void handle(@NotNull MessageContext ctx) {
		World world = LibrarianLib.PROXY.getClientPlayer().world;

		ItemDye.applyBonemeal(new ItemStack(Items.DYE), world, pos);
		ItemDye.applyBonemeal(new ItemStack(Items.DYE), world, pos.add(0,1,0));
	}
}
