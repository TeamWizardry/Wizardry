package com.teamwizardry.wizardry.crafting.burnable;

import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.common.network.PacketDevilDustFizzle;
import com.teamwizardry.wizardry.init.ModSounds;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class FireRecipe {
	protected int maxDuration;
	protected int currentDuration;

	protected ItemStack output;

	public FireRecipe(ItemStack output, int maxDuration) {
		this.output = output.copy();
		this.maxDuration = maxDuration;
		this.currentDuration = 0;
	}

	public void reset() {
		this.currentDuration = 0;
	}

	public void tick(World world, BlockPos pos) {
		currentDuration++;
		if (currentDuration % 10 == 0) {
			PacketHandler.NETWORK.sendToAllAround(new PacketDevilDustFizzle(new Vec3d(pos).addVector(0.5, 0.5, 0.5), currentDuration), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 20));
			world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), ModSounds.FRYING_SIZZLE, SoundCategory.BLOCKS, 0.7F, (float) RandUtil.nextDouble(0.8, 1.3));
		}
	}

	public boolean isFinished() {
		return currentDuration >= maxDuration;
	}

	public ItemStack finish(EntityItem entity) {
		int count = output.getCount();
		ItemStack input = entity.getItem();
		if (input.isEmpty()) {
			entity.setDead();
			return ItemStack.EMPTY;
		}
		count *= input.getCount();
		output.setCount(count);
		return output.copy();
	}

	public FireRecipe copy() {
		return new FireRecipe(output.copy(), maxDuration);
	}

	public ItemStack getOutput() {
		return output;
	}
}
