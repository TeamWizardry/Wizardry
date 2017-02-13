package com.teamwizardry.wizardry.common.block;

import com.teamwizardry.librarianlib.client.fx.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.client.fx.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.client.fx.particle.functions.InterpFadeInOut;
import com.teamwizardry.librarianlib.common.base.ModCreativeTab;
import com.teamwizardry.librarianlib.common.base.block.BlockModContainer;
import com.teamwizardry.librarianlib.common.structure.Structure;
import com.teamwizardry.librarianlib.common.util.math.interpolate.StaticInterp;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.block.IManaSink;
import com.teamwizardry.wizardry.api.render.ClusterObject;
import com.teamwizardry.wizardry.client.render.TileCraftingPlateRenderer;
import com.teamwizardry.wizardry.common.tile.TileCraftingPlate;
import com.teamwizardry.wizardry.init.ModStructures;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Saad on 6/10/2016.
 */
public class BlockCraftingPlate extends BlockModContainer implements IManaSink {

	public static final AxisAlignedBB AABB = new AxisAlignedBB(0.125, 0, 0.125, 0.875, 0.625, 0.875);

	public BlockCraftingPlate() {
		super("crafting_plate", Material.ROCK);
		setHardness(1.0F);
		setLightLevel(15);
		setSoundType(SoundType.STONE);
	}

	@SideOnly(Side.CLIENT)
	public void initModel() {
		ClientRegistry.bindTileEntitySpecialRenderer(TileCraftingPlate.class, new TileCraftingPlateRenderer());
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(World world, IBlockState iBlockState) {
		return new TileCraftingPlate();
	}

	private TileCraftingPlate getTE(World world, BlockPos pos) {
		return (TileCraftingPlate) world.getTileEntity(pos);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (!worldIn.isRemote) {

			TileCraftingPlate plate = getTE(worldIn, pos);
			Structure structure = ModStructures.INSTANCE.structures.get(plate.structureName());
			for (Template.BlockInfo info : structure.blockInfos()) {
				BlockPos newPos = info.pos.add(pos).subtract(new Vec3i(6, 2, 6));
				if (info.blockState == null) continue;
				if (worldIn.getBlockState(newPos).getBlock() != info.blockState.getBlock()) {
					ParticleBuilder glitter = new ParticleBuilder(50);
					glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
					glitter.setColor(Color.RED);
					glitter.setAlphaFunction(new InterpFadeInOut(0.3f, 0.5f));
					ParticleSpawner.spawn(glitter, worldIn, new StaticInterp<>(new Vec3d(newPos).addVector(0.5, 0.5, 0.5)), 50, 0, (aFloat, particleBuilder) -> {
						glitter.setMotion(new Vec3d(0, ThreadLocalRandom.current().nextDouble(0, 0.1), 0));
						glitter.setPositionOffset(new Vec3d(0, ThreadLocalRandom.current().nextDouble(0, 1), 0));
					});
					return false;
				} else {
					if (worldIn.getBlockState(newPos) != info.blockState.getBlock())
						worldIn.setBlockState(newPos, info.blockState);
				}
			}

			if (plate.isCrafting) return false;
			if ((heldItem != null) && (heldItem.stackSize > 0)) {
				ItemStack stack = heldItem.copy();
				stack.stackSize = 1;
				--heldItem.stackSize;
				plate.inventory.add(new ClusterObject(plate, stack, worldIn, plate.random));
				playerIn.openContainer.detectAndSendChanges();

			} else if (plate.output != null) {
				playerIn.setHeldItem(hand, plate.output.copy());
				plate.output = null;
				playerIn.openContainer.detectAndSendChanges();

			} else if (!plate.inventory.isEmpty()) {
				playerIn.setHeldItem(hand, plate.inventory.remove(plate.inventory.size() - 1).stack);
				playerIn.openContainer.detectAndSendChanges();
			}
		}
		worldIn.notifyBlockUpdate(pos, state, state, 3);
		return true;
	}

	@NotNull
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
		return AABB;
	}

	@Override
	public boolean canRenderInLayer(BlockRenderLayer layer) {
		return layer == BlockRenderLayer.CUTOUT;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState blockState) {
		return false;
	}

	@Nullable
	@Override
	public ModCreativeTab getCreativeTab() {
		return Wizardry.tab;
	}
}
