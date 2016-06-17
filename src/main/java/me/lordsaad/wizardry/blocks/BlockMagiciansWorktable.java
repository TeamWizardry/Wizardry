package me.lordsaad.wizardry.blocks;

import me.lordsaad.wizardry.ModBlocks;
import me.lordsaad.wizardry.Wizardry;
import me.lordsaad.wizardry.tileentities.TileMagiciansWorktable;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Saad on 6/12/2016.
 */
public class BlockMagiciansWorktable extends Block implements ITileEntityProvider {

	public static final PropertyEnum<EnumFacing> FACING = PropertyEnum.create("facing", EnumFacing.class);
	public static final PropertyBool ISLEFTSIDE = PropertyBool.create("is_left_side");

	public BlockMagiciansWorktable() {
		super(Material.WOOD);
		setHardness(1F);
		setSoundType(SoundType.WOOD);
		setUnlocalizedName("magicians_worktable");
		setRegistryName("magicians_worktable");
		GameRegistry.register(this);
		GameRegistry.register(new ItemBlock(this), getRegistryName());
		GameRegistry.registerTileEntity(TileMagiciansWorktable.class, "magicians_worktable");
		setCreativeTab(Wizardry.tab);
		setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(ISLEFTSIDE, true));
	}

	@SideOnly(Side.CLIENT)
	public void initModel() {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) 
	{
		return new TileMagiciansWorktable();
	}

	private TileMagiciansWorktable getTE(IBlockAccess world, BlockPos pos)
	{
		return (TileMagiciansWorktable) world.getTileEntity(pos);
	}

	@Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) 
	{
		EnumFacing placerFacing = placer.getHorizontalFacing();
		EnumFacing offsetDir = placerFacing.rotateY();
		BlockPos part2Pos = pos.offset(offsetDir);
		Block block = worldIn.getBlockState(part2Pos).getBlock();
		if(block.isReplaceable(worldIn, part2Pos))
		{
			worldIn.setBlockState(part2Pos, this.getDefaultState().withProperty(FACING, placerFacing.getOpposite()).withProperty(ISLEFTSIDE, false));
			return this.getDefaultState().withProperty(FACING, placerFacing.getOpposite()).withProperty(ISLEFTSIDE, true);
		}
		else
		{
			block = worldIn.getBlockState(part2Pos.offset(offsetDir.getOpposite(), 2)).getBlock();
			part2Pos = part2Pos.offset(offsetDir.getOpposite(), 2);
			if(block.isReplaceable(worldIn, part2Pos))
			{
				worldIn.setBlockState(part2Pos, this.getDefaultState().withProperty(FACING, placerFacing.getOpposite()).withProperty(ISLEFTSIDE, true));
			}
			return this.getDefaultState().withProperty(FACING, placerFacing.getOpposite()).withProperty(ISLEFTSIDE, false);
		}
	}

	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) 
	{
		//The TE is linked here because it hasn't yet been created in Block#onBlockPlaced()
		this.getTE(worldIn, pos).setLinkedTable(getOtherTableBlock(state, pos));
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) 
	{
		super.breakBlock(worldIn, pos, state);
		worldIn.destroyBlock(getOtherTableBlock(state, pos), false);
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) 
	{
		for(EnumFacing facing: EnumFacing.HORIZONTALS)
		{
			Block block1 = worldIn.getBlockState(pos).getBlock();
			Block block2 = worldIn.getBlockState(pos.offset(facing)).getBlock();
			if(block1.isReplaceable(worldIn, pos) && block2.isReplaceable(worldIn, pos)) return true;
		}
		return true;
	}

	private BlockPos getOtherTableBlock(IBlockState tablePart, BlockPos tablePartPos)
	{

		if(tablePart.getValue(ISLEFTSIDE))
		{
			return tablePartPos.offset(tablePart.getValue(FACING).rotateYCCW());
		}
		else 
		{
			return tablePartPos.offset(tablePart.getValue(FACING).rotateY());
		}
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING, ISLEFTSIDE);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) 
	{
		return this.getDefaultState().withProperty(ISLEFTSIDE, meta <=5).withProperty(FACING, EnumFacing.values()[meta <=5 ? meta : meta - 6]);
	}

	@Override
	public int getMetaFromState(IBlockState state) 
	{	
		int facing = state.getValue(FACING).getIndex();
		return state.getValue(ISLEFTSIDE) ? facing : facing + 6;
	}

	@Override
	public boolean canRenderInLayer(BlockRenderLayer layer) 
	{
		return layer == BlockRenderLayer.CUTOUT || layer == BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
		return false;
	}

	@Override
	public boolean isVisuallyOpaque() {
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState blockState) {
		return false;
	}
}
