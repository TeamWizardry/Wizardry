package com.teamwizardry.wizardry.common.entity.angel.zachriel.nemez;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * @author WireSegal
 * Created at 4:19 PM on 1/15/18.
 */
public class NemezManager implements INBTSerializable<NBTTagList> {
	public static final int TIME_COMPRESSION_CONSTANT = 15;
	private final Stack<Moment> moments = new Stack<>();
	private transient Moment currentMoment = new Moment();

	public void pushEntityData(Entity entity) {
		currentMoment.addEntitySnapshot(entity);
	}

	public void pushBlockData(BlockPos pos, IBlockState state) {
		currentMoment.addBlockSnapshot(pos, state);
	}

	public void pushMoment() {
		moments.push(currentMoment);
		currentMoment = new Moment();
	}

	public Moment peekAtMoment() {
		return moments.empty() ? null : moments.peek();
	}

	public Moment popMoment() {
		return moments.pop();
	}

	public void collapse() {
		compress(TIME_COMPRESSION_CONSTANT);
	}

	public boolean needsCompression(int maximumMoments) {
		return moments.size() <= maximumMoments * 1.5;
	}

	public void compressDownTo(int maximumMoments) {
		if (moments.size() <= maximumMoments) return;
		compress((int) Math.ceil((float) maximumMoments / moments.size()));
	}

	public void compress(int factor) {
		List<Moment> collapsedMoments = new ArrayList<>();

		int momentaryIndex = 0;
		Moment lastMoment = null;
		while (moments.size() != 0) {
			Moment currentMoment = moments.pop();
			if (lastMoment == null)
				lastMoment = currentMoment;
			else
				lastMoment.collapse(currentMoment);
			if (++momentaryIndex == factor) {
				collapsedMoments.add(lastMoment);
				momentaryIndex = 0;
				lastMoment = null;
			}
		}
		if (lastMoment != null)
			collapsedMoments.add(lastMoment);

		for (Moment moment : collapsedMoments)
			moments.push(moment);
	}

	public void erase() {
		moments.clear();
		currentMoment = new Moment();
	}

	public NemezManager snapshot() {
		NemezManager manager = new NemezManager();
		for (Moment moment : moments)
			manager.moments.push(moment.snapshot());
		manager.currentMoment = manager.currentMoment.snapshot();
		return manager;
	}

	public NBTTagList serializeFirstN(int n) {
		NBTTagList momentsSerialized = new NBTTagList();
		int i = 0;
		for (Moment moment : moments) {
			momentsSerialized.appendTag(moment.serializeNBT());
			if (i++ == n)
				break;
		}
		return momentsSerialized;
	}

	public void absorb(NBTTagList nbt) {
		Stack<Moment> shifted = new Stack<>();
		for (NBTBase momentUncast : nbt) {
			NBTTagCompound moment = (NBTTagCompound) momentUncast;
			shifted.push(Moment.fromNBT(moment));
		}
		for (Moment moment : moments)
			shifted.push(moment);

		moments.clear();
		for (Moment moment : shifted)
			moments.push(moment);
	}

	@Override
	public NBTTagList serializeNBT() {
		NBTTagList momentsSerialized = new NBTTagList();
		for (Moment moment : moments)
			momentsSerialized.appendTag(moment.serializeNBT());
		return momentsSerialized;
	}

	@Override
	public void deserializeNBT(NBTTagList nbt) {
		moments.clear();
		for (NBTBase momentUncast : nbt) {
			NBTTagCompound moment = (NBTTagCompound) momentUncast;
			moments.push(Moment.fromNBT(moment));
		}
	}
}
