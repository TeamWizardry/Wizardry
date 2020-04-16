package com.teamwizardry.wizardry;

import com.teamwizardry.wizardry.api.StringConsts;
import com.teamwizardry.wizardry.api.task.IRobot;
import com.teamwizardry.wizardry.api.task.TaskController;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.HandSide;
import net.minecraft.world.World;

import java.util.ArrayList;

public class DummyEntity extends LivingEntity implements IRobot {

	public TaskController taskController = new TaskController();

	public DummyEntity(EntityType<? extends LivingEntity> type, World worldIn) {
		super(type, worldIn);
	}

	@Override
	public Iterable<ItemStack> getArmorInventoryList() {
		return new ArrayList<>();
	}

	@Override
	public ItemStack getItemStackFromSlot(EquipmentSlotType slotIn) {
		return ItemStack.EMPTY;
	}

	@Override
	public void setItemStackToSlot(EquipmentSlotType slotIn, ItemStack stack) {

	}

	@Override
	public void tick() {
		super.tick();

		taskController.tick(this);
	}

	@Override
	public HandSide getPrimaryHand() {
		return HandSide.RIGHT;
	}


	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT nbt = super.serializeNBT();

		nbt.put(StringConsts.TASK_CONTROLLER, taskController.serializeNBT());
		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		super.deserializeNBT(nbt);

		if (nbt.contains(StringConsts.TASK_CONTROLLER)) {
			taskController = new TaskController();
			taskController.deserializeNBT(nbt.getCompound(StringConsts.TASK_CONTROLLER));
		}
	}
}
