package com.teamwizardry.wizardry.common.block;

import com.teamwizardry.librarianlib.features.base.block.tile.BlockModContainer;
import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.client.render.block.TileJarRenderer;
import com.teamwizardry.wizardry.common.entity.EntityFairy;
import com.teamwizardry.wizardry.common.item.ItemJar;
import com.teamwizardry.wizardry.common.tile.TileJar;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;

/**
 * Created by Demoniaque.
 */
public class BlockJar extends BlockModContainer {
	private static final AxisAlignedBB AABB_JAR = new AxisAlignedBB(0.25, 0, 0.25, 0.75, 0.75, 0.75);

	public BlockJar() {
		super("jar_block", Material.GLASS);
		setSoundType(SoundType.GLASS);
		setHardness(0.3f);
		setResistance(1.5f);
		setLightOpacity(0);
	}

	@Nullable
	@Override
	public ItemBlock createItemForm() {
		return new ItemJar(this);
	}

	@Override
	public int getLightValue(@Nonnull IBlockState state, IBlockAccess world, @Nonnull BlockPos pos) {
		TileEntity entity = world.getTileEntity(pos);
		if (entity != null && entity instanceof TileJar) {
			TileJar jar = (TileJar) entity;
			return jar.hasFairy ? 15 : 0;
		} else return 0;
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		if (ItemNBTHelper.getBoolean(stack, Constants.NBT.FAIRY_INSIDE, false)) {
			TileEntity entity = worldIn.getTileEntity(pos);
			if (entity != null && entity instanceof TileJar) {
				TileJar jar = (TileJar) entity;
				jar.color = new Color(ItemNBTHelper.getInt(stack, Constants.NBT.FAIRY_COLOR, 0xFFFFFF));
				jar.age = ItemNBTHelper.getInt(stack, Constants.NBT.FAIRY_AGE, 0);
				jar.hasFairy = true;
				jar.markDirty();
				worldIn.checkLight(pos);
			}
		}
	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		ItemStack stack = new ItemStack(this);
		TileEntity entity = world.getTileEntity(pos);
		if (entity != null && entity instanceof TileJar) {
			TileJar jar = (TileJar) entity;
			if (jar.color == null) return stack;
			ItemNBTHelper.setInt(stack, Constants.NBT.FAIRY_COLOR, jar.color.getRGB());
			ItemNBTHelper.setInt(stack, Constants.NBT.FAIRY_AGE, jar.age);
		}
		return stack;
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!worldIn.isRemote) {
			TileEntity tile = worldIn.getTileEntity(pos);
			if (tile != null && tile instanceof TileJar) {
				TileJar jar = (TileJar) tile;
				if (playerIn.isSneaking() && jar.hasFairy) {
					EntityFairy entity = new EntityFairy(worldIn, jar.color, jar.age);
					entity.setPosition(playerIn.posX, playerIn.posY, playerIn.posZ);
					worldIn.spawnEntity(entity);
					jar.hasFairy = false;
					jar.markDirty();
					return true;
				}
			}
		}
		return false;
	}

	@Nonnull
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}

	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return AABB_JAR;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState iBlockState) {
		return new TileJar();
	}

	@SideOnly(Side.CLIENT)
	public void initModel() {
		ClientRegistry.bindTileEntitySpecialRenderer(TileJar.class, new TileJarRenderer());
	}
}
