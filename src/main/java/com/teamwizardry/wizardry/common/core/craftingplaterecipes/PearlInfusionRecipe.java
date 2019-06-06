package com.teamwizardry.wizardry.common.core.craftingplaterecipes;

import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.wizardry.api.ConfigValues;
import com.teamwizardry.wizardry.api.block.ICraftingPlateRecipe;
import com.teamwizardry.wizardry.api.capability.mana.CapManager;
import com.teamwizardry.wizardry.api.capability.mana.IWizardryCapability;
import com.teamwizardry.wizardry.api.capability.mana.WizardryCapabilityProvider;
import com.teamwizardry.wizardry.api.item.INacreProduct;
import com.teamwizardry.wizardry.api.item.ISpellInfusable;
import com.teamwizardry.wizardry.api.spell.SpellBuilder;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.SpellUtils;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.common.network.PacketExplode;
import com.teamwizardry.wizardry.init.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.server.SPacketEntityVelocity;
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

public class PearlInfusionRecipe implements ICraftingPlateRecipe {

	@Override
	public boolean doesRecipeExistForItem(ItemStack stack) {
		return stack.getItem() instanceof ISpellInfusable;
	}

	@Override
	public boolean doesRecipeExistInWorld(World world, BlockPos pos) {
		return false;
	}

	@Override
	public void tick(World world, BlockPos pos, ItemStack input, ItemStackHandler inventoryHandler, Function<IWizardryCapability, Double> consumeMana) {
		if (!CapManager.isManaFull(input)) {
			CapManager.forObject(input).addMana(consumeMana.apply(WizardryCapabilityProvider.getCap(input)));
		}
	}

	@Override
	public void complete(World world, BlockPos pos, ItemStack input, ItemStackHandler inventoryHandler) {
		ArrayList<ItemStack> stacks = new ArrayList<>();

		for (int i = 0; i < inventoryHandler.getSlots(); i++) {
			if (!inventoryHandler.getStackInSlot(i).isEmpty()) {
				stacks.add(inventoryHandler.getStackInSlot(i));
				inventoryHandler.setStackInSlot(i, ItemStack.EMPTY);
			}
		}

		// Process spellData multipliers based on nacre quality
		double pearlMultiplier = 1;
		if (input.getItem() instanceof INacreProduct) {
			float purity = ((INacreProduct) input.getItem()).getQuality(input);
			if (purity >= 1f) pearlMultiplier = ConfigValues.perfectPearlMultiplier * purity;
			else if (purity <= ConfigValues.damagedPearlMultiplier)
				pearlMultiplier = ConfigValues.damagedPearlMultiplier;
			else {
				double base = purity - 1;
				pearlMultiplier = 1 - (base * base * base * base);
			}
		}

		SpellBuilder builder = new SpellBuilder(stacks, pearlMultiplier);

		NBTTagList list = new NBTTagList();
		for (SpellRing spellRing : builder.getSpell()) {
			list.appendTag(spellRing.serializeNBT());
		}

		SpellUtils.infuseSpell(input, list);

		//markDirty();

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
		return CapManager.isManaFull(stack);
	}

	@Override
	public void canceled(World world, BlockPos pos, ItemStack stack) {
		CapManager.forObject(stack).setMana(0);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderInput(World world, BlockPos pos, ItemStack input, float partialTicks) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(0.5, 1, 0.5);
		GlStateManager.scale(0.4, 0.4, 0.4);
		GlStateManager.rotate((float) (world.getTotalWorldTime() * 10.0), 0, 1, 0);
		GlStateManager.translate(0, 0.5 + Math.sin((world.getTotalWorldTime() + partialTicks) / 5) / 10.0, 0);
		Minecraft.getMinecraft().getRenderItem().renderItem(input, ItemCameraTransforms.TransformType.NONE);
		GlStateManager.popMatrix();
	}
}
