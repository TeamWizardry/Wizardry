package com.teamwizardry.wizardry.common.block;

import com.teamwizardry.librarianlib.features.base.ModCreativeTab;
import com.teamwizardry.librarianlib.features.base.block.tile.BlockModContainer;
import com.teamwizardry.librarianlib.features.helpers.NBTHelper;
import com.teamwizardry.wizardry.api.entity.FairyData;
import com.teamwizardry.wizardry.common.entity.EntityFairy;
import com.teamwizardry.wizardry.common.tile.TileJar;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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

	@Override
	public int getLightValue(@Nonnull IBlockState state, IBlockAccess world, @Nonnull BlockPos pos) {
		TileEntity entity = world.getTileEntity(pos);
		if (entity instanceof TileJar) {
			TileJar jar = (TileJar) entity;
			return jar.fairy != null ? (int) (5 + 10 * jar.fairy.handler.getMana() / jar.fairy.handler.getMaxMana() * (jar.fairy.isDepressed ? 0 : 1)) : 0;
		} else return 0;
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		TileEntity entity = worldIn.getTileEntity(pos);
		if (entity instanceof TileJar) {
			TileJar jar = (TileJar) entity;
			jar.fairy = FairyData.deserialize(NBTHelper.getCompound(stack, "fairy"));
			jar.markDirty();
			worldIn.checkLight(pos);
		}
	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		ItemStack stack = new ItemStack(ModItems.JAR_ITEM);
		TileEntity entity = world.getTileEntity(pos);
		if (entity instanceof TileJar) {
			TileJar jar = (TileJar) entity;
			if (jar.fairy == null) return stack;
			stack = new ItemStack(ModItems.JAR_ITEM);
			stack.setItemDamage(2);
			NBTHelper.setTag(stack, "fairy", jar.fairy.serializeNBT());
		}
		return stack;
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		drops.clear();
	}

	@Override
	public void breakBlock(@NotNull World worldIn, @NotNull BlockPos pos, @NotNull IBlockState state) {
		ItemStack stack = new ItemStack(ModItems.JAR_ITEM);
		TileEntity entity = worldIn.getTileEntity(pos);
		if (entity instanceof TileJar) {
			TileJar jar = (TileJar) entity;
			if (jar.fairy == null) {
				return;
			}
			stack.setItemDamage(2);
			NBTHelper.setTag(stack, "fairy", jar.fairy.serializeNBT());
		}
		spawnAsEntity(worldIn, pos, stack);

		super.breakBlock(worldIn, pos, state);
	}

	@Nullable
	@Override
	public ItemBlock createItemForm() {
		return null;
	}

	@Override
	public ModCreativeTab getCreativeTab() {
		return null;
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!worldIn.isRemote) {
			TileEntity tile = worldIn.getTileEntity(pos);
			if (tile instanceof TileJar) {
				TileJar jar = (TileJar) tile;
				if (jar.fairy != null) {
					ItemStack stack = playerIn.getHeldItem(hand);
					if (stack.isEmpty() && playerIn.isSneaking()) {

						if (jar.fairy.isDepressed) {
							ItemStack fairyStack = new ItemStack(ModItems.FAIRY_ITEM);
							NBTHelper.setCompound(fairyStack, "fairy", jar.fairy.serializeNBT());

							EntityItem entityItem = new EntityItem(worldIn, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, fairyStack);
							entityItem.setPickupDelay(20);
							worldIn.spawnEntity(entityItem);

							jar.fairy = null;
							jar.markDirty();

						} else if (jar.fairy.handler.getMana() >= jar.fairy.handler.getMaxMana()) {
							EntityFairy entity = new EntityFairy(worldIn, jar.fairy);
							entity.setPosition(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);

							worldIn.spawnEntity(entity);
							Explosion explosion = worldIn.createExplosion(entity, pos.getX(), pos.getY(), pos.getZ(), 10, true);
							entity.attackEntityFrom(DamageSource.causeExplosionDamage(explosion), 5);

							jar.fairy = null;
							jar.markDirty();

						} else {
							EntityFairy entity = new EntityFairy(worldIn, jar.fairy);
							entity.setPosition(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);

							worldIn.spawnEntity(entity);

							jar.fairy = null;
							jar.markDirty();
						}

						worldIn.notifyBlockUpdate(pos, state, worldIn.getBlockState(pos), 3);
						worldIn.checkLight(pos);
						return true;

					} else if (stack.getItem() == ModItems.SKY_DUST && !jar.fairy.isDepressed) {
						jar.fairy.isDepressed = true;
						jar.markDirty();
						stack.shrink(1);
						worldIn.notifyBlockUpdate(pos, state, worldIn.getBlockState(pos), 3);
						worldIn.checkLight(pos);
					}
				}
			}
		}
		return false;
	}

	@Nonnull
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getRenderLayer() {
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
}
