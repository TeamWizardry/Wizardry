package com.teamwizardry.wizardry.common.tile;

import com.teamwizardry.librarianlib.features.autoregister.TileRegister;
import com.teamwizardry.librarianlib.features.base.block.tile.module.ModuleInventory;
import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.librarianlib.features.saving.Module;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.librarianlib.features.tesr.TileRenderer;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.CraftingPlateRecipeManager;
import com.teamwizardry.wizardry.api.block.TileManaNode;
import com.teamwizardry.wizardry.api.capability.mana.CapManager;
import com.teamwizardry.wizardry.api.capability.mana.IWizardryCapability;
import com.teamwizardry.wizardry.api.capability.mana.WizardryCapabilityProvider;
import com.teamwizardry.wizardry.client.render.block.TileCraftingPlateRenderer;
import com.teamwizardry.wizardry.common.block.BlockCraftingPlate;
import com.teamwizardry.wizardry.common.network.PacketAddItemCraftingPlate;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Random;

/**
 * Created by Demoniaque on 6/10/2016.
 */
@TileRegister(Wizardry.MODID + ":crafting_plate")
@TileRenderer(TileCraftingPlateRenderer.class)
public class TileCraftingPlate extends TileManaNode {

	private static final HashSet<BlockPos> poses = new HashSet<>();

	static {
		poses.add(new BlockPos(3, 2, 3));
		poses.add(new BlockPos(-3, 2, 3));
		poses.add(new BlockPos(3, 2, -3));
		poses.add(new BlockPos(-3, 2, -3));
	}

	@Module
	public ModuleInventory realInventory = new ModuleInventory(new ItemStackHandler(1000) {

		@Override
		public int getSlotLimit(int slot) {
			markDirty();
			return 1;
		}

		@Override
		protected void onContentsChanged(int slot) {
			markDirty();
		}

	});

	@Module
	public ModuleInventory input = new ModuleInventory(new ItemStackHandler() {
		@Override
		protected void onContentsChanged(int slot) {
			markDirty();
		}

		@Override
		protected int getStackLimit(int slot, @Nonnull ItemStack stack) {
			markDirty();
			if (CraftingPlateRecipeManager.doesRecipeExistForItem(stack))
				return 1;
			else return 0;
		}
	});

	@Module
	public ModuleInventory output = new ModuleInventory(new ItemStackHandler() {
		@Override
		protected void onContentsChanged(int slot) {
			markDirty();
		}

		@Override
		protected int getStackLimit(int slot, @Nonnull ItemStack stack) {
			markDirty();
			return stack.getCount();
		}
	});

	@Save
	public boolean revealStructure = true;
	public Random random = new Random(getPos().toLong());
	@Save
	public int suckingCooldown = 0;

	public TileCraftingPlate() {
		super(0, 0);
		setCanSuckFromOutside(false);
		setCanGiveToOutside(false);
		setStructurePos(getPos());
	}

	@Nullable
	@Override
	public IWizardryCapability getWizardryCap() {
		if (!input.getHandler().getStackInSlot(0).isEmpty())
			return WizardryCapabilityProvider.getCap(input.getHandler().getStackInSlot(0));
		return null;
	}

	@Override
	public void onSuckFrom(TileManaNode from) {
		super.onSuckFrom(from);

		suckingCooldown = 10;
		markDirty();
	}

	@Override
	public boolean suckManaAutomatically() {
		return false;
	}

	@Override
	public void update() {
		super.update();

		if (suckingCooldown > 0) {
			suckingCooldown--;
			//markDirty();
		}

		if (!((BlockCraftingPlate) getBlockType()).testStructure(getWorld(), getPos()).isEmpty()) return;

		if (getStructurePos() != getPos()) {
			setStructurePos(getPos());
			markDirty();
		}

		if (!CapManager.isManaFull(getWizardryCap())) {
			for (BlockPos relative : poses) {
				BlockPos target = getPos().add(relative);
				TileEntity tile = world.getTileEntity(target);
				if (tile instanceof TileOrbHolder) {
					if (!((TileOrbHolder) tile).isPartOfStructure() || ((TileOrbHolder) tile).canGiveToOutside() || !((TileOrbHolder) tile).canSuckFromOutside()) {
						((TileOrbHolder) tile).setStructurePos(getPos());
						((TileOrbHolder) tile).setCanGiveToOutside(false);
						((TileOrbHolder) tile).setCanSuckFromOutside(true);
						tile.markDirty();
					}
				}
			}
		}

		if (!world.isRemote) {
			for (EntityItem entityItem : world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(pos))) {
				if (entityItem.getItem().isEmpty()) continue;
				if (hasInput()) break;

				if (!isInventoryEmpty() && CraftingPlateRecipeManager.doesRecipeExistForItem(entityItem.getItem())) {

					ItemStack stack = entityItem.getItem().copy();
					stack.setCount(1);
					entityItem.getItem().shrink(1);

					input.getHandler().insertItem(0, stack, false);

					PacketHandler.NETWORK.sendToAllAround(new PacketAddItemCraftingPlate(getPos(), stack), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 256));

				} else if (!(CraftingPlateRecipeManager.doesRecipeExistForItem(entityItem.getItem()))) {

					ItemStack stack = entityItem.getItem().copy();
					stack.setCount(1);

					ItemStack left = ItemHandlerHelper.insertItem(realInventory.getHandler(), stack, false);
					if (left.isEmpty()) {
						entityItem.getItem().shrink(1);
						PacketHandler.NETWORK.sendToAllAround(new PacketAddItemCraftingPlate(getPos(), stack), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 256));
					}
				}
			}
		}

		boolean done = CraftingPlateRecipeManager.tick(world, pos, getInput(), realInventory.getHandler(), this::suckMana);
		if (done) {
			ItemStack inputStack = input.getHandler().extractItem(0, 1, false);
			if (!inputStack.isEmpty()) {
				output.getHandler().insertItem(0, inputStack, false);
			}
			markDirty();
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

	public ItemStack getInput() {
		return input.getHandler().getStackInSlot(0);
	}

	public boolean hasInput() {
		return !getInput().isEmpty();
	}

	public ItemStack getOutput() {
		return output.getHandler().getStackInSlot(0);
	}

	public boolean hasOutput() {
		return !getOutput().isEmpty();
	}

	public boolean isInventoryEmpty() {
		return realInventory.getHandler().getStackInSlot(0).isEmpty();
	}
}
