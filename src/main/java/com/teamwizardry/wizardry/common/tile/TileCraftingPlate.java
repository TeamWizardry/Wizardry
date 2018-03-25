package com.teamwizardry.wizardry.common.tile;

import com.teamwizardry.librarianlib.features.autoregister.TileRegister;
import com.teamwizardry.librarianlib.features.base.block.tile.module.ModuleInventory;
import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.librarianlib.features.saving.Module;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.librarianlib.features.tesr.TileRenderer;
import com.teamwizardry.librarianlib.features.utilities.client.ClientRunnable;
import com.teamwizardry.wizardry.api.ConfigValues;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.block.TileManaInteracter;
import com.teamwizardry.wizardry.api.capability.CapManager;
import com.teamwizardry.wizardry.api.capability.IWizardryCapability;
import com.teamwizardry.wizardry.api.capability.WizardryCapabilityProvider;
import com.teamwizardry.wizardry.api.item.EnumPearlType;
import com.teamwizardry.wizardry.api.item.IInfusable;
import com.teamwizardry.wizardry.api.item.INacreProduct;
import com.teamwizardry.wizardry.api.spell.SpellBuilder;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.SpellUtils;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.client.render.block.TileCraftingPlateRenderer;
import com.teamwizardry.wizardry.common.block.BlockCraftingPlate;
import com.teamwizardry.wizardry.common.network.PacketExplode;
import com.teamwizardry.wizardry.init.ModSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

/**
 * Created by Demoniaque on 6/10/2016.
 */
@TileRegister("crafting_plate")
@TileRenderer(TileCraftingPlateRenderer.class)
public class TileCraftingPlate extends TileManaInteracter {

	public static final HashSet<BlockPos> poses = new HashSet<>();

	static {
		poses.add(new BlockPos(3, 2, 3));
		poses.add(new BlockPos(-3, 2, 3));
		poses.add(new BlockPos(3, 2, -3));
		poses.add(new BlockPos(-3, 2, -3));
	}

	@Module
	public ModuleInventory realInventory = new ModuleInventory(1000);
	@Module
	public ModuleInventory inputPearl = new ModuleInventory(new ItemStackHandler() {
		@Override
		protected int getStackLimit(int slot, @Nonnull ItemStack stack) {
			if (stack.getItem() instanceof IInfusable)
				return super.getStackLimit(slot, stack);
			else return 0;
		}
	});

	@Module
	public ModuleInventory outputPearl = new ModuleInventory(new ItemStackHandler() {
		@Override
		protected int getStackLimit(int slot, @Nonnull ItemStack stack) {
			if (stack.getItem() instanceof IInfusable)
				return super.getStackLimit(slot, stack);
			else return 0;
		}
	});

	@Save
	public boolean revealStructure = false, isAbleToSuckMana = false;
	public Vec3d[] positions;
	public Random random = new Random(getPos().toLong());

	public TileCraftingPlate() {
		super(500, 500);
		positions = new Vec3d[realInventory.getHandler().getSlots()];
	}

	@Override
	public void readCustomNBT(NBTTagCompound compound) {
	}

	@Override
	public void writeCustomNBT(NBTTagCompound compound, boolean sync) {
	}

	@Nullable
	@Override
	public IWizardryCapability getWizardryCap() {
		if (!inputPearl.getHandler().getStackInSlot(0).isEmpty())
			return WizardryCapabilityProvider.getCap(inputPearl.getHandler().getStackInSlot(0));
		return super.getWizardryCap();
	}

	@Override
	public void update() {
		super.update();

		if (!((BlockCraftingPlate) getBlockType()).isStructureComplete(getWorld(), getPos())) return;

		if (!new CapManager(getWizardryCap()).isManaFull()) {
			for (BlockPos relative : poses) {
				BlockPos target = getPos().add(relative);
				TileEntity tile = world.getTileEntity(target);
				if (tile != null && tile instanceof TilePearlHolder) {
					if (!((TilePearlHolder) tile).isPartOfStructure) {
						((TilePearlHolder) tile).isPartOfStructure = true;
						((TilePearlHolder) tile).structurePos = getPos();
						tile.markDirty();
					}
				}
			}
		}

		for (EntityItem entityItem : world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(pos))) {
			if (!inputPearl.getHandler().getStackInSlot(0).isEmpty()) break;

			if (entityItem.getItem().getItem() instanceof IInfusable) {

				ItemStack stack = entityItem.getItem().copy();
				stack.setCount(1);
				entityItem.getItem().shrink(1);

				inputPearl.getHandler().setStackInSlot(0, stack);

			} else {
				ItemStack stack = entityItem.getItem().copy();
				stack.setCount(1);
				entityItem.getItem().shrink(1);

				for (int i = 0; i < realInventory.getHandler().getSlots(); i++) {
					if (realInventory.getHandler().getStackInSlot(i).isEmpty()) {
						realInventory.getHandler().setStackInSlot(i, stack);

						int finalI = i;
						ClientRunnable.run(new ClientRunnable() {
							@Override
							@SideOnly(Side.CLIENT)
							public void runIfClient() {
								if (renderHandler != null)
									((TileCraftingPlateRenderer) renderHandler).addAnimation(finalI, true, false);
							}
						});
						break;
					}
				}
			}

			markDirty();
		}

