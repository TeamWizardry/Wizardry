package com.teamwizardry.wizardry.common.block;

import com.teamwizardry.librarianlib.common.base.ModCreativeTab;
import com.teamwizardry.librarianlib.common.base.block.BlockModContainer;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.block.IManaAcceptor;
import com.teamwizardry.wizardry.common.tile.TileCraftingPlate;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Saad on 6/10/2016.
 */
public class BlockCraftingPlate extends BlockModContainer implements IManaAcceptor {

    public static final AxisAlignedBB AABB = new AxisAlignedBB(0.125, 0, 0.125, 0.875, 0.625, 0.875);

    public BlockCraftingPlate() {
        super("crafting_plate", Material.ROCK);
        setHardness(1F);
        setLightLevel(15);
        setSoundType(SoundType.STONE);
        GameRegistry.registerTileEntity(TileCraftingPlate.class, "crafting_altar");
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
        TileCraftingPlate te = getTE(worldIn, pos);
        te.validateStructure();
        if (!worldIn.isRemote) {
            if (!te.isStructureComplete()) {
                //Schematic schematic = new Schematic("spell_crafter");
                //te.setStructureComplete(schematic.check(worldIn, pos, this, playerIn));
            }
        }
        return true;
    }

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
