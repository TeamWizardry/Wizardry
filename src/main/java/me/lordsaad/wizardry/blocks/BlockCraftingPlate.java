package me.lordsaad.wizardry.blocks;

import me.lordsaad.wizardry.Wizardry;
import me.lordsaad.wizardry.schematic.Schematic;
import me.lordsaad.wizardry.tileentities.TileCraftingPlate;
import me.lordsaad.wizardry.tileentities.TileCraftingPlateRenderer;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Saad on 6/10/2016.
 */
public class BlockCraftingPlate extends Block implements ITileEntityProvider {

    public BlockCraftingPlate() {
        super(Material.ROCK);
        setHardness(1F);
        setLightLevel(5);
        setSoundType(SoundType.STONE);
        setUnlocalizedName("crafting_plate");
        setRegistryName("crafting_plate");
        GameRegistry.register(this);
        GameRegistry.registerTileEntity(TileCraftingPlate.class, "crafting_altar");
        GameRegistry.register(new ItemBlock(this), getRegistryName());
        setCreativeTab(Wizardry.tab);
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
        ClientRegistry.bindTileEntitySpecialRenderer(TileCraftingPlate.class, new TileCraftingPlateRenderer());
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileCraftingPlate();
    }

    private TileCraftingPlate getTE(World world, BlockPos pos) {
        return (TileCraftingPlate) world.getTileEntity(pos);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        TileCraftingPlate te = getTE(worldIn, pos);
        if (!worldIn.isRemote) {
            if (!te.isStructureComplete()) {
                Schematic schematic = new Schematic("spell_crafter");
                te.setStructureComplete(schematic.check(worldIn, pos, this, playerIn));
            }
        }
        return true;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        return new AxisAlignedBB(0, 0, 0, 1, 0.75, 1);
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
}
