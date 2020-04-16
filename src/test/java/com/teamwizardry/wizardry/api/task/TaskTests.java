package com.teamwizardry.wizardry.api.task;

import com.teamwizardry.wizardry.Wizardry;
import net.minecraft.util.ResourceLocation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TaskTests {

	public static final ResourceLocation DUMMY_TASK_LOC = new ResourceLocation(Wizardry.MODID, "dummy_task");

	@Test
	public void registerTaskTest() {
		TaskRegistry.registerTask(DummyTask::new);

		Task registeredTask = TaskRegistry.supplyTask(DUMMY_TASK_LOC);
		assertTrue(registeredTask instanceof DummyTask);
		assertEquals(registeredTask.getResourceLocation(), DUMMY_TASK_LOC);
	}

	@Test
	public void taskSerializationTest() {
		//	TaskController controller = new TaskController();
//
		//	controller.setTaskQueue(
		//			new TaskWait(5),
		//			new TaskMove(new BlockPos(2, 4, 6), Direction.SOUTH),
		//			new TaskWait(5),
		//			new TaskMove(new BlockPos(-2, -4, -6), Direction.SOUTH)
		//	);
//
		//	int i = 50;
		//	while (--i > 0)
		//TODO:crashy line		controller.next(new DummyEntity(EntityType.BAT, null));
//
		//	CompoundNBT nbt = controller.serializeNBT();
		//	controller = TaskController.deserialize(nbt);
//
		//	assertEquals(controller.getStorage().storageNBT.getString("test_key"), "test_value");
	}
}
