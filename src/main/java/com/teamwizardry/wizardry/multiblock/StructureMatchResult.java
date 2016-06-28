package com.teamwizardry.wizardry.multiblock;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;

public class StructureMatchResult {

	public List<BlockPos> allErrors = new ArrayList<>();
	public List<BlockPos> airErrors = new ArrayList<>();
	public List<BlockPos> nonAirErrors = new ArrayList<>();
	public List<BlockPos> propertyErrors = new ArrayList<>();
	public List<BlockPos> matches = new ArrayList<>();
	public BlockPos posOffset;
	public Rotation rotation;
	public Structure structure;
	
	public StructureMatchResult(BlockPos posOffset, Rotation rotation, Structure structure) {
		super();
		this.posOffset = posOffset;
		this.rotation = rotation;
		this.structure = structure;
	}
}
