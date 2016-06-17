package me.lordsaad.wizardry.blocks;

import me.lordsaad.wizardry.ModBlocks;
import me.lordsaad.wizardry.Wizardry;
import me.lordsaad.wizardry.api.Constants;
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
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Saad on 6/12/2016.
 */
public class BlockMagiciansWorktable extends Block implements ITileEntityProvider {

    public static final PropertyEnum<EnumFacing> FACING = PropertyEnum.create("facing", EnumFacing.class);
    public static final PropertyBool LINKED = PropertyBool.create("linked");
    public static final PropertyInteger LINKEDDIRECTION = PropertyInteger.create("direction", 0, 2);

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
        setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileMagiciansWorktable();
    }

    private TileMagiciansWorktable getTE(World world, BlockPos pos) {
        return (TileMagiciansWorktable) world.getTileEntity(pos);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        playerIn.openGui(Wizardry.instance, Constants.PageNumbers.WORKTABLE, worldIn, (int) playerIn.posX, (int) playerIn.posY, (int) playerIn.posZ);

        return true;
    }

    @Override
    public void onBlockDestroyedByPlayer(World worldIn, BlockPos pos, IBlockState state) {
        if (state.getValue(LINKED)) {
            if (getTE(worldIn, pos) != null) {
                if (getTE(worldIn, pos).getLinkedTable() != null) {
                    worldIn.setBlockToAir(getTE(worldIn, pos).getLinkedTable());
                }
            }
        }
    }

    @Override
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }

    @Override
    public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
        if (world.getBlockState(neighbor).getBlock() == ModBlocks.magiciansWorktable) {
            TileMagiciansWorktable neighborTable = getTE((World) world, neighbor);
            if (neighborTable != null) {
                if (neighborTable.getLinkedTable() == null && getTE((World) world, pos).getLinkedTable() == null) {
                    neighborTable.setLinkedTable(pos);
                    ((World) world).setBlockState(neighbor, world.getBlockState(neighbor).withProperty(LINKED, true));
                    getTE((World) world, pos).setLinkedTable(neighbor);
                    setDefaultState(blockState.getBaseState().withProperty(LINKED, true));
                    Minecraft.getMinecraft().thePlayer.sendChatMessage("NEIGHBOR LINKED WITH: " + ((TileMagiciansWorktable) world.getTileEntity(neighbor)).getLinkedTable());
                    Minecraft.getMinecraft().thePlayer.sendChatMessage("NEIGHBOR LINKED: " + world.getBlockState(neighbor).getValue(LINKED));
                    Minecraft.getMinecraft().thePlayer.sendChatMessage("ME LINKED WITH: " + getTE((World) world, pos).getLinkedTable());
                    Minecraft.getMinecraft().thePlayer.sendChatMessage("ME LINKED: " + blockState.getBaseState().getValue(LINKED));

                    // 0 = default
                    // 1 = right
                    // 2 = left
                    if (pos.getX() - neighbor.getX() > 0) {
                        setDefaultState(blockState.getBaseState().withProperty(LINKEDDIRECTION, 1));
                        ((World) world).setBlockState(neighbor, world.getBlockState(neighbor).withProperty(LINKEDDIRECTION, 2));
                    } else if (pos.getX() - neighbor.getX() < 0) {
                        setDefaultState(blockState.getBaseState().withProperty(LINKEDDIRECTION, 2));
                        ((World) world).setBlockState(neighbor, world.getBlockState(neighbor).withProperty(LINKEDDIRECTION, 1));
                    } else if (pos.getZ() - neighbor.getZ() > 0) {
                        setDefaultState(blockState.getBaseState().withProperty(LINKEDDIRECTION, 2));
                        ((World) world).setBlockState(neighbor, world.getBlockState(neighbor).withProperty(LINKEDDIRECTION, 1));
                    } else if (pos.getZ() - neighbor.getX() < 0) {
                        setDefaultState(blockState.getBaseState().withProperty(LINKEDDIRECTION, 1));
                        ((World) world).setBlockState(neighbor, world.getBlockState(neighbor).withProperty(LINKEDDIRECTION, 2));
                    } else {
                        setDefaultState(blockState.getBaseState().withProperty(LINKEDDIRECTION, 0));
                        ((World) world).setBlockState(neighbor, world.getBlockState(neighbor).withProperty(LINKEDDIRECTION, 0));
                    }

                    Minecraft.getMinecraft().thePlayer.sendChatMessage("MY NEIGHBOR'S LINKED DIRECTION: " + blockState.getBaseState().getValue(LINKEDDIRECTION));
                    Minecraft.getMinecraft().thePlayer.sendChatMessage("MY LINKED DIRECTION FROM THEM: " + world.getBlockState(neighbor).getValue(LINKEDDIRECTION));
                }
            }
        }
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, LINKED, LINKEDDIRECTION);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(FACING, EnumFacing.getFront(meta & 7)).withProperty(LINKED, false).withProperty(LINKEDDIRECTION, 0);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getIndex();
    }

    @Override
    public boolean canRenderInLayer(BlockRenderLayer layer) {
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
