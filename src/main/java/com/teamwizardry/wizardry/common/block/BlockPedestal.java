package com.teamwizardry.wizardry.common.block;

import com.teamwizardry.librarianlib.common.base.ModCreativeTab;
import com.teamwizardry.librarianlib.common.base.block.BlockMod;
import com.teamwizardry.librarianlib.common.base.block.BlockModContainer;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.block.IManaAcceptor;
import com.teamwizardry.wizardry.client.render.TilePedestalRenderer;
import com.teamwizardry.wizardry.common.tile.TilePedestal;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Saad on 5/7/2016.
 */
public class BlockPedestal extends BlockModContainer implements IManaAcceptor {

    public BlockPedestal() {
        super("pedestal", Material.ROCK);
        GameRegistry.registerTileEntity(TilePedestal.class, "pedestal");
        setCreativeTab(Wizardry.tab);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            TilePedestal te = getTE(world, pos);

            if (te.getManaPearl() == null && heldItem != null) {
                te.setManaPearl(heldItem);
                player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                player.openContainer.detectAndSendChanges();

            } else if (te.getManaPearl() != null && heldItem == null) {
                ItemStack stack = te.getManaPearl();
                te.setManaPearl(null);
                if (!player.inventory.addItemStackToInventory(stack)) {
                    EntityItem entityItem = new EntityItem(world, pos.getX(), pos.getY() + 1, pos.getZ(), stack);
                    world.spawnEntityInWorld(entityItem);
                } else player.openContainer.detectAndSendChanges();
            }
        }
        return true;
    }

    private TilePedestal getTE(World world, BlockPos pos) {
        return (TilePedestal) world.getTileEntity(pos);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState iBlockState) {
        return new TilePedestal();
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
