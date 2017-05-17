package com.teamwizardry.wizardry.common.block;

import com.teamwizardry.librarianlib.features.base.ModCreativeTab;
import com.teamwizardry.librarianlib.features.base.block.BlockModContainer;
import com.teamwizardry.librarianlib.features.structure.Structure;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.block.IManaSink;
import com.teamwizardry.wizardry.api.block.IStructure;
import com.teamwizardry.wizardry.client.render.TileManaBatteryRenderer;
import com.teamwizardry.wizardry.common.tile.TileManaBattery;
import com.teamwizardry.wizardry.init.ModStructures;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockManaBattery extends BlockModContainer implements IManaSink, IStructure {

	public BlockManaBattery() {
		super("mana_battery", Material.GROUND);
	}

	@SideOnly(Side.CLIENT)
	public void initModel() {
		ClientRegistry.bindTileEntitySpecialRenderer(TileManaBattery.class, new TileManaBatteryRenderer());
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(World world, IBlockState iBlockState) {
		return new TileManaBattery();
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		tickStructure(worldIn, playerIn, pos);
		return false;
	}

	@Override
	public int getLightValue(@Nonnull IBlockState state, IBlockAccess world, @Nonnull BlockPos pos) {
		return 15;
	}

	@Override
	public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
		return layer == BlockRenderLayer.TRANSLUCENT;
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

	@Override
	public Structure getStructure() {
		return ModStructures.INSTANCE.structures.get("mana_battery");
	}

	@Override
	public Vec3i offsetToCenter() {
		return new Vec3i(7, 4, 7);
	}
}
