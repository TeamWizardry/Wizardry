package com.teamwizardry.wizardry.crafting.craftingplaterecipes;

import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.wizardry.api.block.ICraftingPlateRecipe;
import com.teamwizardry.wizardry.api.capability.player.mana.IManaCapability;
import com.teamwizardry.wizardry.api.capability.player.mana.ManaManager;
import com.teamwizardry.wizardry.api.spell.SpellBuilder;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.common.network.PacketExplode;
import com.teamwizardry.wizardry.common.tile.TileJar;
import com.teamwizardry.wizardry.init.ModSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemStackHandler;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class FairyJarRecipe implements ICraftingPlateRecipe {

	@Override
	public boolean doesRecipeExistForItem(ItemStack stack) {
		return false;
	}

	@Override
	public boolean doesRecipeExistInWorld(World world, BlockPos pos) {
		TileEntity tileEntity = world.getTileEntity(pos.offset(EnumFacing.UP));
		if (!(tileEntity instanceof TileJar)) return false;
		TileJar jar = (TileJar) tileEntity;

		return jar.fairy != null && jar.fairy.infusedSpell == null && !jar.fairy.isDepressed;
	}

	@Override
	public void tick(World world, BlockPos pos, ItemStack input, ItemStackHandler inventoryHandler, Function<IManaCapability, Double> consumeMana) {
		TileEntity tileEntity = world.getTileEntity(pos.offset(EnumFacing.UP));
		if (!(tileEntity instanceof TileJar)) return;
		TileJar jar = (TileJar) tileEntity;

		if (jar.fairy != null
				&& jar.fairy.infusedSpell == null
				&& !jar.fairy.isDepressed
				&& !ManaManager.isManaFull(jar.fairy.handler)) {
			ManaManager.forObject(jar.fairy.handler).addMana(consumeMana.apply(jar.fairy.handler)).close();
			jar.markDirty();
		}
	}

	@Override
	public void complete(World world, BlockPos pos, ItemStack input, ItemStackHandler inventoryHandler) {
		TileEntity tileEntity = world.getTileEntity(pos.offset(EnumFacing.UP));
		if (!(tileEntity instanceof TileJar)) return;
		TileJar jar = (TileJar) tileEntity;

		ArrayList<ItemStack> stacks = new ArrayList<>();

		for (int i = 0; i < inventoryHandler.getSlots(); i++) {
			if (!inventoryHandler.getStackInSlot(i).isEmpty()) {
				stacks.add(inventoryHandler.getStackInSlot(i));
				inventoryHandler.setStackInSlot(i, ItemStack.EMPTY);
			}
		}

		SpellBuilder builder = new SpellBuilder(stacks);
		List<SpellRing> spell = builder.getSpell();
		if (spell.isEmpty()) return;

		NBTTagList list = new NBTTagList();
		for (SpellRing spellRing : builder.getSpell()) {
			list.appendTag(spellRing.serializeNBT());
		}

		jar.fairy.infusedSpell = builder.getSpell().get(0);

		PacketHandler.NETWORK.sendToAllAround(new PacketExplode(new Vec3d(pos).add(0.5, 0.5, 0.5), Color.CYAN, Color.BLUE, 2, 2, 500, 300, 20, true),
				new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 256));

		world.playSound(null, pos, ModSounds.BASS_BOOM, SoundCategory.BLOCKS, 1f, (float) RandUtil.nextDouble(1, 1.5));

		List<Entity> entityList = world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos).grow(32, 32, 32));
		for (Entity entity1 : entityList) {
			double dist = entity1.getDistance(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
			final double upperMag = 3;
			final double scale = 0.8;
			double mag = upperMag * (scale * dist / (-scale * dist - 1) + 1);
			Vec3d dir = entity1.getPositionVector().subtract(new Vec3d(pos).add(0.5, 0.5, 0.5)).normalize().scale(mag);

			entity1.motionX = (dir.x);
			entity1.motionY = (dir.y);
			entity1.motionZ = (dir.z);
			entity1.fallDistance = 0;
			entity1.velocityChanged = true;

			if (entity1 instanceof EntityPlayerMP)
				((EntityPlayerMP) entity1).connection.sendPacket(new SPacketEntityVelocity(entity1));
		}
	}

	@Override
	public boolean isDone(World world, BlockPos pos, ItemStack stack) {
		TileEntity tileEntity = world.getTileEntity(pos.offset(EnumFacing.UP));
		if (!(tileEntity instanceof TileJar)) return false;
		TileJar jar = (TileJar) tileEntity;

		return ManaManager.isManaFull(jar.fairy.handler);
	}

	@Override
	public void canceled(World world, BlockPos pos, ItemStack stack) {
		TileEntity tileEntity = world.getTileEntity(pos.offset(EnumFacing.UP));
		if (!(tileEntity instanceof TileJar)) return;
		TileJar jar = (TileJar) tileEntity;

		if (!ManaManager.isManaFull(jar.fairy.handler)) {
			ManaManager.forObject(jar.fairy.handler).setMana(0).close();
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderInput(World world, BlockPos pos, ItemStack input, float partialTicks) {

	}

}