		if (!inputPearl.getHandler().getStackInSlot(0).isEmpty() && !realInventory.getHandler().getStackInSlot(0).isEmpty()) {
			CapManager manager = new CapManager(inputPearl.getHandler().getStackInSlot(0));

			if (manager.isManaFull()) {

				ArrayList<ItemStack> stacks = new ArrayList<>();

				for (int i = 0; i < realInventory.getHandler().getSlots(); i++) {
					if (!realInventory.getHandler().getStackInSlot(i).isEmpty()) {
						stacks.add(realInventory.getHandler().getStackInSlot(i));
						realInventory.getHandler().setStackInSlot(i, ItemStack.EMPTY);
					}
				}
				SpellBuilder builder = new SpellBuilder(stacks, true);

				ItemStack infusedPearl = inputPearl.getHandler().getStackInSlot(0).copy();
				inputPearl.getHandler().setStackInSlot(0, ItemStack.EMPTY);
				outputPearl.getHandler().setStackInSlot(0, infusedPearl);


				NBTTagList list = new NBTTagList();
				for (SpellRing spellRing : builder.getSpell()) {
					list.appendTag(spellRing.serializeNBT());
				}
				ItemNBTHelper.setList(infusedPearl, Constants.NBT.SPELL, list);

				//Color lastColor = SpellUtils.getAverageSpellColor(builder.getSpell());
				//float[] hsv = ColorUtils.getHSVFromColor(lastColor);
				//ItemNBTHelper.setFloat(infusedPearl, "hue", hsv[0]);
				//ItemNBTHelper.setFloat(infusedPearl, "saturation", hsv[1]);
				ItemNBTHelper.setFloat(infusedPearl, Constants.NBT.RAND, world.rand.nextFloat());
				ItemNBTHelper.setString(infusedPearl, "type", EnumPearlType.INFUSED.toString());

				// Process spellData multipliers based on nacre quality
				if (infusedPearl.getItem() instanceof INacreProduct) {
					float purity = ((INacreProduct) infusedPearl.getItem()).getQuality(infusedPearl);
					double multiplier;
					if (purity >= 1f) multiplier = ConfigValues.perfectPearlMultiplier * purity;
					else if (purity <= ConfigValues.damagedPearlMultiplier)
						multiplier = ConfigValues.damagedPearlMultiplier;
					else {
						double base = purity - 1;
						multiplier = 1 - (base * base * base * base);
					}

					for (SpellRing spellRing : SpellUtils.getAllSpellRings(infusedPearl))
						spellRing.multiplyMultiplierForAll((float) multiplier);
				}
				for (int i = 0; i < positions.length; i++) {
					positions[i] = Vec3d.ZERO;
				}


				markDirty();

				PacketHandler.NETWORK.sendToAllAround(new PacketExplode(new Vec3d(pos).addVector(0.5, 0.5, 0.5), Color.CYAN, Color.BLUE, 2, 2, 500, 300, 20, true),
						new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 256));

				world.playSound(null, getPos(), ModSounds.BASS_BOOM, SoundCategory.BLOCKS, 1f, (float) RandUtil.nextDouble(1, 1.5));

				List<Entity> entityList = world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos).grow(32, 32, 32));
				for (Entity entity1 : entityList) {
					double dist = entity1.getDistance(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
					final double upperMag = 3;
					final double scale = 0.8;
					double mag = upperMag * (scale * dist / (-scale * dist - 1) + 1);
					Vec3d dir = entity1.getPositionVector().subtract(new Vec3d(pos).addVector(0.5, 0.5, 0.5)).normalize().scale(mag);

					entity1.motionX = (dir.x);
					entity1.motionY = (dir.y);
					entity1.motionZ = (dir.z);
					entity1.fallDistance = 0;
					entity1.velocityChanged = true;

					if (entity1 instanceof EntityPlayerMP)
						((EntityPlayerMP) entity1).connection.sendPacket(new SPacketEntityVelocity(entity1));
				}
			}
		}
	}

	@Nonnull
	@SideOnly(Side.CLIENT)
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return TileEntity.INFINITE_EXTENT_AABB;
	}

	@Override
	public double getMaxRenderDistanceSquared() {
		return 4096;
	}
}
