package com.teamwizardry.wizardry.common.block;

import com.teamwizardry.librarianlib.features.base.block.BlockModContainer;
import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.client.render.block.TileJarRenderer;
import com.teamwizardry.wizardry.common.entity.EntityFairy;
import com.teamwizardry.wizardry.common.item.ItemJar;
import com.teamwizardry.wizardry.common.tile.TileJar;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * Created by LordSaad.
 */
public class BlockJar extends BlockModContainer {

	public BlockJar() {
		super("jar_block", Material.GLASS);
	}

	@Nullable
	@Override
	public ItemBlock createItemForm() {
		return new ItemJar(this);
	}

	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
		TileJar jar = (TileJar) world.getTileEntity(pos);
		if (jar == null) return 0;
		return jar.hasFairy ? 15 : 0;
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		if (ItemNBTHelper.getBoolean(stack, Constants.NBT.FAIRY_INSIDE, false)) {

			TileJar jar = (TileJar) worldIn.getTileEntity(pos);
			if (jar == null) return;
			jar.color = new Color(ItemNBTHelper.getInt(stack, Constants.NBT.FAIRY_COLOR, 0xFFFFFF));
			jar.age = ItemNBTHelper.getInt(stack, Constants.NBT.FAIRY_AGE, 0);
			jar.hasFairy = true;
			jar.markDirty();
		}
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!worldIn.isRemote) {
			TileJar jar = (TileJar) worldIn.getTileEntity(pos);
			if (jar == null) return false;
			if (playerIn.isSneaking() && jar.hasFairy) {
				EntityFairy entity = new EntityFairy(worldIn, jar.color, jar.age);
				entity.setPosition(playerIn.posX, playerIn.posY, playerIn.posZ);
				worldIn.spawnEntity(entity);
				return true;
			}
		}
		return false;
	}

	@NotNull
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

	@Nullable
	@Override
	public TileEntity createTileEntity(@NotNull World world, @NotNull IBlockState iBlockState) {
		return new TileJar();
	}

	public void initModel() {
		ClientRegistry.bindTileEntitySpecialRenderer(TileJar.class, new TileJarRenderer());
	}
}
